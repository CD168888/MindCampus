<template>
  <view class="assessment-list-page">
    <!-- 自定义导航栏 -->
    <view class="custom-navbar" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="navbar-content">
        <view class="navbar-left" @tap="goBack">
          <text class="back-icon">←</text>
        </view>
        <view class="navbar-title">心理测评</view>
        <view class="navbar-right"></view>
      </view>
    </view>

    <!-- 统计卡片 -->
    <view class="stats-section">
      <view class="stat-card stat-pending" @tap="scrollToSection('pending')">
        <view class="stat-number">{{ stats.pending }}</view>
        <view class="stat-label">待填问卷</view>
      </view>
      <view class="stat-card stat-completed" @tap="scrollToSection('completed')">
        <view class="stat-number">{{ stats.completed }}</view>
        <view class="stat-label">已完成</view>
      </view>
      <view class="stat-card stat-total">
        <view class="stat-number">{{ stats.total }}</view>
        <view class="stat-label">总计</view>
      </view>
    </view>

    <!-- 待填问卷 -->
    <view class="section pending-section" id="pending-section">
      <view class="section-header">
        <view class="section-indicator"></view>
        <text class="section-title">待填问卷</text>
      </view>

      <view class="questionnaire-list">
        <view v-for="item in pendingList" :key="item.questionnaireId" class="questionnaire-card pending-card"
          :class="{ 'expired-card': item.status === 'expired' }" @tap="handleCardClick(item)">
          <view class="card-body">
            <view class="card-title">{{ item.title }}</view>
            <view class="card-desc">{{ item.description }}</view>
          </view>

          <view class="card-meta">
            <view class="meta-item">
              <text class="meta-text">{{ item.questionCount }} 题</text>
            </view>
            <view class="meta-divider">·</view>
            <view class="meta-item">
              <text class="meta-text">约{{ item.duration }}分钟</text>
            </view>
            <view class="meta-divider">·</view>
            <view class="meta-item">
              <text class="meta-text">{{ item.publisher }}</text>
            </view>
          </view>

          <view class="card-footer">
            <view class="deadline">截止时间：{{ formatDate(item.deadline) }}</view>
            <view class="action-badge" :class="item.status === 'expired' ? 'expired-badge' : 'pending-badge'">
              {{ item.status === 'expired' ? '已截止' : '待填写' }}
            </view>
          </view>
        </view>
      </view>

      <view v-if="pendingList.length === 0" class="empty-state">
        <text class="empty-icon">✨</text>
        <text class="empty-text">暂无待填问卷</text>
      </view>
    </view>

    <!-- 已完成 -->
    <view class="section completed-section" id="completed-section">
      <view class="section-header">
        <view class="section-indicator completed-indicator"></view>
        <text class="section-title">已完成</text>
      </view>

      <view class="questionnaire-list">
        <view v-for="item in completedList" :key="item.questionnaireId" class="questionnaire-card completed-card">
          <view class="card-body">
            <view class="card-title">{{ item.title }}</view>
            <view class="completed-time">已于 {{ formatDate(item.completedTime) }} 完成</view>
          </view>

          <view class="card-meta">
            <view class="meta-item">
              <text class="meta-text">{{ item.questionCount }} 题</text>
            </view>
            <view class="meta-divider">·</view>
            <view class="meta-item">
              <text class="meta-text">得分 {{ item.score }}分</text>
            </view>
            <view class="meta-divider">·</view>
            <view class="meta-item">
              <text class="meta-text">{{ item.publisher }}</text>
            </view>
          </view>

          <view class="card-footer">
            <view class="action-badge" :class="item.status === 'analyzing' ? 'analyzing-badge' : 'completed-badge'">
              {{ item.status === 'analyzing' ? '待分析' : '已完成' }}
            </view>
            <view class="view-result-btn" @tap.stop="viewResult(item)">查看结果</view>
          </view>
        </view>
      </view>

      <view v-if="completedList.length === 0" class="empty-state">
        <text class="empty-icon">📋</text>
        <text class="empty-text">暂无已完成问卷</text>
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
      console.log('点击卡片 - questionnaireId:', item.questionnaireId)
      console.log('点击卡片 - title:', item.title)
      console.log('点击卡片 - resultId:', item.resultId)
      console.log('点击卡片 - 完整数据:', JSON.stringify(item))

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
@import "@/static/scss/theme.scss";

.assessment-list-page {
  min-height: 100vh;
  background: $bg-gray-50;
}

/* ==================== 自定义导航栏 ==================== */
.custom-navbar {
  background: $bg-white;
  box-shadow: $shadow-xs;
  position: sticky;
  top: 0;
  z-index: 999;
}

.navbar-content {
  height: 88rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 $spacing-lg;
}

.navbar-left,
.navbar-right {
  width: 80rpx;
}

.navbar-left {
  display: flex;
  align-items: center;
}

.back-icon {
  font-size: 44rpx;
  color: $text-primary;
  font-weight: $font-normal;
  line-height: 1;
}

.navbar-title {
  font-size: $font-xl;
  font-weight: $font-bold;
  color: $text-primary;
  letter-spacing: -0.5rpx;
}

/* ==================== 统计卡片 ==================== */
.stats-section {
  padding: $spacing-lg;
  display: flex;
  gap: $spacing-md;
}

.stat-card {
  flex: 1;
  background: $bg-white;
  border-radius: $radius-xl;
  padding: $spacing-lg $spacing-md;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: $spacing-xs;
  box-shadow: $shadow-sm;
  transition: all $transition-base $ease-out;
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 3rpx;
  }

  &.stat-pending::before {
    background: $gradient-primary;
  }

  &.stat-completed::before {
    background: $gradient-music;
  }

  &.stat-total::before {
    background: linear-gradient(135deg, #fda4af 0%, #6ee7b7 100%);
  }

  &:active {
    transform: translateY(-2rpx);
    box-shadow: $shadow-base;
  }
}

.stat-number {
  font-size: $font-3xl;
  font-weight: $font-bold;
  background: $gradient-primary;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  line-height: 1.2;
}

.stat-completed .stat-number {
  background: $gradient-music;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.stat-total .stat-number {
  background: linear-gradient(135deg, #fda4af 0%, #6ee7b7 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.stat-label {
  font-size: $font-xs;
  color: $text-tertiary;
  font-weight: $font-medium;
}

/* ==================== 列表区域 ==================== */
.section {
  padding: 0 $spacing-lg $spacing-lg;
}

.section-header {
  display: flex;
  align-items: center;
  gap: $spacing-sm;
  margin-bottom: $spacing-md;
  padding-left: $spacing-xs;
}

.section-indicator {
  width: 6rpx;
  height: 28rpx;
  background: $gradient-primary;
  border-radius: $radius-full;
}

.completed-indicator {
  background: $gradient-music;
}

.section-title {
  font-size: $font-lg;
  font-weight: $font-bold;
  color: $text-primary;
  letter-spacing: -0.5rpx;
}

/* ==================== 问卷卡片 ==================== */
.questionnaire-list {
  display: flex;
  flex-direction: column;
  gap: $spacing-sm;
}

.questionnaire-card {
  background: $bg-white;
  border-radius: $radius-xl;
  padding: $spacing-md;
  box-shadow: $shadow-sm;
  transition: all $transition-base $ease-out;
  border: 1rpx solid transparent;

  &:active {
    transform: translateY(-2rpx);
    box-shadow: $shadow-base;
  }
}

.pending-card {
  position: relative;
  padding-left: calc($spacing-md + 4rpx);

  &::before {
    content: '';
    position: absolute;
    left: 0;
    top: 0;
    bottom: 0;
    width: 4rpx;
    background: $gradient-primary;
    border-radius: $radius-full 0 0 $radius-full;
  }

  &:active {
    box-shadow: $shadow-primary;
  }
}

.completed-card {
  position: relative;

  // 透明蒙版效果
  &::after {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(255, 255, 255, 0.5);
    border-radius: $radius-xl;
    pointer-events: none;
    z-index: 1;
  }

  // 确保内容在蒙版之上
  .card-body,
  .card-meta,
  .card-footer {
    position: relative;
    z-index: 2;
  }
}

/* 卡片主体 */
.card-body {
  margin-bottom: $spacing-sm;
}

.card-title {
  font-size: $font-base;
  font-weight: $font-bold;
  color: $text-primary;
  margin-bottom: $spacing-xs;
  letter-spacing: -0.5rpx;
}

.card-desc {
  font-size: $font-xs;
  color: $text-tertiary;
  line-height: $line-height-normal;
}

.completed-time {
  font-size: $font-xs;
  color: $text-quaternary;
  margin-top: $spacing-xs;
}

/* 卡片元信息 */
.card-meta {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: $spacing-sm;
  margin-bottom: $spacing-sm;
  padding: $spacing-sm 0;
  border-top: 1rpx solid $border-light;
  border-bottom: 1rpx solid $border-light;
}

.meta-item {
  display: flex;
  align-items: center;
}

.meta-divider {
  font-size: $font-xs;
  color: $text-quaternary;
  font-weight: $font-normal;
}

.meta-text {
  font-size: $font-xs;
  color: $text-tertiary;
  font-weight: $font-normal;
}

/* 卡片底部 */
.card-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.deadline {
  font-size: $font-xs;
  color: $text-quaternary;
}

.action-badge {
  padding: 6rpx 16rpx;
  border-radius: $radius-full;
  font-size: $font-xs;
  font-weight: $font-semibold;
  letter-spacing: 0.5rpx;
}

.pending-badge {
  background: linear-gradient(135deg, rgba(110, 231, 183, 0.15) 0%, rgba(167, 139, 250, 0.15) 100%);
  color: $primary-color;
}

.completed-badge {
  background: rgba(16, 185, 129, 0.1);
  color: $success-color;
}

.expired-badge {
  background: rgba(156, 163, 175, 0.1);
  color: #9ca3af;
}

.analyzing-badge {
  background: rgba(251, 191, 36, 0.1);
  color: #f59e0b;
}

.expired-card {
  opacity: 0.6;
  pointer-events: none;
}

.view-result-btn {
  padding: 6rpx 16rpx;
  background: $gradient-primary;
  border-radius: $radius-full;
  font-size: $font-xs;
  font-weight: $font-semibold;
  color: $bg-white;
  letter-spacing: 0.5rpx;
  box-shadow: $shadow-xs;
  transition: all $transition-base $ease-out;
  position: relative;
  z-index: 3;

  &:active {
    opacity: $opacity-active;
    transform: scale(0.95);
  }
}

/* ==================== 空状态 ==================== */
.empty-state {
  padding: $spacing-3xl $spacing-lg;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: $spacing-md;
}

.empty-icon {
  font-size: 80rpx;
  opacity: 0.5;
}

.empty-text {
  font-size: $font-sm;
  color: $text-quaternary;
  font-weight: $font-medium;
}
</style>
