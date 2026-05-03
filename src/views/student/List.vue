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

const router = useRouter()
const message = useMessage()
const dialog = useDialog()

const loading = ref(false)
const searchQuery = ref('')
const filterCourse = ref(null)
const showModal = ref(false)
const modalTitle = ref('添加学生')
const formRef = ref()

const formData = ref({
  id: null,
  name: '',
  age: null,
  gender: '男',
  parentName: '',
  phone: '',
  courses: [],
  enrollDate: null
})

const rules = {
  name: {
    required: true,
    message: '请输入学生姓名',
    trigger: 'blur'
  },
  age: {
    required: true,
    message: '请输入年龄',
    trigger: 'blur'
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

function loadData() {
  loading.value = true

  // Mock数据
  setTimeout(() => {
    studentList.value = [
      {
        id: 1,
        name: '小明',
        age: 8,
        gender: '男',
        parentName: '明爸爸',
        phone: '13900139001',
        courses: ['钢琴基础', '素描基础'],
        enrollDate: '2024-09-01'
      },
      {
        id: 2,
        name: '小红',
        age: 7,
        gender: '女',
        parentName: '红妈妈',
        phone: '13900139002',
        courses: ['芭蕾舞初级', '钢琴基础'],
        enrollDate: '2024-09-01'
      },
      {
        id: 3,
        name: '小刚',
        age: 9,
        gender: '男',
        parentName: '刚爸爸',
        phone: '13900139003',
        courses: ['街舞入门', '素描提高'],
        enrollDate: '2024-09-05'
      },
      {
        id: 4,
        name: '小丽',
        age: 6,
        gender: '女',
        parentName: '丽妈妈',
        phone: '13900139004',
        courses: ['芭蕾舞初级'],
        enrollDate: '2024-09-10'
      },
      {
        id: 5,
        name: '小芳',
        age: 8,
        gender: '女',
        parentName: '芳妈妈',
        phone: '13900139005',
        courses: ['中国舞初级', '水彩入门'],
        enrollDate: '2024-09-15'
      },
      {
        id: 6,
        name: '小华',
        age: 10,
        gender: '男',
        parentName: '华爸爸',
        phone: '13900139006',
        courses: ['素描提高', '国画基础'],
        enrollDate: '2024-10-01'
      },
      {
        id: 7,
        name: '小美',
        age: 7,
        gender: '女',
        parentName: '美妈妈',
        phone: '13900139007',
        courses: ['声乐基础', '钢琴基础'],
        enrollDate: '2024-10-05'
      },
      {
        id: 8,
        name: '小强',
        age: 9,
        gender: '男',
        parentName: '强爸爸',
        phone: '13900139008',
        courses: ['街舞入门'],
        enrollDate: '2024-10-10'
      }
    ]
    loading.value = false
  }, 500)
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
    enrollDate: null
  }
  showModal.value = true
}

function handleEdit(row: any) {
  modalTitle.value = '编辑学生'
  formData.value = {
    ...row,
    enrollDate: new Date(row.enrollDate).getTime()
  }
  showModal.value = true
}

function handleDelete(row: any) {
  dialog.warning({
    title: '确认删除',
    content: `确定要删除学生 ${row.name} 吗？`,
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: () => {
      const index = studentList.value.findIndex(item => item.id === row.id)
      if (index > -1) {
        studentList.value.splice(index, 1)
        message.success('删除成功')
      }
    }
  })
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
      // 编辑
      const index = studentList.value.findIndex(item => item.id === data.id)
      if (index > -1) {
        studentList.value[index] = data
        message.success('编辑成功')
      }
    } else {
      // 新增
      data.id = Date.now()
      studentList.value.unshift(data)
      message.success('添加成功')
    }

    showModal.value = false
  } catch (error) {
    console.error('验证失败:', error)
  }
}
</script>

<style scoped>
.page-container {
  width: 100%;
}
</style>
