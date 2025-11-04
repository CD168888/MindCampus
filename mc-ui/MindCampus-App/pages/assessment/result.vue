<template>
  <view class="assessment-result-page">
    <!-- 自定义导航栏 -->
    <view class="custom-navbar" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="navbar-content">
        <view class="navbar-left" @tap="goBack">
          <text class="back-icon">←</text>
        </view>
        <view class="navbar-title">测评结果</view>
        <view class="navbar-right"></view>
      </view>
    </view>

    <!-- 加载中 -->
    <view v-if="loading" class="loading-container">
      <uni-icons type="spinner-cycle" size="40" color="#6ee7b7"></uni-icons>
      <text class="loading-text">加载中...</text>
    </view>

    <!-- 内容区域 -->
    <view v-else-if="resultData" class="content-wrapper">
      <!-- 结果概览 -->
      <view class="result-overview">
        <view class="overview-icon" :class="'risk-' + (result.riskLevel || '低')">
          <text class="icon-text">{{ getRiskIcon(result.riskLevel) }}</text>
        </view>
        <view class="overview-title">测评完成</view>
        <view class="overview-subtitle">{{ result.questionnaireTitle }}</view>

        <view class="score-display">
          <view class="score-number">{{ result.totalScore }}</view>
          <view class="score-label">总得分</view>
        </view>

        <view class="risk-badge" :class="'risk-badge-' + (result.riskLevel || '低')">
          {{ getRiskText(result.riskLevel) }}
        </view>
      </view>

      <!-- 统计信息 -->
      <view class="statistics-section">
        <view class="stat-item">
          <view class="stat-value">{{ resultData.totalQuestions }}</view>
          <view class="stat-label">总题数</view>
        </view>
        <view class="stat-divider"></view>
        <view class="stat-item">
          <view class="stat-value">{{ resultData.correctCount }}</view>
          <view class="stat-label">正确题数</view>
        </view>
        <view class="stat-divider"></view>
        <view class="stat-item">
          <view class="stat-value">{{ resultData.accuracy }}</view>
          <view class="stat-label">准确率</view>
        </view>
      </view>

      <!-- AI分析 -->
      <view class="ai-analysis-section">
        <view class="section-header">
          <uni-icons type="star" size="20" color="#a78bfa"></uni-icons>
          <text class="section-title">AI 分析报告</text>
        </view>
        <view class="ai-content">
          <view v-if="result.aiStatus === '1' && result.aiAnalysis" class="ai-text">
            {{ result.aiAnalysis }}
          </view>
          <view v-else class="ai-pending">
            <uni-icons type="clock" size="24" color="#f59e0b"></uni-icons>
            <text class="ai-pending-text">AI分析结果生成中，请稍后查看</text>
          </view>
        </view>
      </view>

      <!-- 答题详情 -->
      <view class="answers-section">
        <view class="section-header">
          <uni-icons type="list" size="20" color="#6ee7b7"></uni-icons>
          <text class="section-title">答题详情</text>
        </view>

        <view class="answer-list">
          <view v-for="(answer, index) in resultData.answers" :key="answer.answerId" class="answer-card">
            <view class="answer-header">
              <view class="answer-number">第 {{ index + 1 }} 题</view>
              <view class="answer-score-info">
                <text class="score-obtained">{{ answer.obtainScore }}分</text>
                <text class="score-divider">/</text>
                <text class="score-total">{{ answer.score }}分</text>
              </view>
            </view>

            <view class="answer-question">
              <text class="question-text">{{ answer.content }}</text>
            </view>

            <!-- 选择题 -->
            <view v-if="answer.type === 'choice'" class="answer-details">
              <view class="detail-row">
                <text class="detail-label">标准答案：</text>
                <text class="detail-value answer-standard">{{ answer.standardAnswer }}</text>
              </view>
              <view class="detail-row">
                <text class="detail-label">你的答案：</text>
                <text class="detail-value answer-user"
                  :class="{ 'answer-correct': answer.isCorrect === 1, 'answer-wrong': answer.isCorrect === 0 }">
                  {{ answer.userAnswer }}
                  <text v-if="answer.isCorrect === 1" class="result-icon">✓</text>
                  <text v-else-if="answer.isCorrect === 0" class="result-icon">✗</text>
                </text>
              </view>
            </view>

            <!-- 简答题 -->
            <view v-else class="answer-details">
              <view class="detail-row detail-full">
                <text class="detail-label">你的答案：</text>
                <text class="detail-value answer-text">{{ answer.userAnswer }}</text>
              </view>
            </view>
          </view>
        </view>
      </view>

      <!-- 底部操作 -->
      <view class="bottom-actions">
        <button class="action-btn btn-secondary" @tap="goToList">返回列表</button>
        <button class="action-btn btn-primary" @tap="shareResult">分享结果</button>
      </view>
    </view>
  </view>
</template>

<script>
import {getResultDetail} from '@/api/assessment'

export default {
  data() {
    return {
      statusBarHeight: 0,
      loading: false,
      resultId: null,
      resultData: null,
      result: null
    }
  },
  onLoad(options) {
    // 获取状态栏高度
    const systemInfo = uni.getSystemInfoSync()
    this.statusBarHeight = systemInfo.statusBarHeight || 0

    // 获取结果ID
    this.resultId = options.id
    if (this.resultId) {
      this.loadResult()
    }
  },
  methods: {
    // 加载结果详情
    async loadResult() {
      try {
        this.loading = true
        const res = await getResultDetail(this.resultId)

        if (res.code === 200) {
          this.resultData = res.data
          this.result = res.data.result
        } else {
          uni.showToast({
            title: res.msg || '加载失败',
            icon: 'none'
          })
        }
      } catch (error) {
        console.error('加载结果失败:', error)
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

    // 返回列表
    goToList() {
      uni.navigateBack({
        delta: 2 // 返回两层，跳过答题页面
      })
    },

    // 分享结果
    shareResult() {
      uni.showToast({
        title: '分享功能开发中',
        icon: 'none'
      })
    },

    // 获取风险图标
    getRiskIcon(riskLevel) {
      const icons = {
        '低': '😊',
        '中': '😐',
        '高': '😟'
      }
      return icons[riskLevel] || '😊'
    },

    // 获取风险文本
    getRiskText(riskLevel) {
      const texts = {
        '低': '风险等级：低',
        '中': '风险等级：中',
        '高': '风险等级：高'
      }
      return texts[riskLevel] || '风险等级：低'
    }
  }
}
</script>

<style lang="scss" scoped>
@import "@/static/scss/theme.scss";

.assessment-result-page {
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

/* ==================== 加载中 ==================== */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 200rpx 0;
  gap: $spacing-md;
}

.loading-text {
  font-size: $font-sm;
  color: $text-tertiary;
}

/* ==================== 内容区域 ==================== */
.content-wrapper {
  padding: $spacing-lg;
  padding-bottom: 180rpx;
}

/* ==================== 结果概览 ==================== */
.result-overview {
  background: $bg-white;
  border-radius: $radius-xl;
  padding: $spacing-2xl $spacing-lg;
  margin-bottom: $spacing-md;
  box-shadow: $shadow-sm;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.overview-icon {
  width: 120rpx;
  height: 120rpx;
  border-radius: $radius-full;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: $spacing-md;

  &.risk-低 {
    background: linear-gradient(135deg, rgba(110, 231, 183, 0.2) 0%, rgba(167, 139, 250, 0.2) 100%);
  }

  &.risk-中 {
    background: linear-gradient(135deg, rgba(251, 191, 36, 0.2) 0%, rgba(251, 146, 60, 0.2) 100%);
  }

  &.risk-高 {
    background: linear-gradient(135deg, rgba(248, 113, 113, 0.2) 0%, rgba(239, 68, 68, 0.2) 100%);
  }
}

.icon-text {
  font-size: 80rpx;
}

.overview-title {
  font-size: $font-xl;
  font-weight: $font-bold;
  color: $text-primary;
  margin-bottom: $spacing-xs;
}

.overview-subtitle {
  font-size: $font-sm;
  color: $text-tertiary;
  margin-bottom: $spacing-lg;
}

.score-display {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: $spacing-md;
}

.score-number {
  font-size: 80rpx;
  font-weight: $font-bold;
  background: $gradient-primary;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  line-height: 1;
}

.score-label {
  font-size: $font-xs;
  color: $text-tertiary;
  margin-top: $spacing-xs;
}

.risk-badge {
  padding: 8rpx 24rpx;
  border-radius: $radius-full;
  font-size: $font-sm;
  font-weight: $font-semibold;

  &.risk-badge-低 {
    background: linear-gradient(135deg, rgba(110, 231, 183, 0.15) 0%, rgba(167, 139, 250, 0.15) 100%);
    color: $success-color;
  }

  &.risk-badge-中 {
    background: rgba(251, 191, 36, 0.15);
    color: #f59e0b;
  }

  &.risk-badge-高 {
    background: rgba(248, 113, 113, 0.15);
    color: #ef4444;
  }
}

/* ==================== 统计信息 ==================== */
.statistics-section {
  background: $bg-white;
  border-radius: $radius-xl;
  padding: $spacing-lg;
  margin-bottom: $spacing-md;
  box-shadow: $shadow-sm;
  display: flex;
  align-items: center;
  justify-content: space-around;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: $spacing-xs;
}

.stat-value {
  font-size: $font-2xl;
  font-weight: $font-bold;
  color: $text-primary;
}

.stat-label {
  font-size: $font-xs;
  color: $text-tertiary;
}

.stat-divider {
  width: 1rpx;
  height: 60rpx;
  background: $border-light;
}

/* ==================== AI分析 ==================== */
.ai-analysis-section {
  background: linear-gradient(135deg, rgba(167, 139, 250, 0.1) 0%, rgba(110, 231, 183, 0.1) 100%);
  border-radius: $radius-xl;
  padding: $spacing-lg;
  margin-bottom: $spacing-md;
  border: 2rpx solid rgba(167, 139, 250, 0.2);
}

.section-header {
  display: flex;
  align-items: center;
  gap: $spacing-sm;
  margin-bottom: $spacing-md;
}

.section-title {
  font-size: $font-base;
  font-weight: $font-bold;
  color: $text-primary;
}

.ai-content {
  padding: $spacing-md;
  background: $bg-white;
  border-radius: $radius-lg;
}

.ai-text {
  font-size: $font-sm;
  color: $text-secondary;
  line-height: $line-height-relaxed;
}

.ai-pending {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: $spacing-xl;
  color: #f59e0b;
}

.ai-pending-text {
  margin-top: $spacing-sm;
  font-size: $font-sm;
  color: #f59e0b;
}

/* ==================== 答题详情 ==================== */
.answers-section {
  margin-bottom: $spacing-md;
}

.answer-list {
  margin-top: $spacing-md;
  display: flex;
  flex-direction: column;
  gap: $spacing-sm;
}

.answer-card {
  background: $bg-white;
  border-radius: $radius-xl;
  padding: $spacing-lg;
  box-shadow: $shadow-sm;
}

.answer-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: $spacing-sm;
}

.answer-number {
  font-size: $font-sm;
  font-weight: $font-semibold;
  color: $primary-color;
  padding: 4rpx 12rpx;
  background: rgba(110, 231, 183, 0.15);
  border-radius: $radius-full;
}

.answer-score-info {
  font-size: $font-sm;
}

.score-obtained {
  font-weight: $font-bold;
  color: $primary-color;
}

.score-divider {
  color: $text-quaternary;
  margin: 0 4rpx;
}

.score-total {
  color: $text-tertiary;
}

.answer-question {
  margin-bottom: $spacing-md;
}

.question-text {
  font-size: $font-base;
  color: $text-primary;
  line-height: $line-height-relaxed;
}

.answer-details {
  padding-top: $spacing-sm;
  border-top: 1rpx solid $border-light;
}

.detail-row {
  display: flex;
  align-items: flex-start;
  margin-bottom: $spacing-xs;

  &.detail-full {
    flex-direction: column;
    gap: $spacing-xs;
  }

  &:last-child {
    margin-bottom: 0;
  }
}

.detail-label {
  font-size: $font-sm;
  color: $text-tertiary;
  min-width: 140rpx;
}

.detail-value {
  flex: 1;
  font-size: $font-sm;
  color: $text-secondary;
}

.answer-standard {
  font-weight: $font-semibold;
  color: $primary-color;
}

.answer-user {
  display: flex;
  align-items: center;
  gap: $spacing-xs;

  &.answer-correct {
    color: $success-color;
    font-weight: $font-semibold;
  }

  &.answer-wrong {
    color: #ef4444;
    font-weight: $font-semibold;
  }
}

.answer-text {
  line-height: $line-height-relaxed;
  padding: $spacing-sm;
  background: $bg-gray-50;
  border-radius: $radius-md;
}

.result-icon {
  font-weight: $font-bold;
}

/* ==================== 底部操作 ==================== */
.bottom-actions {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: $spacing-lg;
  background: $bg-white;
  box-shadow: 0 -2rpx 20rpx rgba(0, 0, 0, 0.05);
  display: flex;
  gap: $spacing-md;
}

.action-btn {
  flex: 1;
  height: 88rpx;
  border-radius: $radius-xl;
  font-size: $font-base;
  font-weight: $font-bold;
  border: none;
  transition: all $transition-base $ease-out;

  &:active {
    transform: translateY(-2rpx);
  }
}

.btn-secondary {
  background: $bg-gray-100;
  color: $text-secondary;

  &:active {
    background: $bg-gray-200;
  }
}

.btn-primary {
  background: $gradient-primary;
  color: $bg-white;
  box-shadow: $shadow-base;

  &:active {
    box-shadow: $shadow-lg;
  }
}
</style>
