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
        
        <!-- 总得分 -->
        <view class="score-display" v-if="aiAnalysisData.total_score">
          <text class="score-number">{{ aiAnalysisData.total_score }}</text>
          <text class="score-label">总得分</text>
        </view>
        
        <!-- 风险等级 -->
        <view class="risk-badge" :class="'risk-badge-' + (result.riskLevel || '低')">
          {{ getRiskText(result.riskLevel) }}
        </view>
      </view>

      <!-- 统计信息（已移除） -->

      <!-- AI分析 -->
      <view class="ai-analysis-section">
        <view class="section-header">
          <uni-icons type="star" size="20" color="#a78bfa"></uni-icons>
          <text class="section-title">AI 分析报告</text>
        </view>
        
        <view v-if="result.aiStatus === '1' && result.aiAnalysis" class="ai-content">
          <!-- 雷达图 -->
          <view class="radar-chart" v-if="aiAnalysisData.indicators">
            <canvas canvas-id="radarChart" :style="{width: '300px', height: '300px'}" :width="300" :height="300"></canvas>
          </view>
          
          <!-- 主要问题 -->
          <view class="ai-section" v-if="aiAnalysisData.main_issues && aiAnalysisData.main_issues.length > 0">
            <view class="section-subtitle">主要问题</view>
            <view class="issues-list">
              <view v-for="(issue, index) in aiAnalysisData.main_issues" :key="index" class="issue-item">
                <uni-icons type="info" size="16" color="#f59e0b"></uni-icons>
                <text class="issue-text">{{ issue }}</text>
              </view>
            </view>
          </view>
          
          <!-- 建议 -->
          <view class="ai-section" v-if="aiAnalysisData.suggestions && aiAnalysisData.suggestions.length > 0">
            <view class="section-subtitle">建议</view>
            <view class="suggestions-list">
              <view v-for="(suggestion, index) in aiAnalysisData.suggestions" :key="index" class="suggestion-item">
                <uni-icons type="checkmark-circle" size="16" color="#6ee7b7"></uni-icons>
                <text class="suggestion-text">{{ suggestion }}</text>
              </view>
            </view>
          </view>
          
          <!-- 详细分析 -->
          <view class="ai-section" v-if="aiAnalysisData.detailed_analysis">
            <view class="section-subtitle">详细分析</view>
            <view class="analysis-text">
              {{ aiAnalysisData.detailed_analysis }}
            </view>
          </view>
        </view>
        
        <view v-else class="ai-pending">
          <uni-icons type="clock" size="24" color="#f59e0b"></uni-icons>
          <text class="ai-pending-text">AI分析结果生成中，请稍后查看</text>
        </view>
      </view>

      <!-- 答题详情 - 轮播图 -->
      <view class="answers-section">
        <view class="section-header">
          <uni-icons type="list" size="20" color="#6ee7b7"></uni-icons>
          <text class="section-title">答题详情</text>
          <text class="total-questions">(共 {{ resultData.totalQuestions }} 题)</text>
        </view>

        <view class="swiper-container">
          <swiper :indicator-dots="true" :autoplay="false" :interval="3000" :duration="500" class="answer-swiper">
            <swiper-item v-for="(answer, index) in resultData.answers" :key="answer.answerId">
              <view class="answer-card">
                <view class="answer-header">
                  <view class="answer-number">第 {{ index + 1 }} 题</view>
                </view>

                <view class="answer-question">
                  <text class="question-text">{{ answer.content }}</text>
                </view>

                <!-- 选择题 -->
                <view v-if="answer.type === 'choice'" class="answer-details">
                  <view class="detail-row">
                    <text class="detail-label">你的答案：</text>
                    <text class="detail-value answer-user">
                      {{ answer.userAnswer }}
                    </text>
                  </view>
                  
                  <!-- 选项列表 -->
                  <view class="options-list" v-if="answer.options">
                    <view v-for="(option, optKey) in parseOptions(answer.options)" :key="optKey" class="option-item">
                      <text class="option-label">{{ optKey }}</text>
                      <text class="option-text">{{ option }}</text>
                    </view>
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
            </swiper-item>
          </swiper>
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
      result: null,
      aiAnalysisData: {}, // 解析后的AI分析数据
      radarChart: null
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
  onReady() {
    // 页面渲染完成后绘制雷达图
    this.$nextTick(() => {
      this.drawRadarChart()
    })
  },
  watch: {
    // 监听AI分析数据变化，重新绘制雷达图
    aiAnalysisData: {
      handler() {
        this.$nextTick(() => {
          this.drawRadarChart()
        })
      },
      deep: true
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
          
          // 解析AI分析数据
          if (this.result.aiAnalysis) {
            try {
              this.aiAnalysisData = JSON.parse(this.result.aiAnalysis)
              console.log('AI分析数据:', this.aiAnalysisData)
              
              // 确保indicators对象存在
              if (!this.aiAnalysisData.indicators) {
                this.aiAnalysisData.indicators = {}
              }
              
              // 为缺失的指标提供默认值
              const defaultIndicators = {
                sleep_score: 50,
                social_score: 50,
                stress_score: 50,
                anxiety_score: 50,
                emotion_score: 50,
                depression_score: 50,
                self_efficacy_score: 50
              }
              
              // 合并默认值和实际数据
              for (const key in defaultIndicators) {
                if (this.aiAnalysisData.indicators[key] === undefined) {
                  this.aiAnalysisData.indicators[key] = defaultIndicators[key]
                }
              }
            } catch (e) {
              console.error('解析AI分析数据失败:', e)
              this.aiAnalysisData = {
                indicators: {
                  sleep_score: 50,
                  social_score: 50,
                  stress_score: 50,
                  anxiety_score: 50,
                  emotion_score: 50,
                  depression_score: 50,
                  self_efficacy_score: 50
                }
              }
            }
          } else {
            // 如果没有AI分析数据，提供默认数据
            this.aiAnalysisData = {
              indicators: {
                sleep_score: 50,
                social_score: 50,
                stress_score: 50,
                anxiety_score: 50,
                emotion_score: 50,
                depression_score: 50,
                self_efficacy_score: 50
              }
            }
          }
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
    },
    
    // 解析选项
    parseOptions(optionsStr) {
      try {
        console.log('选项字符串:', optionsStr)
        const options = JSON.parse(optionsStr)
        console.log('解析后的选项:', options)
        return options
      } catch (e) {
        console.error('解析选项失败:', e)
        return {}
      }
    },
    
    // 绘制雷达图
    drawRadarChart() {
      if (!this.aiAnalysisData.indicators) {
        return
      }
      
      const ctx = uni.createCanvasContext('radarChart')
      if (!ctx) {
        console.error('无法获取Canvas上下文')
        return
      }
      
      // 雷达图配置
      const width = 300
      const height = 300
      const centerX = width / 2
      const centerY = height / 2
      const radius = Math.min(centerX, centerY) - 40
      
      // 指标配置
      const indicatorConfig = [
        { key: 'sleep_score', name: '睡眠质量', color: '#FF6B6B' },
        { key: 'social_score', name: '社交功能', color: '#4ECDC4' },
        { key: 'stress_score', name: '压力水平', color: '#FFD166' },
        { key: 'anxiety_score', name: '焦虑程度', color: '#06D6A0' },
        { key: 'emotion_score', name: '情绪稳定性', color: '#118AB2' },
        { key: 'depression_score', name: '抑郁倾向', color: '#7209B7' },
        { key: 'self_efficacy_score', name: '自我效能感', color: '#00BBF9' }
      ]
      
      const indicators = indicatorConfig.map(config => {
        return {
          name: config.name,
          value: this.aiAnalysisData.indicators[config.key] || 0,
          color: config.color
        }
      })
      
      const angleStep = 2 * Math.PI / indicators.length
      
      // 清空画布
      ctx.clearRect(0, 0, width, height)
      
      // 绘制雷达图背景
      ctx.beginPath()
      for (let i = 0; i < 5; i++) {
        const r = radius * (i + 1) / 5
        ctx.moveTo(centerX + r, centerY)
        for (let j = 1; j < indicators.length; j++) {
          const angle = j * angleStep
          const x = centerX + r * Math.cos(angle)
          const y = centerY + r * Math.sin(angle)
          ctx.lineTo(x, y)
        }
        ctx.closePath()
        ctx.strokeStyle = '#E5E7EB'
        ctx.lineWidth = 1
        ctx.stroke()
      }
      
      // 绘制坐标轴
      for (let i = 0; i < indicators.length; i++) {
        const angle = i * angleStep
        const x = centerX + radius * Math.cos(angle)
        const y = centerY + radius * Math.sin(angle)
        ctx.beginPath()
        ctx.moveTo(centerX, centerY)
        ctx.lineTo(x, y)
        ctx.strokeStyle = '#E5E7EB'
        ctx.lineWidth = 1
        ctx.stroke()
      }
      
      // 绘制数据区域
      ctx.beginPath()
      for (let i = 0; i < indicators.length; i++) {
        const angle = i * angleStep
        const r = (indicators[i].value / 100) * radius
        const x = centerX + r * Math.cos(angle)
        const y = centerY + r * Math.sin(angle)
        if (i === 0) {
          ctx.moveTo(x, y)
        } else {
          ctx.lineTo(x, y)
        }
      }
      ctx.closePath()
      ctx.fillStyle = 'rgba(110, 231, 183, 0.3)'
      ctx.fill()
      ctx.strokeStyle = '#6EE7B7'
      ctx.lineWidth = 2
      ctx.stroke()
      
      // 绘制数据点
      for (let i = 0; i < indicators.length; i++) {
        const angle = i * angleStep
        const r = (indicators[i].value / 100) * radius
        const x = centerX + r * Math.cos(angle)
        const y = centerY + r * Math.sin(angle)
        ctx.beginPath()
        ctx.arc(x, y, 4, 0, 2 * Math.PI)
        ctx.fillStyle = '#6EE7B7'
        ctx.fill()
        ctx.strokeStyle = '#FFFFFF'
        ctx.lineWidth = 2
        ctx.stroke()
      }
      
      // 绘制指标名称
      ctx.font = '12px sans-serif'
      ctx.textAlign = 'center'
      ctx.textBaseline = 'middle'
      ctx.fillStyle = '#4B5563'
      for (let i = 0; i < indicators.length; i++) {
        const angle = i * angleStep
        const r = radius + 20
        const x = centerX + r * Math.cos(angle)
        const y = centerY + r * Math.sin(angle)
        ctx.fillText(indicators[i].name, x, y)
      }
      
      // 绘制数值
      ctx.font = '10px sans-serif'
      ctx.textAlign = 'center'
      ctx.textBaseline = 'middle'
      ctx.fillStyle = '#4B5563'
      for (let i = 0; i < indicators.length; i++) {
        const angle = i * angleStep
        const r = (indicators[i].value / 100) * radius
        const x = centerX + r * Math.cos(angle)
        const y = centerY + r * Math.sin(angle) - 15
        ctx.fillText(indicators[i].value, x, y)
      }
      
      // 绘制中心标题
      ctx.font = '14px sans-serif'
      ctx.textAlign = 'center'
      ctx.textBaseline = 'middle'
      ctx.fillStyle = '#111827'
      ctx.fillText('各维度得分', centerX, centerY - 20)
      
      // 绘制图例
      ctx.font = '12px sans-serif'
      ctx.textAlign = 'left'
      ctx.textBaseline = 'middle'
      
      // 执行绘制
      ctx.draw()
    }
  }
}
</script>

<style lang="scss" scoped>
@import "@/static/scss/theme.scss";

// 使用项目中已定义的uni.scss变量覆盖
$font-sm: $uni-font-size-sm;
$font-base: $uni-font-size-base;
$font-lg: $uni-font-size-lg;

$spacing-sm: $uni-spacing-col-sm;
$spacing-md: $uni-spacing-col-base;
$spacing-lg: $uni-spacing-col-lg;

$radius-sm: $uni-border-radius-sm;
$radius-base: $uni-border-radius-base;
$radius-lg: $uni-border-radius-lg;
$radius-full: $uni-border-radius-circle;

$bg-white: $uni-bg-color;
$text-primary: $uni-text-color;
$text-secondary: $uni-text-color;
$text-tertiary: $uni-text-color-grey;

$border-light: $uni-border-color;

$success-color: $uni-color-success;
$warning-color: $uni-color-warning;
$danger-color: $uni-color-error;

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

/* ==================== 结果概览 ==================== */
.result-overview {
  text-align: center;
  margin-bottom: $spacing-lg;
}

.total-score {
  font-size: 48px;
  font-weight: $font-bold;
  color: $text-primary;
  margin-bottom: $spacing-sm;
}

.risk-level {
  font-size: 20px;
  font-weight: $font-bold;
  margin-bottom: $spacing-lg;
}

.risk-low {
  color: $success-color;
}

.risk-medium {
  color: $warning-color;
}

.risk-high {
  color: $danger-color;
}

/* ==================== AI分析报告 ==================== */
.ai-analysis-report {
  margin-top: $spacing-lg;
  padding: $spacing-lg;
  background: $bg-gray-50;
  border-radius: $radius-xl;
}

.radar-chart-container {
  display: flex;
  justify-content: center;
  margin: $spacing-lg 0;
}

.radar-chart {
  width: 300px;
  height: 300px;
}

.main-issues {
  margin-top: $spacing-lg;
  padding: $spacing-md;
  background: #fff5f5;
  border-radius: $radius-lg;
  border-left: 4px solid #ee0a24;
}

.suggestions {
  margin-top: $spacing-lg;
  padding: $spacing-md;
  background: #f0f9eb;
  border-radius: $radius-lg;
  border-left: 4px solid #07c160;
}

.detailed-analysis {
  margin-top: $spacing-lg;
  padding: $spacing-md;
  background: #e6f7ff;
  border-radius: $radius-lg;
  border-left: 4px solid #1890ff;
}

.analysis-section-title {
  font-size: $font-lg;
  font-weight: $font-bold;
  color: $text-primary;
  margin-bottom: $spacing-sm;
}

.analysis-section-content {
  font-size: $font-sm;
  color: $text-secondary;
  line-height: $line-height-relaxed;
}

.analysis-list {
  margin-left: $spacing-md;
}

.analysis-list-item {
  font-size: $font-sm;
  color: $text-secondary;
  margin-bottom: $spacing-xs;
  line-height: $line-height-relaxed;
}

/* ==================== 轮播图答题详情 ==================== */
.swiper-container {
  margin-top: $spacing-md;
}

.answer-swiper {
  min-height: 280px;
  border-radius: $radius-xl;
  overflow: hidden;
}

.swiper-item {
  padding: $spacing-md;
  background: $bg-white;
  min-height: 200px;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
}

.question-content {
  font-size: $font-lg;
  color: $text-primary;
  margin-bottom: $spacing-md;
  line-height: $line-height-relaxed;
  flex-shrink: 0;
}

.options-list {
  margin-top: $spacing-md;
  flex: 1;
  overflow-y: auto;
}

.option-item {
  font-size: $font-sm;
  color: $text-secondary;
  margin-bottom: $spacing-sm;
  line-height: $line-height-relaxed;
  padding: $spacing-sm $spacing-md;
  border-radius: $radius-lg;
  background: $bg-gray-50;
  border: 1px solid $border-light;
  transition: all $transition-base $ease-out;
  min-height: 60rpx;
  display: flex;
  align-items: flex-start;
  white-space: normal;
  word-wrap: break-word;
  word-break: break-all;

  &:last-child {
    margin-bottom: 0;
  }
}

.option-label {
  font-weight: $font-bold;
  margin-right: $spacing-sm;
  color: $text-primary;
  min-width: 40rpx;
}

.option-text {
  flex: 1;
  word-wrap: break-word;
  word-break: break-all;
}

.user-answer {
  font-size: $font-sm;
  color: $primary-color;
  margin-top: $spacing-sm;
  font-weight: $font-bold;
  flex-shrink: 0;
}

.swiper-pagination {
  margin-top: 0rpx;
}

.swiper-pagination .swiper-pagination-item {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: $border-light;
  margin: 0 4px;
}

.swiper-pagination .swiper-pagination-item-active {
  background: $primary-color;
  width: 12px;
  height: 12px;
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
