<template>
  <div class="dashboard-container">
    <!-- 欢迎卡片 -->
    <el-card class="welcome-card" shadow="hover">
      <div class="welcome-content">
        <div class="welcome-left">
          <h2 class="welcome-title">欢迎回来，{{ userInfo.name }}</h2>
          <p class="welcome-subtitle">{{ getCurrentGreeting() }}，今天是{{ getCurrentDate() }}</p>
          <div class="welcome-stats">
            <div class="stat-item">
              <el-icon class="stat-icon"><User /></el-icon>
              <span class="stat-text">{{ userInfo.role }}</span>
            </div>
            <div class="stat-item">
              <el-icon class="stat-icon"><Location /></el-icon>
              <span class="stat-text">{{ userInfo.department }}</span>
            </div>
          </div>
        </div>
        <div class="welcome-right">
          <el-avatar :size="80" :src="userInfo.avatar">
            <el-icon><User /></el-icon>
          </el-avatar>
        </div>
      </div>
    </el-card>

    <!-- 数据统计卡片 -->
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

    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-row">
      <!-- 测评趋势 -->
      <el-col :xs="24" :lg="16">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="card-header-title">
                <el-icon><TrendCharts /></el-icon>
                测评趋势分析
              </span>
              <el-radio-group v-model="trendPeriod" size="small">
                <el-radio-button label="week">近7天</el-radio-button>
                <el-radio-button label="month">近30天</el-radio-button>
                <el-radio-button label="year">近一年</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div ref="trendChart" class="chart-container"></div>
        </el-card>
      </el-col>

      <!-- 风险等级分布 -->
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

    <!-- 待办事项和最新动态 -->
    <el-row :gutter="20" class="content-row">
      <!-- 待办事项 -->
      <el-col :xs="24" :lg="12">
        <el-card class="content-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="card-header-title">
                <el-icon><List /></el-icon>
                待办事项
              </span>
              <el-badge :value="todoList.filter(item => !item.completed).length" class="badge-item">
                <el-button size="small" text>查看全部</el-button>
              </el-badge>
            </div>
          </template>
          <div class="todo-list">
            <div v-for="(todo, index) in todoList" :key="index" class="todo-item" :class="{ completed: todo.completed }">
              <el-checkbox v-model="todo.completed" @change="handleTodoChange(index)">
                <span class="todo-text">{{ todo.text }}</span>
              </el-checkbox>
              <div class="todo-meta">
                <el-tag :type="todo.priority === 'high' ? 'danger' : todo.priority === 'medium' ? 'warning' : 'info'" size="small">
                  {{ getPriorityText(todo.priority) }}
                </el-tag>
                <span class="todo-time">{{ todo.time }}</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 最新动态 -->
      <el-col :xs="24" :lg="12">
        <el-card class="content-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="card-header-title">
                <el-icon><BellFilled /></el-icon>
                最新动态
              </span>
              <el-button size="small" text>更多</el-button>
            </div>
          </template>
          <el-timeline class="activity-timeline">
            <el-timeline-item
              v-for="(activity, index) in activities"
              :key="index"
              :timestamp="activity.time"
              :type="activity.type"
              placement="top"
            >
              <div class="activity-content">
                <p class="activity-title">{{ activity.title }}</p>
                <p class="activity-desc">{{ activity.desc }}</p>
              </div>
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
    </el-row>

    <!-- 快捷入口 -->
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
import {nextTick, onMounted, ref} from 'vue'
import * as echarts from 'echarts'
import {BellFilled, Grid, List, Location, PieChart, TrendCharts, User} from '@element-plus/icons-vue'

// 用户信息
const userInfo = ref({
  name: '张老师',
  role: '心理咨询师',
  department: '学生工作处',
  avatar: ''
})

// 统计数据
const statistics = ref([
  {
    type: 'primary',
    label: '待处理问卷',
    value: 28,
    change: '+12% 较上周',
    changeType: 'up',
    changeIcon: 'ArrowUp',
    icon: 'Document',
    gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
  },
  {
    type: 'success',
    label: '已完成测评',
    value: 156,
    change: '+8% 较上周',
    changeType: 'up',
    changeIcon: 'ArrowUp',
    icon: 'DataAnalysis',
    gradient: 'linear-gradient(135deg, #6ee7b7 0%, #10b981 100%)'
  },
  {
    type: 'warning',
    label: '待关注学生',
    value: 12,
    change: '-3% 较上周',
    changeType: 'down',
    changeIcon: 'ArrowDown',
    icon: 'User',
    gradient: 'linear-gradient(135deg, #fbbf24 0%, #f59e0b 100%)'
  },
  {
    type: 'danger',
    label: '高风险预警',
    value: 3,
    change: '-1人 较上周',
    changeType: 'down',
    changeIcon: 'ArrowDown',
    icon: 'BellFilled',
    gradient: 'linear-gradient(135deg, #f87171 0%, #ef4444 100%)'
  }
])

// 趋势周期
const trendPeriod = ref('week')

// 待办事项
const todoList = ref([
  {
    text: '审核新提交的心理测评问卷',
    priority: 'high',
    time: '今天 14:00',
    completed: false
  },
  {
    text: '跟进张三同学的心理咨询预约',
    priority: 'high',
    time: '今天 16:00',
    completed: false
  },
  {
    text: '完成本周心理健康报告',
    priority: 'medium',
    time: '明天',
    completed: false
  },
  {
    text: '准备下周心理讲座PPT',
    priority: 'low',
    time: '本周五',
    completed: false
  },
  {
    text: '回复学生咨询邮件',
    priority: 'medium',
    time: '今天',
    completed: true
  }
])

// 最新动态
const activities = ref([
  {
    title: '张三完成了焦虑测评',
    desc: '测评结果为中度焦虑，建议关注',
    time: '10分钟前',
    type: 'primary'
  },
  {
    title: '李四提交了心理咨询预约',
    desc: '预约时间：明天下午2点',
    time: '30分钟前',
    type: 'success'
  },
  {
    title: '王五的测评结果需要审核',
    desc: '抑郁量表测评已完成',
    time: '1小时前',
    type: 'warning'
  },
  {
    title: '系统通知',
    desc: '新的心理测评问卷已发布',
    time: '2小时前',
    type: 'info'
  },
  {
    title: '赵六完成了人格测试',
    desc: '测评结果正常',
    time: '3小时前',
    type: 'success'
  }
])

// 快捷入口
const quickEntries = ref([
  { label: '问卷管理', icon: 'Document', gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)', path: '/questionnaire/info' },
  { label: '测评结果', icon: 'DataAnalysis', gradient: 'linear-gradient(135deg, #6ee7b7 0%, #10b981 100%)', path: '/evaluation/result' },
  { label: '学生信息', icon: 'User', gradient: 'linear-gradient(135deg, #60a5fa 0%, #3b82f6 100%)', path: '/student/info' },
  { label: '咨询预约', icon: 'Calendar', gradient: 'linear-gradient(135deg, #fbbf24 0%, #f59e0b 100%)', path: '/counselor/info' },
  { label: '数据分析', icon: 'TrendCharts', gradient: 'linear-gradient(135deg, #a78bfa 0%, #8b5cf6 100%)', path: '#' },
  { label: '系统设置', icon: 'Setting', gradient: 'linear-gradient(135deg, #f87171 0%, #ef4444 100%)', path: '/system/config' }
])

// 图表实例
const trendChart = ref(null)
const riskChart = ref(null)
let trendChartInstance = null
let riskChartInstance = null

// 获取当前问候语
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

// 获取当前日期
function getCurrentDate() {
  const date = new Date()
  const week = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六']
  return `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日 ${week[date.getDay()]}`
}

// 获取优先级文本
function getPriorityText(priority) {
  const map = { high: '紧急', medium: '普通', low: '一般' }
  return map[priority] || '普通'
}

// 处理待办事项变化
function handleTodoChange(index) {
  console.log('Todo changed:', index, todoList.value[index])
}

// 处理快捷入口点击
function handleEntryClick(entry) {
  if (entry.path && entry.path !== '#') {
    // 这里可以使用 router.push(entry.path)
    console.log('Navigate to:', entry.path)
  } else {
    ElMessage.info('功能开发中')
  }
}

// 初始化趋势图表
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
      data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
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
        data: [45, 52, 48, 60, 55, 38, 42],
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#667eea' },
            { offset: 1, color: '#764ba2' }
          ])
        }
      },
      {
        name: '完成人数',
        type: 'bar',
        data: [42, 48, 45, 56, 52, 35, 39],
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#6ee7b7' },
            { offset: 1, color: '#10b981' }
          ])
        }
      },
      {
        name: '高风险人数',
        type: 'line',
        data: [3, 5, 2, 4, 3, 2, 1],
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

// 初始化风险等级分布图表
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
            value: 141, 
            name: '低风险',
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 1, 1, [
                { offset: 0, color: '#6ee7b7' },
                { offset: 1, color: '#10b981' }
              ])
            }
          },
          { 
            value: 12, 
            name: '中风险',
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 1, 1, [
                { offset: 0, color: '#fbbf24' },
                { offset: 1, color: '#f59e0b' }
              ])
            }
          },
          { 
            value: 3, 
            name: '高风险',
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 1, 1, [
                { offset: 0, color: '#f87171' },
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

// 监听窗口大小变化
function handleResize() {
  trendChartInstance?.resize()
  riskChartInstance?.resize()
}

// 生命周期
onMounted(() => {
  nextTick(() => {
    initTrendChart()
    initRiskChart()
    window.addEventListener('resize', handleResize)
  })
})
</script>

<style scoped lang="scss">
.dashboard-container {
  padding: 20px;
  background: #f3f4f6;
  min-height: calc(100vh - 84px);
}

/* 欢迎卡片 */
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
  color: #667eea;
}

.welcome-right {
  .el-avatar {
    border: 3px solid #f3f4f6;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }
}

/* 统计卡片 */
.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  border-radius: 12px;
  transition: all 0.3s ease;
  
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

/* 图表区域 */
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

/* 内容区域 */
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

/* 待办事项 */
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
  
  &.completed {
    opacity: 0.6;
    
    .todo-text {
      text-decoration: line-through;
    }
  }
  
  :deep(.el-checkbox) {
    flex: 1;
  }
}

.todo-text {
  font-size: 14px;
  color: #1f2937;
  margin-left: 8px;
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

/* 最新动态 */
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

/* 快捷入口 */
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

/* 响应式 */
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
