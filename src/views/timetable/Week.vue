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
          <n-button type="primary" @click="handlePrint">
            <template #icon>
              <n-icon><print-outline /></n-icon>
            </template>
            打印课表
          </n-button>
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
              <!-- 课程卡片：按实际时长跨多格显示 -->
              <div
                v-for="course in getCourses(day.value, time)"
                :key="course.id"
                class="course-card"
                :class="getCourseClass(course.category)"
                :style="getCourseCardStyle(course)"
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
          <n-popconfirm
            v-if="isAdmin"
            @positive-click="handleCancelCourse"
            positive-text="确认取消"
            negative-text="再想想"
          >
            <template #trigger>
              <n-button type="error" :loading="cancelling">取消课程</n-button>
            </template>
            确定取消该课程？此操作不可恢复。
          </n-popconfirm>
        </n-space>
      </template>
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useMessage } from 'naive-ui'
import { PersonOutline, BusinessOutline, PrintOutline } from '@vicons/ionicons5'
import dayjs from 'dayjs'
import weekOfYear from 'dayjs/plugin/weekOfYear'
import { getWeekSchedule, cancelSchedule } from '@/api/schedule'
import { useUserStore } from '@/stores/user'

dayjs.extend(weekOfYear)

const message = useMessage()
const userStore = useUserStore()
const isAdmin = computed(() => userStore.userInfo.role === 'admin')

const currentWeek = ref(Date.now())
const showDetailModal = ref(false)
const loading = ref(false)
const cancelling = ref(false)
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

// 时间段（完整覆盖 08:00 - 21:00，卡片按时长跨多格）
const timeSlots = [
  { id: 1, start: '08:00', end: '09:00' },
  { id: 2, start: '09:00', end: '10:00' },
  { id: 3, start: '10:00', end: '11:00' },
  { id: 4, start: '11:00', end: '12:00' },
  { id: 5, start: '12:00', end: '13:00' },
  { id: 6, start: '13:00', end: '14:00' },
  { id: 7, start: '14:00', end: '15:00' },
  { id: 8, start: '15:00', end: '16:00' },
  { id: 9, start: '16:00', end: '17:00' },
  { id: 10, start: '17:00', end: '18:00' },
  { id: 11, start: '18:00', end: '19:00' },
  { id: 12, start: '19:00', end: '20:00' },
  { id: 13, start: '20:00', end: '21:00' }
]

const courses = ref<any[]>([])

onMounted(() => {
  loadCourses()
})

watch(currentWeek, () => {
  loadCourses()
})

async function loadCourses() {
  loading.value = true
  try {
    const monday = dayjs(currentWeek.value).startOf('week').add(1, 'day')
    const dateStr = monday.format('YYYY-MM-DD')
    const data = await getWeekSchedule(dateStr) as any[]

    courses.value = data.map((item: any) => ({
      id: item.id,
      name: item.className,
      category: item.courseCategory,
      teacher: item.teacherName,
      classroom: item.classroomName,
      dayOfWeek: item.dayOfWeek,
      startTime: item.startTime?.substring(0, 5),
      endTime: item.endTime?.substring(0, 5),
      type: item.courseType || '小组课',
      students: []
    }))
  } catch (err: any) {
    message.error(err.message || '加载课表失败')
  } finally {
    loading.value = false
  }
}

// 获取指定日期和时间段的课程（只在开始格渲染卡片，卡片按时长跨多格）
function getCourses(dayOfWeek: number, timeSlot: any) {
  return courses.value.filter(course => {
    if (course.dayOfWeek !== dayOfWeek) return false
    return course.startTime >= timeSlot.start && course.startTime < timeSlot.end
  }).map(course => ({
    ...course,
    date: getDateStr(dayOfWeek)
  }))
}

// 把 HH:mm 转为分钟
function toMinutes(t: string) {
  const [h, m] = t.split(':').map(Number)
  return h * 60 + m
}

// 单元格高度（与 .course-cell min-height 保持一致）
const CELL_HEIGHT = 80
const SLOT_MINUTES = 60

// 计算课程卡片的绝对定位样式：按时长跨多格
function getCourseCardStyle(course: any) {
  const startMin = toMinutes(course.startTime)
  const endMin = toMinutes(course.endTime)
  const duration = endMin - startMin

  // 起始时间相对于所在格起点的偏移（一般为 0）
  const slotStart = timeSlots.find(
    s => course.startTime >= s.start && course.startTime < s.end
  )
  const offsetMin = slotStart ? startMin - toMinutes(slotStart.start) : 0

  const top = (offsetMin / SLOT_MINUTES) * CELL_HEIGHT
  const height = (duration / SLOT_MINUTES) * CELL_HEIGHT - 8

  return {
    position: 'absolute',
    top: `${top + 4}px`,
    left: '4px',
    right: '4px',
    height: `${height}px`,
    zIndex: 2,
    overflow: 'hidden'
  }
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

// 取消课程
async function handleCancelCourse() {
  if (!selectedCourse.value) return
  cancelling.value = true
  try {
    await cancelSchedule(selectedCourse.value.id)
    message.success('课程已取消')
    showDetailModal.value = false
    await loadCourses()
  } catch (err: any) {
    message.error(err.message || '取消失败')
  } finally {
    cancelling.value = false
  }
}

// 打印课表
function handlePrint() {
  window.print()
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
  gap: 0;
  border: 1px solid #E5E7EB;
  min-width: 1000px;
}

.cell-header {
  background-color: #F3F4F6;
  padding: 12px;
  font-weight: 600;
  text-align: center;
  color: #374151;
  border-right: 1px solid #E5E7EB;
  border-bottom: 1px solid #E5E7EB;
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
  border-right: 1px solid #E5E7EB;
  border-bottom: 1px solid #E5E7EB;
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
  height: 80px;
  cursor: pointer;
  transition: background-color 0.2s;
  border-right: 1px solid #E5E7EB;
  border-bottom: 1px solid #E5E7EB;
  position: relative;
}

.course-cell:hover {
  background-color: #F9FAFB;
}

.course-card {
  padding: 8px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  flex-direction: column;
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

@media print {
  * {
    overflow: visible !important;
  }

  .page-container {
    padding: 0 !important;
    margin: 0 !important;
    width: 100% !important;
  }

  :deep(.n-card) {
    border: none !important;
    box-shadow: none !important;
    margin: 0 !important;
    height: auto !important;
  }

  :deep(.n-card-header) {
    padding: 8px 0 !important;
    border-bottom: 2px solid #E5E7EB;
    margin: 0 !important;
  }

  :deep(.n-card-header__extra) {
    display: none !important;
  }

  :deep(.n-card__content) {
    padding: 8px 0 !important;
    margin: 0 !important;
    overflow: visible !important;
  }

  .timetable-container {
    overflow: visible !important;
    margin: 0 !important;
    width: 100% !important;
  }

  .timetable-grid {
    width: 100% !important;
    min-width: 100% !important;
    page-break-inside: avoid;
    margin: 0 !important;
    display: grid !important;
  }

  .course-cell {
    cursor: default;
    overflow: visible !important;
  }

  .course-card {
    box-shadow: none !important;
    transform: none !important;
    page-break-inside: avoid;
  }

  .course-card:hover {
    transform: none;
    box-shadow: none;
  }
}
</style>
