<template>
  <div class="page-container">
    <n-card title="调课管理" :bordered="false">
      <n-space :size="16" style="margin-bottom: 16px">
        <n-select
          v-model:value="filterStatus"
          placeholder="状态筛选"
          clearable
          style="width: 150px"
          :options="statusOptions"
          @update:value="loadData(1)"
        />
        <n-date-picker
          v-model:value="dateRange"
          type="daterange"
          clearable
          style="width: 300px"
          @update:value="loadData(1)"
        />
        <n-button type="primary" @click="handleAdd">
          <template #icon>
            <n-icon><add-outline /></n-icon>
          </template>
          申请调课
        </n-button>
      </n-space>

      <n-data-table
        :columns="columns"
        :data="adjustList"
        :loading="loading"
        :pagination="pagination"
        :bordered="false"
        remote
        @update:page="loadData"
      />
    </n-card>

    <!-- 申请调课对话框 -->
    <n-modal
      v-model:show="showModal"
      preset="card"
      title="申请调课"
      style="width: 600px"
      :bordered="false"
    >
      <n-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-placement="left"
        label-width="100"
      >
        <n-form-item label="班级" path="classId">
          <n-select
            v-model:value="formData.classId"
            placeholder="请选择班级"
            :options="classOptions"
            :loading="classLoading"
          />
        </n-form-item>
        <n-form-item label="原上课时间" path="originalTime">
          <n-input v-model:value="formData.originalTime" placeholder="如: 周一 09:00-10:00" />
        </n-form-item>
        <n-form-item label="调整原因" path="reason">
          <n-input
            v-model:value="formData.reason"
            type="textarea"
            :rows="3"
            placeholder="请输入调课原因"
          />
        </n-form-item>
        <n-form-item label="新上课日期" path="newDayOfWeek">
          <n-select
            v-model:value="formData.newDayOfWeek"
            placeholder="请选择星期"
            :options="dayOptions"
          />
        </n-form-item>
        <n-form-item label="新开始时间" path="newStartTime">
          <n-time-picker
            v-model:value="formData.newStartTime"
            format="HH:mm"
            placeholder="请选择开始时间"
            style="width: 100%"
          />
        </n-form-item>
        <n-form-item label="新结束时间">
          <n-time-picker
            v-model:value="formData.newEndTime"
            format="HH:mm"
            placeholder="可选"
            style="width: 100%"
          />
        </n-form-item>
      </n-form>
      <template #footer>
        <n-space justify="end">
          <n-button @click="showModal = false">取消</n-button>
          <n-button type="primary" :loading="submitting" @click="handleSubmit">提交申请</n-button>
        </n-space>
      </template>
    </n-modal>

    <!-- 详情对话框 -->
    <n-modal
      v-model:show="showDetailModal"
      preset="card"
      title="调课详情"
      style="width: 500px"
      :bordered="false"
    >
      <n-descriptions v-if="selectedAdjust" :column="1" bordered>
        <n-descriptions-item label="申请人">{{ selectedAdjust.applicantName }}</n-descriptions-item>
        <n-descriptions-item label="课程">{{ selectedAdjust.courseName }}</n-descriptions-item>
        <n-descriptions-item label="班级">{{ selectedAdjust.className }}</n-descriptions-item>
        <n-descriptions-item label="原时间">{{ selectedAdjust.originalTime }}</n-descriptions-item>
        <n-descriptions-item label="新时间">{{ formatNewTime(selectedAdjust) }}</n-descriptions-item>
        <n-descriptions-item label="调整原因">{{ selectedAdjust.reason }}</n-descriptions-item>
        <n-descriptions-item label="申请时间">{{ formatDateTime(selectedAdjust.applyTime) }}</n-descriptions-item>
        <n-descriptions-item label="状态">
          <n-tag :type="getStatusType(selectedAdjust.status)" :bordered="false">
            {{ selectedAdjust.status }}
          </n-tag>
        </n-descriptions-item>
        <n-descriptions-item v-if="selectedAdjust.rejectReason" label="拒绝原因">
          {{ selectedAdjust.rejectReason }}
        </n-descriptions-item>
      </n-descriptions>
      <template #footer>
        <n-space justify="end">
          <template v-if="isAdmin && selectedAdjust?.status === '待审核'">
            <n-button type="error" :loading="actionLoading" @click="openRejectModal">拒绝</n-button>
            <n-button type="success" :loading="actionLoading" @click="handleApprove">通过</n-button>
          </template>
          <n-button @click="showDetailModal = false">关闭</n-button>
        </n-space>
      </template>
    </n-modal>

    <!-- 拒绝原因对话框 -->
    <n-modal
      v-model:show="showRejectModal"
      preset="card"
      title="拒绝调课"
      style="width: 400px"
      :bordered="false"
    >
      <n-form label-placement="left" label-width="80">
        <n-form-item label="拒绝原因">
          <n-input
            v-model:value="rejectReason"
            type="textarea"
            :rows="3"
            placeholder="请输入拒绝原因"
          />
        </n-form-item>
      </n-form>
      <template #footer>
        <n-space justify="end">
          <n-button @click="showRejectModal = false">取消</n-button>
          <n-button type="error" :loading="actionLoading" @click="handleReject">确认拒绝</n-button>
        </n-space>
      </template>
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, h, onMounted } from 'vue'
import { useMessage, useDialog, NButton, NTag, NSpace } from 'naive-ui'
import { AddOutline } from '@vicons/ionicons5'
import dayjs from 'dayjs'
import { useUserStore } from '@/stores/user'
import { getAdjustments, createAdjustment, approveAdjustment, rejectAdjustment, deleteAdjustment } from '@/api/adjustment'
import { getClasses } from '@/api/class'

const message = useMessage()
const dialog = useDialog()
const userStore = useUserStore()

const isAdmin = computed(() => userStore.userInfo.role === 'admin')

const loading = ref(false)
const submitting = ref(false)
const actionLoading = ref(false)
const classLoading = ref(false)

const filterStatus = ref<string | null>(null)
const dateRange = ref<[number, number] | null>(null)

const adjustList = ref<any[]>([])
const classOptions = ref<any[]>([])

const showModal = ref(false)
const showDetailModal = ref(false)
const showRejectModal = ref(false)
const rejectReason = ref('')
const selectedAdjust = ref<any>(null)
const formRef = ref()

const formData = ref({
  classId: null as number | null,
  originalTime: '',
  reason: '',
  newDayOfWeek: null as number | null,
  newStartTime: null as number | null,
  newEndTime: null as number | null,
})

const rules = {
  classId: { required: true, type: 'number' as const, message: '请选择班级', trigger: 'change' },
  originalTime: { required: true, message: '请输入原上课时间', trigger: 'blur' },
  reason: { required: true, message: '请输入调课原因', trigger: 'blur' },
  newDayOfWeek: { required: true, type: 'number' as const, message: '请选择新的上课星期', trigger: 'change' },
  newStartTime: { required: true, type: 'number' as const, message: '请选择新的开始时间', trigger: 'change' },
}

const pagination = reactive({
  page: 1,
  pageSize: 10,
  itemCount: 0,
  showSizePicker: false,
})

const statusOptions = [
  { label: '待审核', value: '待审核' },
  { label: '已通过', value: '已通过' },
  { label: '已拒绝', value: '已拒绝' },
]

const dayOptions = [
  { label: '周一', value: 1 },
  { label: '周二', value: 2 },
  { label: '周三', value: 3 },
  { label: '周四', value: 4 },
  { label: '周五', value: 5 },
  { label: '周六', value: 6 },
  { label: '周日', value: 7 },
]

const columns = [
  { title: '申请人', key: 'applicantName', width: 100 },
  { title: '课程', key: 'courseName', width: 120 },
  { title: '班级', key: 'className', width: 150 },
  { title: '原时间', key: 'originalTime', width: 180 },
  {
    title: '新时间',
    key: 'newTime',
    width: 180,
    render: (row: any) => formatNewTime(row),
  },
  {
    title: '申请时间',
    key: 'applyTime',
    width: 160,
    render: (row: any) => formatDateTime(row.applyTime),
  },
  {
    title: '状态',
    key: 'status',
    width: 100,
    render: (row: any) =>
      h(NTag, { type: getStatusType(row.status), bordered: false }, { default: () => row.status }),
  },
  {
    title: '操作',
    key: 'actions',
    width: 120,
    fixed: 'right',
    render: (row: any) =>
      h(NSpace, {}, {
        default: () => [
          h(NButton, { size: 'small', type: 'primary', text: true, onClick: () => handleView(row) }, { default: () => '查看' }),
          h(NButton, {
            size: 'small', type: 'error', text: true,
            disabled: row.status !== '待审核',
            onClick: () => handleCancel(row),
          }, { default: () => '撤销' }),
        ],
      }),
  },
]

onMounted(() => loadData(1))

async function loadData(page: number = pagination.page) {
  loading.value = true
  pagination.page = page
  try {
    const params: Record<string, unknown> = {
      page,
      size: pagination.pageSize,
    }
    if (filterStatus.value) params.status = filterStatus.value
    if (dateRange.value) {
      params.startDate = dayjs(dateRange.value[0]).format('YYYY-MM-DD')
      params.endDate = dayjs(dateRange.value[1]).format('YYYY-MM-DD')
    }
    const res = await getAdjustments(params) as any
    adjustList.value = res.records ?? res.list ?? res
    pagination.itemCount = res.total ?? adjustList.value.length
  } catch (err: any) {
    message.error(err.message || '加载失败')
  } finally {
    loading.value = false
  }
}

async function loadClassOptions() {
  classLoading.value = true
  try {
    const params: Record<string, unknown> = { size: 100 }
    if (!isAdmin.value && userStore.userInfo.teacherId) {
      params.teacherId = userStore.userInfo.teacherId
    }
    const res = await getClasses(params) as any
    const list: any[] = res.records ?? res.list ?? res
    classOptions.value = list.map((c: any) => ({ label: c.name, value: c.id }))
  } catch { /* ignore */ } finally {
    classLoading.value = false
  }
}

function getStatusType(status: string) {
  return ({ '待审核': 'warning', '已通过': 'success', '已拒绝': 'error' } as Record<string, any>)[status] ?? 'default'
}

function formatNewTime(row: any) {
  const days = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
  const day = row.newDayOfWeek ? days[row.newDayOfWeek - 1] : ''
  const start = row.newStartTime ? String(row.newStartTime).slice(0, 5) : ''
  const end = row.newEndTime ? String(row.newEndTime).slice(0, 5) : ''
  if (!day && !start) return '-'
  return `${day} ${start}${end ? '-' + end : ''}`.trim()
}

function formatDateTime(dt: string) {
  return dt ? dayjs(dt).format('YYYY-MM-DD HH:mm') : '-'
}

async function handleAdd() {
  formData.value = { classId: null, originalTime: '', reason: '', newDayOfWeek: null, newStartTime: null, newEndTime: null }
  await loadClassOptions()
  showModal.value = true
}

function handleView(row: any) {
  selectedAdjust.value = row
  showDetailModal.value = true
}

function handleCancel(row: any) {
  dialog.warning({
    title: '确认撤销',
    content: '确定要撤销这条调课申请吗？',
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: async () => {
      try {
        await deleteAdjustment(row.id)
        message.success('撤销成功')
        loadData(pagination.page)
      } catch (err: any) {
        message.error(err.message || '撤销失败')
      }
    },
  })
}

async function handleApprove() {
  if (!selectedAdjust.value) return
  actionLoading.value = true
  try {
    await approveAdjustment(selectedAdjust.value.id)
    message.success('审核通过')
    showDetailModal.value = false
    loadData(pagination.page)
  } catch (err: any) {
    message.error(err.message || '操作失败')
  } finally {
    actionLoading.value = false
  }
}

function openRejectModal() {
  rejectReason.value = ''
  showRejectModal.value = true
}

async function handleReject() {
  if (!selectedAdjust.value) return
  if (!rejectReason.value.trim()) {
    message.warning('请输入拒绝原因')
    return
  }
  actionLoading.value = true
  try {
    await rejectAdjustment(selectedAdjust.value.id, rejectReason.value.trim())
    message.success('已拒绝')
    showRejectModal.value = false
    showDetailModal.value = false
    loadData(pagination.page)
  } catch (err: any) {
    message.error(err.message || '操作失败')
  } finally {
    actionLoading.value = false
  }
}

async function handleSubmit() {
  try {
    await formRef.value?.validate()
  } catch {
    return
  }
  submitting.value = true
  try {
    const payload: Record<string, unknown> = {
      classId: formData.value.classId,
      originalTime: formData.value.originalTime,
      reason: formData.value.reason,
      newDayOfWeek: formData.value.newDayOfWeek,
      newStartTime: formData.value.newStartTime ? dayjs(formData.value.newStartTime).format('HH:mm') : undefined,
      newEndTime: formData.value.newEndTime ? dayjs(formData.value.newEndTime).format('HH:mm') : undefined,
    }
    await createAdjustment(payload)
    message.success('申请提交成功')
    showModal.value = false
    loadData(1)
  } catch (err: any) {
    message.error(err.message || '提交失败')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.page-container {
  width: 100%;
}
</style>
