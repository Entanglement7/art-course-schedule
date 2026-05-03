<template>
  <div class="page-container">
    <n-card title="教室管理" :bordered="false">
      <template #header-extra>
        <n-button type="primary" @click="handleAdd">
          <template #icon>
            <n-icon>
              <add-outline />
            </n-icon>
          </template>
          添加教室
        </n-button>
      </template>

      <!-- 搜索栏 -->
      <n-space :size="16" style="margin-bottom: 16px">
        <n-input
          v-model:value="searchQuery"
          placeholder="搜索教室名称"
          clearable
          style="width: 200px"
        >
          <template #prefix>
            <n-icon :component="SearchOutline" />
          </template>
        </n-input>

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

      <!-- 教室列表 -->
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
        <n-form-item label="教室名称" path="name">
          <n-input v-model:value="formData.name" placeholder="请输入教室名称" />
        </n-form-item>

        <n-form-item label="教室类型" path="type">
          <n-select
            v-model:value="formData.type"
            placeholder="请选择教室类型"
            :options="typeOptions"
          />
        </n-form-item>

        <n-form-item label="容纳人数" path="capacity">
          <n-input-number v-model:value="formData.capacity" :min="1" :max="50" placeholder="请输入容纳人数" style="width: 100%" />
        </n-form-item>

        <n-form-item label="楼层" path="floor">
          <n-input v-model:value="formData.floor" placeholder="例如：3楼" />
        </n-form-item>

        <n-form-item label="设备配置" path="equipment">
          <n-select
            v-model:value="formData.equipment"
            placeholder="请选择设备配置"
            multiple
            :options="equipmentOptions"
          />
        </n-form-item>

        <n-form-item label="备注" path="remark">
          <n-input
            v-model:value="formData.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
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
import { useMessage, useDialog, NButton, NTag, NSpace } from 'naive-ui'
import { AddOutline, SearchOutline } from '@vicons/ionicons5'

const router = useRouter()
const message = useMessage()
const dialog = useDialog()

const loading = ref(false)
const searchQuery = ref('')
const filterType = ref(null)
const showModal = ref(false)
const modalTitle = ref('添加教室')
const formRef = ref()

const formData = ref({
  id: null,
  name: '',
  type: null,
  capacity: null,
  floor: '',
  equipment: [],
  remark: ''
})

const rules = {
  name: {
    required: true,
    message: '请输入教室名称',
    trigger: 'blur'
  },
  type: {
    required: true,
    message: '请选择教室类型',
    trigger: 'change'
  },
  capacity: {
    required: true,
    message: '请输入容纳人数',
    trigger: 'blur'
  }
}

// 类型选项
const typeOptions = [
  { label: '音乐教室', value: '音乐教室' },
  { label: '舞蹈教室', value: '舞蹈教室' },
  { label: '美术教室', value: '美术教室' },
  { label: '多功能教室', value: '多功能教室' }
]

// 设备选项
const equipmentOptions = [
  { label: '钢琴', value: '钢琴' },
  { label: '电子琴', value: '电子琴' },
  { label: '音响设备', value: '音响设备' },
  { label: '投影仪', value: '投影仪' },
  { label: '镜子墙', value: '镜子墙' },
  { label: '把杆', value: '把杆' },
  { label: '画架', value: '画架' },
  { label: '画板', value: '画板' },
  { label: '空调', value: '空调' },
  { label: '饮水机', value: '饮水机' }
]

// Mock数据
const classroomList = ref<any[]>([])

// 表格列定义
const columns = [
  {
    title: '教室名称',
    key: 'name',
    width: 150
  },
  {
    title: '类型',
    key: 'type',
    width: 120,
    render(row: any) {
      const colorMap: Record<string, string> = {
        '音乐教室': '#8B5CF6',
        '舞蹈教室': '#EC4899',
        '美术教室': '#F59E0B',
        '多功能教室': '#10B981'
      }
      return h(
        NTag,
        {
          type: 'info',
          bordered: false,
          style: {
            backgroundColor: `${colorMap[row.type]}20`,
            color: colorMap[row.type]
          }
        },
        { default: () => row.type }
      )
    }
  },
  {
    title: '容纳人数',
    key: 'capacity',
    width: 100,
    render(row: any) {
      return `${row.capacity}人`
    }
  },
  {
    title: '楼层',
    key: 'floor',
    width: 100
  },
  {
    title: '设备配置',
    key: 'equipment',
    render(row: any) {
      return h(
        NSpace,
        { size: 4 },
        {
          default: () =>
            row.equipment.slice(0, 3).map((item: string) =>
              h(
                NTag,
                { size: 'small', type: 'success', bordered: false },
                { default: () => item }
              )
            ).concat(
              row.equipment.length > 3
                ? [h(NTag, { size: 'small', type: 'success', bordered: false }, { default: () => `+${row.equipment.length - 3}` })]
                : []
            )
        }
      )
    }
  },
  {
    title: '状态',
    key: 'status',
    width: 100,
    render(row: any) {
      return h(
        NTag,
        {
          type: row.status === '空闲' ? 'success' : 'warning',
          bordered: false
        },
        { default: () => row.status }
      )
    }
  },
  {
    title: '操作',
    key: 'actions',
    width: 200,
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
                onClick: () => router.push(`/timetable/classroom?id=${row.id}`)
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
  let data = classroomList.value

  if (searchQuery.value) {
    data = data.filter(item =>
      item.name.toLowerCase().includes(searchQuery.value.toLowerCase())
    )
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
    classroomList.value = [
      {
        id: 1,
        name: '音乐教室1',
        type: '音乐教室',
        capacity: 10,
        floor: '3楼',
        equipment: ['钢琴', '音响设备', '空调', '饮水机'],
        status: '使用中',
        remark: '配备施坦威钢琴'
      },
      {
        id: 2,
        name: '音乐教室2',
        type: '音乐教室',
        capacity: 8,
        floor: '3楼',
        equipment: ['电子琴', '音响设备', '空调'],
        status: '空闲',
        remark: ''
      },
      {
        id: 3,
        name: '音乐教室3',
        type: '音乐教室',
        capacity: 1,
        floor: '3楼',
        equipment: ['钢琴', '空调'],
        status: '使用中',
        remark: '一对一专用教室'
      },
      {
        id: 4,
        name: '舞蹈教室1',
        type: '舞蹈教室',
        capacity: 20,
        floor: '2楼',
        equipment: ['镜子墙', '把杆', '音响设备', '空调', '饮水机'],
        status: '使用中',
        remark: '大型舞蹈教室'
      },
      {
        id: 5,
        name: '舞蹈教室2',
        type: '舞蹈教室',
        capacity: 15,
        floor: '2楼',
        equipment: ['镜子墙', '把杆', '音响设备', '空调'],
        status: '空闲',
        remark: ''
      },
      {
        id: 6,
        name: '美术教室1',
        type: '美术教室',
        capacity: 15,
        floor: '4楼',
        equipment: ['画架', '画板', '投影仪', '空调', '饮水机'],
        status: '使用中',
        remark: '采光良好'
      },
      {
        id: 7,
        name: '美术教室2',
        type: '美术教室',
        capacity: 12,
        floor: '4楼',
        equipment: ['画架', '画板', '空调'],
        status: '空闲',
        remark: ''
      },
      {
        id: 8,
        name: '多功能教室',
        type: '多功能教室',
        capacity: 30,
        floor: '1楼',
        equipment: ['投影仪', '音响设备', '空调', '饮水机'],
        status: '空闲',
        remark: '可用于演出和活动'
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
  filterType.value = null
}

function handleAdd() {
  modalTitle.value = '添加教室'
  formData.value = {
    id: null,
    name: '',
    type: null,
    capacity: null,
    floor: '',
    equipment: [],
    remark: ''
  }
  showModal.value = true
}

function handleEdit(row: any) {
  modalTitle.value = '编辑教室'
  formData.value = { ...row }
  showModal.value = true
}

function handleDelete(row: any) {
  dialog.warning({
    title: '确认删除',
    content: `确定要删除教室 ${row.name} 吗？`,
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: () => {
      const index = classroomList.value.findIndex(item => item.id === row.id)
      if (index > -1) {
        classroomList.value.splice(index, 1)
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
      const index = classroomList.value.findIndex(item => item.id === data.id)
      if (index > -1) {
        classroomList.value[index] = { ...data, status: classroomList.value[index].status }
        message.success('编辑成功')
      }
    } else {
      // 新增
      data.id = Date.now()
      data.status = '空闲'
      classroomList.value.unshift(data)
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
