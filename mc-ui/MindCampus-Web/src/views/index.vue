<template>
  <div class="dashboard-container" v-loading="loading">
    <el-card class="welcome-card" shadow="hover">
      <div class="welcome-content">
        <div class="welcome-left">
          <h2 class="welcome-title">欢迎回来，{{ userStore.nickName || userStore.name }}</h2>
          <p class="welcome-subtitle">{{ getCurrentGreeting() }}，今天是{{ getCurrentDate() }}</p>
          <div class="welcome-stats">
            <div class="stat-item">
              <el-icon class="stat-icon"><User /></el-icon>
              <span class="stat-text">{{ getRoleName() }}</span>
            </div>
            <div class="stat-item">
              <el-icon class="stat-icon"><Location /></el-icon>
              <span class="stat-text">心理健康管理系统</span>
            </div>
          </div>
        </div>
        <div class="welcome-right">
          <el-avatar :size="80" :src="userStore.avatar">
            <el-icon><User /></el-icon>
          </el-avatar>
        </div>
      </div>
    </el-card>

    <el-row :gutter="20" class="stats-row">
      <el-col :xs="24" :sm="12" :md="6" v-for="(stat, index) in statistics" :key="index">
        <el-card class="stat-card" :class="'stat-card-' + stat.type" shadow="hover">
          <div class="stat-card-content">
            <div class="stat-card-icon" :style="{ background: stat.gradient }">
              <el-icon :size="28"><component :is="stat.icon" /></el-icon>
            </div>
            <div class="stat-card-info">
              <p class="stat-card-label">{{ stat.label }}</p>
              <h3 class="stat-card-value">{{ stat.value }}</h3>
              <p class="stat-card-change" :class="stat.changeType">
                <el-icon><component :is="stat.changeIcon" /></el-icon>
                {{ stat.change }}
              </p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <el-col :xs="24" :lg="16">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="card-header-title">
                <el-icon><TrendCharts /></el-icon>
                测评趋势分析
              </span>
              <el-radio-group v-model="trendPeriod" size="small" @change="loadTrendData">
                <el-radio-button label="week">近7天</el-radio-button>
                <el-radio-button label="month">近30天</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div ref="trendChart" class="chart-container"></div>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="8">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="card-header-title">
                <el-icon><PieChart /></el-icon>
                风险等级分布
              </span>
            </div>
          </template>
          <div ref="riskChart" class="chart-container-small"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="content-row">
      <el-col :xs="24" :lg="12">
        <el-card class="content-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="card-header-title">
                <el-icon><List /></el-icon>
                待办事项
              </span>
              <el-badge :value="todoList.length" class="badge-item" v-if="todoList.length > 0">
                <el-button size="small" text @click="goToNotification">查看全部</el-button>
              </el-badge>
            </div>
          </template>
          <div class="todo-list" v-if="todoList.length > 0">
            <div v-for="(todo, index) in todoList" :key="index" class="todo-item">
              <div class="todo-content">
                <span class="todo-text">{{ todo.content }}</span>
              </div>
              <div class="todo-meta">
                <el-tag :type="todo.priority === 'high' ? 'danger' : todo.priority === 'medium' ? 'warning' : 'info'" size="small">
                  {{ getPriorityText(todo.priority) }}
                </el-tag>
                <span class="todo-time">{{ formatTime(todo.createTime) }}</span>
              </div>
            </div>
          </div>
          <el-empty description="暂无待办事项" v-else :image-size="80" />
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="12">
        <el-card class="content-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="card-header-title">
                <el-icon><BellFilled /></el-icon>
                最新动态
              </span>
              <el-button size="small" text @click="goToEvaluationResult">更多</el-button>
            </div>
          </template>
          <el-timeline class="activity-timeline" v-if="activities.length > 0">
            <el-timeline-item
              v-for="(activity, index) in activities"
              :key="index"
              :timestamp="formatTime(activity.createTime)"
              :type="activity.activityType"
              placement="top"
            >
              <div class="activity-content">
                <p class="activity-title">{{ activity.title }}</p>
                <p class="activity-desc">{{ activity.description }}</p>
              </div>
            </el-timeline-item>
          </el-timeline>
          <el-empty description="暂无最新动态" v-else :image-size="80" />
        </el-card>
      </el-col>
    </el-row>

    <el-card class="quick-entry-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span class="card-header-title">
            <el-icon><Grid /></el-icon>
            快捷入口
          </span>
        </div>
      </template>
      <el-row :gutter="16" class="quick-entry-row">
        <el-col :xs="12" :sm="8" :md="6" :lg="4" v-for="(entry, index) in quickEntries" :key="index">
          <div class="quick-entry-item" @click="handleEntryClick(entry)">
            <div class="entry-icon" :style="{ background: entry.gradient }">
              <el-icon :size="32"><component :is="entry.icon" /></el-icon>
            </div>
            <p class="entry-label">{{ entry.label }}</p>
          </div>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup name="Index">
import { nextTick, onMounted, onUnmounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import { BellFilled, Grid, List, Location, PieChart, TrendCharts, User } from '@element-plus/icons-vue'
import useUserStore from '@/store/modules/user'
import { getStatistics, getTrendData, getRecentActivities, getTodoList } from '@/api/dashboard/dashboard'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)

const statistics = ref([
  {
    type: 'primary',
    label: '学生总数',
    value: 0,
    change: '已注册学生',
    changeType: 'up',
    changeIcon: 'User',
    icon: 'User',
    gradient: 'linear-gradient(135deg, #0f766e 0%, #14b8a6 100%)'
  },
  {
    type: 'success',
    label: '已完成测评',
    value: 0,
    change: '累计完成',
    changeType: 'up',
    changeIcon: 'ArrowUp',
    icon: 'DataAnalysis',
    gradient: 'linear-gradient(135deg, #059669 0%, #10b981 100%)'
  },
  {
    type: 'warning',
    label: '待关注学生',
    value: 0,
    change: '需重点关注',
    changeType: 'up',
    changeIcon: 'User',
    icon: 'User',
    gradient: 'linear-gradient(135deg, #d97706 0%, #f59e0b 100%)'
  },
  {
    type: 'danger',
    label: '高风险预警',
    value: 0,
    change: '高风险测评',
    changeType: 'up',
    changeIcon: 'BellFilled',
    icon: 'BellFilled',
    gradient: 'linear-gradient(135deg, #dc2626 0%, #ef4444 100%)'
  }
])

const trendPeriod = ref('week')
const todoList = ref([])
const activities = ref([])

const quickEntries = ref([
  { label: '问卷管理', icon: 'Document', gradient: 'linear-gradient(135deg, #0f766e 0%, #14b8a6 100%)', path: '/questionnaire/questionnaireinfo' },
  { label: '测评结果', icon: 'DataAnalysis', gradient: 'linear-gradient(135deg, #059669 0%, #10b981 100%)', path: '/evaluation/evaluationResult' },
  { label: '学生信息', icon: 'User', gradient: 'linear-gradient(135deg, #0369a1 0%, #0ea5e9 100%)', path: '/student/info' },
  { label: '干预通知', icon: 'Bell', gradient: 'linear-gradient(135deg, #d97706 0%, #f59e0b 100%)', path: '/intervention/notification' },
  { label: '风险配置', icon: 'Setting', gradient: 'linear-gradient(135deg, #7c3aed 0%, #a855f7 100%)', path: '/intervention/riskConfig' },
  { label: '系统设置', icon: 'Setting', gradient: 'linear-gradient(135deg, #dc2626 0%, #ef4444 100%)', path: '/system/config' }
])

const trendChart = ref(null)
const riskChart = ref(null)
let trendChartInstance = null
let riskChartInstance = null

let statisticsData = ref({
  totalStudents: 0,
  completedEvaluations: 0,
  attentionStudents: 0,
  highRiskCount: 0,
  lowRiskCount: 0,
  mediumRiskCount: 0,
  weekNewEvaluations: 0,
  lastWeekEvaluations: 0
})

function getCurrentGreeting() {
  const hour = new Date().getHours()
  if (hour < 6) return '凌晨好'
  if (hour < 9) return '早上好'
  if (hour < 12) return '上午好'
  if (hour < 14) return '中午好'
  if (hour < 18) return '下午好'
  if (hour < 22) return '晚上好'
  return '夜深了'
}

function getCurrentDate() {
  const date = new Date()
  const week = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六']
  return `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日 ${week[date.getDay()]}`
}

function getRoleName() {
  const roles = userStore.roles
  if (roles && roles.length > 0) {
    const roleMap = {
      'admin': '系统管理员',
      'counselor': '心理咨询师',
      'teacher': '教师'
    }
    return roleMap[roles[0]] || roles[0]
  }
  return '用户'
}

function getPriorityText(priority) {
  const map = { high: '紧急', medium: '普通', low: '一般' }
  return map[priority] || '普通'
}

function formatTime(time) {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
  if (diff < 604800000) return Math.floor(diff / 86400000) + '天前'
  
  return `${date.getMonth() + 1}月${date.getDate()}日`
}

function handleEntryClick(entry) {
  if (entry.path) {
    router.push(entry.path)
  }
}

function goToNotification() {
  router.push('/intervention/notification')
}

function goToEvaluationResult() {
  router.push('/evaluation/evaluationResult')
}

async function loadDashboardData() {
  loading.value = true
  try {
    const [statsRes, trendRes, activitiesRes, todosRes] = await Promise.all([
      getStatistics(),
      getTrendData(trendPeriod.value),
      getRecentActivities(5),
      getTodoList(5)
    ])
    
    if (statsRes.code === 200) {
      statisticsData.value = statsRes.data
      updateStatistics()
    }
    
    if (trendRes.code === 200) {
      updateTrendChart(trendRes.data)
    }
    
    if (activitiesRes.code === 200) {
      activities.value = activitiesRes.data
    }
    
    if (todosRes.code === 200) {
      todoList.value = todosRes.data
    }
  } catch (error) {
    console.error('加载首页数据失败:', error)
  } finally {
    loading.value = false
  }
}

function updateStatistics() {
  const data = statisticsData.value
  
  statistics.value[0].value = data.totalStudents || 0
  statistics.value[1].value = data.completedEvaluations || 0
  statistics.value[2].value = data.attentionStudents || 0
  statistics.value[3].value = data.highRiskCount || 0
  
  if (data.weekNewEvaluations && data.lastWeekEvaluations) {
    const changePercent = data.lastWeekEvaluations > 0 
      ? Math.round((data.weekNewEvaluations - data.lastWeekEvaluations) / data.lastWeekEvaluations * 100)
      : 100
    statistics.value[1].change = `${changePercent >= 0 ? '+' : ''}${changePercent}% 较上周`
  }
  
  updateRiskChart()
}

async function loadTrendData() {
  try {
    const res = await getTrendData(trendPeriod.value)
    if (res.code === 200) {
      updateTrendChart(res.data)
    }
  } catch (error) {
    console.error('加载趋势数据失败:', error)
  }
}

function initTrendChart() {
  if (!trendChart.value) return
  
  trendChartInstance = echarts.init(trendChart.value)
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    legend: {
      data: ['测评人数', '完成人数', '高风险人数']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: [],
      axisLine: {
        lineStyle: {
          color: '#e5e7eb'
        }
      },
      axisLabel: {
        color: '#6b7280'
      }
    },
    yAxis: {
      type: 'value',
      axisLine: {
        lineStyle: {
          color: '#e5e7eb'
        }
      },
      axisLabel: {
        color: '#6b7280'
      },
      splitLine: {
        lineStyle: {
          color: '#f3f4f6'
        }
      }
    },
    series: [
      {
        name: '测评人数',
        type: 'bar',
        data: [],
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#0f766e' },
            { offset: 1, color: '#14b8a6' }
          ])
        }
      },
      {
        name: '完成人数',
        type: 'bar',
        data: [],
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#059669' },
            { offset: 1, color: '#10b981' }
          ])
        }
      },
      {
        name: '高风险人数',
        type: 'line',
        data: [],
        smooth: true,
        itemStyle: {
          color: '#ef4444'
        },
        lineStyle: {
          width: 3
        }
      }
    ]
  }
  
  trendChartInstance.setOption(option)
}

function updateTrendChart(data) {
  if (!trendChartInstance || !data) return
  
  const xData = data.map(item => item.date)
  const evaluationData = data.map(item => item.evaluationCount || 0)
  const completedData = data.map(item => item.completedCount || 0)
  const highRiskData = data.map(item => item.highRiskCount || 0)
  
  trendChartInstance.setOption({
    xAxis: {
      data: xData
    },
    series: [
      { data: evaluationData },
      { data: completedData },
      { data: highRiskData }
    ]
  })
}

function initRiskChart() {
  if (!riskChart.value) return
  
  riskChartInstance = echarts.init(riskChart.value)
  
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      right: 10,
      top: 'center',
      data: ['低风险', '中风险', '高风险']
    },
    series: [
      {
        name: '风险等级',
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['35%', '50%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 20,
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: [
          { 
            value: 0, 
            name: '低风险',
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 1, 1, [
                { offset: 0, color: '#059669' },
                { offset: 1, color: '#10b981' }
              ])
            }
          },
          { 
            value: 0, 
            name: '中风险',
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 1, 1, [
                { offset: 0, color: '#d97706' },
                { offset: 1, color: '#f59e0b' }
              ])
            }
          },
          { 
            value: 0, 
            name: '高风险',
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 1, 1, [
                { offset: 0, color: '#dc2626' },
                { offset: 1, color: '#ef4444' }
              ])
            }
          }
        ]
      }
    ]
  }
  
  riskChartInstance.setOption(option)
}

function updateRiskChart() {
  if (!riskChartInstance) return
  
  const data = statisticsData.value
  riskChartInstance.setOption({
    series: [{
      data: [
        { value: data.lowRiskCount || 0, name: '低风险' },
        { value: data.mediumRiskCount || 0, name: '中风险' },
        { value: data.highRiskCount || 0, name: '高风险' }
      ]
    }]
  })
}

function handleResize() {
  trendChartInstance?.resize()
  riskChartInstance?.resize()
}

onMounted(() => {
  nextTick(() => {
    initTrendChart()
    initRiskChart()
    loadDashboardData()
    window.addEventListener('resize', handleResize)
  })
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  trendChartInstance?.dispose()
  riskChartInstance?.dispose()
})
</script>

<style scoped lang="scss">
.dashboard-container {
  padding: 20px;
  background: #f3f4f6;
  min-height: calc(100vh - 84px);
}

.welcome-card {
  margin-bottom: 20px;
  border-radius: 12px;
  
  :deep(.el-card__body) {
    padding: 24px;
  }
}

.welcome-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.welcome-left {
  flex: 1;
}

.welcome-title {
  font-size: 28px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 8px 0;
}

.welcome-subtitle {
  font-size: 14px;
  color: #6b7280;
  margin: 0 0 16px 0;
}

.welcome-stats {
  display: flex;
  gap: 24px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #6b7280;
  font-size: 14px;
}

.stat-icon {
  color: #0f766e;
}

.welcome-right {
  .el-avatar {
    border: 3px solid #f3f4f6;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  border-radius: 12px;
  transition: all 0.3s ease;
  margin-bottom: 10px;
  
  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 24px rgba(0, 0, 0, 0.1);
  }
  
  :deep(.el-card__body) {
    padding: 20px;
  }
}

.stat-card-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-card-icon {
  width: 64px;
  height: 64px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
}

.stat-card-info {
  flex: 1;
  min-width: 0;
}

.stat-card-label {
  font-size: 14px;
  color: #6b7280;
  margin: 0 0 8px 0;
}

.stat-card-value {
  font-size: 32px;
  font-weight: 700;
  color: #1f2937;
  margin: 0 0 8px 0;
}

.stat-card-change {
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 4px;
  
  &.up {
    color: #10b981;
  }
  
  &.down {
    color: #ef4444;
  }
}

.chart-row {
  margin-bottom: 20px;
}

.chart-card {
  border-radius: 12px;
  
  :deep(.el-card__header) {
    border-bottom: 1px solid #f3f4f6;
    padding: 16px 20px;
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header-title {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  display: flex;
  align-items: center;
  gap: 8px;
}

.chart-container {
  height: 360px;
  width: 100%;
}

.chart-container-small {
  height: 360px;
  width: 100%;
}

.content-row {
  margin-bottom: 20px;
}

.content-card {
  border-radius: 12px;
  height: 100%;
  
  :deep(.el-card__header) {
    border-bottom: 1px solid #f3f4f6;
    padding: 16px 20px;
  }
  
  :deep(.el-card__body) {
    padding: 20px;
  }
}

.badge-item {
  :deep(.el-badge__content) {
    background-color: #ef4444;
    border: none;
  }
}

.todo-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.todo-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background: #f9fafb;
  border-radius: 8px;
  transition: all 0.2s ease;
  
  &:hover {
    background: #f3f4f6;
  }
}

.todo-content {
  flex: 1;
}

.todo-text {
  font-size: 14px;
  color: #1f2937;
}

.todo-meta {
  display: flex;
  align-items: center;
  gap: 12px;
}

.todo-time {
  font-size: 12px;
  color: #9ca3af;
}

.activity-timeline {
  padding: 0;
  
  :deep(.el-timeline-item__wrapper) {
    padding-left: 28px;
  }
  
  :deep(.el-timeline-item__timestamp) {
    color: #9ca3af;
    font-size: 12px;
  }
}

.activity-content {
  .activity-title {
    font-size: 14px;
    color: #1f2937;
    font-weight: 500;
    margin: 0 0 4px 0;
  }
  
  .activity-desc {
    font-size: 12px;
    color: #6b7280;
    margin: 0;
  }
}

.quick-entry-card {
  border-radius: 12px;
  
  :deep(.el-card__header) {
    border-bottom: 1px solid #f3f4f6;
    padding: 16px 20px;
  }
  
  :deep(.el-card__body) {
    padding: 20px;
  }
}

.quick-entry-row {
  margin: -8px;
}

.quick-entry-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  
  &:hover {
    background: #f9fafb;
    transform: translateY(-4px);
    
    .entry-icon {
      transform: scale(1.1);
    }
  }
}

.entry-icon {
  width: 64px;
  height: 64px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin-bottom: 12px;
  transition: all 0.3s ease;
}

.entry-label {
  font-size: 14px;
  color: #1f2937;
  font-weight: 500;
  margin: 0;
}

@media (max-width: 768px) {
  .dashboard-container {
    padding: 12px;
  }
  
  .welcome-content {
    flex-direction: column;
    text-align: center;
  }
  
  .welcome-left {
    margin-bottom: 16px;
  }
  
  .welcome-stats {
    justify-content: center;
  }
  
  .stat-card-value {
    font-size: 24px;
  }
  
  .chart-container,
  .chart-container-small {
    height: 280px;
  }
}
</style>
