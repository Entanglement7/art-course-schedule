<template>
  <div class="page-container">
    <n-card title="教师管理" :bordered="false">
      <template #header-extra>
        <n-button type="primary" @click="handleAdd">
          <template #icon>
            <n-icon>
              <add-outline />
            </n-icon>
          </template>
          添加教师
        </n-button>
      </template>

      <!-- 搜索栏 -->
      <n-space :size="16" style="margin-bottom: 16px">
        <n-input
          v-model:value="searchQuery"
          placeholder="搜索教师姓名"
          clearable
          style="width: 200px"
        >
          <template #prefix>
            <n-icon :component="SearchOutline" />
          </template>
        </n-input>

        <n-select
          v-model:value="filterSpecialty"
          placeholder="专业筛选"
          clearable
          style="width: 150px"
          :options="specialtyOptions"
        />

        <n-button @click="handleSearch">搜索</n-button>
        <n-button @click="handleReset">重置</n-button>
      </n-space>

      <!-- 教师列表 -->
      <n-data-table
        :columns="columns"
        :data="filteredData"
        :loading="loading"
        :pagination="pagination"
        :bordered="false"
      />
    </n-card>

    <!-- 编辑对话框 -->
    <n-modal
      v-model:show="showModal"
      preset="card"
      :title="modalTitle"
      style="width: 600px"
      :bordered="false"
    >
      <n-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-placement="left"
        label-width="80"
      >
        <n-form-item label="姓名" path="name">
          <n-input v-model:value="formData.name" placeholder="请输入教师姓名" />
        </n-form-item>

        <n-form-item label="联系电话" path="phone">
          <n-input v-model:value="formData.phone" placeholder="请输入联系电话" />
        </n-form-item>

        <n-form-item label="专业" path="specialty">
          <n-select
            v-model:value="formData.specialty"
            placeholder="请选择专业"
            :options="specialtyOptions"
          />
        </n-form-item>

        <n-form-item label="可授课程" path="courses">
          <n-select
            v-model:value="formData.courses"
            placeholder="请选择可授课程"
            multiple
            :options="courseOptions"
          />
        </n-form-item>

        <n-form-item label="入职时间" path="joinDate">
          <n-date-picker
            v-model:value="formData.joinDate"
            type="date"
            placeholder="请选择入职时间"
            style="width: 100%"
          />
        </n-form-item>

        <n-form-item label="关联账号" path="userId">
          <n-select
            v-model:value="formData.userId"
            placeholder="请选择要关联的教师账号"
            clearable
            :options="unlinkedUserOptions"
            :loading="loadingUsers"
          />
          <template #feedback>
            <span style="font-size: 12px; color: #999">
              只显示未关联的教师账号，教师需先注册账号
            </span>
          </template>
        </n-form-item>
      </n-form>

      <template #footer>
        <n-space justify="end">
          <n-button @click="showModal = false">取消</n-button>
          <n-button type="primary" @click="handleSubmit">确定</n-button>
        </n-space>
      </template>
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, h, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useMessage, useDialog, NButton, NTag, NSpace } from 'naive-ui'
import { AddOutline, SearchOutline, CreateOutline, TrashOutline } from '@vicons/ionicons5'
import dayjs from 'dayjs'
import { getTeachers, createTeacher, updateTeacher, deleteTeacher } from '@/api/teacher'

const router = useRouter()
const message = useMessage()
const dialog = useDialog()

const loading = ref(false)
const searchQuery = ref('')
const filterSpecialty = ref(null)
const showModal = ref(false)
const modalTitle = ref('添加教师')
const formRef = ref()
const loadingUsers = ref(false)
const unlinkedUserOptions = ref<any[]>([])

const formData = ref({
  id: null,
  name: '',
  phone: '',
  specialty: null,
  courses: [],
  joinDate: null,
  userId: null
})

const rules = {
  name: {
    required: true,
    message: '请输入教师姓名',
    trigger: 'blur'
  },
  phone: {
    required: true,
    message: '请输入联系电话',
    trigger: 'blur'
  },
  specialty: {
    required: true,
    message: '请选择专业',
    trigger: 'change'
  }
}

// 专业选项
const specialtyOptions = [
  { label: '钢琴', value: '钢琴' },
  { label: '声乐', value: '声乐' },
  { label: '吉他', value: '吉他' },
  { label: '古筝', value: '古筝' },
  { label: '小提琴', value: '小提琴' },
  { label: '芭蕾舞', value: '芭蕾舞' },
  { label: '中国舞', value: '中国舞' },
  { label: '街舞', value: '街舞' },
  { label: '拉丁舞', value: '拉丁舞' },
  { label: '素描', value: '素描' },
  { label: '水彩', value: '水彩' },
  { label: '国画', value: '国画' },
  { label: '创意美术', value: '创意美术' }
]

// 课程选项
const courseOptions = [
  { label: '钢琴基础', value: '钢琴基础' },
  { label: '钢琴进阶', value: '钢琴进阶' },
  { label: '声乐基础', value: '声乐基础' },
  { label: '芭蕾舞初级', value: '芭蕾舞初级' },
  { label: '芭蕾舞中级', value: '芭蕾舞中级' },
  { label: '素描基础', value: '素描基础' },
  { label: '素描提高', value: '素描提高' },
  { label: '水彩入门', value: '水彩入门' }
]

// Mock数据
const teacherList = ref<any[]>([])

// 表格列定义
const columns = [
  {
    title: '姓名',
    key: 'name',
    width: 120
  },
  {
    title: '专业',
    key: 'specialty',
    width: 120,
    render(row: any) {
      const colorMap: Record<string, string> = {
        钢琴: '#8B5CF6',
        声乐: '#8B5CF6',
        吉他: '#8B5CF6',
        古筝: '#8B5CF6',
        小提琴: '#8B5CF6',
        芭蕾舞: '#EC4899',
        中国舞: '#EC4899',
        街舞: '#EC4899',
        拉丁舞: '#EC4899',
        素描: '#F59E0B',
        水彩: '#F59E0B',
        国画: '#F59E0B',
        创意美术: '#F59E0B'
      }
      return h(
        NTag,
        {
          type: 'info',
          bordered: false,
          style: {
            backgroundColor: `${colorMap[row.specialty]}20`,
            color: colorMap[row.specialty]
          }
        },
        { default: () => row.specialty }
      )
    }
  },
  {
    title: '联系电话',
    key: 'phone',
    width: 140
  },
  {
    title: '可授课程',
    key: 'courses',
    render(row: any) {
      return h(
        NSpace,
        { size: 4 },
        {
          default: () =>
            row.courses.slice(0, 2).map((course: string) =>
              h(
                NTag,
                { size: 'small', type: 'info', bordered: false },
                { default: () => course }
              )
            ).concat(
              row.courses.length > 2
                ? [h(NTag, { size: 'small', type: 'info', bordered: false }, { default: () => `+${row.courses.length - 2}` })]
                : []
            )
        }
      )
    }
  },
  {
    title: '入职时间',
    key: 'joinDate',
    width: 120
  },
  {
    title: '操作',
    key: 'actions',
    width: 180,
    render(row: any) {
      return h(
        NSpace,
        {},
        {
          default: () => [
            h(
              NButton,
              {
                size: 'small',
                type: 'primary',
                text: true,
                onClick: () => handleEdit(row)
              },
              { default: () => '编辑' }
            ),
            h(
              NButton,
              {
                size: 'small',
                type: 'error',
                text: true,
                onClick: () => handleDelete(row)
              },
              { default: () => '删除' }
            ),
            h(
              NButton,
              {
                size: 'small',
                type: 'info',
                text: true,
                onClick: () => router.push(`/timetable/teacher?id=${row.id}`)
              },
              { default: () => '查看课表' }
            )
          ]
        }
      )
    }
  }
]

// 分页
const pagination = {
  pageSize: 10
}

// 过滤后的数据
const filteredData = computed(() => {
  let data = teacherList.value

  if (searchQuery.value) {
    data = data.filter(item =>
      item.name.toLowerCase().includes(searchQuery.value.toLowerCase())
    )
  }

  if (filterSpecialty.value) {
    data = data.filter(item => item.specialty === filterSpecialty.value)
  }

  return data
})

onMounted(() => {
  loadData()
})

async function loadData() {
  loading.value = true
  try {
    const res = await getTeachers({ size: 100 }) as any
    const list: any[] = res.records ?? res.list ?? res
    teacherList.value = list.map((item: any) => ({
      id: item.id,
      name: item.name,
      phone: item.phone,
      specialty: item.specialty,
      courses: item.courses || [],
      joinDate: item.joinDate,
      userId: item.userId
    }))
  } catch (err: any) {
    message.error(err.message || '加载教师列表失败')
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  // 搜索逻辑已在computed中实现
}

function handleReset() {
  searchQuery.value = ''
  filterSpecialty.value = null
}

function handleAdd() {
  modalTitle.value = '添加教师'
  formData.value = {
    id: null,
    name: '',
    phone: '',
    specialty: null,
    courses: [],
    joinDate: null,
    userId: null
  }
  loadUnlinkedUsers()
  showModal.value = true
}

function handleEdit(row: any) {
  modalTitle.value = '编辑教师'
  formData.value = {
    ...row,
    joinDate: new Date(row.joinDate).getTime(),
    userId: null
  }
  loadUnlinkedUsers()
  showModal.value = true
}

async function loadUnlinkedUsers() {
  loadingUsers.value = true
  try {
    const response = await fetch('http://localhost:8080/api/auth/unlinked-users?role=teacher', {
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    })
    const result = await response.json()
    if (result.code === 200) {
      unlinkedUserOptions.value = result.data.map((user: any) => ({
        label: `${user.name} (${user.username})`,
        value: user.id
      }))
    }
  } catch (error) {
    console.error('加载未关联账号失败:', error)
  } finally {
    loadingUsers.value = false
  }
}

async function handleSubmit() {
  try {
    await formRef.value?.validate()

    const data = {
      ...formData.value,
      joinDate: formData.value.joinDate
        ? dayjs(formData.value.joinDate).format('YYYY-MM-DD')
        : ''
    }

    if (data.id) {
      await updateTeacher(data.id, data)
      message.success('编辑成功')
    } else {
      await createTeacher(data)
      message.success('添加成功')
      if (data.userId) {
        message.info('已关联教师账号，该账号现在可以登录查看课表')
      }
    }

    showModal.value = false
    await loadData()
  } catch (error: any) {
    if (error?.message) {
      message.error(error.message)
    } else {
      console.error('验证失败:', error)
    }
  }
}

async function handleDelete(row: any) {
  dialog.warning({
    title: '确认删除',
    content: `确定要删除教师"${row.name}"吗？`,
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: async () => {
      try {
        await deleteTeacher(row.id)
        message.success('删除成功')
        await loadData()
      } catch (err: any) {
        message.error(err.message || '删除失败')
      }
    }
  })
}
</script>

<style scoped>
.page-container {
  width: 100%;
}
</style>
