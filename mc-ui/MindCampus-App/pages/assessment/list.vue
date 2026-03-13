<template>
  <view class="assessment-list-page">
    <view class="ambient-background"></view>

    <view class="glass-header" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="navbar-content">
        <view class="navbar-left" @tap="goBack">
          <view class="nav-icon-glass">
            <uni-icons type="left" size="22" color="#1D1D1F"></uni-icons>
          </view>
        </view>
        <view class="navbar-title">心理测评</view>
        <view class="navbar-right"></view>
      </view>
    </view>

    <view class="page-content">
      
      <view class="stats-section">
        <view class="glass-card stat-card stat-primary" @tap="scrollToSection('pending')">
          <view class="stat-glow"></view>
          <text class="stat-number cyan-text">{{ stats.pending }}</text>
          <text class="stat-label">待填问卷</text>
        </view>
        <view class="stat-group">
          <view class="glass-card stat-card stat-sub" @tap="scrollToSection('completed')">
            <text class="stat-number">{{ stats.completed }}</text>
            <text class="stat-label">已完成</text>
          </view>
          <view class="glass-card stat-card stat-sub">
            <text class="stat-number text-muted">{{ stats.total }}</text>
            <text class="stat-label">总计</text>
          </view>
        </view>
      </view>

      <view class="section pending-section" id="pending-section">
        <view class="section-header">
          <view class="header-left">
            <uni-icons type="compose" size="22" color="#1D1D1F"></uni-icons>
            <text class="section-title">待填任务</text>
          </view>
        </view>

        <view class="questionnaire-list">
          <view v-for="item in pendingList" :key="item.questionnaireId" 
                class="glass-card questionnaire-card"
                :class="{ 'expired-card': item.status === 'expired' }" 
                @tap="handleCardClick(item)">
            
            <view class="card-body">
              <text class="card-title">{{ item.title }}</text>
              <text class="card-desc">{{ item.description }}</text>
            </view>

            <view class="card-meta">
              <view class="meta-tag">
                <uni-icons type="list" size="14" color="#86868B"></uni-icons>
                <text class="meta-text">{{ item.questionCount }} 题</text>
              </view>
              <view class="meta-tag">
                <uni-icons type="clock" size="14" color="#86868B"></uni-icons>
                <text class="meta-text">约 {{ item.duration }} 分钟</text>
              </view>
              <view class="meta-tag">
                <uni-icons type="person" size="14" color="#86868B"></uni-icons>
                <text class="meta-text">{{ item.publisher }}</text>
              </view>
            </view>

            <view class="card-footer">
              <view class="deadline-box">
                <uni-icons type="calendar" size="14" :color="item.status === 'expired' ? '#A1A1A6' : '#FF9500'"></uni-icons>
                <text class="deadline-text" :class="{'text-expired': item.status === 'expired'}">
                  截止: {{ formatDate(item.deadline) }}
                </text>
              </view>
              
              <view class="action-btn" :class="item.status === 'expired' ? 'btn-expired' : 'btn-primary'">
                <text>{{ item.status === 'expired' ? '已截止' : '去填写' }}</text>
                <uni-icons v-if="item.status !== 'expired'" type="right" size="12" color="#2CB5A0"></uni-icons>
              </view>
            </view>
          </view>
        </view>

        <view v-if="pendingList.length === 0" class="empty-state glass-card">
          <uni-icons type="checkmarkempty" size="40" color="#2CB5A0"></uni-icons>
          <text class="empty-title">太棒了，任务全清！</text>
          <text class="empty-text">当前没有需要填写的测评问卷</text>
        </view>
      </view>

      <view class="section completed-section" id="completed-section">
        <view class="section-header">
          <view class="header-left">
            <uni-icons type="folder-add" size="22" color="#1D1D1F"></uni-icons>
            <text class="section-title">测评归档</text>
          </view>
        </view>

        <view class="questionnaire-list">
          <view v-for="item in completedList" :key="item.questionnaireId" class="glass-card questionnaire-card completed-card">
            
            <view class="card-body">
              <view class="title-row">
                <text class="card-title">{{ item.title }}</text>
                <view class="status-badge" :class="item.status === 'analyzing' ? 'badge-warning' : 'badge-success'">
                  {{ item.status === 'analyzing' ? '分析中' : '已完成' }}
                </view>
              </view>
            </view>

            <view class="card-meta">
              <view class="meta-tag">
                <uni-icons type="list" size="14" color="#86868B"></uni-icons>
                <text class="meta-text">{{ item.questionCount }} 题</text>
              </view>
              <view class="meta-tag">
                <uni-icons type="person" size="14" color="#86868B"></uni-icons>
                <text class="meta-text">{{ item.publisher }}</text>
              </view>
            </view>

            <view class="card-footer">
              <view class="completed-time">
                <uni-icons type="flag" size="14" color="#86868B"></uni-icons>
                <text class="time-text">完成于 {{ formatDate(item.completedTime) }}</text>
              </view>
              
              <view class="action-btn btn-secondary" @tap.stop="viewResult(item)" v-if="item.status !== 'analyzing'">
                <text>查看报告</text>
                <uni-icons type="right" size="12" color="#007AFF"></uni-icons>
              </view>
            </view>

          </view>
        </view>

        <view v-if="completedList.length === 0" class="empty-state glass-card">
          <uni-icons type="info" size="32" color="#86868B"></uni-icons>
          <text class="empty-text">暂无已完成的测评记录</text>
        </view>
      </view>

    </view>
  </view>
</template>

<script>
import {getStatistics, listQuestionnaires} from '@/api/assessment'

export default {
  data() {
    return {
      statusBarHeight: 0,
      loading: false,
      stats: {
        pending: 0,
        completed: 0,
        total: 0
      },
      // 待填问卷列表
      pendingList: [],
      // 已完成问卷列表
      completedList: []
    }
  },
  onLoad() {
    // 获取状态栏高度
    const systemInfo = uni.getSystemInfoSync()
    this.statusBarHeight = systemInfo.statusBarHeight || 0

    // 加载数据
    this.loadData()
  },
  onShow() {
    // 每次显示页面时刷新数据
    this.loadData()
  },
  methods: {
    // 加载数据
    async loadData() {
      try {
        this.loading = true

        // 并行请求统计数据和问卷列表
        const [statsRes, listRes] = await Promise.all([
          getStatistics(),
          listQuestionnaires()
        ])

        if (statsRes.code === 200) {
          this.stats = statsRes.data
        }

        if (listRes.code === 200) {
          console.log('问卷列表数据:', listRes.data)
          this.pendingList = listRes.data.pendingList || []
          this.completedList = listRes.data.completedList || []
          console.log('待填问卷:', this.pendingList)
          console.log('已完成问卷:', this.completedList)
        } else {
          console.error('获取问卷列表失败:', listRes)
          uni.showToast({
            title: listRes.msg || '获取问卷列表失败',
            icon: 'none'
          })
        }
      } catch (error) {
        console.error('加载数据失败:', error)
        uni.showToast({
          title: '加载失败，请稍后重试',
          icon: 'none'
        })
      } finally {
        this.loading = false
      }
    },

    // 返回上一页
    goBack() {
      uni.navigateBack()
    },

    // 滚动到指定区域
    scrollToSection(section) {
      const query = uni.createSelectorQuery().in(this)
      query.select(`#${section}-section`).boundingClientRect()
      query.exec((res) => {
        if (res[0]) {
          uni.pageScrollTo({
            scrollTop: res[0].top - 100,
            duration: 300
          })
        }
      })
    },

    // 处理卡片点击
    handleCardClick(item) {
      if (item.status === 'expired') {
        uni.showToast({
          title: '该问卷已截止',
          icon: 'none'
        })
        return
      }
      this.startQuestionnaire(item)
    },

    // 开始填写问卷
    startQuestionnaire(item) {
      if (!item || !item.questionnaireId) {
        uni.showToast({
          title: '问卷信息错误',
          icon: 'none'
        })
        return
      }

      console.log('准备跳转到答题页面，URL:', '/pages/assessment/detail?id=' + item.questionnaireId)
      uni.navigateTo({
        url: '/pages/assessment/detail?id=' + item.questionnaireId,
        success: function () {
          console.log('跳转成功')
        },
        fail: function (err) {
          console.error('跳转失败:', err)
          uni.showToast({
            title: '页面跳转失败',
            icon: 'none'
          })
        }
      })
    },

    // 查看结果
    viewResult(item) {
      uni.navigateTo({
        url: '/pages/assessment/result?id=' + item.resultId
      })
    },

    // 下拉刷新
    onPullDownRefresh() {
      this.loadData().then(() => {
        uni.stopPullDownRefresh()
      })
    },

    // 格式化日期
    formatDate(date) {
      if (!date) return ''
      const d = new Date(date)
      const year = d.getFullYear()
      const month = String(d.getMonth() + 1).padStart(2, '0')
      const day = String(d.getDate()).padStart(2, '0')
      return `${year}-${month}-${day}`
    }
  }
}
</script>

<style lang="scss" scoped>
/* ==================== iOS 风格核心变量 ==================== */
$ios-text-primary: #1D1D1F;
$ios-text-secondary: #86868B;
$ios-blue: #007AFF;
$ios-orange: #FF9500;
$theme-cyan: #2CB5A0;

.assessment-list-page {
  min-height: 100vh;
  position: relative;
  background-color: #F5F5F7;
  font-family: -apple-system, BlinkMacSystemFont, "SF Pro Text", "Helvetica Neue", Arial, sans-serif;
  padding-bottom: calc(80rpx + env(safe-area-inset-bottom));
}

/* --- 弥散光影背景 --- */
.ambient-background {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  z-index: 0;
  background-image: 
    radial-gradient(at 0% 0%, rgba(224, 242, 241, 0.8) 0px, transparent 50%),
    radial-gradient(at 100% 0%, rgba(255, 243, 224, 0.8) 0px, transparent 50%),
    radial-gradient(at 100% 100%, rgba(232, 234, 246, 0.8) 0px, transparent 50%),
    radial-gradient(at 0% 100%, rgba(240, 238, 245, 0.8) 0px, transparent 50%);
  pointer-events: none;
}

/* --- 毛玻璃通用类 --- */
.glass-card {
  background: rgba(255, 255, 255, 0.65);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.03);
  border-radius: 32rpx;
}

/* ==================== 顶部导航 (Sticky) ==================== */
.glass-header {
  position: sticky;
  top: 0;
  z-index: 100;
  background: rgba(245, 245, 247, 0.6);
  backdrop-filter: saturate(180%) blur(20px);
  -webkit-backdrop-filter: saturate(180%) blur(20px);
  border-bottom: 0.5px solid rgba(0, 0, 0, 0.05);
}

.navbar-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 88rpx;
  padding: 0 32rpx;
}

.navbar-left, .navbar-right {
  width: 80rpx;
  display: flex;
  align-items: center;
}

.nav-icon-glass {
  width: 64rpx;
  height: 64rpx;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.04);
  transition: transform 0.2s ease;
  &:active { transform: scale(0.9); opacity: 0.8; }
}

.navbar-title {
  font-size: 32rpx;
  font-weight: 600;
  color: $ios-text-primary;
  letter-spacing: 1rpx;
}

/* ==================== 页面滚动区 ==================== */
.page-content {
  position: relative;
  z-index: 1;
  padding-top: 30rpx;
}

/* ==================== 统计看板 ==================== */
.stats-section {
  padding: 0 32rpx 40rpx;
  display: flex;
  gap: 24rpx;
}

.stat-card {
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 32rpx;
  transition: transform 0.2s ease;
  
  &:active { transform: scale(0.96); }
}

/* 重点突出的主数据卡片 */
.stat-primary {
  flex: 1.2;
  position: relative;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.75);
}

.stat-glow {
  position: absolute;
  top: -40rpx;
  left: -40rpx;
  width: 140rpx;
  height: 140rpx;
  background: radial-gradient(circle, rgba(44, 181, 160, 0.15) 0%, transparent 70%);
  border-radius: 50%;
  pointer-events: none;
}

/* 侧边辅助数据区 */
.stat-group {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.stat-sub {
  flex: 1;
  padding: 20rpx 32rpx;
  align-items: center;
  flex-direction: row;
  justify-content: space-between;
}

.stat-number {
  font-size: 48rpx;
  font-weight: 700;
  color: $ios-text-primary;
  line-height: 1.2;
  font-family: "SF Pro Display", sans-serif;
}

.stat-sub .stat-number {
  font-size: 36rpx;
}

.cyan-text {
  color: $theme-cyan;
}

.text-muted {
  color: #A1A1A6;
}

.stat-label {
  font-size: 24rpx;
  color: $ios-text-secondary;
  font-weight: 500;
  margin-top: 4rpx;
}

.stat-sub .stat-label {
  margin-top: 0;
}

/* ==================== 列表通用头部 ==================== */
.section {
  padding: 0 32rpx 50rpx;
}

.section-header {
  display: flex;
  align-items: center;
  margin-bottom: 24rpx;
  padding-left: 8rpx;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.section-title {
  font-size: 36rpx;
  font-weight: 700;
  color: $ios-text-primary;
  letter-spacing: -0.5rpx;
}

/* ==================== 问卷卡片通用 ==================== */
.questionnaire-list {
  display: flex;
  flex-direction: column;
  gap: 28rpx;
}

.questionnaire-card {
  padding: 32rpx;
  display: flex;
  flex-direction: column;
  transition: all 0.25s cubic-bezier(0.2, 0.8, 0.2, 1);
  
  &:active {
    transform: scale(0.97);
    background: rgba(255, 255, 255, 0.85);
  }
}

.expired-card {
  opacity: 0.65;
  filter: grayscale(30%);
}

/* 卡片主体 */
.card-body {
  margin-bottom: 24rpx;
}

.title-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16rpx;
}

.card-title {
  font-size: 34rpx;
  font-weight: 600;
  color: $ios-text-primary;
  margin-bottom: 12rpx;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-desc {
  font-size: 26rpx;
  color: $ios-text-secondary;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 卡片元信息 (标签排版) */
.card-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 20rpx;
  margin-bottom: 24rpx;
  padding-bottom: 24rpx;
  border-bottom: 1px solid rgba(0, 0, 0, 0.04);
}

.meta-tag {
  display: flex;
  align-items: center;
  gap: 8rpx;
  background: rgba(0, 0, 0, 0.03);
  padding: 6rpx 16rpx;
  border-radius: 12rpx;
}

.meta-text {
  font-size: 22rpx;
  color: $ios-text-secondary;
  font-weight: 500;
}

/* 卡片底部操作栏 */
.card-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.deadline-box, .completed-time {
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.deadline-text {
  font-size: 24rpx;
  color: $ios-orange;
  font-weight: 500;
}

.text-expired {
  color: $ios-text-secondary;
}

.time-text {
  font-size: 24rpx;
  color: $ios-text-secondary;
  font-weight: 500;
}

/* 状态角标 */
.status-badge {
  padding: 4rpx 16rpx;
  border-radius: 12rpx;
  font-size: 22rpx;
  font-weight: 600;
  flex-shrink: 0;
}

.badge-success {
  background: rgba(44, 181, 160, 0.1);
  color: $theme-cyan;
}

.badge-warning {
  background: rgba(255, 149, 0, 0.1);
  color: $ios-orange;
}

/* 操作按钮 (iOS Tinted Button 风格) */
.action-btn {
  padding: 12rpx 28rpx;
  border-radius: 30rpx;
  font-size: 26rpx;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 6rpx;
}

.btn-primary {
  background: rgba(44, 181, 160, 0.12);
  color: $theme-cyan;
}

.btn-secondary {
  background: rgba(0, 122, 255, 0.1);
  color: $ios-blue;
}

.btn-expired {
  background: rgba(0, 0, 0, 0.05);
  color: $ios-text-secondary;
}

/* ==================== 空状态 ==================== */
.empty-state {
  padding: 80rpx 40rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
}

.empty-title {
  font-size: 32rpx;
  font-weight: 600;
  color: $ios-text-primary;
  margin: 24rpx 0 12rpx;
}

.empty-text {
  font-size: 26rpx;
  color: $ios-text-secondary;
}
</style>