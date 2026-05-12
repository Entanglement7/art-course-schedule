<template>
  <div class="page-container">
    <n-card title="学生课表" :bordered="false">
      <n-space :size="16" style="margin-bottom: 16px">
        <n-select
          v-if="isAdmin"
          v-model:value="selectedStudentId"
          placeholder="选择学生"
          style="width: 200px"
          :options="studentOptions"
          @update:value="loadSchedule"
        />
        <n-date-picker
          v-model:value="selectedWeek"
          type="week"
          placeholder="选择周"
          style="width: 200px"
          @update:value="loadSchedule"
        />
      </n-space>

      <!-- 学生信息卡片 -->
      <n-card v-if="studentInfo" style="margin-bottom: 16px" :bordered="false" embedded>
        <n-space align="center" :size="12">
          <n-avatar round :size="48" :style="{ background: studentInfo.gender === '男' ? '#7C3AED' : '#EC4899' }">
            {{ studentInfo.name?.charAt(0) }}
          </n-avatar>
          <div>
            <div style="font-size: 16px; font-weight: 600; margin-bottom: 4px">
              {{ studentInfo.name }}
              <n-tag size="small" :bordered="false">{{ studentInfo.age }}岁</n-tag>
            </div>
            <n-space :size="8">
              <n-tag
                v-for="course in studentInfo.courses"
                :key="course"
                type="info"
                size="small"
                :bordered="false"
              >
                {{ course }}
              </n-tag>
              <n-tag type="success" size="small" :bordered="false">
                本周 {{ scheduleData.length }} 节课
              </n-tag>
            </n-space>
          </div>
        </n-space>
      </n-card>

      <!-- 课表 -->
      <n-spin :show="loading">
        <div class="timetable-container">
          <table class="timetable">
            <thead>
              <tr>
                <th class="time-column">时间</th>
                <th v-for="day in weekDays" :key="day.value">
                  <div>{{ day.label }}</div>
                  <div class="date-text">{{ day.date }}</div>
                </th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="slot in timeSlots" :key="slot.start">
                <td class="time-column">
                  <div class="time-text">{{ slot.start }}-{{ slot.end }}</div>
                </td>
                <td v-for="day in weekDays" :key="day.value" class="course-cell">
                  <div
                    v-for="course in getCourseForCell(day.value, slot.start)"
                    :key="course.id"
                    class="course-card"
                    :class="`course-${course.courseCategory}`"
                    @click="handleCourseClick(course)"
                  >
                    <div class="course-name">{{ course.courseName }}</div>
                    <div class="course-info">{{ course.teacherName }}</div>
                    <div class="course-info">{{ course.classroomName }}</div>
                    <div class="course-info">{{ course.startTime?.slice(0,5) }}-{{ course.endTime?.slice(0,5) }}</div>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </n-spin>
    </n-card>

    <!-- 课程详情 -->
    <n-modal v-model:show="showDetailModal" preset="card" title="课程详情" style="width: 500px" :bordered="false">
      <n-descriptions v-if="selectedCourse" :column="1" bordered>
        <n-descriptions-item label="课程名称">{{ selectedCourse.courseName }}</n-descriptions-item>
        <n-descriptions-item label="授课教师">{{ selectedCourse.teacherName }}</n-descriptions-item>
        <n-descriptions-item label="班级">{{ selectedCourse.className }}</n-descriptions-item>
        <n-descriptions-item label="教室">{{ selectedCourse.classroomName }}</n-descriptions-item>
        <n-descriptions-item label="上课时间">
          {{ selectedCourse.startTime?.slice(0,5) }} - {{ selectedCourse.endTime?.slice(0,5) }}
        </n-descriptions-item>
        <n-descriptions-item label="课程类别">
          <n-tag :type="getCategoryType(selectedCourse.courseCategory)" :bordered="false">
            {{ selectedCourse.courseCategory }}
          </n-tag>
        </n-descriptions-item>
      </n-descriptions>
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useMessage } from 'naive-ui'
import { useRoute } from 'vue-router'
import dayjs from 'dayjs'
import weekOfYear from 'dayjs/plugin/weekOfYear'
import { useUserStore } from '@/stores/user'
import { getStudentSchedule } from '@/api/schedule'
import { getStudents, getStudent } from '@/api/student'

dayjs.extend(weekOfYear)

const message = useMessage()
const userStore = useUserStore()
const route = useRoute()

const isAdmin = computed(() => userStore.userInfo.role === 'admin')
const selectedStudentId = ref<number | null>(null)
const selectedWeek = ref(Date.now())
const studentInfo = ref<any>(null)
const studentOptions = ref<any[]>([])
const scheduleData = ref<any[]>([])
const loading = ref(false)
const showDetailModal = ref(false)
const selectedCourse = ref<any>(null)

const weekDays = computed(() => {
  const mon = dayjs(selectedWeek.value).startOf('week').add(1, 'day')
  return Array.from({ length: 7 }, (_, i) => ({
    label: ['周一','周二','周三','周四','周五','周六','周日'][i],
    value: i + 1,
    date: mon.add(i, 'day').format('MM-DD'),
  }))
})

const timeSlots = [
  { start: '08:00', end: '09:00' },
  { start: '09:00', end: '10:00' },
  { start: '10:00', end: '11:00' },
  { start: '11:00', end: '12:00' },
  { start: '14:00', end: '15:00' },
  { start: '15:00', end: '16:00' },
  { start: '16:00', end: '17:00' },
  { start: '17:00', end: '18:00' },
  { start: '19:00', end: '20:00' },
  { start: '20:00', end: '21:00' },
]

onMounted(async () => {
  const queryId = route.query.id ? Number(route.query.id) : null

  if (isAdmin.value) {
    try {
      const res = await getStudents({ size: 100 }) as any
      const list: any[] = res.records ?? res.list ?? res
      studentOptions.value = list.map((s: any) => ({ label: s.name, value: s.id }))
      const initId = queryId ?? (list.length ? list[0].id : null)
      if (initId) {
        selectedStudentId.value = initId
        await loadStudentInfo(initId)
      }
    } catch { /* ignore */ }
  } else {
    const sid = userStore.userInfo.studentId
    if (sid) {
      selectedStudentId.value = sid
      await loadStudentInfo(sid)
    } else {
      message.warning('账号未关联学生信息，请联系管理员')
    }
  }
  await loadSchedule()
})

async function loadStudentInfo(studentId: number) {
  try {
    studentInfo.value = await getStudent(studentId)
  } catch { /* ignore */ }
}

async function loadSchedule() {
  if (!selectedStudentId.value) return
  loading.value = true
  try {
    const monday = dayjs(selectedWeek.value).startOf('week').add(1, 'day')
    const dateStr = monday.format('YYYY-MM-DD')
    const data = await getStudentSchedule(selectedStudentId.value, dateStr) as any[]
    scheduleData.value = data

    if (isAdmin.value) {
      await loadStudentInfo(selectedStudentId.value)
    }
  } catch (err: any) {
    message.error(err.message || '加载失败')
  } finally {
    loading.value = false
  }
}

function getCourseForCell(dayOfWeek: number, slotStart: string) {
  return scheduleData.value.filter(s => {
    return s.dayOfWeek === dayOfWeek && s.startTime?.slice(0, 5) === slotStart
  })
}

function handleCourseClick(course: any) {
  selectedCourse.value = course
  showDetailModal.value = true
}

function getCategoryType(category: string) {
  return { '音乐': 'info', '舞蹈': 'error', '美术': 'warning' }[category] ?? 'default'
}
</script>

<style scoped>
.page-container { width: 100%; }
.timetable-container { overflow-x: auto; }
.timetable { width: 100%; border-collapse: collapse; min-width: 900px; }
.timetable th, .timetable td { border: 1px solid #e5e7eb; padding: 8px; text-align: center; }
.timetable th { background: #7C3AED; color: white; font-weight: 600; padding: 12px 8px; }
.time-column { width: 100px; background: #f9fafb; font-weight: 500; }
.date-text { font-size: 12px; margin-top: 4px; opacity: 0.9; }
.time-text { font-size: 13px; color: #6b7280; }
.course-cell { vertical-align: top; padding: 4px; min-height: 60px; }
.course-card { padding: 8px; border-radius: 6px; cursor: pointer; transition: all 0.2s; margin-bottom: 4px; }
.course-card:hover { transform: translateY(-2px); box-shadow: 0 4px 12px rgba(0,0,0,.15); }
.course-音乐 { background: rgba(139,92,246,.2); border-left: 3px solid #8B5CF6; }
.course-舞蹈 { background: rgba(236,72,153,.2); border-left: 3px solid #EC4899; }
.course-美术 { background: rgba(245,158,11,.2); border-left: 3px solid #F59E0B; }
.course-name { font-weight: 600; font-size: 14px; margin-bottom: 4px; color: #1f2937; }
.course-info { font-size: 12px; color: #6b7280; margin-top: 2px; }
</style>
