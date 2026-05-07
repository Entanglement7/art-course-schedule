<template>
  <div class="dashboard-container">
    <!-- 管理员视图 -->
    <template v-if="userRole === 'admin'">
    <!-- 统计卡片 -->
    <n-grid :x-gap="16" :y-gap="16" :cols="4" responsive="screen">
      <n-gi :span="isMobile ? 2 : 1">
        <n-card class="stat-card" hoverable>
          <div class="stat-content">
            <div class="stat-icon" style="background: #7C3AED">
              <n-icon size="32" color="#fff">
                <people-outline />
              </n-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalStudents }}</div>
              <div class="stat-label">在读学生</div>
            </div>
          </div>
        </n-card>
      </n-gi>

      <n-gi :span="isMobile ? 2 : 1">
        <n-card class="stat-card" hoverable>
          <div class="stat-content">
            <div class="stat-icon" style="background: #EC4899">
              <n-icon size="32" color="#fff">
                <person-outline />
              </n-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalTeachers }}</div>
              <div class="stat-label">在职教师</div>
            </div>
          </div>
        </n-card>
      </n-gi>

      <n-gi :span="isMobile ? 2 : 1">
        <n-card class="stat-card" hoverable>
          <div class="stat-content">
            <div class="stat-icon" style="background: #F59E0B">
              <n-icon size="32" color="#fff">
                <calendar-outline />
              </n-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.todayCourses }}</div>
              <div class="stat-label">今日课程</div>
            </div>
          </div>
        </n-card>
      </n-gi>

      <n-gi :span="isMobile ? 2 : 1">
        <n-card class="stat-card" hoverable>
          <div class="stat-content">
            <div class="stat-icon" style="background: #10B981">
              <n-icon size="32" color="#fff">
                <school-outline />
              </n-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalClassrooms }}</div>
              <div class="stat-label">教室数量</div>
            </div>
          </div>
        </n-card>
      </n-gi>
    </n-grid>

    <!-- 课程类型统计 -->
    <n-grid :x-gap="16" :y-gap="16" :cols="3" responsive="screen" style="margin-top: 16px">
      <n-gi :span="isMobile ? 3 : 1">
        <n-card title="音乐课程" hoverable>
          <template #header-extra>
            <n-tag type="info" :bordered="false" style="background-color: rgba(139, 92, 246, 0.1); color: #8B5CF6">
              {{ courseStats.music }}节/周
            </n-tag>
          </template>
          <n-progress
            type="line"
            :percentage="(courseStats.music / courseStats.total) * 100"
            :show-indicator="false"
            :color="'#8B5CF6'"
            :rail-color="'rgba(139, 92, 246, 0.1)'"
          />
          <div style="margin-top: 12px; color: #6B7280; font-size: 14px">
            占比 {{ ((courseStats.music / courseStats.total) * 100).toFixed(1) }}%
          </div>
        </n-card>
      </n-gi>

      <n-gi :span="isMobile ? 3 : 1">
        <n-card title="舞蹈课程" hoverable>
          <template #header-extra>
            <n-tag type="info" :bordered="false" style="background-color: rgba(236, 72, 153, 0.1); color: #EC4899">
              {{ courseStats.dance }}节/周
            </n-tag>
          </template>
          <n-progress
            type="line"
            :percentage="(courseStats.dance / courseStats.total) * 100"
            :show-indicator="false"
            :color="'#EC4899'"
            :rail-color="'rgba(236, 72, 153, 0.1)'"
          />
          <div style="margin-top: 12px; color: #6B7280; font-size: 14px">
            占比 {{ ((courseStats.dance / courseStats.total) * 100).toFixed(1) }}%
          </div>
        </n-card>
      </n-gi>

      <n-gi :span="isMobile ? 3 : 1">
        <n-card title="美术课程" hoverable>
          <template #header-extra>
            <n-tag type="info" :bordered="false" style="background-color: rgba(245, 158, 11, 0.1); color: #F59E0B">
              {{ courseStats.art }}节/周
            </n-tag>
          </template>
          <n-progress
            type="line"
            :percentage="(courseStats.art / courseStats.total) * 100"
            :show-indicator="false"
            :color="'#F59E0B'"
            :rail-color="'rgba(245, 158, 11, 0.1)'"
          />
          <div style="margin-top: 12px; color: #6B7280; font-size: 14px">
            占比 {{ ((courseStats.art / courseStats.total) * 100).toFixed(1) }}%
          </div>
        </n-card>
      </n-gi>
    </n-grid>

    <!-- 今日课程和待办事项 -->
    <n-grid :x-gap="16" :y-gap="16" :cols="2" responsive="screen" style="margin-top: 16px">
      <n-gi :span="isMobile ? 2 : 1">
        <n-card title="今日课程" hoverable>
          <template #header-extra>
            <n-button text type="primary" @click="$router.push('/timetable/week')">
              查看更多
            </n-button>
          </template>
          <n-list>
            <n-list-item v-for="course in todayCourses" :key="course.id">
              <template #prefix>
                <n-tag
                  :type="course.category === '音乐' ? 'info' : course.category === '���蹈' ? 'error' : 'warning'"
                  :bordered="false"
                  size="small"
                >
                  {{ course.category }}
                </n-tag>
              </template>
              <div>
                <div style="font-weight: 500">{{ course.name }}</div>
                <div style="font-size: 12px; color: #9CA3AF; margin-top: 4px">
                  {{ course.time }} | {{ course.teacher }} | {{ course.classroom }}
                </div>
              </div>
            </n-list-item>
          </n-list>
        </n-card>
      </n-gi>

      <n-gi :span="isMobile ? 2 : 1">
        <n-card title="待办事项" hoverable>

          <n-list>
            <n-list-item v-for="task in pendingTasks" :key="task.id">
              <template #prefix>
                <n-icon size="20" :color="task.priority === 'high' ? '#EF4444' : '#F59E0B'">
                  <alert-circle-outline />
                </n-icon>
              </template>
              <div>
                <div style="font-weight: 500">{{ task.title }}</div>
                <div style="font-size: 12px; color: #9CA3AF; margin-top: 4px">
                  {{ task.time }}
                </div>
              </div>
              <template #suffix>
                <n-button size="small" type="primary" text @click="handleTask(task.id)">
                  处理
                </n-button>
              </template>
            </n-list-item>
          </n-list>
        </n-card>
      </n-gi>
    </n-grid>
    </template>

    <!-- 学生视图 -->
    <template v-else-if="userRole === 'student'">
      <!-- 未关联提示 -->
      <n-alert v-if="studentStats.unlinked" type="warning" title="账号未关联" style="margin-bottom: 16px">
        {{ studentStats.message }}
      </n-alert>

      <!-- 学生统计卡片 -->
      <n-grid :x-gap="16" :y-gap="16" :cols="4" responsive="screen">
        <n-gi :span="isMobile ? 2 : 1">
          <n-card class="stat-card" hoverable>
            <div class="stat-content">
              <div class="stat-icon" style="background: #7C3AED">
                <n-icon size="32" color="#fff">
                  <book-outline />
                </n-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ studentStats.enrolledCourses }}</div>
                <div class="stat-label">报名课程</div>
              </div>
            </div>
          </n-card>
        </n-gi>

        <n-gi :span="isMobile ? 2 : 1">
          <n-card class="stat-card" hoverable>
            <div class="stat-content">
              <div class="stat-icon" style="background: #EC4899">
                <n-icon size="32" color="#fff">
                  <calendar-outline />
                </n-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ studentStats.todayClasses }}</div>
                <div class="stat-label">今日课程</div>
              </div>
            </div>
          </n-card>
        </n-gi>

        <n-gi :span="isMobile ? 2 : 1">
          <n-card class="stat-card" hoverable>
            <div class="stat-content">
              <div class="stat-icon" style="background: #F59E0B">
                <n-icon size="32" color="#fff">
                  <time-outline />
                </n-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ studentStats.totalHours }}</div>
                <div class="stat-label">累计课时</div>
              </div>
            </div>
          </n-card>
        </n-gi>

        <n-gi :span="isMobile ? 2 : 1">
          <n-card class="stat-card" hoverable>
            <div class="stat-content">
              <div class="stat-icon" style="background: #10B981">
                <n-icon size="32" color="#fff">
                  <school-outline />
                </n-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ studentStats.weekHours }}</div>
                <div class="stat-label">本周课时</div>
              </div>
            </div>
          </n-card>
        </n-gi>
      </n-grid>

      <!-- 我的课程和班级 -->
      <n-grid :x-gap="16" :y-gap="16" :cols="2" responsive="screen" style="margin-top: 16px">
        <n-gi :span="isMobile ? 2 : 1">
          <n-card title="我的课程" hoverable>
            <template #header-extra>
              <n-button text type="primary" @click="$router.push('/timetable/student')">
                查看课表
              </n-button>
            </template>
            <n-list>
              <n-list-item v-for="course in studentCourses" :key="course.id">
                <template #prefix>
                  <n-tag
                    :type="course.category === '音乐' ? 'info' : course.category === '舞蹈' ? 'error' : 'warning'"
                    :bordered="false"
                    size="small"
                  >
                    {{ course.category }}
                  </n-tag>
                </template>
                <div>
                  <div style="font-weight: 500">{{ course.name }}</div>
                  <div style="font-size: 12px; color: #9CA3AF; margin-top: 4px">
                    {{ course.teacher }} | {{ course.schedule }}
                  </div>
                  <div style="font-size: 12px; color: #9CA3AF">
                    {{ course.classroom }}
                  </div>
                </div>
              </n-list-item>
            </n-list>
          </n-card>
        </n-gi>

        <n-gi :span="isMobile ? 2 : 1">
          <n-card title="我的班级" hoverable>
            <n-list>
              <n-list-item v-for="cls in studentClasses" :key="cls.id">
                <div>
                  <div style="font-weight: 500">{{ cls.name }}</div>
                  <div style="font-size: 12px; color: #9CA3AF; margin-top: 4px">
                    教师：{{ cls.teacher }} | 班级人数：{{ cls.studentCount }}人
                  </div>
                </div>
              </n-list-item>
            </n-list>
          </n-card>
        </n-gi>
      </n-grid>

      <!-- 今日课程和待办 -->
      <n-grid :x-gap="16" :y-gap="16" :cols="2" responsive="screen" style="margin-top: 16px">
        <n-gi :span="isMobile ? 2 : 1">
          <n-card title="今日课程" hoverable>
            <n-list>
              <n-list-item v-for="course in todayCourses" :key="course.id">
                <template #prefix>
                  <n-tag
                    :type="course.category === '音乐' ? 'info' : course.category === '舞蹈' ? 'error' : 'warning'"
                    :bordered="false"
                    size="small"
                  >
                    {{ course.category }}
                  </n-tag>
                </template>
                <div>
                  <div style="font-weight: 500">{{ course.name }}</div>
                  <div style="font-size: 12px; color: #9CA3AF; margin-top: 4px">
                    {{ course.time }} | {{ course.teacher }} | {{ course.classroom }}
                  </div>
                </div>
              </n-list-item>
              <n-empty v-if="todayCourses.length === 0" description="今天没有课程" />
            </n-list>
          </n-card>
        </n-gi>

        <n-gi :span="isMobile ? 2 : 1">
          <n-card title="待办事项" hoverable>
            <n-list>
              <n-list-item v-for="task in pendingTasks" :key="task.id">
                <template #prefix>
                  <n-icon size="20" :color="task.priority === 'high' ? '#EF4444' : '#F59E0B'">
                    <alert-circle-outline />
                  </n-icon>
                </template>
                <div>
                  <div style="font-weight: 500">{{ task.title }}</div>
                  <div style="font-size: 12px; color: #9CA3AF; margin-top: 4px">
                    {{ task.time }}
                  </div>
                </div>
                <template #suffix>
                  <n-button size="small" type="primary" text @click="handleTask(task.id)">
                    处理
                  </n-button>
                </template>
              </n-list-item>
            </n-list>
          </n-card>
        </n-gi>
      </n-grid>
    </template>

    <!-- 教师视图 -->
    <template v-else-if="userRole === 'teacher'">
      <!-- 未关联提示 -->
      <n-alert v-if="teacherStats.unlinked" type="warning" title="账号未关联" style="margin-bottom: 16px">
        {{ teacherStats.message }}
      </n-alert>

      <!-- 教师统计卡片 -->
      <n-grid :x-gap="16" :y-gap="16" :cols="4" responsive="screen">
        <n-gi :span="isMobile ? 2 : 1">
          <n-card class="stat-card" hoverable>
            <div class="stat-content">
              <div class="stat-icon" style="background: #7C3AED">
                <n-icon size="32" color="#fff">
                  <school-outline />
                </n-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ teacherStats.myClasses }}</div>
                <div class="stat-label">我的班级</div>
              </div>
            </div>
          </n-card>
        </n-gi>

        <n-gi :span="isMobile ? 2 : 1">
          <n-card class="stat-card" hoverable>
            <div class="stat-content">
              <div class="stat-icon" style="background: #EC4899">
                <n-icon size="32" color="#fff">
                  <calendar-outline />
                </n-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ teacherStats.todayClasses }}</div>
                <div class="stat-label">今日课程</div>
              </div>
            </div>
          </n-card>
        </n-gi>

        <n-gi :span="isMobile ? 2 : 1">
          <n-card class="stat-card" hoverable>
            <div class="stat-content">
              <div class="stat-icon" style="background: #F59E0B">
                <n-icon size="32" color="#fff">
                  <people-outline />
                </n-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ teacherStats.totalStudents }}</div>
                <div class="stat-label">学生总数</div>
              </div>
            </div>
          </n-card>
        </n-gi>

        <n-gi :span="isMobile ? 2 : 1">
          <n-card class="stat-card" hoverable>
            <div class="stat-content">
              <div class="stat-icon" style="background: #10B981">
                <n-icon size="32" color="#fff">
                  <time-outline />
                </n-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ teacherStats.weekHours }}</div>
                <div class="stat-label">本周课时</div>
              </div>
            </div>
          </n-card>
        </n-gi>
      </n-grid>

      <!-- 今日课程和待办 -->
      <n-grid :x-gap="16" :y-gap="16" :cols="2" responsive="screen" style="margin-top: 16px">
        <n-gi :span="isMobile ? 2 : 1">
          <n-card title="今日课程" hoverable>
            <template #header-extra>
              <n-button text type="primary" @click="$router.push('/timetable/teacher')">
                查看课表
              </n-button>
            </template>
            <n-list>
              <n-list-item v-for="course in todayCourses" :key="course.id">
                <template #prefix>
                  <n-tag
                    :type="course.category === '音乐' ? 'info' : course.category === '舞蹈' ? 'error' : 'warning'"
                    :bordered="false"
                    size="small"
                  >
                    {{ course.category }}
                  </n-tag>
                </template>
                <div>
                  <div style="font-weight: 500">{{ course.name }}</div>
                  <div style="font-size: 12px; color: #9CA3AF; margin-top: 4px">
                    {{ course.time }} | {{ course.classroom }} | {{ course.studentCount }}人
                  </div>
                </div>
              </n-list-item>
            </n-list>
          </n-card>
        </n-gi>

        <n-gi :span="isMobile ? 2 : 1">
          <n-card title="待办事项" hoverable>
            <n-list>
              <n-list-item v-for="task in pendingTasks" :key="task.id">
                <template #prefix>
                  <n-icon size="20" :color="task.priority === 'high' ? '#EF4444' : '#F59E0B'">
                    <alert-circle-outline />
                  </n-icon>
                </template>
                <div>
                  <div style="font-weight: 500">{{ task.title }}</div>
                  <div style="font-size: 12px; color: #9CA3AF; margin-top: 4px">
                    {{ task.time }}
                  </div>
                </div>
                <template #suffix>
                  <n-button size="small" type="primary" text @click="handleTask(task.id)">
                    处理
                  </n-button>
                </template>
              </n-list-item>
            </n-list>
          </n-card>
        </n-gi>
      </n-grid>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  PeopleOutline,
  PersonOutline,
  CalendarOutline,
  SchoolOutline,

  AlertCircleOutline,
  BookOutline,
  TimeOutline
} from '@vicons/ionicons5'
import { useUserStore } from '@/stores/user'
import { getAdjustments } from '@/api/adjustment'
import { getDashboard } from '@/api/dashboard'

const userStore = useUserStore()
const router = useRouter()

// 响应式判断
const isMobile = computed(() => window.innerWidth < 768)

// 当前用户角色
const userRole = computed(() => userStore.userInfo.role)

// 统计数据
const stats = ref({
  totalStudents: 0,
  totalTeachers: 0,
  todayCourses: 0,
  totalClassrooms: 0
})

// 学生统计数据
const studentStats = ref({
  unlinked: false,
  message: '',
  enrolledCourses: 0,
  todayClasses: 0,
  totalHours: 0,
  weekHours: 0
})

// 教师统计数据
const teacherStats = ref({
  unlinked: false,
  message: '',
  myClasses: 0,
  todayClasses: 0,
  totalStudents: 0,
  weekHours: 0
})

// 课程类型统计
const courseStats = ref({
  music: 0,
  dance: 0,
  art: 0,
  total: 0
})

// 今日课程
const todayCourses = ref<any[]>([])

// 学生的课程列表
const studentCourses = ref<any[]>([])

// 学生的班级信息
const studentClasses = ref<any[]>([])

// 待办事项
const pendingTasks = ref<any[]>([])

// 加载数据
onMounted(() => {
  userStore.loadUserInfo()
  loadData()
  loadPendingTasks()
})

async function loadData() {
  try {
    const res = await getDashboard() as any
    const data = res.data || res

    if (userRole.value === 'admin') {
      loadAdminData(data)
    } else if (userRole.value === 'teacher') {
      loadTeacherData(data)
    } else if (userRole.value === 'student') {
      loadStudentData(data)
    }
  } catch (error) {
    console.error('Failed to load dashboard data:', error)
  }
}

function loadAdminData(data: any) {
  stats.value = {
    totalStudents: data.totalStudents || 0,
    totalTeachers: data.totalTeachers || 0,
    todayCourses: data.todayCourses || 0,
    totalClassrooms: data.totalClassrooms || 0
  }

  const courseStatsData = data.courseStats || {}
  courseStats.value = {
    music: courseStatsData.music || 0,
    dance: courseStatsData.dance || 0,
    art: courseStatsData.art || 0,
    total: courseStatsData.total || 0
  }

  // 转换后端数据格式为前端需要的格式
  const courseList = data.todayCourseList || []
  todayCourses.value = courseList.map((item: any) => ({
    id: item.id,
    name: item.className,
    category: item.courseCategory,
    time: `${item.startTime?.substring(0, 5)}-${item.endTime?.substring(0, 5)}`,
    teacher: item.teacherName,
    classroom: item.classroomName
  }))
}

function loadTeacherData(data: any) {
  if (data.unlinked) {
    teacherStats.value.unlinked = true
    teacherStats.value.message = data.message || 'Account not linked to teacher profile'
    return
  }

  teacherStats.value = {
    unlinked: false,
    message: '',
    myClasses: data.myClasses || 0,
    todayClasses: data.todayClasses || 0,
    totalStudents: data.totalStudents || 0,
    weekHours: data.weekHours || 0
  }

  // 转换后端数据格式为前端需要的格式
  const courseList = data.todayCourseList || []
  todayCourses.value = courseList.map((item: any) => ({
    id: item.id,
    name: item.className,
    category: item.courseCategory,
    time: `${item.startTime?.substring(0, 5)}-${item.endTime?.substring(0, 5)}`,
    classroom: item.classroomName,
    studentCount: item.studentCount || 0
  }))
}

function loadStudentData(data: any) {
  if (data.unlinked) {
    studentStats.value.unlinked = true
    studentStats.value.message = data.message || 'Account not linked to student profile'
    return
  }

  studentStats.value = {
    unlinked: false,
    message: '',
    enrolledCourses: data.enrolledCourses || 0,
    todayClasses: data.todayClasses || 0,
    totalHours: data.totalHours || 0,
    weekHours: data.weekHours || 0
  }

  studentCourses.value = data.myCourses || []
  studentClasses.value = data.myClasses || []

  // 转换后端数据格式为前端需要的格式
  const courseList = data.todayCourseList || []
  todayCourses.value = courseList.map((item: any) => ({
    id: item.id,
    name: item.className,
    category: item.courseCategory,
    time: `${item.startTime?.substring(0, 5)}-${item.endTime?.substring(0, 5)}`,
    teacher: item.teacherName,
    classroom: item.classroomName
  }))
}

async function loadPendingTasks() {
  try {
    const params: Record<string, unknown> = { status: '待审核', size: 100 }
    // 教师只看自己的申请（后端已按角色过滤）
    const res = await getAdjustments(params) as any
    const list: any[] = res.records ?? res.list ?? res
    pendingTasks.value = list.map((a: any) => ({
      id: a.id,
      title: `${a.applicantName} 申请调课：${a.className ?? ''}`,
      time: a.applyTime ? new Date(a.applyTime).toLocaleDateString('zh-CN') : '',
      priority: 'high',
      route: '/schedule/adjust'
    }))
  } catch { /* ignore */ }
}

function handleTask(id: number) {
  const task = pendingTasks.value.find(t => t.id === id)
  if (task?.route) {
    router.push(task.route)
  }
}
</script>

<style scoped>
.dashboard-container {
  width: 100%;
}

.stat-card {
  height: 100%;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 64px;
  height: 64px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #1F2937;
  line-height: 1;
}

.stat-label {
  font-size: 14px;
  color: #6B7280;
  margin-top: 8px;
}

@media (max-width: 768px) {
  .stat-value {
    font-size: 24px;
  }

  .stat-icon {
    width: 48px;
    height: 48px;
  }
}
</style>
