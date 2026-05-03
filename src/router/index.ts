import { createRouter, createWebHistory } from 'vue-router'
import MainLayout from '../components/layout/MainLayout.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/Login.vue')
    },
    {
      path: '/',
      component: MainLayout,
      redirect: '/dashboard',
      children: [
        {
          path: 'dashboard',
          name: 'dashboard',
          component: () => import('../views/dashboard/Index.vue')
        },
        // 排课管理 - 仅管理员
        {
          path: 'schedule/auto',
          name: 'schedule-auto',
          component: () => import('../views/schedule/Auto.vue'),
          meta: { requiresAuth: true, roles: ['admin'] }
        },
        {
          path: 'schedule/manual',
          name: 'schedule-manual',
          component: () => import('../views/schedule/Manual.vue'),
          meta: { requiresAuth: true, roles: ['admin'] }
        },
        {
          path: 'schedule/adjust',
          name: 'schedule-adjust',
          component: () => import('../views/schedule/Adjust.vue'),
          meta: { requiresAuth: true, roles: ['admin', 'teacher'] }
        },
        // 课表查看
        {
          path: 'timetable/week',
          name: 'timetable-week',
          component: () => import('../views/timetable/Week.vue'),
          meta: { requiresAuth: true, roles: ['admin'] }
        },
        {
          path: 'timetable/teacher',
          name: 'timetable-teacher',
          component: () => import('../views/timetable/Teacher.vue'),
          meta: { requiresAuth: true, roles: ['admin', 'teacher'] }
        },
        {
          path: 'timetable/student',
          name: 'timetable-student',
          component: () => import('../views/timetable/Student.vue'),
          meta: { requiresAuth: true, roles: ['admin', 'student'] }
        },
        {
          path: 'timetable/classroom',
          name: 'timetable-classroom',
          component: () => import('../views/timetable/Classroom.vue'),
          meta: { requiresAuth: true, roles: ['admin'] }
        },
        // 教师管理 - 仅管理员
        {
          path: 'teacher/list',
          name: 'teacher-list',
          component: () => import('../views/teacher/List.vue'),
          meta: { requiresAuth: true, roles: ['admin'] }
        },
        {
          path: 'teacher/add',
          name: 'teacher-add',
          component: () => import('../views/teacher/Form.vue'),
          meta: { requiresAuth: true, roles: ['admin'] }
        },
        {
          path: 'teacher/edit/:id',
          name: 'teacher-edit',
          component: () => import('../views/teacher/Form.vue'),
          meta: { requiresAuth: true, roles: ['admin'] }
        },
        // 学生管理 - 仅管理员
        {
          path: 'student/list',
          name: 'student-list',
          component: () => import('../views/student/List.vue'),
          meta: { requiresAuth: true, roles: ['admin'] }
        },
        {
          path: 'student/add',
          name: 'student-add',
          component: () => import('../views/student/Form.vue'),
          meta: { requiresAuth: true, roles: ['admin'] }
        },
        {
          path: 'student/edit/:id',
          name: 'student-edit',
          component: () => import('../views/student/Form.vue'),
          meta: { requiresAuth: true, roles: ['admin'] }
        },
        // 教室管理 - 仅管理员
        {
          path: 'classroom/list',
          name: 'classroom-list',
          component: () => import('../views/classroom/List.vue'),
          meta: { requiresAuth: true, roles: ['admin'] }
        },
        {
          path: 'classroom/add',
          name: 'classroom-add',
          component: () => import('../views/classroom/Form.vue'),
          meta: { requiresAuth: true, roles: ['admin'] }
        },
        {
          path: 'classroom/edit/:id',
          name: 'classroom-edit',
          component: () => import('../views/classroom/Form.vue'),
          meta: { requiresAuth: true, roles: ['admin'] }
        },
        // 班级管理 - 仅管理员
        {
          path: 'class/list',
          name: 'class-list',
          component: () => import('../views/class/List.vue'),
          meta: { requiresAuth: true, roles: ['admin'] }
        },
        {
          path: 'class/add',
          name: 'class-add',
          component: () => import('../views/class/Form.vue'),
          meta: { requiresAuth: true, roles: ['admin'] }
        },
        {
          path: 'class/edit/:id',
          name: 'class-edit',
          component: () => import('../views/class/Form.vue'),
          meta: { requiresAuth: true, roles: ['admin'] }
        },
        // 课程管理 - 仅管理员
        {
          path: 'course/list',
          name: 'course-list',
          component: () => import('../views/course/List.vue'),
          meta: { requiresAuth: true, roles: ['admin'] }
        },
        {
          path: 'course/add',
          name: 'course-add',
          component: () => import('../views/course/Form.vue'),
          meta: { requiresAuth: true, roles: ['admin'] }
        },
        {
          path: 'course/edit/:id',
          name: 'course-edit',
          component: () => import('../views/course/Form.vue'),
          meta: { requiresAuth: true, roles: ['admin'] }
        },
        // 个人中心
        {
          path: 'profile',
          name: 'profile',
          component: () => import('../views/profile/Index.vue')
        }
      ]
    }
  ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userInfo = localStorage.getItem('userInfo')
  const user = userInfo ? JSON.parse(userInfo) : null

  // 如果访问登录页，直接放行
  if (to.path === '/login') {
    next()
    return
  }

  // 如果未登录，跳转到登录页
  if (!user || !user.role) {
    next('/login')
    return
  }

  // 检查路由权限
  const roles = to.meta.roles as string[] | undefined
  if (roles && !roles.includes(user.role)) {
    // 无权限，跳转到仪表盘
    next('/dashboard')
    return
  }

  next()
})

export default router
