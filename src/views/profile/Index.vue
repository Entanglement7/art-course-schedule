<template>
  <div class="page-container">
    <n-space vertical :size="24">
      <!-- 个人信息卡片 -->
      <n-card title="个人信息" :bordered="false">
        <n-space align="center" :size="24">
          <n-avatar
            round
            :size="100"
            :style="{
              background: userInfo.role === 'admin'
                ? '#7C3AED'
                : userInfo.role === 'teacher'
                ? '#EC4899'
                : '#3B82F6'
            }"
          >
            {{ userInfo.name.charAt(0) }}
          </n-avatar>

          <div style="flex: 1">
            <div style="font-size: 24px; font-weight: 600; margin-bottom: 12px">
              {{ userInfo.name }}
              <n-tag :type="getRoleType(userInfo.role)" :bordered="false" style="margin-left: 8px">
                {{ getRoleLabel(userInfo.role) }}
              </n-tag>
            </div>

            <n-descriptions :column="2" size="small">
              <n-descriptions-item label="账号">
                {{ userInfo.username }}
              </n-descriptions-item>
              <n-descriptions-item label="手机号">
                {{ userInfo.phone }}
              </n-descriptions-item>
              <n-descriptions-item label="邮箱">
                {{ userInfo.email }}
              </n-descriptions-item>
              <n-descriptions-item label="注册时间">
                {{ userInfo.registerTime }}
              </n-descriptions-item>
            </n-descriptions>

            <n-space style="margin-top: 16px">
              <n-button type="primary" @click="showEditModal = true">
                编辑资料
              </n-button>
              <n-button @click="showPasswordModal = true">
                修改密码
              </n-button>
            </n-space>
          </div>
        </n-space>
      </n-card>

      <!-- 教师专属信息 -->
      <n-card v-if="userInfo.role === 'teacher'" title="教学信息" :bordered="false">
        <n-space vertical :size="16">
          <n-descriptions :column="3" bordered>
            <n-descriptions-item label="专业特长">
              <n-space :size="4">
                <n-tag
                  v-for="specialty in teacherInfo.specialties"
                  :key="specialty"
                  type="info"
                  :bordered="false"
                >
                  {{ specialty }}
                </n-tag>
              </n-space>
            </n-descriptions-item>
            <n-descriptions-item label="任教班级">
              {{ teacherInfo.classCount }}个
            </n-descriptions-item>
            <n-descriptions-item label="学生人数">
              {{ teacherInfo.studentCount }}人
            </n-descriptions-item>
            <n-descriptions-item label="本周课时">
              {{ teacherInfo.weekCourseCount }}节
            </n-descriptions-item>
            <n-descriptions-item label="本月课时">
              {{ teacherInfo.monthCourseCount }}节
            </n-descriptions-item>
            <n-descriptions-item label="教龄">
              {{ teacherInfo.teachingYears }}年
            </n-descriptions-item>
          </n-descriptions>

          <div>
            <div style="font-weight: 600; margin-bottom: 12px">任教班级</div>
            <n-space :size="8">
              <n-tag
                v-for="cls in teacherInfo.classes"
                :key="cls"
                type="success"
                :bordered="false"
              >
                {{ cls }}
              </n-tag>
            </n-space>
          </div>
        </n-space>
      </n-card>

      <!-- 学生专属信息 -->
      <n-card v-if="userInfo.role === 'student'" title="学习信息" :bordered="false">
        <n-space vertical :size="16">
          <n-descriptions :column="3" bordered>
            <n-descriptions-item label="年龄">
              {{ studentInfo.age }}岁
            </n-descriptions-item>
            <n-descriptions-item label="性别">
              {{ studentInfo.gender }}
            </n-descriptions-item>
            <n-descriptions-item label="入学时间">
              {{ studentInfo.enrollDate }}
            </n-descriptions-item>
            <n-descriptions-item label="家长姓名">
              {{ studentInfo.parentName }}
            </n-descriptions-item>
            <n-descriptions-item label="联系电话">
              {{ studentInfo.parentPhone }}
            </n-descriptions-item>
            <n-descriptions-item label="报名课程">
              {{ studentInfo.courseCount }}门
            </n-descriptions-item>
          </n-descriptions>

          <div>
            <div style="font-weight: 600; margin-bottom: 12px">报名课程</div>
            <n-space :size="8">
              <n-tag
                v-for="course in studentInfo.courses"
                :key="course"
                type="info"
                :bordered="false"
              >
                {{ course }}
              </n-tag>
            </n-space>
          </div>

          <div>
            <div style="font-weight: 600; margin-bottom: 12px">学习统计</div>
            <n-grid :cols="4" :x-gap="16">
              <n-gi>
                <n-statistic label="本周课时" :value="studentInfo.weekCourseCount">
                  <template #suffix>节</template>
                </n-statistic>
              </n-gi>
              <n-gi>
                <n-statistic label="本月课时" :value="studentInfo.monthCourseCount">
                  <template #suffix>节</template>
                </n-statistic>
              </n-gi>
              <n-gi>
                <n-statistic label="累计课时" :value="studentInfo.totalCourseCount">
                  <template #suffix>节</template>
                </n-statistic>
              </n-gi>
              <n-gi>
                <n-statistic label="出勤率" :value="studentInfo.attendanceRate">
                  <template #suffix>%</template>
                </n-statistic>
              </n-gi>
            </n-grid>
          </div>
        </n-space>
      </n-card>

      <!-- 系统统计 -->
      <n-card v-if="userInfo.role === 'admin'" title="系统统计" :bordered="false">
        <n-grid :cols="4" :x-gap="16">
          <n-gi>
            <n-statistic label="学生总数" :value="adminStats.studentCount">
              <template #suffix>人</template>
            </n-statistic>
          </n-gi>
          <n-gi>
            <n-statistic label="教师总数" :value="adminStats.teacherCount">
              <template #suffix>人</template>
            </n-statistic>
          </n-gi>
          <n-gi>
            <n-statistic label="班级总数" :value="adminStats.classCount">
              <template #suffix">个</template>
            </n-statistic>
          </n-gi>
          <n-gi>
            <n-statistic label="教室总数" :value="adminStats.classroomCount">
              <template #suffix>间</template>
            </n-statistic>
          </n-gi>
        </n-grid>
      </n-card>
    </n-space>

    <!-- 编辑资料对话框 -->
    <n-modal
      v-model:show="showEditModal"
      preset="card"
      title="编辑资料"
      style="width: 500px"
      :bordered="false"
    >
      <n-form
        ref="editFormRef"
        :model="editFormData"
        label-placement="left"
        label-width="80"
      >
        <n-form-item label="姓名">
          <n-input v-model:value="editFormData.name" placeholder="请输入姓名" />
        </n-form-item>

        <n-form-item label="手机号">
          <n-input v-model:value="editFormData.phone" placeholder="请输入手机号" />
        </n-form-item>

        <n-form-item label="邮箱">
          <n-input v-model:value="editFormData.email" placeholder="请输入邮箱" />
        </n-form-item>
      </n-form>

      <template #footer>
        <n-space justify="end">
          <n-button @click="showEditModal = false">取消</n-button>
          <n-button type="primary" @click="handleEditSubmit">保存</n-button>
        </n-space>
      </template>
    </n-modal>

    <!-- 修改密码对话框 -->
    <n-modal
      v-model:show="showPasswordModal"
      preset="card"
      title="修改密码"
      style="width: 500px"
      :bordered="false"
    >
      <n-form
        ref="passwordFormRef"
        :model="passwordFormData"
        :rules="passwordRules"
        label-placement="left"
        label-width="100"
      >
        <n-form-item label="原密码" path="oldPassword">
          <n-input
            v-model:value="passwordFormData.oldPassword"
            type="password"
            placeholder="请输入原密码"
            show-password-on="click"
          />
        </n-form-item>

        <n-form-item label="新密码" path="newPassword">
          <n-input
            v-model:value="passwordFormData.newPassword"
            type="password"
            placeholder="请输入新密码"
            show-password-on="click"
          />
        </n-form-item>

        <n-form-item label="确认新密码" path="confirmPassword">
          <n-input
            v-model:value="passwordFormData.confirmPassword"
            type="password"
            placeholder="请再次输入新密码"
            show-password-on="click"
          />
        </n-form-item>
      </n-form>

      <template #footer>
        <n-space justify="end">
          <n-button @click="showPasswordModal = false">取消</n-button>
          <n-button type="primary" @click="handlePasswordSubmit">确定</n-button>
        </n-space>
      </template>
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useMessage } from 'naive-ui'
import { useUserStore } from '@/stores/user'

const message = useMessage()
const userStore = useUserStore()

const showEditModal = ref(false)
const showPasswordModal = ref(false)
const editFormRef = ref()
const passwordFormRef = ref()

// 用户信息 - 从store获取
const userInfo = reactive({
  name: '',
  username: '',
  phone: '',
  email: '',
  role: '',
  registerTime: ''
})

// 教师信息
const teacherInfo = reactive({
  specialties: ['钢琴', '声乐'],
  classCount: 4,
  studentCount: 32,
  weekCourseCount: 12,
  monthCourseCount: 48,
  teachingYears: 5,
  classes: ['钢琴基础A班', '钢琴基础B班', '钢琴进阶班', '声乐基础班']
})

// 学生信息
const studentInfo = reactive({
  age: 8,
  gender: '男',
  enrollDate: '2024-09-01',
  parentName: '明爸爸',
  parentPhone: '13900139001',
  courseCount: 2,
  courses: ['钢琴基础', '素描基础'],
  weekCourseCount: 3,
  monthCourseCount: 12,
  totalCourseCount: 48,
  attendanceRate: 95
})

// 管理员统计
const adminStats = reactive({
  studentCount: 156,
  teacherCount: 24,
  classCount: 18,
  classroomCount: 8
})

const editFormData = ref({
  name: '',
  phone: '',
  email: ''
})

const passwordFormData = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const passwordRules = {
  oldPassword: {
    required: true,
    message: '请输入原密码',
    trigger: 'blur'
  },
  newPassword: {
    required: true,
    message: '请输入新密码',
    trigger: 'blur'
  },
  confirmPassword: [
    {
      required: true,
      message: '请再次输入新密码',
      trigger: 'blur'
    },
    {
      validator: (rule: any, value: string) => {
        return value === passwordFormData.value.newPassword
      },
      message: '两次输入的密码不一致',
      trigger: 'blur'
    }
  ]
}

// 加载用户信息
onMounted(() => {
  loadUserInfo()
})

function loadUserInfo() {
  const user = userStore.userInfo

  // 根据角色设置不同的用户信息
  const userDataMap: Record<string, any> = {
    admin: {
      name: '管理员',
      username: 'admin',
      phone: '13800138000',
      email: 'admin@example.com',
      role: 'admin',
      registerTime: '2024-01-01'
    },
    teacher: {
      name: '张老师',
      username: 'teacher',
      phone: '13800138001',
      email: 'teacher@example.com',
      role: 'teacher',
      registerTime: '2024-02-01'
    },
    student: {
      name: '小明',
      username: 'student',
      phone: '13800138002',
      email: 'student@example.com',
      role: 'student',
      registerTime: '2024-09-01'
    }
  }

  const userData = userDataMap[user.role] || userDataMap.admin
  Object.assign(userInfo, userData)

  editFormData.value = {
    name: userInfo.name,
    phone: userInfo.phone,
    email: userInfo.email
  }
}

function getRoleType(role: string) {
  const typeMap: Record<string, any> = {
    admin: 'info',
    teacher: 'success',
    student: 'warning'
  }
  return typeMap[role] || 'default'
}

function getRoleLabel(role: string) {
  const labelMap: Record<string, string> = {
    admin: '管理员',
    teacher: '教师',
    student: '学生'
  }
  return labelMap[role] || role
}

function handleEditSubmit() {
  userInfo.name = editFormData.value.name
  userInfo.phone = editFormData.value.phone
  userInfo.email = editFormData.value.email
  message.success('资料更新成功')
  showEditModal.value = false
}

async function handlePasswordSubmit() {
  try {
    await passwordFormRef.value?.validate()
    message.success('密码修改成功')
    showPasswordModal.value = false
    passwordFormData.value = {
      oldPassword: '',
      newPassword: '',
      confirmPassword: ''
    }
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
