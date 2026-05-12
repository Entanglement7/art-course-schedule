<template>
  <div class="page-container">
    <n-card title="学生管理" :bordered="false">
      <template #header-extra>
        <n-button type="primary" @click="handleAdd">
          <template #icon>
            <n-icon>
              <add-outline />
            </n-icon>
          </template>
          添加学生
        </n-button>
      </template>

      <!-- 搜索栏 -->
      <n-space :size="16" style="margin-bottom: 16px">
        <n-input
          v-model:value="searchQuery"
          placeholder="搜索学生姓名"
          clearable
          style="width: 200px"
        >
          <template #prefix>
            <n-icon :component="SearchOutline" />
          </template>
        </n-input>

        <n-select
          v-model:value="filterCourse"
          placeholder="课程筛选"
          clearable
          style="width: 150px"
          :options="courseOptions"
        />

        <n-button @click="handleSearch">搜索</n-button>
        <n-button @click="handleReset">重置</n-button>
      </n-space>

      <!-- 学生列表 -->
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
          <n-input v-model:value="formData.name" placeholder="请输入学生姓名" />
        </n-form-item>

        <n-form-item label="年龄" path="age">
          <n-input-number v-model:value="formData.age" :min="3" :max="18" placeholder="请输入年龄" style="width: 100%" />
        </n-form-item>

        <n-form-item label="性别" path="gender">
          <n-radio-group v-model:value="formData.gender">
            <n-space>
              <n-radio value="男">男</n-radio>
              <n-radio value="女">女</n-radio>
            </n-space>
          </n-radio-group>
        </n-form-item>

        <n-form-item label="家长姓名" path="parentName">
          <n-input v-model:value="formData.parentName" placeholder="请输入家长姓名" />
        </n-form-item>

        <n-form-item label="联系电话" path="phone">
          <n-input v-model:value="formData.phone" placeholder="请输入联系电话" />
        </n-form-item>

        <n-form-item label="报名课程" path="courses">
          <n-select
            v-model:value="formData.courses"
            placeholder="请选择报名课程"
            multiple
            :options="courseOptions"
          />
        </n-form-item>

        <n-form-item label="入学时间" path="enrollDate">
          <n-date-picker
            v-model:value="formData.enrollDate"
            type="date"
            placeholder="请选择入学时间"
            style="width: 100%"
          />
        </n-form-item>

        <n-form-item label="关联账号" path="userId">
          <n-select
            v-model:value="formData.userId"
            placeholder="请选择要关联的学生账号"
            clearable
            :options="unlinkedUserOptions"
            :loading="loadingUsers"
          />
          <template #feedback>
            <span style="font-size: 12px; color: #999">
              只显示未关联的学生账号，学生需先注册账号
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
import { useMessage, useDialog, NButton, NTag, NSpace, NAvatar } from 'naive-ui'
import { AddOutline, SearchOutline } from '@vicons/ionicons5'
import dayjs from 'dayjs'
import { getStudents, createStudent, updateStudent, deleteStudent } from '@/api/student'

const router = useRouter()
const message = useMessage()
const dialog = useDialog()

const loading = ref(false)
const searchQuery = ref('')
const filterCourse = ref(null)
const showModal = ref(false)
const modalTitle = ref('添加学生')
const formRef = ref()
const loadingUsers = ref(false)
const unlinkedUserOptions = ref<any[]>([])

const formData = ref({
  id: null,
  name: '',
  age: null,
  gender: '男',
  parentName: '',
  phone: '',
  courses: [],
  enrollDate: null,
  userId: null
})

const rules = {
  name: {
    required: true,
    message: '请输入学生姓名',
    trigger: 'blur'
  },
  age: {
    type: 'number',
    required: true,
    message: '请输入年龄',
    trigger: ['blur', 'change']
  },
  parentName: {
    required: true,
    message: '请输入家长姓名',
    trigger: 'blur'
  },
  phone: {
    required: true,
    message: '请输入联系电话',
    trigger: 'blur'
  }
}

// 课程选项
const courseOptions = [
  { label: '钢琴基础', value: '钢琴基础' },
  { label: '钢琴进阶', value: '钢琴进阶' },
  { label: '声乐基础', value: '声乐基础' },
  { label: '芭蕾舞初级', value: '芭蕾舞初级' },
  { label: '芭蕾舞中级', value: '芭蕾舞中级' },
  { label: '中国舞初级', value: '中国舞初级' },
  { label: '街舞入门', value: '街舞入门' },
  { label: '素描基础', value: '素描基础' },
  { label: '素描提高', value: '素描提高' },
  { label: '水彩入门', value: '水彩入门' },
  { label: '国画基础', value: '国画基础' }
]

// Mock数据
const studentList = ref<any[]>([])

// 表格列定义
const columns = [
  {
    title: '学生',
    key: 'name',
    width: 150,
    render(row: any) {
      return h(
        NSpace,
        { align: 'center' },
        {
          default: () => [
            h(
              NAvatar,
              {
                round: true,
                size: 'small',
                style: {
                  background: row.gender === '男' ? '#7C3AED' : '#EC4899'
                }
              },
              { default: () => row.name.charAt(0) }
            ),
            h('span', row.name)
          ]
        }
      )
    }
  },
  {
    title: '年龄',
    key: 'age',
    width: 80
  },
  {
    title: '性别',
    key: 'gender',
    width: 80
  },
  {
    title: '家长',
    key: 'parentName',
    width: 120
  },
  {
    title: '联系电话',
    key: 'phone',
    width: 140
  },
  {
    title: '报名课程',
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
    title: '入学时间',
    key: 'enrollDate',
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
                onClick: () => router.push(`/timetable/student?id=${row.id}`)
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
  let data = studentList.value

  if (searchQuery.value) {
    data = data.filter(item =>
      item.name.toLowerCase().includes(searchQuery.value.toLowerCase())
    )
  }

  if (filterCourse.value) {
    data = data.filter(item => item.courses.includes(filterCourse.value))
  }

  return data
})

onMounted(() => {
  loadData()
})

async function loadData() {
  loading.value = true
  try {
    const res = await getStudents({ size: 100 }) as any
    const list: any[] = res.records ?? res.list ?? res
    studentList.value = list.map((item: any) => ({
      id: item.id,
      name: item.name,
      age: item.age,
      gender: item.gender,
      parentName: item.parentName,
      phone: item.phone,
      courses: item.courses || [],
      enrollDate: item.enrollDate
    }))
  } catch (err: any) {
    message.error(err.message || '加载学生列表失败')
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  // 搜索逻辑已在computed中实现
}

function handleReset() {
  searchQuery.value = ''
  filterCourse.value = null
}

function handleAdd() {
  modalTitle.value = '添加学生'
  formData.value = {
    id: null,
    name: '',
    age: null,
    gender: '男',
    parentName: '',
    phone: '',
    courses: [],
    enrollDate: null,
    userId: null
  }
  loadUnlinkedUsers()
  showModal.value = true
}

function handleEdit(row: any) {
  modalTitle.value = '编辑学生'
  formData.value = {
    ...row,
    enrollDate: new Date(row.enrollDate).getTime(),
    userId: null
  }
  loadUnlinkedUsers()
  showModal.value = true
}

async function loadUnlinkedUsers() {
  loadingUsers.value = true
  try {
    const response = await fetch('http://localhost:8080/api/auth/unlinked-users?role=student', {
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
      enrollDate: formData.value.enrollDate
        ? dayjs(formData.value.enrollDate).format('YYYY-MM-DD')
        : ''
    }

    if (data.id) {
      await updateStudent(data.id, data)
      message.success('编辑成功')
    } else {
      await createStudent(data)
      message.success('添加成功')
      if (data.userId) {
        message.info('已关联学生账号，该账号现在可以登录查看课表')
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
    content: `确定要删除学生"${row.name}"吗？`,
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: async () => {
      try {
        await deleteStudent(row.id)
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
