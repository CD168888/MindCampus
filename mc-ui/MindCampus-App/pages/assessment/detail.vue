<template>
  <view class="assessment-detail-page">
    <view class="ambient-background"></view>

    <view class="glass-header" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="navbar-content">
        <view class="navbar-left" @tap="goBack">
          <view class="nav-icon-glass">
            <uni-icons type="left" size="22" color="#1D1D1F"></uni-icons>
          </view>
        </view>
        <view class="navbar-title">问卷详情</view>
        <view class="navbar-right"></view>
      </view>
    </view>

    <view v-if="loading" class="loading-container">
      <view class="glass-card loading-card">
        <uni-icons type="spinner-cycle" size="36" color="#2CB5A0" class="spin-icon"></uni-icons>
        <text class="loading-text">正在加载问卷内容...</text>
      </view>
    </view>

    <view v-else-if="questionnaire" class="content-wrapper">
      
      <view class="glass-card header-card">
        <view class="header-info">
          <text class="header-title">{{ questionnaire.title }}</text>
          <text class="header-desc">{{ questionnaire.description }}</text>
          
          <view class="header-meta">
            <view class="meta-tag">
              <uni-icons type="list" size="14" color="#86868B"></uni-icons>
              <text class="meta-text">共 {{ questions.length }} 题</text>
            </view>
            <view class="meta-tag">
              <uni-icons type="clock" size="14" color="#86868B"></uni-icons>
              <text class="meta-text">预计 {{ questions.length }} 分钟</text>
            </view>
          </view>
        </view>

        <view class="progress-section">
          <view class="progress-header">
            <text class="progress-title">答题进度</text>
            <text class="progress-count cyan-text">{{ answeredCount }} <text class="text-muted">/ {{ questions.length }}</text></text>
          </view>
          <view class="progress-track">
            <view class="progress-fill" :style="{ width: progressPercent + '%' }"></view>
          </view>
        </view>
      </view>

      <view class="questions-section">
        <view v-for="(question, index) in questions" :key="question.questionId" class="glass-card question-card">
          <view class="question-header">
            <view class="question-number-badge">Q{{ index + 1 }}</view>
            <view v-if="answers[question.questionId]" class="question-status bounce-in">
              <uni-icons type="checkmarkempty" size="20" color="#2CB5A0"></uni-icons>
            </view>
          </view>

          <view class="question-content">
            <text class="question-text">{{ question.content }}</text>
          </view>

          <view v-if="question.type === 'choice'" class="options-list">
            <view
              v-for="(option, optionKey) in parseOptions(question.options)"
              :key="optionKey"
              class="option-item"
              :class="{ 'option-selected': answers[question.questionId] === optionKey }"
              @tap="selectOption(question.questionId, optionKey)"
            >
              <view class="option-key">{{ optionKey }}</view>
              <text class="option-label">{{ option }}</text>
              <view class="option-check" v-if="answers[question.questionId] === optionKey">
                <uni-icons type="checkmarkempty" size="18" color="#2CB5A0"></uni-icons>
              </view>
            </view>
          </view>

          <view v-else class="answer-input">
            <textarea
              v-model="answers[question.questionId]"
              placeholder="分享一下你的想法..."
              placeholder-class="textarea-placeholder"
              :maxlength="500"
              class="answer-textarea"
              @input="onInputAnswer(question.questionId, $event)"
            />
            <text class="answer-count">{{ (answers[question.questionId] || '').length }} / 500</text>
          </view>
        </view>
      </view>
    </view>

    <view class="submit-glass-bar" v-if="!loading && questionnaire">
      <view class="submit-btn" :class="{ 'btn-disabled': !isAllAnswered, 'btn-active': isAllAnswered }" @tap="submitAnswers">
        <text>{{ isAllAnswered ? '完成并提交' : `还有 ${questions.length - answeredCount} 题未作答` }}</text>
        <uni-icons v-if="isAllAnswered" type="paperplane-filled" size="18" color="#FFFFFF"></uni-icons>
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
          confirmColor: '#2CB5A0',
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
        confirmColor: '#2CB5A0',
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
/* ==================== iOS 风格核心变量 ==================== */
$ios-text-primary: #1D1D1F;
$ios-text-secondary: #86868B;
$theme-cyan: #2CB5A0;

.assessment-detail-page {
  min-height: 100vh;
  position: relative;
  background-color: #F5F5F7;
  font-family: -apple-system, BlinkMacSystemFont, "SF Pro Text", "Helvetica Neue", Arial, sans-serif;
  padding-bottom: calc(160rpx + env(safe-area-inset-bottom)); /* 给底部悬浮栏留出空间 */
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
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.04);
  border-radius: 36rpx;
}

/* ==================== 顶部导航 ==================== */
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

/* ==================== 加载状态 ==================== */
.loading-container {
  position: relative;
  z-index: 1;
  padding: 120rpx 40rpx;
  display: flex;
  justify-content: center;
}

.loading-card {
  padding: 60rpx 80rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 24rpx;
}

.spin-icon {
  animation: spin 1.5s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.loading-text {
  font-size: 28rpx;
  color: $ios-text-secondary;
}

/* ==================== 内容区域 ==================== */
.content-wrapper {
  position: relative;
  z-index: 1;
  padding: 30rpx 32rpx 0;
  display: flex;
  flex-direction: column;
  gap: 32rpx;
}

/* ==================== 问卷信息头部 ==================== */
.header-card {
  padding: 40rpx;
  display: flex;
  flex-direction: column;
}

.header-info {
  margin-bottom: 40rpx;
}

.header-title {
  font-size: 40rpx;
  font-weight: 700;
  color: $ios-text-primary;
  margin-bottom: 16rpx;
  line-height: 1.3;
  display: block;
}

.header-desc {
  font-size: 28rpx;
  color: $ios-text-secondary;
  line-height: 1.5;
  margin-bottom: 24rpx;
  display: block;
}

.header-meta {
  display: flex;
  gap: 20rpx;
}

.meta-tag {
  display: flex;
  align-items: center;
  gap: 8rpx;
  background: rgba(0, 0, 0, 0.04);
  padding: 8rpx 20rpx;
  border-radius: 16rpx;
}

.meta-text {
  font-size: 24rpx;
  color: $ios-text-secondary;
  font-weight: 500;
}

/* 进度条 */
.progress-section {
  padding-top: 30rpx;
  border-top: 1px solid rgba(0, 0, 0, 0.05);
}

.progress-header {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  margin-bottom: 16rpx;
}

.progress-title {
  font-size: 26rpx;
  font-weight: 600;
  color: $ios-text-primary;
}

.progress-count {
  font-size: 32rpx;
  font-weight: 700;
  font-family: "SF Pro Display", sans-serif;
}

.cyan-text { color: $theme-cyan; }
.text-muted { color: $ios-text-secondary; font-size: 24rpx; font-weight: 500; }

.progress-track {
  height: 16rpx;
  background: rgba(0, 0, 0, 0.06);
  border-radius: 10rpx;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #74EBD5 0%, #2CB5A0 100%);
  border-radius: 10rpx;
  transition: width 0.4s cubic-bezier(0.2, 0.8, 0.2, 1);
}

/* ==================== 题目列表区 ==================== */
.questions-section {
  display: flex;
  flex-direction: column;
  gap: 32rpx;
}

.question-card {
  padding: 40rpx;
}

.question-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24rpx;
}

.question-number-badge {
  background: $ios-text-primary;
  color: #FFFFFF;
  font-size: 24rpx;
  font-weight: 700;
  padding: 6rpx 16rpx;
  border-radius: 12rpx;
  letter-spacing: 1rpx;
}

.bounce-in {
  animation: bounceIn 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

@keyframes bounceIn {
  0% { transform: scale(0.3); opacity: 0; }
  50% { transform: scale(1.1); }
  100% { transform: scale(1); opacity: 1; }
}

.question-content {
  margin-bottom: 32rpx;
}

.question-text {
  font-size: 32rpx;
  font-weight: 600;
  color: $ios-text-primary;
  line-height: 1.5;
}

/* ==================== 选项卡片式交互 ==================== */
.options-list {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.option-item {
  display: flex;
  align-items: flex-start;
  padding: 28rpx 32rpx;
  background: rgba(255, 255, 255, 0.5);
  border: 1px solid rgba(0, 0, 0, 0.05);
  border-radius: 24rpx;
  transition: all 0.2s ease;
  
  &:active {
    transform: scale(0.98);
    background: rgba(255, 255, 255, 0.8);
  }
}

.option-selected {
  background: rgba(44, 181, 160, 0.08); /* 极弱青色背景 */
  border-color: rgba(44, 181, 160, 0.3);
}

.option-key {
  font-size: 30rpx;
  font-weight: 700;
  color: $ios-text-primary;
  width: 48rpx;
  flex-shrink: 0;
  line-height: 1.4;
}

.option-label {
  flex: 1;
  font-size: 30rpx;
  color: $ios-text-secondary;
  line-height: 1.4;
  padding-right: 16rpx;
  transition: color 0.2s ease;
}

.option-selected .option-key,
.option-selected .option-label {
  color: $ios-text-primary;
  font-weight: 500;
}

.option-check {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  height: 42rpx; /* 匹配文字行高实现视觉居中 */
}

/* ==================== 简答题文本框 ==================== */
.answer-input {
  position: relative;
}

.answer-textarea {
  width: 100%;
  min-height: 240rpx;
  padding: 32rpx;
  box-sizing: border-box;
  background: rgba(255, 255, 255, 0.5);
  border: 1px solid rgba(0, 0, 0, 0.08);
  border-radius: 24rpx;
  font-size: 30rpx;
  color: $ios-text-primary;
  line-height: 1.6;
  transition: all 0.3s ease;

  &:focus {
    background: rgba(255, 255, 255, 0.9);
    border-color: rgba(44, 181, 160, 0.4);
    box-shadow: 0 0 0 6rpx rgba(44, 181, 160, 0.1);
  }
}

.textarea-placeholder {
  color: #A1A1A6;
}

.answer-count {
  position: absolute;
  right: 24rpx;
  bottom: 24rpx;
  font-size: 24rpx;
  color: $ios-text-secondary;
  font-weight: 500;
  background: rgba(255, 255, 255, 0.8);
  padding: 4rpx 12rpx;
  border-radius: 10rpx;
}

/* ==================== 底部悬浮提交栏 ==================== */
.submit-glass-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 24rpx 40rpx calc(24rpx + env(safe-area-inset-bottom));
  background: rgba(245, 245, 247, 0.75);
  backdrop-filter: saturate(180%) blur(20px);
  -webkit-backdrop-filter: saturate(180%) blur(20px);
  border-top: 0.5px solid rgba(0, 0, 0, 0.05);
  z-index: 100;
}

.submit-btn {
  width: 100%;
  height: 96rpx;
  border-radius: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
  font-size: 32rpx;
  font-weight: 600;
  transition: all 0.3s ease;
}

/* 未完成状态：灰色玻璃质感 */
.btn-disabled {
  background: rgba(0, 0, 0, 0.06);
  color: $ios-text-secondary;
}

/* 激活状态：治愈青色高亮 */
.btn-active {
  background: linear-gradient(135deg, #48D1CC 0%, #2CB5A0 100%);
  color: #FFFFFF;
  box-shadow: 0 8rpx 24rpx rgba(44, 181, 160, 0.3);
  
  &:active {
    transform: scale(0.98);
    box-shadow: 0 4rpx 12rpx rgba(44, 181, 160, 0.2);
  }
}
</style>