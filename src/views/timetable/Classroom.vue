<template>
  <div class="page-container">
    <n-card title="教室课表" :bordered="false">
      <!-- 教室选择 -->
      <n-space :size="16" style="margin-bottom: 16px">
        <n-select
          v-model:value="selectedClassroom"
          placeholder="选择教室"
          style="width: 200px"
          :options="classroomOptions"
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

      <!-- 教室信息卡片 -->
      <n-card v-if="classroomInfo" style="margin-bottom: 16px" :bordered="false" embedded>
        <n-space align="center">
          <div
            style="
              width: 64px;
              height: 64px;
              border-radius: 50%;
              display: flex;
              align-items: center;
              justify-content: center;
              font-size: 24px;
              font-weight: 600;
              color: white;
            "
            :style="{ background: getClassroomColor(classroomInfo.type) }"
          >
            {{ classroomInfo.name.slice(-1) }}
          </div>
          <div>
            <div style="font-size: 18px; font-weight: 600; margin-bottom: 8px">
              {{ classroomInfo.name }}
              <n-tag :type="getTypeTag(classroomInfo.type)" size="small" :bordered="false">
                {{ classroomInfo.type }}
              </n-tag>
            </div>
            <n-space :size="8">
              <n-tag type="info" size="small" :bordered="false">
                容纳{{ classroomInfo.capacity }}人
              </n-tag>
              <n-tag type="info" size="small" :bordered="false">
                {{ classroomInfo.floor }}
              </n-tag>
              <n-tag type="success" size="small" :bordered="false">
                本周 {{ weekCourseCount }} 节课
              </n-tag>
              <n-tag
                :type="utilizationRate >= 70 ? 'error' : utilizationRate >= 50 ? 'warning' : 'success'"
                size="small"
                :bordered="false"
              >
                使用率 {{ utilizationRate }}%
              </n-tag>
            </n-space>
          </div>
        </n-space>
      </n-card>

      <!-- 课表 -->
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
            <tr v-for="slot in timeSlots" :key="slot.id">
              <td class="time-column">
                <div class="time-text">{{ slot.time }}</div>
              </td>
              <td
                v-for="day in weekDays"
                :key="day.value"
                class="course-cell"
                :class="{ 'empty-cell': !getCourseForSlot(day.value, slot.id).length }"
              >
                <div
                  v-for="course in getCourseForSlot(day.value, slot.id)"
                  :key="course.id"
                  class="course-card"
                  :class="`course-${course.category}`"
                  @click="handleCourseClick(course)"
                >
                  <div class="course-name">{{ course.name }}</div>
                  <div class="course-info">{{ course.className }}</div>
                  <div class="course-info">{{ course.teacher }}</div>
                  <div class="course-info">{{ course.time }}</div>
                </div>
                <div v-if="!getCourseForSlot(day.value, slot.id).length" class="empty-text">
                  空闲
                </div>
              </td>
            </tr>
          </tbody>
        </table>
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
      <n-descriptions v-if="selectedCourse" :column="1" bordered>
        <n-descriptions-item label="课程名称">
          {{ selectedCourse.name }}
        </n-descriptions-item>
        <n-descriptions-item label="班级">
          {{ selectedCourse.className }}
        </n-descriptions-item>
        <n-descriptions-item label="授课教师">
          {{ selectedCourse.teacher }}
        </n-descriptions-item>
        <n-descriptions-item label="上课时间">
          {{ selectedCourse.time }}
        </n-descriptions-item>
        <n-descriptions-item label="学生人数">
          {{ selectedCourse.studentCount }}人
        </n-descriptions-item>
        <n-descriptions-item label="课程类别">
          <n-tag :type="getCategoryType(selectedCourse.category)" :bordered="false">
            {{ selectedCourse.category }}
          </n-tag>
        </n-descriptions-item>
      </n-descriptions>
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useMessage } from 'naive-ui'
import dayjs from 'dayjs'
import weekOfYear from 'dayjs/plugin/weekOfYear'

dayjs.extend(weekOfYear)

const message = useMessage()

const selectedClassroom = ref('音乐教室1')
const selectedWeek = ref(Date.now())
const classroomInfo = ref<any>(null)
const scheduleData = ref<any[]>([])
const showDetailModal = ref(false)
const selectedCourse = ref<any>(null)

const classroomOptions = [
  { label: '音乐教室1', value: '音乐教室1' },
  { label: '音乐教室2', value: '音乐教室2' },
  { label: '音乐教室3', value: '音乐教室3' },
  { label: '舞蹈教室1', value: '舞蹈教室1' },
  { label: '舞蹈教室2', value: '舞蹈教室2' },
  { label: '美术教室1', value: '美术教室1' },
  { label: '美术教室2', value: '美术教室2' },
  { label: '多功能教室', value: '多功能教室' }
]

const weekDays = computed(() => {
  const start = dayjs(selectedWeek.value).startOf('week')
  return [
    { label: '周一', value: 1, date: start.add(0, 'day').format('MM-DD') },
    { label: '周二', value: 2, date: start.add(1, 'day').format('MM-DD') },
    { label: '周三', value: 3, date: start.add(2, 'day').format('MM-DD') },
    { label: '周四', value: 4, date: start.add(3, 'day').format('MM-DD') },
    { label: '周五', value: 5, date: start.add(4, 'day').format('MM-DD') },
    { label: '周六', value: 6, date: start.add(5, 'day').format('MM-DD') },
    { label: '周日', value: 7, date: start.add(6, 'day').format('MM-DD') }
  ]
})

const timeSlots = [
  { id: 1, time: '08:00-09:00' },
  { id: 2, time: '09:00-10:00' },
  { id: 3, time: '10:00-11:00' },
  { id: 4, time: '11:00-12:00' },
  { id: 5, time: '14:00-15:00' },
  { id: 6, time: '15:00-16:00' },
  { id: 7, time: '16:00-17:00' },
  { id: 8, time: '17:00-18:00' },
  { id: 9, time: '19:00-20:00' },
  { id: 10, time: '20:00-21:00' }
]

const weekCourseCount = computed(() => scheduleData.value.length)

const utilizationRate = computed(() => {
  const totalSlots = 7 * 10 // 7天 * 10个时间段
  const usedSlots = scheduleData.value.length
  return Math.round((usedSlots / totalSlots) * 100)
})

onMounted(() => {
  loadSchedule()
})

function loadSchedule() {
  // Mock数据
  const classroomSchedules: Record<string, any> = {
    '音乐教室1': {
      info: {
        name: '音乐教室1',
        type: '音乐教室',
        capacity: 10,
        floor: '3楼'
      },
      courses: [
        { id: 1, name: '钢琴基础', className: '钢琴基础A班', teacher: '张老师', time: '09:00-10:00', day: 1, slot: 2, category: '音乐', studentCount: 8 },
        { id: 2, name: '钢琴基础', className: '钢琴基础A班', teacher: '张老师', time: '09:00-10:00', day: 3, slot: 2, category: '音乐', studentCount: 8 },
        { id: 3, name: '钢琴基础', className: '钢琴基础B班', teacher: '张老师', time: '10:00-11:00', day: 2, slot: 3, category: '音乐', studentCount: 10 },
        { id: 4, name: '钢琴基础', className: '钢琴基础B班', teacher: '张老师', time: '10:00-11:00', day: 4, slot: 3, category: '音乐', studentCount: 10 },
        { id: 5, name: '钢琴进阶', className: '钢琴进阶班', teacher: '张老师', time: '14:00-15:00', day: 1, slot: 5, category: '音乐', studentCount: 8 },
        { id: 6, name: '钢琴进阶', className: '钢琴进阶班', teacher: '张老师', time: '14:00-15:00', day: 3, slot: 5, category: '音乐', studentCount: 8 }
      ]
    },
    '音乐教室2': {
      info: {
        name: '音乐教室2',
        type: '音乐教室',
        capacity: 8,
        floor: '3楼'
      },
      courses: [
        { id: 7, name: '声乐基础', className: '声乐基础班', teacher: '张老师', time: '15:00-16:00', day: 5, slot: 6, category: '音乐', studentCount: 12 },
        { id: 8, name: '声乐基础', className: '声乐基础班', teacher: '张老师', time: '15:00-16:00', day: 6, slot: 6, category: '音乐', studentCount: 12 }
      ]
    },
    '舞蹈教室1': {
      info: {
        name: '舞蹈教室1',
        type: '舞蹈教室',
        capacity: 20,
        floor: '2楼'
      },
      courses: [
        { id: 9, name: '芭蕾舞初级', className: '芭蕾舞初级班', teacher: '李老师', time: '10:00-11:30', day: 1, slot: 3, category: '舞蹈', studentCount: 12 },
        { id: 10, name: '芭蕾舞初级', className: '芭蕾舞初级班', teacher: '李老师', time: '10:00-11:30', day: 3, slot: 3, category: '舞蹈', studentCount: 12 },
        { id: 11, name: '芭蕾舞中级', className: '芭蕾舞中级班', teacher: '李老师', time: '14:00-15:30', day: 2, slot: 5, category: '舞蹈', studentCount: 10 },
        { id: 12, name: '芭蕾舞中级', className: '芭蕾舞中级班', teacher: '李老师', time: '14:00-15:30', day: 4, slot: 5, category: '舞蹈', studentCount: 10 },
        { id: 13, name: '中国舞初级', className: '中国舞初级班', teacher: '刘老师', time: '15:00-16:30', day: 2, slot: 6, category: '舞蹈', studentCount: 14 },
        { id: 14, name: '中国舞初级', className: '中国舞初级班', teacher: '刘老师', time: '15:00-16:30', day: 4, slot: 6, category: '舞蹈', studentCount: 14 }
      ]
    },
    '美术教室1': {
      info: {
        name: '美术教室1',
        type: '美术教室',
        capacity: 15,
        floor: '4楼'
      },
      courses: [
        { id: 15, name: '素描基础', className: '素描基础班', teacher: '王老师', time: '14:00-16:00', day: 6, slot: 5, category: '美术', studentCount: 10 },
        { id: 16, name: '素描提高', className: '素描提高班', teacher: '王老师', time: '14:00-16:00', day: 7, slot: 5, category: '美术', studentCount: 8 }
      ]
    }
  }

  const schedule = classroomSchedules[selectedClassroom.value] || classroomSchedules['音乐教室1']
  classroomInfo.value = schedule.info
  scheduleData.value = schedule.courses
}

function getCourseForSlot(day: number, slot: number) {
  return scheduleData.value.filter(course => course.day === day && course.slot === slot)
}

function handleCourseClick(course: any) {
  selectedCourse.value = course
  showDetailModal.value = true
}

function getCategoryType(category: string) {
  const typeMap: Record<string, any> = {
    '音乐': 'info',
    '舞蹈': 'error',
    '美术': 'warning'
  }
  return typeMap[category] || 'default'
}

function getClassroomColor(type: string) {
  const colorMap: Record<string, string> = {
    '音乐教室': '#8B5CF6',
    '舞蹈教室': '#EC4899',
    '美术教室': '#F59E0B',
    '多功能教室': '#10B981'
  }
  return colorMap[type] || '#7C3AED'
}

function getTypeTag(type: string) {
  const tagMap: Record<string, any> = {
    '音乐教室': 'info',
    '舞蹈教室': 'error',
    '美术教室': 'warning',
    '多功能教室': 'success'
  }
  return tagMap[type] || 'default'
}
</script>

<style scoped>
.page-container {
  width: 100%;
}

.timetable-container {
  overflow-x: auto;
}

.timetable {
  width: 100%;
  border-collapse: collapse;
  min-width: 900px;
}

.timetable th,
.timetable td {
  border: 1px solid #e5e7eb;
  padding: 8px;
  text-align: center;
}

.timetable th {
  background: #7C3AED;
  color: white;
  font-weight: 600;
  padding: 12px 8px;
}

.time-column {
  width: 100px;
  background: #f9fafb;
  font-weight: 500;
}

.date-text {
  font-size: 12px;
  margin-top: 4px;
  opacity: 0.9;
}

.time-text {
  font-size: 13px;
  color: #6b7280;
}

.course-cell {
  vertical-align: top;
  padding: 4px;
  min-height: 60px;
}

.empty-cell {
  background: #f9fafb;
}

.empty-text {
  color: #9ca3af;
  font-size: 12px;
  padding: 20px 0;
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

.course-音乐 {
  background: rgba(139, 92, 246, 0.2);
  border-left: 3px solid #8B5CF6;
}

.course-舞蹈 {
  background: rgba(236, 72, 153, 0.2);
  border-left: 3px solid #EC4899;
}

.course-美术 {
  background: rgba(245, 158, 11, 0.2);
  border-left: 3px solid #F59E0B;
}

.course-name {
  font-weight: 600;
  font-size: 14px;
  margin-bottom: 4px;
  color: #1f2937;
}

.course-info {
  font-size: 12px;
  color: #6b7280;
  margin-top: 2px;
}
</style>
