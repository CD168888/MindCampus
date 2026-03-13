<template>
  <view class="assessment-result-page">
    <view class="ambient-background"></view>

    <view class="glass-header" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="navbar-content">
        <view class="navbar-left" @tap="goBack">
          <view class="nav-icon-glass">
            <uni-icons type="left" size="22" color="#1D1D1F"></uni-icons>
          </view>
        </view>
        <view class="navbar-title">测评报告</view>
        <view class="navbar-right"></view>
      </view>
    </view>

    <view v-if="loading" class="loading-container">
      <view class="glass-card loading-card">
        <uni-icons type="spinner-cycle" size="36" color="#2CB5A0" class="spin-icon"></uni-icons>
        <text class="loading-text">正在生成您的专属报告...</text>
      </view>
    </view>

    <view v-else-if="resultData" class="content-wrapper">
      
      <view class="glass-card overview-card">
        <view class="overview-header">
          <view class="icon-box" :class="'icon-risk-' + (result.riskLevel || '低')">
            <text class="emoji-icon">{{ getRiskIcon(result.riskLevel) }}</text>
          </view>
          <text class="overview-title">{{ result.questionnaireTitle }}</text>
        </view>
        
        <view class="score-display" v-if="aiAnalysisData.total_score">
          <view class="score-glow"></view>
          <text class="score-number">{{ aiAnalysisData.total_score }}</text>
          <text class="score-label">综合评估得分</text>
        </view>
        
        <view class="risk-badge" :class="'badge-risk-' + (result.riskLevel || '低')">
          <view class="dot" :style="{ backgroundColor: getRiskColor(result.riskLevel) }"></view>
          <text>{{ getRiskText(result.riskLevel) }}</text>
        </view>
      </view>

      <view class="glass-card ai-card">
        <view class="section-header">
          <view class="header-left">
            <uni-icons type="star-filled" size="20" color="#2CB5A0"></uni-icons>
            <text class="section-title">AI 深度解析</text>
          </view>
        </view>
        
        <view v-if="result.aiStatus === '1' && result.aiAnalysis" class="ai-content">
          <view class="radar-chart-container" v-if="aiAnalysisData.indicators">
            <canvas canvas-id="radarChart" style="width: 300px; height: 300px;" class="radar-canvas"></canvas>
          </view>
          
          <view class="ai-section" v-if="aiAnalysisData.main_issues && aiAnalysisData.main_issues.length > 0">
            <view class="section-subtitle">
              <uni-icons type="info" size="16" color="#FF9500"></uni-icons> 需要关注
            </view>
            <view class="issues-list">
              <view v-for="(issue, index) in aiAnalysisData.main_issues" :key="index" class="list-item issue-item">
                <text class="item-text">{{ issue }}</text>
              </view>
            </view>
          </view>
          
          <view class="ai-section" v-if="aiAnalysisData.suggestions && aiAnalysisData.suggestions.length > 0">
            <view class="section-subtitle">
              <uni-icons type="heart-filled" size="16" color="#2CB5A0"></uni-icons> 疗愈建议
            </view>
            <view class="suggestions-list">
              <view v-for="(suggestion, index) in aiAnalysisData.suggestions" :key="index" class="list-item suggestion-item">
                <text class="item-text">{{ suggestion }}</text>
              </view>
            </view>
          </view>
          
          <view class="ai-section" v-if="aiAnalysisData.detailed_analysis">
            <view class="section-subtitle">
              <uni-icons type="chatbubble-filled" size="16" color="#007AFF"></uni-icons> 详细解读
            </view>
            <view class="analysis-text-box">
              <text class="analysis-text">{{ aiAnalysisData.detailed_analysis }}</text>
            </view>
          </view>
        </view>
        
        <view v-else class="ai-pending">
          <uni-icons type="pulldown" size="32" color="#86868B" class="bounce-icon"></uni-icons>
          <text class="ai-pending-text">AI 正在深度解读您的数据，请稍候...</text>
        </view>
      </view>

      <view class="answers-section">
        <view class="section-header answers-header">
          <view class="header-left">
            <uni-icons type="list" size="20" color="#1D1D1F"></uni-icons>
            <text class="section-title">答题回顾</text>
          </view>
          <text class="total-questions">{{ resultData.totalQuestions }} 题</text>
        </view>

        <swiper :indicator-dots="false" :autoplay="false" class="answer-swiper" previous-margin="0" next-margin="40rpx">
          <swiper-item v-for="(answer, index) in resultData.answers" :key="answer.answerId" class="swiper-item-wrapper">
            <view class="glass-card answer-card">
              <view class="answer-header">
                <view class="answer-number-badge">Q{{ index + 1 }}</view>
              </view>

              <text class="question-text">{{ answer.content }}</text>

              <view v-if="answer.type === 'choice'" class="options-list">
                <view v-for="(option, optKey) in parseOptions(answer.options)" :key="optKey" 
                      class="option-item" 
                      :class="{ 'option-selected': answer.userAnswer === optKey }">
                  <view class="option-key">{{ optKey }}</view>
                  <text class="option-text">{{ option }}</text>
                  <uni-icons v-if="answer.userAnswer === optKey" type="checkmarkempty" size="16" color="#2CB5A0"></uni-icons>
                </view>
              </view>

              <view v-else class="answer-text-box">
                <text class="answer-text">{{ answer.userAnswer }}</text>
              </view>
            </view>
          </swiper-item>
        </swiper>
      </view>
    </view>

    <view class="bottom-glass-bar" v-if="!loading && resultData">
      <view class="action-btn btn-secondary" @tap="goToList">
        <text>返回列表</text>
      </view>
      <view class="action-btn btn-primary" @tap="shareResult">
        <uni-icons type="paperplane-filled" size="18" color="#FFFFFF"></uni-icons>
        <text>分享报告</text>
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
      result: null,
      aiAnalysisData: {},
      radarChart: null
    }
  },
  onLoad(options) {
    const systemInfo = uni.getSystemInfoSync()
    this.statusBarHeight = systemInfo.statusBarHeight || 0
    this.resultId = options.id
    if (this.resultId) {
      this.loadResult()
    }
  },
  watch: {
    aiAnalysisData: {
      handler() {
        this.$nextTick(() => {
          setTimeout(() => {
            this.drawRadarChart()
          }, 300) // 给予一点延迟确保 canvas 容器已经渲染
        })
      },
      deep: true
    }
  },
  methods: {
    async loadResult() {
      try {
        this.loading = true
        const res = await getResultDetail(this.resultId)

        if (res.code === 200) {
          this.resultData = res.data
          this.result = res.data.result
          
          if (this.result.aiAnalysis) {
            try {
              this.aiAnalysisData = JSON.parse(this.result.aiAnalysis)
              if (!this.aiAnalysisData.indicators) {
                this.aiAnalysisData.indicators = {}
              }
              const defaultIndicators = {
                sleep_score: 50, social_score: 50, stress_score: 50,
                anxiety_score: 50, emotion_score: 50, depression_score: 50, self_efficacy_score: 50
              }
              for (const key in defaultIndicators) {
                if (this.aiAnalysisData.indicators[key] === undefined) {
                  this.aiAnalysisData.indicators[key] = defaultIndicators[key]
                }
              }
            } catch (e) {
              this.aiAnalysisData = this.getDefaultAIAnalysis()
            }
          } else {
            this.aiAnalysisData = this.getDefaultAIAnalysis()
          }
        } else {
          uni.showToast({ title: res.msg || '加载失败', icon: 'none' })
        }
      } catch (error) {
        uni.showToast({ title: '加载失败，请稍后重试', icon: 'none' })
      } finally {
        this.loading = false
      }
    },

    getDefaultAIAnalysis() {
      return {
        indicators: { sleep_score: 50, social_score: 50, stress_score: 50, anxiety_score: 50, emotion_score: 50, depression_score: 50, self_efficacy_score: 50 }
      }
    },

    goBack() { uni.navigateBack() },
    goToList() { uni.navigateBack({ delta: 2 }) },
    shareResult() { uni.showToast({ title: '分享功能开发中', icon: 'none' }) },

    // 恢复 Emoji 图标方法
    getRiskIcon(riskLevel) {
      const icons = {
        '低': '😊',
        '中': '😐',
        '高': '😟'
      }
      return icons[riskLevel] || '😊'
    },

    // 颜色和文本依然保留更温和的浅色系
    getRiskColor(riskLevel) {
      const colors = { '低': '#2CB5A0', '中': '#FF9500', '高': '#FF3B30' }
      return colors[riskLevel] || '#2CB5A0'
    },

    getRiskText(riskLevel) {
      const texts = { '低': '状态良好', '中': '轻度风险', '高': '需要关注' }
      return texts[riskLevel] || '状态良好'
    },
    
    parseOptions(optionsStr) {
      try {
        return JSON.parse(optionsStr)
      } catch (e) {
        return {}
      }
    },
    
    // 重构高质感雷达图绘制逻辑，统一为治愈青色系
    drawRadarChart() {
      if (!this.aiAnalysisData.indicators) return
      
      const ctx = uni.createCanvasContext('radarChart', this)
      if (!ctx) return
      
      const width = 300
      const height = 300
      const centerX = width / 2
      const centerY = height / 2
      const radius = Math.min(centerX, centerY) - 40
      
      const indicatorConfig = [
        { key: 'sleep_score', name: '睡眠' },
        { key: 'social_score', name: '社交' },
        { key: 'stress_score', name: '压力' },
        { key: 'anxiety_score', name: '焦虑' },
        { key: 'emotion_score', name: '情绪' },
        { key: 'depression_score', name: '抑郁' },
        { key: 'self_efficacy_score', name: '效能' }
      ]
      
      const indicators = indicatorConfig.map(config => ({
        name: config.name,
        value: this.aiAnalysisData.indicators[config.key] || 0
      }))
      
      const angleStep = 2 * Math.PI / indicators.length
      
      ctx.clearRect(0, 0, width, height)
      
      // 绘制蜘蛛网底图
      ctx.beginPath()
      for (let i = 0; i < 5; i++) {
        const r = radius * (i + 1) / 5
        ctx.moveTo(centerX + r * Math.cos(-Math.PI/2), centerY + r * Math.sin(-Math.PI/2))
        for (let j = 1; j <= indicators.length; j++) {
          const angle = j * angleStep - Math.PI/2
          ctx.lineTo(centerX + r * Math.cos(angle), centerY + r * Math.sin(angle))
        }
        ctx.closePath()
        ctx.strokeStyle = 'rgba(0, 0, 0, 0.06)'
        ctx.lineWidth = 1
        ctx.stroke()
      }
      
      // 绘制中心辐射轴
      for (let i = 0; i < indicators.length; i++) {
        const angle = i * angleStep - Math.PI/2
        ctx.beginPath()
        ctx.moveTo(centerX, centerY)
        ctx.lineTo(centerX + radius * Math.cos(angle), centerY + radius * Math.sin(angle))
        ctx.strokeStyle = 'rgba(0, 0, 0, 0.06)'
        ctx.lineWidth = 1
        ctx.stroke()
      }
      
      // 绘制数据核心多边形 (治愈青色)
      ctx.beginPath()
      for (let i = 0; i < indicators.length; i++) {
        const angle = i * angleStep - Math.PI/2
        const r = (indicators[i].value / 100) * radius
        const x = centerX + r * Math.cos(angle)
        const y = centerY + r * Math.sin(angle)
        if (i === 0) ctx.moveTo(x, y)
        else ctx.lineTo(x, y)
      }
      ctx.closePath()
      ctx.fillStyle = 'rgba(44, 181, 160, 0.25)' // 半透明青色
      ctx.fill()
      ctx.strokeStyle = '#2CB5A0'
      ctx.lineWidth = 2
      ctx.stroke()
      
      // 绘制数据发光圆点
      for (let i = 0; i < indicators.length; i++) {
        const angle = i * angleStep - Math.PI/2
        const r = (indicators[i].value / 100) * radius
        const x = centerX + r * Math.cos(angle)
        const y = centerY + r * Math.sin(angle)
        ctx.beginPath()
        ctx.arc(x, y, 4, 0, 2 * Math.PI)
        ctx.fillStyle = '#FFFFFF'
        ctx.fill()
        ctx.strokeStyle = '#2CB5A0'
        ctx.lineWidth = 2
        ctx.stroke()
      }
      
      // 绘制指标文字
      ctx.font = '12px sans-serif'
      ctx.textAlign = 'center'
      ctx.textBaseline = 'middle'
      
      for (let i = 0; i < indicators.length; i++) {
        const angle = i * angleStep - Math.PI/2
        const r = radius + 22
        const x = centerX + r * Math.cos(angle)
        const y = centerY + r * Math.sin(angle)
        
        ctx.fillStyle = '#86868B'
        ctx.fillText(indicators[i].name, x, y - 8)
        
        ctx.font = 'bold 12px sans-serif'
        ctx.fillStyle = '#1D1D1F'
        ctx.fillText(indicators[i].value, x, y + 8)
        ctx.font = '12px sans-serif'
      }
      
      ctx.draw()
    }
  }
}
</script>

<style lang="scss" scoped>
/* ==================== iOS 风格核心变量 ==================== */
$ios-text-primary: #1D1D1F;
$ios-text-secondary: #86868B;
$ios-blue: #007AFF;
$theme-cyan: #2CB5A0;

.assessment-result-page {
  min-height: 100vh;
  position: relative;
  background-color: #F5F5F7;
  font-family: -apple-system, BlinkMacSystemFont, "SF Pro Text", "Helvetica Neue", Arial, sans-serif;
  padding-bottom: calc(140rpx + env(safe-area-inset-bottom));
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

/* ==================== 加载中状态 ==================== */
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

/* ==================== 内容区域容器 ==================== */
.content-wrapper {
  position: relative;
  z-index: 1;
  padding: 30rpx 32rpx 0;
  display: flex;
  flex-direction: column;
  gap: 32rpx;
}

/* ==================== 结果概览 (大卡片) ==================== */
.overview-card {
  padding: 40rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.overview-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 40rpx;
}

.icon-box {
  width: 96rpx;
  height: 96rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 24rpx;
  box-shadow: 0 8rpx 20rpx rgba(0, 0, 0, 0.05);
}

.icon-risk-低 { background: rgba(44, 181, 160, 0.15); }
.icon-risk-中 { background: rgba(255, 149, 0, 0.15); }
.icon-risk-高 { background: rgba(255, 59, 48, 0.15); }

/* Emoji 尺寸调整 */
.emoji-icon {
  font-size: 56rpx;
  line-height: 1;
}

.overview-title {
  font-size: 30rpx;
  color: $ios-text-secondary;
  font-weight: 500;
}

/* 得分大数字展示 */
.score-display {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 40rpx;
}

.score-glow {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 180rpx;
  height: 180rpx;
  background: radial-gradient(circle, rgba(44, 181, 160, 0.2) 0%, transparent 70%);
  border-radius: 50%;
  pointer-events: none;
}

.score-number {
  font-size: 100rpx;
  font-weight: 800;
  color: $theme-cyan;
  font-family: "SF Pro Display", sans-serif;
  line-height: 1;
  letter-spacing: -2rpx;
  text-shadow: 0 4px 16px rgba(44, 181, 160, 0.2);
}

.score-label {
  font-size: 26rpx;
  color: $ios-text-secondary;
  margin-top: 12rpx;
}

/* 风险等级角标 */
.risk-badge {
  display: flex;
  align-items: center;
  gap: 12rpx;
  padding: 12rpx 32rpx;
  border-radius: 40rpx;
  font-size: 28rpx;
  font-weight: 600;
}

.dot {
  width: 12rpx;
  height: 12rpx;
  border-radius: 50%;
}

.badge-risk-低 { background: rgba(44, 181, 160, 0.1); color: $theme-cyan; }
.badge-risk-中 { background: rgba(255, 149, 0, 0.1); color: #FF9500; }
.badge-risk-高 { background: rgba(255, 59, 48, 0.1); color: #FF3B30; }

/* ==================== AI 分析报告 ==================== */
.ai-card {
  padding: 40rpx;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30rpx;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.section-title {
  font-size: 34rpx;
  font-weight: 700;
  color: $ios-text-primary;
}

.ai-content {
  display: flex;
  flex-direction: column;
  gap: 40rpx;
}

.radar-chart-container {
  display: flex;
  justify-content: center;
  margin: 20rpx 0;
}

.ai-section {
  display: flex;
  flex-direction: column;
}

.section-subtitle {
  display: flex;
  align-items: center;
  gap: 8rpx;
  font-size: 28rpx;
  font-weight: 600;
  color: $ios-text-primary;
  margin-bottom: 20rpx;
}

/* 列表容器 */
.list-item {
  padding: 24rpx;
  border-radius: 20rpx;
  margin-bottom: 16rpx;
  font-size: 28rpx;
  line-height: 1.5;
  color: $ios-text-primary;
}

.issue-item {
  background: rgba(255, 149, 0, 0.08);
  border: 1px solid rgba(255, 149, 0, 0.15);
}

.suggestion-item {
  background: rgba(44, 181, 160, 0.08);
  border: 1px solid rgba(44, 181, 160, 0.15);
}

.analysis-text-box {
  background: rgba(255, 255, 255, 0.5);
  border: 1px solid rgba(0, 0, 0, 0.05);
  padding: 32rpx;
  border-radius: 24rpx;
}

.analysis-text {
  font-size: 28rpx;
  color: $ios-text-primary;
  line-height: 1.6;
}

/* AI 处理中 */
.ai-pending {
  padding: 60rpx 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20rpx;
}

.bounce-icon {
  animation: bounce 2s infinite;
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10rpx); }
}

.ai-pending-text {
  font-size: 28rpx;
  color: $ios-text-secondary;
}

/* ==================== 答题回顾 ==================== */
.answers-section {
  margin-bottom: 40rpx;
}

.answers-header {
  padding: 0 10rpx;
}

.total-questions {
  font-size: 26rpx;
  color: $ios-text-secondary;
}

.answer-swiper {
  height: 520rpx; /* 固定高度确保卡片对齐 */
}

.swiper-item-wrapper {
  padding-right: 20rpx; /* 卡片之间的间距 */
  box-sizing: border-box;
}

.answer-card {
  height: 100%;
  padding: 40rpx;
  display: flex;
  flex-direction: column;
  box-sizing: border-box;
}

.answer-header {
  margin-bottom: 24rpx;
}

.answer-number-badge {
  display: inline-block;
  background: $ios-text-primary;
  color: #FFFFFF;
  font-size: 24rpx;
  font-weight: 700;
  padding: 6rpx 16rpx;
  border-radius: 12rpx;
}

.question-text {
  font-size: 32rpx;
  font-weight: 600;
  color: $ios-text-primary;
  line-height: 1.5;
  margin-bottom: 32rpx;
  flex-shrink: 0;
}

.options-list {
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.option-item {
  display: flex;
  align-items: flex-start;
  padding: 24rpx;
  background: rgba(255, 255, 255, 0.4);
  border: 1px solid rgba(0, 0, 0, 0.05);
  border-radius: 20rpx;
  transition: all 0.2s ease;
}

.option-selected {
  background: rgba(44, 181, 160, 0.1);
  border-color: rgba(44, 181, 160, 0.4);
}

.option-key {
  font-size: 28rpx;
  font-weight: 700;
  color: $ios-text-primary;
  width: 40rpx;
  flex-shrink: 0;
}

.option-text {
  flex: 1;
  font-size: 28rpx;
  color: $ios-text-secondary;
  line-height: 1.4;
  padding-right: 16rpx;
}

.option-selected .option-text {
  color: $ios-text-primary;
  font-weight: 500;
}

.answer-text-box {
  flex: 1;
  background: rgba(255, 255, 255, 0.4);
  border: 1px solid rgba(0, 0, 0, 0.05);
  border-radius: 20rpx;
  padding: 24rpx;
}

.answer-text {
  font-size: 28rpx;
  color: $ios-text-primary;
  line-height: 1.6;
}

/* ==================== 底部毛玻璃操作栏 ==================== */
.bottom-glass-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 24rpx 40rpx calc(24rpx + env(safe-area-inset-bottom));
  background: rgba(245, 245, 247, 0.75);
  backdrop-filter: saturate(180%) blur(20px);
  -webkit-backdrop-filter: saturate(180%) blur(20px);
  border-top: 0.5px solid rgba(0, 0, 0, 0.05);
  display: flex;
  gap: 24rpx;
  z-index: 100;
}

.action-btn {
  flex: 1;
  height: 90rpx;
  border-radius: 45rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
  font-size: 30rpx;
  font-weight: 600;
  transition: transform 0.2s ease;
  
  &:active { transform: scale(0.96); }
}

.btn-secondary {
  background: rgba(0, 0, 0, 0.05);
  color: $ios-text-primary;
}

.btn-primary {
  background: linear-gradient(135deg, #48D1CC 0%, #2CB5A0 100%);
  color: #FFFFFF;
  box-shadow: 0 8rpx 20rpx rgba(44, 181, 160, 0.25);
}
</style>