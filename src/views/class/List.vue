<template>
  <div class="page-container">
    <n-card title="班级管理" :bordered="false">
      <template #header-extra>
        <n-button type="primary" @click="handleAdd">
          <template #icon>
            <n-icon>
              <add-outline />
            </n-icon>
          </template>
          添加班级
        </n-button>
      </template>

      <!-- 搜索栏 -->
      <n-space :size="16" style="margin-bottom: 16px">
        <n-input
          v-model:value="searchQuery"
          placeholder="搜索班级名称"
          clearable
          style="width: 200px"
        >
          <template #prefix>
            <n-icon :component="SearchOutline" />
          </template>
        </n-input>

        <n-select
          v-model:value="filterCategory"
          placeholder="类别筛选"
          clearable
          style="width: 150px"
          :options="categoryOptions"
        />

        <n-button @click="handleSearch">搜索</n-button>
        <n-button @click="handleReset">重置</n-button>
      </n-space>

      <!-- 班级列表 -->
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
        <n-form-item label="班级名称" path="name">
          <n-input v-model:value="formData.name" placeholder="请输入班级名称" />
        </n-form-item>

        <n-form-item label="课程类别" path="category">
          <n-select
            v-model:value="formData.category"
            placeholder="请选择课程类别"
            :options="categoryOptions"
          />
        </n-form-item>

        <n-form-item label="课程名称" path="courseName">
          <n-input v-model:value="formData.courseName" placeholder="例如：钢琴基础" />
        </n-form-item>

        <n-form-item label="授课教师" path="teacher">
          <n-select
            v-model:value="formData.teacher"
            placeholder="请选择授课教师"
            :options="teacherOptions"
          />
        </n-form-item>

        <n-form-item label="班级人数" path="studentCount">
          <n-input-number v-model:value="formData.studentCount" :min="1" :max="30" placeholder="请输入班级人数" style="width: 100%" />
        </n-form-item>

        <n-form-item label="上课时间" path="schedule">
          <n-input v-model:value="formData.schedule" placeholder="例如：周一 09:00-10:00" />
        </n-form-item>

        <n-form-item label="开班日期" path="startDate">
          <n-date-picker
            v-model:value="formData.startDate"
            type="date"
            placeholder="请选择开班日期"
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

    <!-- 学生名单对话框 -->
    <n-modal
      v-model:show="showStudentModal"
      preset="card"
      :title="`${selectedClass?.name} - 学生名单`"
      style="width: 600px"
      :bordered="false"
    >
      <n-spin :show="loadingStudents">
        <n-empty v-if="!classStudents.length" description="暂无学生" />
        <n-list v-else bordered>
          <n-list-item v-for="student in classStudents" :key="student.id">
            <template #prefix>
              <n-avatar
                round
                :size="40"
                :style="{ background: student.gender === '男' ? '#7C3AED' : '#EC4899' }"
              >
                {{ student.name.charAt(0) }}
              </n-avatar>
            </template>
            <n-thing>
              <template #header>
                {{ student.name }}
                <n-tag
                  :type="student.gender === '男' ? 'info' : 'error'"
                  size="small"
                  :bordered="false"
                  style="margin-left: 8px"
                >
                  {{ student.gender }}
                </n-tag>
              </template>
              <template #description>
                <n-space :size="8">
                  <span>年龄：{{ student.age }}岁</span>
                  <n-divider vertical />
                  <span>家长：{{ student.parentName }}</span>
                  <n-divider vertical />
                  <span>电话：{{ student.phone }}</span>
                </n-space>
              </template>
            </n-thing>
          </n-list-item>
        </n-list>
      </n-spin>
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, h, onMounted } from 'vue'
import { useMessage, useDialog, NButton, NTag, NSpace, NProgress, NAvatar, NThing, NDivider } from 'naive-ui'
import { AddOutline, SearchOutline } from '@vicons/ionicons5'
import dayjs from 'dayjs'
import { getClasses, createClass, updateClass, deleteClass, getClassStudents } from '@/api/class'
import { getTeachers } from '@/api/teacher'

const message = useMessage()
const dialog = useDialog()

const loading = ref(false)
const searchQuery = ref('')
const filterCategory = ref(null)
const showModal = ref(false)
const showStudentModal = ref(false)
const loadingStudents = ref(false)
const selectedClass = ref<any>(null)
const classStudents = ref<any[]>([])
const modalTitle = ref('添加班级')
const formRef = ref()

const formData = ref({
  id: null,
  name: '',
  category: null,
  courseName: '',
  teacher: null,
  studentCount: null,
  schedule: '',
  startDate: null
})

const rules = {
  name: {
    required: true,
    message: '请输入班级名称',
    trigger: 'blur'
  },
  category: {
    required: true,
    message: '请选择课程类别',
    trigger: 'change'
  },
  teacher: {
    required: true,
    message: '请选择授课教师',
    trigger: 'change'
  }
}

// 类别选项
const categoryOptions = [
  { label: '音乐', value: '音乐' },
  { label: '舞蹈', value: '舞蹈' },
  { label: '美术', value: '美术' }
]

const teacherOptions = ref<any[]>([])

const classList = ref<any[]>([])

onMounted(async () => {
  await loadTeachers()
  await loadData()
})

async function loadTeachers() {
  try {
    const res = await getTeachers({ size: 100 }) as any
    const list: any[] = res.records ?? res.list ?? res
    teacherOptions.value = list.map((t: any) => ({
      label: t.name,
      value: t.name
    }))
  } catch (err: any) {
    message.error(err.message || '加载教师列表失败')
  }
}

// 表格列定义
const columns = [
  {
    title: '班级名称',
    key: 'name',
    width: 150
  },
  {
    title: '类别',
    key: 'category',
    width: 100,
    render(row: any) {
      const colorMap: Record<string, string> = {
        '音乐': '#8B5CF6',
        '舞蹈': '#EC4899',
        '美术': '#F59E0B'
      }
      return h(
        NTag,
        {
          type: 'info',
          bordered: false,
          style: {
            backgroundColor: `${colorMap[row.category]}20`,
            color: colorMap[row.category]
          }
        },
        { default: () => row.category }
      )
    }
  },
  {
    title: '课程名称',
    key: 'courseName',
    width: 150
  },
  {
    title: '授课教师',
    key: 'teacher',
    width: 120
  },
  {
    title: '班级人数',
    key: 'studentCount',
    width: 150,
    render(row: any) {
      const percentage = (row.currentCount / row.studentCount) * 100
      return h(
        'div',
        { style: 'display: flex; align-items: center; gap: 8px' },
        [
          h('span', `${row.currentCount}/${row.studentCount}`),
          h(NProgress, {
            type: 'line',
            percentage: percentage,
            showIndicator: false,
            height: 6,
            color: percentage >= 80 ? '#10B981' : '#F59E0B',
            style: 'flex: 1'
          })
        ]
      )
    }
  },
  {
    title: '上课时间',
    key: 'schedule',
    width: 180
  },
  {
    title: '开班日期',
    key: 'startDate',
    width: 120
  },
  {
    title: '操作',
    key: 'actions',
    width: 150,
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
                onClick: () => handleViewStudents(row)
              },
              { default: () => '学生名单' }
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
  let data = classList.value

  if (searchQuery.value) {
    data = data.filter(item =>
      item.name.toLowerCase().includes(searchQuery.value.toLowerCase())
    )
  }

  if (filterCategory.value) {
    data = data.filter(item => item.category === filterCategory.value)
  }

  return data
})

async function loadData() {
  loading.value = true
  try {
    const res = await getClasses({ size: 100 }) as any
    const list: any[] = res.records ?? res.list ?? res
    classList.value = list.map((item: any) => ({
      id: item.id,
      name: item.name,
      category: item.category,
      courseName: item.courseName,
      teacher: item.teacherName || item.teacher,
      studentCount: item.capacity || item.studentCount,
      currentCount: item.currentCount || 0,
      schedule: item.schedule,
      startDate: item.startDate
    }))
  } catch (err: any) {
    message.error(err.message || '加载班级列表失败')
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  // 搜索逻辑已在computed中实现
}

function handleReset() {
  searchQuery.value = ''
  filterCategory.value = null
}

function handleAdd() {
  modalTitle.value = '添加班级'
  formData.value = {
    id: null,
    name: '',
    category: null,
    courseName: '',
    teacher: null,
    studentCount: null,
    schedule: '',
    startDate: null
  }
  showModal.value = true
}

function handleEdit(row: any) {
  modalTitle.value = '编辑班级'
  formData.value = {
    ...row,
    startDate: new Date(row.startDate).getTime()
  }
  showModal.value = true
}

async function handleViewStudents(row: any) {
  selectedClass.value = row
  showStudentModal.value = true
  loadingStudents.value = true
  classStudents.value = []

  try {
    const data = await getClassStudents(row.id) as any[]
    classStudents.value = data
  } catch (err: any) {
    message.error(err.message || '加载学生名单失败')
  } finally {
    loadingStudents.value = false
  }
}

function handleDelete(row: any) {
  dialog.warning({
    title: '确认删除',
    content: `确定要删除班级 ${row.name} 吗？`,
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: async () => {
      try {
        await deleteClass(row.id)
        message.success('删除成功')
        await loadData()
      } catch (err: any) {
        message.error(err.message || '删除失败')
      }
    }
  })
}

async function handleSubmit() {
  try {
    await formRef.value?.validate()

    const data = {
      ...formData.value,
      startDate: formData.value.startDate
        ? dayjs(formData.value.startDate).format('YYYY-MM-DD')
        : ''
    }

    if (data.id) {
      await updateClass(data.id, data)
      message.success('编辑成功')
    } else {
      await createClass(data)
      message.success('添加成功')
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
</script>

<style scoped>
.page-container {
  width: 100%;
}
</style>
