<template>
  <div class="page-container">
    <n-card title="手动排课" :bordered="false">
      <n-steps :current="currentStep" style="margin-bottom: 24px">
        <n-step title="选择班级" />
        <n-step title="选择时间" />
        <n-step title="选择教室" />
        <n-step title="确认排课" />
      </n-steps>

      <!-- 步骤1: 选择班级 -->
      <div v-if="currentStep === 1">
        <n-space vertical :size="16">
          <n-alert type="info" :bordered="false">
            请选择需要排课的班级
          </n-alert>

          <n-space :size="16">
            <n-select
              v-model:value="filterCategory"
              placeholder="类别筛选"
              clearable
              style="width: 150px"
              :options="categoryOptions"
            />
            <n-input
              v-model:value="searchQuery"
              placeholder="搜索班级名称"
              clearable
              style="width: 200px"
            />
          </n-space>

          <n-data-table
            :columns="classColumns"
            :data="filteredClasses"
            :pagination="{ pageSize: 5 }"
            :bordered="false"
          />

          <n-space justify="end">
            <n-button type="primary" :disabled="!formData.classId" @click="nextStep">
              下一步
            </n-button>
          </n-space>
        </n-space>
      </div>

      <!-- 步骤2: 选择时间 -->
      <div v-if="currentStep === 2">
        <n-space vertical :size="16">
          <n-alert type="info" :bordered="false">
            已选择班级：{{ selectedClass?.name }}
          </n-alert>

          <n-form :model="formData" label-placement="left" label-width="100">
            <n-form-item label="星期">
              <n-select
                v-model:value="formData.dayOfWeek"
                placeholder="请选择星期"
                :options="dayOptions"
                style="width: 200px"
              />
            </n-form-item>

            <n-form-item label="开始时间">
              <n-time-picker
                v-model:value="formData.startTime"
                format="HH:mm"
                placeholder="请选择开始时间"
                style="width: 200px"
                @update:value="handleStartTimeChange"
              />
            </n-form-item>

            <n-form-item label="结束时间">
              <n-time-picker
                :value="formData.endTime"
                format="HH:mm"
                placeholder="根据课程时长自动计算"
                style="width: 200px"
                :disabled="true"
              />
            </n-form-item>

            <n-form-item label="重复周期">
              <n-radio-group v-model:value="formData.repeatType">
                <n-space>
                  <n-radio value="once">单次</n-radio>
                  <n-radio value="weekly">每周</n-radio>
                </n-space>
              </n-radio-group>
            </n-form-item>

            <n-form-item v-if="formData.repeatType === 'weekly'" label="持续周数">
              <n-input-number
                v-model:value="formData.weekCount"
                :min="1"
                :max="52"
                placeholder="请输入周数"
                style="width: 200px"
              />
            </n-form-item>
          </n-form>

          <n-space justify="end">
            <n-button @click="prevStep">上一步</n-button>
            <n-button
              type="primary"
              :disabled="!formData.dayOfWeek || !formData.startTime"
              @click="checkTimeConflict"
            >
              下一步
            </n-button>
          </n-space>
        </n-space>
      </div>

      <!-- 步骤3: 选择教室 -->
      <div v-if="currentStep === 3">
        <n-space vertical :size="16">
          <n-alert type="info" :bordered="false">
            已选择时间：{{ getTimeDisplay() }}
          </n-alert>

          <n-alert v-if="availableClassrooms.length === 0" type="warning" :bordered="false">
            该时间段没有可用教室，请返回修改时间
          </n-alert>

          <n-data-table
            v-if="availableClassrooms.length > 0"
            :columns="classroomColumns"
            :data="availableClassrooms"
            :pagination="{ pageSize: 5 }"
            :bordered="false"
          />

          <n-space justify="end">
            <n-button @click="prevStep">上一步</n-button>
            <n-button
              type="primary"
              :disabled="!formData.classroomId"
              @click="nextStep"
            >
              下一步
            </n-button>
          </n-space>
        </n-space>
      </div>

      <!-- 步骤4: 确认排课 -->
      <div v-if="currentStep === 4">
        <n-space vertical :size="16">
          <n-alert type="success" :bordered="false">
            请确认排课信息
          </n-alert>

          <n-descriptions :column="1" bordered>
            <n-descriptions-item label="班级">
              {{ selectedClass?.name }}
            </n-descriptions-item>
            <n-descriptions-item label="课程">
              {{ selectedClass?.courseName }}
            </n-descriptions-item>
            <n-descriptions-item label="授课教师">
              {{ selectedClass?.teacher }}
            </n-descriptions-item>
            <n-descriptions-item label="上课时间">
              {{ getTimeDisplay() }}
            </n-descriptions-item>
            <n-descriptions-item label="教室">
              {{ selectedClassroom?.name }}
            </n-descriptions-item>
            <n-descriptions-item label="重复">
              {{ formData.repeatType === 'once' ? '单次' : `每周，持续${formData.weekCount}周` }}
            </n-descriptions-item>
          </n-descriptions>

          <n-space justify="end">
            <n-button @click="prevStep">上一步</n-button>
            <n-button type="primary" @click="handleSubmit">
              确认排课
            </n-button>
          </n-space>
        </n-space>
      </div>
    </n-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, h, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useMessage, NButton, NTag, NRadio } from 'naive-ui'
import dayjs from 'dayjs'
import { getClasses } from '@/api/class'
import { getClassrooms } from '@/api/classroom'
import { createManualSchedule } from '@/api/schedule'

const router = useRouter()
const message = useMessage()

const currentStep = ref(1)
const searchQuery = ref('')
const filterCategory = ref(null)
const loading = ref(false)

const formData = ref({
  classId: null,
  dayOfWeek: null,
  startTime: null,
  endTime: null,
  repeatType: 'once',
  weekCount: 12,
  classroomId: null
})

const categoryOptions = [
  { label: '音乐', value: '音乐' },
  { label: '舞蹈', value: '舞蹈' },
  { label: '美术', value: '美术' }
]

const dayOptions = [
  { label: '周一', value: 1 },
  { label: '周二', value: 2 },
  { label: '周三', value: 3 },
  { label: '周四', value: 4 },
  { label: '周五', value: 5 },
  { label: '周六', value: 6 },
  { label: '周日', value: 7 }
]

const classList = ref<any[]>([])
const classroomList = ref<any[]>([])

onMounted(async () => {
  await loadClasses()
  await loadClassrooms()
})

async function loadClasses() {
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
      courseDuration: item.courseDuration || 60
    }))
  } catch (err: any) {
    message.error(err.message || '加载班级列表失败')
  } finally {
    loading.value = false
  }
}

async function loadClassrooms() {
  try {
    const res = await getClassrooms({ size: 100 }) as any
    const list: any[] = res.records ?? res.list ?? res
    classroomList.value = list.map((item: any) => ({
      id: item.id,
      name: item.name,
      type: item.type,
      capacity: item.capacity,
      floor: item.floor
    }))
  } catch (err: any) {
    message.error(err.message || '加载教室列表失败')
  }
}

const filteredClasses = computed(() => {
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

const selectedClass = computed(() => {
  return classList.value.find(c => c.id === formData.value.classId)
})

const selectedClassroom = computed(() => {
  return classroomList.value.find(c => c.id === formData.value.classroomId)
})

const availableClassrooms = computed(() => {
  if (!selectedClass.value) return []

  // 根据课程类别筛选合适的教室
  const categoryMap: Record<string, string[]> = {
    '音乐': ['音乐教室', '多功能教室'],
    '舞蹈': ['舞蹈教室', '多功能教室'],
    '美术': ['美术教室', '多功能教室']
  }

  const suitableTypes = categoryMap[selectedClass.value.category] || []

  return classroomList.value.filter(room =>
    suitableTypes.includes(room.type) && room.capacity >= selectedClass.value.currentCount
  )
})

const classColumns = [
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
    title: '课程',
    key: 'courseName',
    width: 120
  },
  {
    title: '教师',
    key: 'teacher',
    width: 100
  },
  {
    title: '人数',
    key: 'studentCount',
    width: 100,
    render(row: any) {
      return `${row.currentCount}/${row.studentCount}`
    }
  },
  {
    title: '操作',
    key: 'actions',
    width: 100,
    render(row: any) {
      return h(
        NButton,
        {
          size: 'small',
          type: formData.value.classId === row.id ? 'success' : 'primary',
          onClick: () => {
            formData.value.classId = row.id
          }
        },
        { default: () => formData.value.classId === row.id ? '已选择' : '选择' }
      )
    }
  }
]

const classroomColumns = [
  {
    title: '教室名称',
    key: 'name',
    width: 150
  },
  {
    title: '类型',
    key: 'type',
    width: 120
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
    title: '操作',
    key: 'actions',
    width: 100,
    render(row: any) {
      return h(
        NButton,
        {
          size: 'small',
          type: formData.value.classroomId === row.id ? 'success' : 'primary',
          onClick: () => {
            formData.value.classroomId = row.id
          }
        },
        { default: () => formData.value.classroomId === row.id ? '已选择' : '选择' }
      )
    }
  }
]

function handleStartTimeChange(val: number | null) {
  if (val == null) {
    formData.value.endTime = null
    return
  }
  const duration = selectedClass.value?.courseDuration ?? 60
  formData.value.endTime = val + duration * 60 * 1000
}

function nextStep() {
  currentStep.value++
}

function prevStep() {
  currentStep.value--
}

function checkTimeConflict() {
  // 直接进入下一步，冲突检测由后端在提交时处理
  nextStep()
}

function getTimeDisplay() {
  if (!formData.value.dayOfWeek || !formData.value.startTime || !formData.value.endTime) {
    return ''
  }

  const dayLabel = dayOptions.find(d => d.value === formData.value.dayOfWeek)?.label
  const startTime = dayjs(formData.value.startTime).format('HH:mm')
  const endTime = dayjs(formData.value.endTime).format('HH:mm')

  return `${dayLabel} ${startTime}-${endTime}`
}

async function handleSubmit() {
  try {
    const startTime = dayjs(formData.value.startTime).format('HH:mm') + ':00'
    const endTime = dayjs(formData.value.endTime).format('HH:mm') + ':00'

    // 开始日期从今天算起，后端会从该日期找下一个（含今天）匹配的星期
    const startDate = dayjs().format('YYYY-MM-DD')

    const data = {
      classId: formData.value.classId,
      dayOfWeek: formData.value.dayOfWeek,
      startTime: startTime,
      endTime: endTime,
      startDate: startDate,
      classroomId: formData.value.classroomId,
      repeatType: formData.value.repeatType,
      weekCount: formData.value.repeatType === 'weekly' ? formData.value.weekCount : 1
    }

    await createManualSchedule(data)
    message.success('排课成功！')

    // 重置表单
    currentStep.value = 1
    formData.value = {
      classId: null,
      dayOfWeek: null,
      startTime: null,
      endTime: null,
      repeatType: 'once',
      weekCount: 12,
      classroomId: null
    }
  } catch (err: any) {
    message.error(err.message || '排课失败')
  }
}
</script>

<style scoped>
.page-container {
  width: 100%;
}
</style>
