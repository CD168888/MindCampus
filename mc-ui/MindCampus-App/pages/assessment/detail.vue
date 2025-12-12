<template>
  <view class="assessment-detail-page">
    <!-- 自定义导航栏 -->
    <view class="custom-navbar" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="navbar-content">
        <view class="navbar-left" @tap="goBack">
          <text class="back-icon">←</text>
        </view>
        <view class="navbar-title">问卷详情</view>
        <view class="navbar-right"></view>
      </view>
    </view>

    <!-- 加载中 -->
    <view v-if="loading" class="loading-container">
      <uni-icons type="spinner-cycle" size="40" color="#6ee7b7"></uni-icons>
      <text class="loading-text">加载中...</text>
    </view>

    <!-- 内容区域 -->
    <view v-else-if="questionnaire" class="content-wrapper">
      <!-- 问卷头部 -->
      <view class="questionnaire-header">
        <view class="header-title">{{ questionnaire.title }}</view>
        <view class="header-desc">{{ questionnaire.description }}</view>
        <view class="header-meta">
          <view class="meta-item">
            <uni-icons type="compose" size="16" color="#a78bfa"></uni-icons>
            <text class="meta-text">{{ questions.length }} 题</text>
          </view>
          <view class="meta-item">
            <uni-icons type="clock" size="16" color="#6ee7b7"></uni-icons>
            <text class="meta-text">约 {{ questions.length }} 分钟</text>
          </view>

        </view>
      </view>

      <!-- 答题进度 -->
      <view class="progress-section">
        <view class="progress-header">
          <text class="progress-title">答题进度</text>
          <text class="progress-count">{{ answeredCount }} / {{ questions.length }}</text>
        </view>
        <view class="progress-bar">
          <view class="progress-fill" :style="{ width: progressPercent + '%' }"></view>
        </view>
      </view>

      <!-- 题目列表 -->
      <view class="questions-section">
        <view v-for="(question, index) in questions" :key="question.questionId" class="question-card">
          <view class="question-header">
            <view class="question-number">第 {{ index + 1 }} 题</view>
            <view v-if="answers[question.questionId]" class="question-status">
              <uni-icons type="checkmarkempty" size="18" color="#10b981"></uni-icons>
            </view>
          </view>

          <view class="question-content">
            <text class="question-text">{{ question.content }}</text>
          </view>

          <!-- 选择题 -->
          <view v-if="question.type === 'choice'" class="options-list">
            <view
              v-for="(option, optionKey) in parseOptions(question.options)"
              :key="optionKey"
              class="option-item"
              :class="{ 'option-selected': answers[question.questionId] === optionKey }"
              @tap="selectOption(question.questionId, optionKey)"
            >
              <view class="option-radio">
                <view v-if="answers[question.questionId] === optionKey" class="option-radio-inner"></view>
              </view>
              <text class="option-label">{{ optionKey }}. {{ option }}</text>
            </view>
          </view>

          <!-- 简答题 -->
          <view v-else class="answer-input">
            <textarea
              v-model="answers[question.questionId]"
              placeholder="请输入你的答案..."
              :maxlength="500"
              class="answer-textarea"
              @input="onInputAnswer(question.questionId, $event)"
            />
            <text class="answer-count">{{ (answers[question.questionId] || '').length }}/500</text>
          </view>
        </view>
      </view>

      <!-- 提交按钮 -->
      <view class="submit-section">
        <button class="submit-btn" :disabled="!isAllAnswered" @tap="submitAnswers">
          {{ isAllAnswered ? '提交问卷' : `还有 ${questions.length - answeredCount} 题未作答` }}
        </button>
      </view>
    </view>
  </view>
</template>

<script>
import {getQuestionnaireDetail, submitAnswer} from '@/api/assessment'

export default {
  data() {
    return {
      statusBarHeight: 0,
      loading: false,
      questionnaireId: null,
      questionnaire: null,
      questions: [],
      answers: {}, // { questionId: answer }
    }
  },
  computed: {
    // 已回答数量
    answeredCount() {
      return Object.keys(this.answers).filter(key => {
        const answer = this.answers[key]
        return answer && answer.toString().trim() !== ''
      }).length
    },
    // 进度百分比
    progressPercent() {
      if (this.questions.length === 0) return 0
      return Math.floor((this.answeredCount / this.questions.length) * 100)
    },
    // 是否全部作答
    isAllAnswered() {
      return this.answeredCount === this.questions.length
    }
  },
  onLoad(options) {
    // 获取状态栏高度
    const systemInfo = uni.getSystemInfoSync()
    this.statusBarHeight = systemInfo.statusBarHeight || 0

    // 获取问卷ID
    this.questionnaireId = options.id
    if (this.questionnaireId) {
      this.loadQuestionnaire()
    }
  },
  methods: {
    // 加载问卷详情
    async loadQuestionnaire() {
      try {
        this.loading = true
        console.log('开始加载问卷，ID:', this.questionnaireId)
        const res = await getQuestionnaireDetail(this.questionnaireId)
        console.log('问卷详情响应:', res)
        
        if (res.code === 200) {
          this.questionnaire = res.data
          this.questions = res.data.questions || []
          console.log('问卷信息:', this.questionnaire)
          console.log('题目列表:', this.questions)
        } else {
          console.error('加载问卷失败:', res)
          uni.showToast({
            title: res.msg || '加载失败',
            icon: 'none'
          })
        }
      } catch (error) {
        console.error('加载问卷异常:', error)
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
      if (this.answeredCount > 0) {
        uni.showModal({
          title: '提示',
          content: '当前答题尚未提交，确定要离开吗？',
          success: (res) => {
            if (res.confirm) {
              uni.navigateBack()
            }
          }
        })
      } else {
        uni.navigateBack()
      }
    },

    // 解析选项
    parseOptions(optionsStr) {
      try {
        return JSON.parse(optionsStr || '{}')
      } catch (e) {
        return {}
      }
    },

    // 选择选项
    selectOption(questionId, optionKey) {
      this.$set(this.answers, questionId, optionKey)
    },

    // 输入答案
    onInputAnswer(questionId, event) {
      const value = event.detail.value
      this.$set(this.answers, questionId, value)
    },

    // 提交答案
    async submitAnswers() {
      if (!this.isAllAnswered) {
        uni.showToast({
          title: '请完成所有题目',
          icon: 'none'
        })
        return
      }

      // 确认提交
      uni.showModal({
        title: '确认提交',
        content: '提交后将无法修改，确定要提交吗？',
        success: async (res) => {
          if (res.confirm) {
            await this.doSubmit()
          }
        }
      })
    },

    // 执行提交
    async doSubmit() {
      try {
        uni.showLoading({
          title: '提交中...',
          mask: true
        })

        // 构建提交数据
        const answerList = Object.keys(this.answers).map(questionId => ({
          questionId: parseInt(questionId),
          userAnswer: this.answers[questionId]
        }))

        const data = {
          questionnaireId: parseInt(this.questionnaireId),
          answers: answerList
        }
        
        console.log('提交答题数据:', data)
        const res = await submitAnswer(data)
        console.log('提交答题响应:', res)

        if (res.code === 200) {
          const resultId = res.data
          uni.hideLoading()
          uni.showToast({
            title: '提交成功',
            icon: 'success'
          })

          // 延迟跳转到结果页面
          setTimeout(() => {
            uni.redirectTo({
              url: '/pages/assessment/result?id=' + resultId
            })
          }, 1500)
        } else {
          uni.hideLoading()
          uni.showToast({
            title: res.msg || '提交失败',
            icon: 'none'
          })
        }
      } catch (error) {
        console.error('提交失败:', error)
        uni.hideLoading()
        uni.showToast({
          title: '提交失败，请稍后重试',
          icon: 'none'
        })
      }
    }
  }
}
</script>

<style lang="scss" scoped>
@import "@/static/scss/theme.scss";

.assessment-detail-page {
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

/* ==================== 问卷头部 ==================== */
.questionnaire-header {
  background: $bg-white;
  border-radius: $radius-xl;
  padding: $spacing-lg;
  margin-bottom: $spacing-md;
  box-shadow: $shadow-sm;
}

.header-title {
  font-size: $font-xl;
  font-weight: $font-bold;
  color: $text-primary;
  margin-bottom: $spacing-sm;
  letter-spacing: -0.5rpx;
}

.header-desc {
  font-size: $font-sm;
  color: $text-tertiary;
  line-height: $line-height-relaxed;
  margin-bottom: $spacing-md;
}

.header-meta {
  display: flex;
  align-items: center;
  gap: $spacing-lg;
  padding-top: $spacing-sm;
  border-top: 1rpx solid $border-light;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: $spacing-xs;
}

.meta-text {
  font-size: $font-xs;
  color: $text-secondary;
}

/* ==================== 答题进度 ==================== */
.progress-section {
  background: $bg-white;
  border-radius: $radius-xl;
  padding: $spacing-lg;
  margin-bottom: $spacing-md;
  box-shadow: $shadow-sm;
}

.progress-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: $spacing-sm;
}

.progress-title {
  font-size: $font-base;
  font-weight: $font-semibold;
  color: $text-primary;
}

.progress-count {
  font-size: $font-sm;
  font-weight: $font-semibold;
  background: $gradient-primary;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.progress-bar {
  height: 16rpx;
  background: $bg-gray-100;
  border-radius: $radius-full;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: $gradient-primary;
  border-radius: $radius-full;
  transition: width $transition-base $ease-out;
}

/* ==================== 题目卡片 ==================== */
.questions-section {
  display: flex;
  flex-direction: column;
  gap: $spacing-md;
}

.question-card {
  background: $bg-white;
  border-radius: $radius-xl;
  padding: $spacing-lg;
  box-shadow: $shadow-sm;
}

.question-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: $spacing-sm;
}

.question-number {
  font-size: $font-sm;
  font-weight: $font-semibold;
  color: $primary-color;
  padding: 4rpx 12rpx;
  background: rgba(110, 231, 183, 0.15);
  border-radius: $radius-full;
}

.question-status {
  display: flex;
  align-items: center;
}

.question-content {
  display: flex;
  align-items: flex-start;
  gap: $spacing-xs;
  margin-bottom: $spacing-md;
}

.question-text {
  flex: 1;
  font-size: $font-base;
  color: $text-primary;
  line-height: $line-height-relaxed;
}

.question-score {
  font-size: $font-xs;
  color: $text-tertiary;
  white-space: nowrap;
}

/* ==================== 选择题选项 ==================== */
.options-list {
  display: flex;
  flex-direction: column;
  gap: $spacing-sm;
}

.option-item {
  display: flex;
  align-items: center;
  gap: $spacing-sm;
  padding: $spacing-md;
  background: $bg-gray-50;
  border: 2rpx solid $border-light;
  border-radius: $radius-lg;
  transition: all $transition-base $ease-out;

  &:active {
    transform: scale(0.98);
  }

  &.option-selected {
    background: linear-gradient(135deg, rgba(110, 231, 183, 0.1) 0%, rgba(167, 139, 250, 0.1) 100%);
    border-color: $primary-color;
  }
}

.option-radio {
  width: 40rpx;
  height: 40rpx;
  border: 3rpx solid $border-base;
  border-radius: $radius-full;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: all $transition-base $ease-out;

  .option-selected & {
    border-color: $primary-color;
  }
}

.option-radio-inner {
  width: 20rpx;
  height: 20rpx;
  background: $gradient-primary;
  border-radius: $radius-full;
}

.option-label {
  flex: 1;
  font-size: $font-sm;
  color: $text-secondary;
  line-height: $line-height-normal;

  .option-selected & {
    color: $text-primary;
    font-weight: $font-medium;
  }
}

/* ==================== 简答题 ==================== */
.answer-input {
  position: relative;
}

.answer-textarea {
  width: 100%;
  min-height: 200rpx;
  padding: $spacing-md;
  background: $bg-gray-50;
  border: 2rpx solid $border-light;
  border-radius: $radius-lg;
  font-size: $font-sm;
  color: $text-primary;
  line-height: $line-height-relaxed;

  &:focus {
    border-color: $primary-color;
    background: $bg-white;
  }
}

.answer-count {
  position: absolute;
  right: $spacing-sm;
  bottom: $spacing-xs;
  font-size: $font-xs;
  color: $text-quaternary;
}

/* ==================== 提交按钮 ==================== */
.submit-section {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: $spacing-lg;
  background: $bg-white;
  box-shadow: 0 -2rpx 20rpx rgba(0, 0, 0, 0.05);
}

.submit-btn {
  width: 100%;
  height: 88rpx;
  background: $gradient-primary;
  border-radius: $radius-xl;
  font-size: $font-lg;
  font-weight: $font-bold;
  color: $bg-white;
  border: none;
  box-shadow: $shadow-base;
  transition: all $transition-base $ease-out;

  &:active:not([disabled]) {
    transform: translateY(-2rpx);
    box-shadow: $shadow-lg;
  }

  &[disabled] {
    background: $bg-gray-200;
    color: $text-quaternary;
    box-shadow: none;
  }
}
</style>

