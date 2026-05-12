<template>
  <div class="page-container">
    <n-card title="智能排课" :bordered="false">
      <n-alert type="info" title="功能说明" style="margin-bottom: 16px">
        选择班级、教师、学生和教室，系统自动找到无冲突的时间槽并生成课表。
      </n-alert>

      <n-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-placement="left"
        label-width="120"
      >
        <n-form-item label="班级" path="classId">
          <n-select
            v-model:value="form.classId"
            :options="classOptions"
            placeholder="请选择班级"
            @update:value="handleClassChange"
          />
        </n-form-item>

        <n-form-item label="教师" path="teacherId">
          <n-select
            v-model:value="form.teacherId"
            :options="teacherOptions"
            placeholder="请选择教师"
          />
        </n-form-item>

        <n-form-item label="学生" path="studentIds">
          <n-select
            v-model:value="form.studentIds"
            :options="studentOptions"
            placeholder="请选择学生"
            multiple
            filterable
          />
        </n-form-item>

        <n-form-item label="教室" path="classroomId">
          <n-select
            v-model:value="form.classroomId"
            :options="classroomOptions"
            placeholder="请选择教室"
          />
        </n-form-item>

        <n-form-item label="排课周期" path="schedulePeriod">
          <n-select
            v-model:value="form.schedulePeriod"
            :options="periodOptions"
            placeholder="请选择排课周期"
            style="width: 200px"
          />
        </n-form-item>

        <n-form-item label="开始日期" path="startDate">
          <n-date-picker
            v-model:value="form.startDate"
            type="date"
            placeholder="请选择开始日期"
          />
        </n-form-item>

        <n-form-item>
          <n-space>
            <n-button @click="handleReset">重置</n-button>
            <n-button type="primary" :loading="loading" @click="handleSchedule">
              开始排课
            </n-button>
          </n-space>
        </n-form-item>
      </n-form>

      <n-divider />

      <n-card title="排课结果" :bordered="false" embedded v-if="scheduleResult">
        <n-alert
          :type="scheduleResult.success ? 'success' : 'error'"
          style="margin-bottom: 16px"
        >
          {{ scheduleResult.message }}
        </n-alert>

        <div v-if="scheduleResult.success && scheduleResult.schedules">
          <n-statistic-group>
            <n-statistic label="生成课程数" :value="scheduleResult.totalSchedules" />
          </n-statistic-group>

          <n-divider />

          <n-list bordered>
            <n-list-item v-for="schedule in scheduleResult.schedules" :key="schedule.id">
              <template #prefix>
                <n-tag
                  :type="schedule.courseCategory === '音乐' ? 'info' :
                         schedule.courseCategory === '舞蹈' ? 'error' : 'warning'"
                  :bordered="false"
                >
                  {{ schedule.courseCategory }}
                </n-tag>
              </template>
              <div>
                <div style="font-weight: 500">{{ schedule.className }}</div>
                <div style="font-size: 12px; color: #9CA3AF; margin-top: 4px">
                  {{ formatDate(schedule.date) }} {{ formatTime(schedule.startTime) }}-{{ formatTime(schedule.endTime) }}
                  | {{ schedule.teacherName }} | {{ schedule.classroomName }}
                </div>
              </div>
            </n-list-item>
          </n-list>

          <n-space style="margin-top: 16px">
            <n-button type="primary" @click="$router.push('/timetable/week')">
              查看课表
            </n-button>
            <n-button @click="handleReset">继续添加</n-button>
          </n-space>
        </div>
      </n-card>
    </n-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useMessage } from 'naive-ui'
import { smartSchedule } from '@/api/schedule'
import { getTeachers } from '@/api/teacher'
import { getStudents } from '@/api/student'
import { getClassrooms } from '@/api/classroom'
import { getClasses } from '@/api/class'

const message = useMessage()
const formRef = ref()
const loading = ref(false)
const scheduleResult = ref<any>(null)

const form = ref({
  classId: null,
  teacherId: null,
  studentIds: [],
  classroomId: null,
  schedulePeriod: 'week',
  startDate: Date.now()
})

const rules = {
  classId: { required: true, message: '请选择班级', trigger: 'change', type: 'number' },
  teacherId: { required: true, message: '请选择教师', trigger: 'change', type: 'number' },
  studentIds: { required: true, message: '请选择学生', trigger: 'change', type: 'array' },
  classroomId: { required: true, message: '请选择教室', trigger: 'change', type: 'number' },
  schedulePeriod: { required: true, message: '请选择排课周期', trigger: 'change' },
  startDate: { required: true, message: '请选择开始日期', trigger: 'change', type: 'number' }
}

const classOptions = ref<any[]>([])
const teacherOptions = ref<any[]>([])
const studentOptions = ref<any[]>([])
const classroomOptions = ref<any[]>([])

const periodOptions = [
  { label: '一周', value: 'week' },
  { label: '两周', value: 'two-weeks' },
  { label: '一个月', value: 'month' }
]

onMounted(() => {
  loadOptions()
})

async function loadOptions() {
  try {
    // 加载班级
    const classesRes = await getClasses({ size: 100 }) as any
    const classes = classesRes.records || classesRes.list || classesRes || []
    classOptions.value = classes.map((c: any) => ({
      label: `${c.name} - ${c.category}`,
      value: c.id,
      teacherId: c.teacherId,
      category: c.category
    }))

    // 加载教师
    const teachersRes = await getTeachers({ size: 100 }) as any
    const teachers = teachersRes.records || teachersRes.list || teachersRes || []
    teacherOptions.value = teachers.map((t: any) => ({
      label: `${t.name} - ${t.specialty}`,
      value: t.id,
      specialty: t.specialty
    }))

    // 加载学生
    const studentsRes = await getStudents({ size: 100 }) as any
    const students = studentsRes.records || studentsRes.list || studentsRes || []
    studentOptions.value = students.map((s: any) => ({
      label: `${s.name} (${s.age}岁)`,
      value: s.id
    }))

    // 加载教室
    const classroomsRes = await getClassrooms({ size: 100 }) as any
    const classrooms = classroomsRes.records || classroomsRes.list || classroomsRes || []
    classroomOptions.value = classrooms.map((c: any) => ({
      label: `${c.name} - ${c.type} (容量${c.capacity}人)`,
      value: c.id,
      type: c.type
    }))
  } catch (error) {
    message.error('加载数据失败')
  }
}

function handleClassChange(classId: number) {
  const selectedClass = classOptions.value.find(c => c.value === classId)
  if (selectedClass?.teacherId) {
    form.value.teacherId = selectedClass.teacherId
  }
}

async function handleSchedule() {
  try {
    await formRef.value?.validate()

    loading.value = true
    scheduleResult.value = null

    const res = await smartSchedule({
      classId: form.value.classId,
      teacherId: form.value.teacherId,
      classroomId: form.value.classroomId,
      studentIds: form.value.studentIds,
      sessionsPerWeek: 1,
      schedulePeriod: form.value.schedulePeriod,
      startDate: formatDateToString(form.value.startDate)
    }) as any

    scheduleResult.value = res

    if (res.success) {
      message.success(res.message)
    } else {
      message.error(res.message)
    }
  } catch (error: any) {
    if (error?.message) {
      // 表单验证错误
      return
    }
    message.error('排课失败，请重试')
    console.error(error)
  } finally {
    loading.value = false
  }
}

function handleReset() {
  scheduleResult.value = null
  form.value = {
    classId: null,
    teacherId: null,
    studentIds: [],
    classroomId: null,
    schedulePeriod: 'week',
    startDate: Date.now()
  }
}

function formatDateToString(timestamp: number): string {
  const date = new Date(timestamp)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

function formatDate(dateStr: string): string {
  const date = new Date(dateStr)
  const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  return `${date.getMonth() + 1}月${date.getDate()}日 ${weekdays[date.getDay()]}`
}

function formatTime(timeStr: string): string {
  return timeStr.substring(0, 5)
}
</script>

<style scoped>
.page-container {
  width: 100%;
}
</style>
