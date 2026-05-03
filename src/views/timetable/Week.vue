<template>
  <div class="page-container">
    <n-card title="周课表" :bordered="false">
      <template #header-extra>
        <n-space>
          <n-date-picker
            v-model:value="currentWeek"
            type="week"
            placeholder="选择周"
            @update:value="handleWeekChange"
          />
          <n-button @click="goToday">今天</n-button>
        </n-space>
      </template>

      <!-- 课表网格 -->
      <div class="timetable-container">
        <div class="timetable-grid">
          <!-- 表头 - 时间列 -->
          <div class="time-header cell-header">时间</div>

          <!-- 表头 - 星期 -->
          <div
            v-for="day in weekDays"
            :key="day.value"
            class="day-header cell-header"
            :class="{ today: isToday(day.value) }"
          >
            <div class="day-name">{{ day.label }}</div>
            <div class="day-date">{{ getDateStr(day.value) }}</div>
          </div>

          <!-- 时间段行 -->
          <template v-for="time in timeSlots" :key="time.id">
            <!-- 时间列 -->
            <div class="time-cell">
              <div class="time-label">{{ time.start }}</div>
              <div class="time-divider">-</div>
              <div class="time-label">{{ time.end }}</div>
            </div>

            <!-- 课程单元格 -->
            <div
              v-for="day in weekDays"
              :key="`${time.id}-${day.value}`"
              class="course-cell"
              @click="handleCellClick(day.value, time)"
            >
              <div
                v-for="course in getCourses(day.value, time)"
                :key="course.id"
                class="course-card"
                :class="getCourseClass(course.category)"
                @click.stop="handleCourseClick(course)"
              >
                <div class="course-name">{{ course.name }}</div>
                <div class="course-info">
                  <n-icon size="14">
                    <person-outline />
                  </n-icon>
                  {{ course.teacher }}
                </div>
                <div class="course-info">
                  <n-icon size="14">
                    <business-outline />
                  </n-icon>
                  {{ course.classroom }}
                </div>
                <div class="course-type">
                  <n-tag size="tiny" :bordered="false">
                    {{ course.type }}
                  </n-tag>
                </div>
              </div>
            </div>
          </template>
        </div>
      </div>
    </n-card>

    <!-- 课程详情对话框 -->
    <n-modal
      v-model:show="showDetailModal"
      preset="card"
      title="课程详情"
      style="width: 500px"
      :bordered="false"
    >
      <n-descriptions :column="1" bordered v-if="selectedCourse">
        <n-descriptions-item label="课程名称">
          {{ selectedCourse.name }}
        </n-descriptions-item>
        <n-descriptions-item label="课程类型">
          <n-tag
            :type="selectedCourse.category === '音乐' ? 'info' : selectedCourse.category === '舞蹈' ? 'error' : 'warning'"
            :bordered="false"
          >
            {{ selectedCourse.category }}
          </n-tag>
        </n-descriptions-item>
        <n-descriptions-item label="授课教师">
          {{ selectedCourse.teacher }}
        </n-descriptions-item>
        <n-descriptions-item label="教室">
          {{ selectedCourse.classroom }}
        </n-descriptions-item>
        <n-descriptions-item label="上课时间">
          {{ selectedCourse.date }} {{ selectedCourse.startTime }}-{{ selectedCourse.endTime }}
        </n-descriptions-item>
        <n-descriptions-item label="课程类型">
          {{ selectedCourse.type }}
        </n-descriptions-item>
        <n-descriptions-item label="学生">
          <n-space>
            <n-tag
              v-for="student in selectedCourse.students"
              :key="student.id"
              size="small"
              :bordered="false"
            >
              {{ student.name }}
            </n-tag>
          </n-space>
        </n-descriptions-item>
      </n-descriptions>

      <template #footer>
        <n-space justify="end">
          <n-button @click="showDetailModal = false">关闭</n-button>
          <n-button type="warning" @click="handleAdjustCourse">调课</n-button>
          <n-button type="error" @click="handleCancelCourse">取消课程</n-button>
        </n-space>
      </template>
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useMessage } from 'naive-ui'
import { PersonOutline, BusinessOutline } from '@vicons/ionicons5'
import dayjs from 'dayjs'
import weekOfYear from 'dayjs/plugin/weekOfYear'

dayjs.extend(weekOfYear)

const message = useMessage()

const currentWeek = ref(Date.now())
const showDetailModal = ref(false)

const selectedCourse = ref<any>(null)

// 星期
const weekDays = [
  { label: '周一', value: 1 },
  { label: '周二', value: 2 },
  { label: '周三', value: 3 },
  { label: '周四', value: 4 },
  { label: '周五', value: 5 },
  { label: '周六', value: 6 },
  { label: '周日', value: 0 }
]

// 时间段
const timeSlots = [
  { id: 1, start: '08:00', end: '09:00' },
  { id: 2, start: '09:00', end: '10:00' },
  { id: 3, start: '10:00', end: '11:00' },
  { id: 4, start: '11:00', end: '12:00' },
  { id: 5, start: '14:00', end: '15:00' },
  { id: 6, start: '15:00', end: '16:00' },
  { id: 7, start: '16:00', end: '17:00' },
  { id: 8, start: '17:00', end: '18:00' },
  { id: 9, start: '19:00', end: '20:00' },
  { id: 10, start: '20:00', end: '21:00' }
]

// Mock课程数据
const courses = ref<any[]>([])

onMounted(() => {
  loadCourses()
})

function loadCourses() {
  // Mock数据
  courses.value = [
    {
      id: 1,
      name: '钢琴基础班',
      category: '音乐',
      teacher: '张老师',
      classroom: '音乐教室1',
      dayOfWeek: 1,
      startTime: '09:00',
      endTime: '10:00',
      type: '小组课',
      students: [
        { id: 1, name: '小明' },
        { id: 2, name: '小红' },
        { id: 3, name: '小刚' }
      ]
    },
    {
      id: 2,
      name: '芭蕾舞初级',
      category: '舞蹈',
      teacher: '李老师',
      classroom: '舞蹈教室1',
      dayOfWeek: 1,
      startTime: '10:00',
      endTime: '11:00',
      type: '小组课',
      students: [
        { id: 4, name: '小丽' },
        { id: 5, name: '小芳' }
      ]
    },
    {
      id: 3,
      name: '素描提高班',
      category: '美术',
      teacher: '王老师',
      classroom: '美术教室2',
      dayOfWeek: 2,
      startTime: '14:00',
      endTime: '16:00',
      type: '小组课',
      students: [
        { id: 6, name: '小华' },
        { id: 7, name: '小强' }
      ]
    },
    {
      id: 4,
      name: '声乐一对一',
      category: '音乐',
      teacher: '赵老师',
      classroom: '音乐教室3',
      dayOfWeek: 3,
      startTime: '16:00',
      endTime: '17:00',
      type: '一对一',
      students: [
        { id: 8, name: '小美' }
      ]
    },
    {
      id: 5,
      name: '中国舞中级',
      category: '舞蹈',
      teacher: '刘老师',
      classroom: '舞蹈教室2',
      dayOfWeek: 4,
      startTime: '15:00',
      endTime: '16:00',
      type: '小组课',
      students: [
        { id: 9, name: '小兰' },
        { id: 10, name: '小梅' }
      ]
    },
    {
      id: 6,
      name: '水彩入门',
      category: '美术',
      teacher: '周老师',
      classroom: '美术教室1',
      dayOfWeek: 5,
      startTime: '10:00',
      endTime: '11:00',
      type: '小组课',
      students: [
        { id: 11, name: '小雪' },
        { id: 12, name: '小冰' }
      ]
    },
    {
      id: 7,
      name: '钢琴进阶',
      category: '音乐',
      teacher: '张老师',
      classroom: '音乐教室1',
      dayOfWeek: 6,
      startTime: '09:00',
      endTime: '10:00',
      type: '小组课',
      students: [
        { id: 13, name: '小天' },
        { id: 14, name: '小地' }
      ]
    }
  ]
}

// 获取指定日期和时间段的课程
function getCourses(dayOfWeek: number, timeSlot: any) {
  return courses.value.filter(course => {
    if (course.dayOfWeek !== dayOfWeek) return false

    const courseStart = course.startTime
    const courseEnd = course.endTime
    const slotStart = timeSlot.start
    const slotEnd = timeSlot.end

    // 判断课程时间是否在时间段内
    return (
      (courseStart >= slotStart && courseStart < slotEnd) ||
      (courseEnd > slotStart && courseEnd <= slotEnd) ||
      (courseStart <= slotStart && courseEnd >= slotEnd)
    )
  }).map(course => ({
    ...course,
    date: getDateStr(dayOfWeek)
  }))
}

// 获取课程样式类
function getCourseClass(category: string) {
  const classMap: Record<string, string> = {
    '音乐': 'course-music',
    '舞蹈': 'course-dance',
    '美术': 'course-art'
  }
  return classMap[category] || ''
}

// 判断是否是今天
function isToday(dayOfWeek: number) {
  return dayjs().day() === dayOfWeek
}

// 获取日期字符串
function getDateStr(dayOfWeek: number) {
  const weekStart = dayjs(currentWeek.value).startOf('week')
  const targetDate = weekStart.add(dayOfWeek === 0 ? 6 : dayOfWeek - 1, 'day')
  return targetDate.format('MM-DD')
}

// 回到今天
function goToday() {
  currentWeek.value = Date.now()
}

// 周变化
function handleWeekChange(value: number) {
  currentWeek.value = value
}

// 单元格点击
function handleCellClick(dayOfWeek: number, timeSlot: any) {
  message.info(`点击了 ${getDateStr(dayOfWeek)} ${timeSlot.start}-${timeSlot.end}`)
}

// 课程点击
function handleCourseClick(course: any) {
  selectedCourse.value = course
  showDetailModal.value = true
}

// 调课
function handleAdjustCourse() {
  message.info('调课功能开发中')
  showDetailModal.value = false
}

// 取消课程
function handleCancelCourse() {
  message.warning('取消课程功能开发中')
  showDetailModal.value = false
}

</script>

<style scoped>
.page-container {
  width: 100%;
}

.timetable-container {
  overflow-x: auto;
}

.timetable-grid {
  display: grid;
  grid-template-columns: 100px repeat(7, 1fr);
  gap: 1px;
  background-color: #E5E7EB;
  border: 1px solid #E5E7EB;
  min-width: 1000px;
}

.cell-header {
  background-color: #F3F4F6;
  padding: 12px;
  font-weight: 600;
  text-align: center;
  color: #374151;
}

.time-header {
  display: flex;
  align-items: center;
  justify-content: center;
}

.day-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.day-header.today {
  background: #7C3AED;
  color: white;
}

.day-name {
  font-size: 16px;
}

.day-date {
  font-size: 12px;
  opacity: 0.8;
}

.time-cell {
  background-color: #F9FAFB;
  padding: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 2px;
}

.time-label {
  font-size: 13px;
  color: #6B7280;
}

.time-divider {
  font-size: 12px;
  color: #9CA3AF;
}

.course-cell {
  background-color: white;
  padding: 4px;
  min-height: 80px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.course-cell:hover {
  background-color: #F9FAFB;
}

.course-card {
  padding: 8px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
  margin-bottom: 4px;
}

.course-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.course-music {
  background: rgba(139, 92, 246, 0.15);
  border-left: 3px solid #8B5CF6;
}

.course-dance {
  background: rgba(236, 72, 153, 0.15);
  border-left: 3px solid #EC4899;
}

.course-art {
  background: rgba(245, 158, 11, 0.15);
  border-left: 3px solid #F59E0B;
}

.course-name {
  font-weight: 600;
  font-size: 14px;
  color: #1F2937;
  margin-bottom: 4px;
}

.course-info {
  font-size: 12px;
  color: #6B7280;
  display: flex;
  align-items: center;
  gap: 4px;
  margin-bottom: 2px;
}

.course-type {
  margin-top: 4px;
}

@media (max-width: 768px) {
  .timetable-grid {
    grid-template-columns: 80px repeat(7, 120px);
  }

  .course-name {
    font-size: 12px;
  }

  .course-info {
    font-size: 11px;
  }
}
</style>
