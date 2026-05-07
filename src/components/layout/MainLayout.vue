<template>
  <n-config-provider :theme-overrides="themeOverrides">
    <n-message-provider>
      <n-dialog-provider>
        <n-layout has-sider style="height: 100vh">
      <!-- 侧边栏 -->
      <n-layout-sider
        v-if="!isMobile"
        bordered
        collapse-mode="width"
        :collapsed-width="64"
        :width="240"
        :collapsed="collapsed"
        show-trigger
        @collapse="collapsed = true"
        @expand="collapsed = false"
        :native-scrollbar="false"
        style="background: #7C3AED"
      >
        <div class="logo-container">
          <div class="logo">
            <span v-if="!collapsed" class="logo-text">艺术排课系统</span>
            <span v-else class="logo-icon">艺</span>
          </div>
        </div>

        <n-menu
          :collapsed="collapsed"
          :collapsed-width="64"
          :collapsed-icon-size="22"
          :options="menuOptions"
          :value="activeKey"
          @update:value="handleMenuSelect"
          :inverted="true"
        />
      </n-layout-sider>

      <!-- 主内容区 -->
      <n-layout>
        <!-- 顶部导航栏 -->
        <n-layout-header bordered :style="{ height: '64px', padding: isMobile ? '0 12px' : '0 24px', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }">
          <div style="display: flex; align-items: center; gap: 16px">
            <!-- 移动端菜单按钮 -->
            <n-button v-if="isMobile" text @click="showDrawer = true">
              <template #icon>
                <n-icon size="24">
                  <menu-outline />
                </n-icon>
              </template>
            </n-button>

            <n-breadcrumb v-if="!isMobile">
              <n-breadcrumb-item v-for="item in breadcrumbs" :key="item.path">
                {{ item.title }}
              </n-breadcrumb-item>
            </n-breadcrumb>
            <span v-else style="font-weight: 600; font-size: 16px">{{ currentPageTitle }}</span>
          </div>

          <div style="display: flex; align-items: center; gap: 16px">
            <n-dropdown :options="userOptions" @select="handleUserAction">
              <div style="display: flex; align-items: center; gap: 8px; cursor: pointer">
                <n-avatar round :size="32" :style="{ background: '#7C3AED', flexShrink: 0 }">
                  {{ userInfo.name?.charAt(0) || 'U' }}
                </n-avatar>
                <span v-if="!isMobile">{{ userInfo.name || '用户' }}</span>
              </div>
            </n-dropdown>
          </div>
        </n-layout-header>

        <!-- 内容区域 -->
        <n-layout-content
          :native-scrollbar="false"
          :style="{ padding: isMobile ? '12px' : '24px', backgroundColor: '#F9FAFB' }"
        >
          <router-view v-slot="{ Component }">
            <transition name="fade" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
        </n-layout-content>
      </n-layout>
    </n-layout>

    <!-- 移动端抽屉菜单 -->
    <n-drawer v-model:show="showDrawer" :width="280" placement="left">
      <n-drawer-content title="菜单" :native-scrollbar="false">
        <div class="mobile-logo">
          <span class="logo-text">艺术排课系统</span>
        </div>
        <n-menu
          :options="menuOptions"
          :value="activeKey"
          @update:value="handleMobileMenuSelect"
        />
      </n-drawer-content>
    </n-drawer>
      </n-dialog-provider>
    </n-message-provider>
  </n-config-provider>
</template>

<script setup lang="ts">
import { ref, computed, h, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { NIcon } from 'naive-ui'
import type { MenuOption } from 'naive-ui'
import {
  HomeOutline,
  CalendarOutline,
  TimeOutline,
  PeopleOutline,
  PersonOutline,
  BusinessOutline,
  SchoolOutline,
  BookOutline,
  NotificationsOutline,
  LogOutOutline,
  MenuOutline
} from '@vicons/ionicons5'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const collapsed = ref(false)
const showDrawer = ref(false)
const isMobile = ref(false)

// 检测屏幕尺寸
const checkMobile = () => {
  isMobile.value = window.innerWidth < 768
}

// 加载用户信息
onMounted(() => {
  userStore.loadUserInfo()
  checkMobile()
  window.addEventListener('resize', checkMobile)
})

const userInfo = computed(() => userStore.userInfo)

// 当前页面标题
const currentPageTitle = computed(() => {
  const breadcrumb = breadcrumbs.value
  return breadcrumb.length > 0 ? breadcrumb[breadcrumb.length - 1].title : '仪表盘'
})

// 主题配置
const themeOverrides = {
  common: {
    primaryColor: '#7C3AED',
    primaryColorHover: '#8B5CF6',
    primaryColorPressed: '#6D28D9',
    primaryColorSuppl: '#A78BFA'
  }
}

// 渲染图标
function renderIcon(icon: any) {
  return () => h(NIcon, null, { default: () => h(icon) })
}

// 根据角色生成菜单
const menuOptions = computed<MenuOption[]>(() => {
  const role = userInfo.value.role

  // 管理员菜单
  if (role === 'admin') {
    return [
      {
        label: '仪表盘',
        key: 'dashboard',
        icon: renderIcon(HomeOutline)
      },
      {
        label: '排课管理',
        key: 'schedule',
        icon: renderIcon(CalendarOutline),
        children: [
          {
            label: '智能排课',
            key: 'schedule-auto'
          },
          {
            label: '手动排课',
            key: 'schedule-manual'
          },
          {
            label: '调课管理',
            key: 'schedule-adjust'
          }
        ]
      },
      {
        label: '课表查看',
        key: 'timetable',
        icon: renderIcon(TimeOutline),
        children: [
          {
            label: '周课表',
            key: 'timetable-week'
          },
          {
            label: '教师课表',
            key: 'timetable-teacher'
          },
          {
            label: '学生课表',
            key: 'timetable-student'
          },
          {
            label: '教室课表',
            key: 'timetable-classroom'
          }
        ]
      },
      {
        label: '基础数据',
        key: 'data',
        icon: renderIcon(BusinessOutline),
        children: [
          {
            label: '教师管理',
            key: 'teacher-list',
            icon: renderIcon(PersonOutline)
          },
          {
            label: '学生管理',
            key: 'student-list',
            icon: renderIcon(PeopleOutline)
          },
          {
            label: '教室管理',
            key: 'classroom-list',
            icon: renderIcon(SchoolOutline)
          },
          {
            label: '班级管理',
            key: 'class-list',
            icon: renderIcon(PeopleOutline)
          },
          {
            label: '课程管理',
            key: 'course-list',
            icon: renderIcon(BookOutline)
          }
        ]
      }
    ]
  }

  // 教师菜单
  if (role === 'teacher') {
    return [
      {
        label: '仪表盘',
        key: 'dashboard',
        icon: renderIcon(HomeOutline)
      },
      {
        label: '我的课表',
        key: 'timetable-teacher',
        icon: renderIcon(TimeOutline)
      },
      {
        label: '调课申请',
        key: 'schedule-adjust',
        icon: renderIcon(CalendarOutline)
      }
    ]
  }

  // 学生菜单
  if (role === 'student') {
    return [
      {
        label: '仪表盘',
        key: 'dashboard',
        icon: renderIcon(HomeOutline)
      },
      {
        label: '我的课表',
        key: 'timetable-student',
        icon: renderIcon(TimeOutline)
      }
    ]
  }

  return []
})

// 用户下拉菜单
const userOptions = [
  {
    label: '个人中心',
    key: 'profile',
    icon: renderIcon(PersonOutline)
  },
  {
    type: 'divider',
    key: 'd1'
  },
  {
    label: '退出登录',
    key: 'logout',
    icon: renderIcon(LogOutOutline)
  }
]

// 当前激活的菜单项
const activeKey = computed(() => {
  const path = route.path
  if (path.includes('/schedule/auto')) return 'schedule-auto'
  if (path.includes('/schedule/manual')) return 'schedule-manual'
  if (path.includes('/schedule/adjust')) return 'schedule-adjust'
  if (path.includes('/timetable/week')) return 'timetable-week'
  if (path.includes('/timetable/teacher')) return 'timetable-teacher'
  if (path.includes('/timetable/student')) return 'timetable-student'
  if (path.includes('/timetable/classroom')) return 'timetable-classroom'
  if (path.includes('/teacher')) return 'teacher-list'
  if (path.includes('/student')) return 'student-list'
  if (path.includes('/classroom')) return 'classroom-list'
  if (path.includes('/class')) return 'class-list'
  if (path.includes('/course')) return 'course-list'
  return 'dashboard'
})

// 面包屑
const breadcrumbs = computed(() => {
  const path = route.path
  const crumbs = [{ title: '首页', path: '/dashboard' }]

  if (path.includes('/schedule')) {
    crumbs.push({ title: '排课管理', path: '/schedule' })
    if (path.includes('/auto')) crumbs.push({ title: '智能排课', path: '/schedule/auto' })
    if (path.includes('/manual')) crumbs.push({ title: '手动排课', path: '/schedule/manual' })
    if (path.includes('/adjust')) crumbs.push({ title: '调课管理', path: '/schedule/adjust' })
  } else if (path.includes('/timetable')) {
    crumbs.push({ title: '课表查看', path: '/timetable' })
    if (path.includes('/week')) crumbs.push({ title: '周课表', path: '/timetable/week' })
    if (path.includes('/teacher')) crumbs.push({ title: '教师课表', path: '/timetable/teacher' })
    if (path.includes('/student')) crumbs.push({ title: '学生课表', path: '/timetable/student' })
    if (path.includes('/classroom')) crumbs.push({ title: '教室课表', path: '/timetable/classroom' })
  } else if (path.includes('/teacher')) {
    crumbs.push({ title: '教师管理', path: '/teacher/list' })
  } else if (path.includes('/student')) {
    crumbs.push({ title: '学生管理', path: '/student/list' })
  } else if (path.includes('/classroom')) {
    crumbs.push({ title: '教室管理', path: '/classroom/list' })
  } else if (path.includes('/class')) {
    crumbs.push({ title: '班级管理', path: '/class/list' })
  } else if (path.includes('/course')) {
    crumbs.push({ title: '课程管理', path: '/course/list' })
  }

  return crumbs
})

// 菜单选择
function handleMenuSelect(key: string) {
  const routeMap: Record<string, string> = {
    'dashboard': '/dashboard',
    'schedule-auto': '/schedule/auto',
    'schedule-manual': '/schedule/manual',
    'schedule-adjust': '/schedule/adjust',
    'timetable-week': '/timetable/week',
    'timetable-teacher': '/timetable/teacher',
    'timetable-student': '/timetable/student',
    'timetable-classroom': '/timetable/classroom',
    'teacher-list': '/teacher/list',
    'student-list': '/student/list',
    'classroom-list': '/classroom/list',
    'class-list': '/class/list',
    'course-list': '/course/list'
  }

  if (routeMap[key]) {
    router.push(routeMap[key])
  }
}

// 移动端菜单选择
function handleMobileMenuSelect(key: string) {
  handleMenuSelect(key)
  showDrawer.value = false
}

// 用户操作
function handleUserAction(key: string) {
  if (key === 'logout') {
    userStore.logout()
    router.push('/login')
  } else if (key === 'profile') {
    router.push('/profile')
  } else if (key === 'settings') {
    router.push('/settings')
  }
}
</script>

<style scoped>
.logo-container {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo {
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-text {
  font-size: 20px;
  font-weight: bold;
  color: white;
  letter-spacing: 2px;
}

.logo-icon {
  font-size: 24px;
  font-weight: bold;
  color: white;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease, transform 0.3s ease;
}

.fade-enter-from {
  opacity: 0;
  transform: translateY(10px);
}

.fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

.mobile-logo {
  padding: 16px;
  text-align: center;
  border-bottom: 1px solid #e5e7eb;
  margin-bottom: 16px;
}

.mobile-logo .logo-text {
  font-size: 18px;
  font-weight: bold;
  color: #7C3AED;
  letter-spacing: 2px;
}

@media (max-width: 768px) {
  :deep(.n-layout-header) {
    height: 56px !important;
  }

  :deep(.n-card) {
    margin-bottom: 12px;
  }

  :deep(.n-data-table) {
    font-size: 13px;
  }

  :deep(.n-button) {
    font-size: 13px;
  }
}

@media print {
  * {
    overflow: visible !important;
  }

  .n-layout-sider {
    display: none !important;
    width: 0 !important;
    min-width: 0 !important;
  }

  .n-layout-header {
    display: none !important;
    height: 0 !important;
    min-height: 0 !important;
  }

  .n-layout-content {
    padding: 0 !important;
    background-color: white !important;
    margin: 0 !important;
    height: auto !important;
    min-height: auto !important;
  }

  .n-layout {
    height: auto !important;
    min-height: auto !important;
    display: block !important;
  }

  :deep(.n-layout) {
    display: block !important;
    height: auto !important;
    min-height: auto !important;
  }

  :deep(.n-layout-scroll-container) {
    overflow: visible !important;
    height: auto !important;
  }

  :deep(.n-scrollbar) {
    overflow: visible !important;
    height: auto !important;
  }

  :deep(.n-scrollbar-container) {
    overflow: visible !important;
    height: auto !important;
  }
}
</style>
