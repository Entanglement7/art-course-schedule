<template>
  <div class="page-container">
    <n-card title="智能排课" :bordered="false">
      <n-alert type="info" title="功能说明" style="margin-bottom: 16px">
        智能排课功能可以根据教师、学生、教室的可用时间自动生成最优课表，避免时间冲突。
      </n-alert>

      <n-space vertical :size="16">
        <n-card title="排课设置" :bordered="false" embedded>
          <n-form label-placement="left" label-width="120">
            <n-form-item label="排课方式">
              <n-radio-group v-model:value="scheduleType">
                <n-space>
                  <n-radio value="class">按班级排课</n-radio>
                  <n-radio value="teacher">按教师排课</n-radio>
                  <n-radio value="classroom">按教室排课</n-radio>
                </n-space>
              </n-radio-group>
            </n-form-item>

            <n-form-item label="排课周期">
              <n-select
                v-model:value="schedulePeriod"
                :options="periodOptions"
                placeholder="请选择排课周期"
                style="width: 200px"
              />
            </n-form-item>

            <n-form-item label="开始日期">
              <n-date-picker
                v-model:value="startDate"
                type="date"
                placeholder="请选择开始日期"
              />
            </n-form-item>
          </n-form>
        </n-card>

        <n-card title="排课结果预览" :bordered="false" embedded>
          <n-empty description="请先执行排课操作" v-if="!scheduleResult">
            <template #icon>
              <n-icon size="48" color="#D1D5DB">
                <calendar-outline />
              </n-icon>
            </template>
          </n-empty>

          <div v-else>
            <n-alert type="success" style="margin-bottom: 16px">
              排课成功！共生成 {{ scheduleResult.totalCourses }} 节课程
            </n-alert>
            <n-statistic-group>
              <n-statistic label="音乐课程" :value="scheduleResult.musicCourses" />
              <n-statistic label="舞蹈课程" :value="scheduleResult.danceCourses" />
              <n-statistic label="美术课程" :value="scheduleResult.artCourses" />
              <n-statistic label="冲突数量" :value="scheduleResult.conflicts" />
            </n-statistic-group>
          </div>
        </n-card>

        <n-space justify="end">
          <n-button @click="handleReset">重置</n-button>
          <n-button type="primary" :loading="loading" @click="handleSchedule">
            开始排课
          </n-button>
        </n-space>
      </n-space>
    </n-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useMessage } from 'naive-ui'
import { CalendarOutline } from '@vicons/ionicons5'

const message = useMessage()

const scheduleType = ref('class')
const schedulePeriod = ref('week')
const startDate = ref(Date.now())
const loading = ref(false)
const scheduleResult = ref<any>(null)

const periodOptions = [
  { label: '一周', value: 'week' },
  { label: '两周', value: 'two-weeks' },
  { label: '一个月', value: 'month' }
]

function handleSchedule() {
  loading.value = true

  // Mock排课
  setTimeout(() => {
    scheduleResult.value = {
      totalCourses: 45,
      musicCourses: 18,
      danceCourses: 15,
      artCourses: 12,
      conflicts: 0
    }
    loading.value = false
    message.success('排课成功！')
  }, 2000)
}

function handleReset() {
  scheduleResult.value = null
  scheduleType.value = 'class'
  schedulePeriod.value = 'week'
  startDate.value = Date.now()
}
</script>

<style scoped>
.page-container {
  width: 100%;
}
</style>
