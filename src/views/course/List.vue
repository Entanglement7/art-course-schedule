<template>
  <div class="page-container">
    <n-card title="课程管理" :bordered="false">
      <template #header-extra>
        <n-button type="primary" @click="handleAdd">
          <template #icon>
            <n-icon>
              <add-outline />
            </n-icon>
          </template>
          添加课程
        </n-button>
      </template>

      <!-- 搜索栏 -->
      <n-space :size="16" style="margin-bottom: 16px">
        <n-input
          v-model:value="searchQuery"
          placeholder="搜索课程名称"
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

        <n-select
          v-model:value="filterType"
          placeholder="类型筛选"
          clearable
          style="width: 150px"
          :options="typeOptions"
        />

        <n-button @click="handleSearch">搜索</n-button>
        <n-button @click="handleReset">重置</n-button>
      </n-space>

      <!-- 课程列表 -->
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
        label-width="100"
      >
        <n-form-item label="课程名称" path="name">
          <n-input v-model:value="formData.name" placeholder="请输入课程名称" />
        </n-form-item>

        <n-form-item label="课程类别" path="category">
          <n-select
            v-model:value="formData.category"
            placeholder="请选择课程类别"
            :options="categoryOptions"
          />
        </n-form-item>

        <n-form-item label="课程类型" path="type">
          <n-radio-group v-model:value="formData.type">
            <n-space>
              <n-radio value="小组课">小组课</n-radio>
              <n-radio value="一对一">一对一</n-radio>
            </n-space>
          </n-radio-group>
        </n-form-item>

        <n-form-item label="课程时长" path="duration">
          <n-select
            v-model:value="formData.duration"
            placeholder="请选择课程时长"
            :options="durationOptions"
          />
        </n-form-item>

        <n-form-item label="适合年龄" path="ageRange">
          <n-input v-model:value="formData.ageRange" placeholder="例如：6-12岁" />
        </n-form-item>

        <n-form-item label="课程费用" path="price">
          <n-input-number v-model:value="formData.price" :min="0" placeholder="请输入课程费用" style="width: 100%">
            <template #suffix>
              元/节
            </template>
          </n-input-number>
        </n-form-item>

        <n-form-item label="课程简介" path="description">
          <n-input
            v-model:value="formData.description"
            type="textarea"
            :rows="4"
            placeholder="请输入课程简介"
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
import { useMessage, useDialog, NButton, NTag, NSpace } from 'naive-ui'
import { AddOutline, SearchOutline } from '@vicons/ionicons5'

const message = useMessage()
const dialog = useDialog()

const loading = ref(false)
const searchQuery = ref('')
const filterCategory = ref(null)
const filterType = ref(null)
const showModal = ref(false)
const modalTitle = ref('添加课程')
const formRef = ref()

const formData = ref({
  id: null,
  name: '',
  category: null,
  type: '小组课',
  duration: null,
  ageRange: '',
  price: null,
  description: ''
})

const rules = {
  name: {
    required: true,
    message: '请输入课程名称',
    trigger: 'blur'
  },
  category: {
    required: true,
    message: '请选择课程类别',
    trigger: 'change'
  },
  duration: {
    required: true,
    message: '请选择课程时长',
    trigger: 'change'
  }
}

// 类别选项
const categoryOptions = [
  { label: '音乐', value: '音乐' },
  { label: '舞蹈', value: '舞蹈' },
  { label: '美术', value: '美术' }
]

// 类型选项
const typeOptions = [
  { label: '小组课', value: '小组课' },
  { label: '一对一', value: '一对一' }
]

// 时长选项
const durationOptions = [
  { label: '45分钟', value: 45 },
  { label: '60分钟', value: 60 },
  { label: '90分钟', value: 90 },
  { label: '120分钟', value: 120 }
]

// Mock数据
const courseList = ref<any[]>([])

// 表格列定义
const columns = [
  {
    title: '课程名称',
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
    title: '类型',
    key: 'type',
    width: 100,
    render(row: any) {
      return h(
        NTag,
        {
          type: row.type === '一对一' ? 'warning' : 'success',
          bordered: false
        },
        { default: () => row.type }
      )
    }
  },
  {
    title: '时长',
    key: 'duration',
    width: 100,
    render(row: any) {
      return `${row.duration}分钟`
    }
  },
  {
    title: '适合年龄',
    key: 'ageRange',
    width: 120
  },
  {
    title: '费用',
    key: 'price',
    width: 120,
    render(row: any) {
      return h(
        'span',
        { style: 'color: #F59E0B; font-weight: 600' },
        `¥${row.price}/节`
      )
    }
  },
  {
    title: '课程简介',
    key: 'description',
    ellipsis: {
      tooltip: true
    }
  },
  {
    title: '操作',
    key: 'actions',
    width: 150,
    fixed: 'right',
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
  let data = courseList.value

  if (searchQuery.value) {
    data = data.filter(item =>
      item.name.toLowerCase().includes(searchQuery.value.toLowerCase())
    )
  }

  if (filterCategory.value) {
    data = data.filter(item => item.category === filterCategory.value)
  }

  if (filterType.value) {
    data = data.filter(item => item.type === filterType.value)
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
    courseList.value = [
      {
        id: 1,
        name: '钢琴基础',
        category: '音乐',
        type: '小组课',
        duration: 60,
        ageRange: '6-12岁',
        price: 200,
        description: '适合零基础学员，学习基本指法、音阶、简单曲目演奏'
      },
      {
        id: 2,
        name: '钢琴进阶',
        category: '音乐',
        type: '小组课',
        duration: 60,
        ageRange: '8-15岁',
        price: 250,
        description: '适合有一定基础的学员，学习复杂曲目、演奏技巧提升'
      },
      {
        id: 3,
        name: '钢琴一对一',
        category: '音乐',
        type: '一对一',
        duration: 60,
        ageRange: '不限',
        price: 400,
        description: '个性化教学，根据学员水平定制课程内容'
      },
      {
        id: 4,
        name: '声乐基础',
        category: '音乐',
        type: '小组课',
        duration: 60,
        ageRange: '8-16岁',
        price: 180,
        description: '学习发声技巧、气息控制、简单歌曲演唱'
      },
      {
        id: 5,
        name: '芭蕾舞初级',
        category: '舞蹈',
        type: '小组课',
        duration: 90,
        ageRange: '5-10岁',
        price: 220,
        description: '学习芭蕾基本功、形体训练、简单舞蹈组合'
      },
      {
        id: 6,
        name: '芭蕾舞中级',
        category: '舞蹈',
        type: '小组课',
        duration: 90,
        ageRange: '8-14岁',
        price: 260,
        description: '提升芭蕾技巧、学习经典芭蕾舞剧片段'
      },
      {
        id: 7,
        name: '中国舞初级',
        category: '舞蹈',
        type: '小组课',
        duration: 90,
        ageRange: '6-12岁',
        price: 200,
        description: '学习中国舞基本功、身韵、民族民间舞'
      },
      {
        id: 8,
        name: '街舞入门',
        category: '舞蹈',
        type: '小组课',
        duration: 60,
        ageRange: '8-16岁',
        price: 180,
        description: '学习街舞基础动作、节奏感训练、简单舞蹈编排'
      },
      {
        id: 9,
        name: '素描基础',
        category: '美术',
        type: '小组课',
        duration: 120,
        ageRange: '8-16岁',
        price: 200,
        description: '学习素描基础知识、几何体、静物写生'
      },
      {
        id: 10,
        name: '素描提高',
        category: '美术',
        type: '小组课',
        duration: 120,
        ageRange: '10-18岁',
        price: 240,
        description: '深入学习素描技法、人物头像、石膏像写生'
      },
      {
        id: 11,
        name: '水彩入门',
        category: '美术',
        type: '小组课',
        duration: 90,
        ageRange: '7-14岁',
        price: 180,
        description: '学习水彩基础技法、色彩搭配、简单风景创作'
      },
      {
        id: 12,
        name: '国画基础',
        category: '美术',
        type: '小组课',
        duration: 90,
        ageRange: '8-16岁',
        price: 200,
        description: '学习国画基础、笔墨技法、花鸟鱼虫创作'
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
  filterCategory.value = null
  filterType.value = null
}

function handleAdd() {
  modalTitle.value = '添加课程'
  formData.value = {
    id: null,
    name: '',
    category: null,
    type: '小组课',
    duration: null,
    ageRange: '',
    price: null,
    description: ''
  }
  showModal.value = true
}

function handleEdit(row: any) {
  modalTitle.value = '编辑课程'
  formData.value = { ...row }
  showModal.value = true
}

function handleDelete(row: any) {
  dialog.warning({
    title: '确认删除',
    content: `确定要删除课程 ${row.name} 吗？`,
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: () => {
      const index = courseList.value.findIndex(item => item.id === row.id)
      if (index > -1) {
        courseList.value.splice(index, 1)
        message.success('删除成功')
      }
    }
  })
}

async function handleSubmit() {
  try {
    await formRef.value?.validate()

    const data = { ...formData.value }

    if (data.id) {
      // 编辑
      const index = courseList.value.findIndex(item => item.id === data.id)
      if (index > -1) {
        courseList.value[index] = data
        message.success('编辑成功')
      }
    } else {
      // 新增
      data.id = Date.now()
      courseList.value.unshift(data)
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
