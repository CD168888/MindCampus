<template>
  <view class="assessment-wrapper">
    <view class="assessment-card" @tap="onCardClick">
      <view class="card-background">
        <!-- 装饰元素 -->
        <view class="deco-circle circle-1"></view>
        <view class="deco-circle circle-2"></view>
        <view class="deco-circle circle-3"></view>
      </view>
      
      <view class="card-content">
        <view class="card-header">
          <view class="icon-wrapper">
            <text class="card-icon">{{ icon }}</text>
          </view>
          <view class="badge">
            <text class="badge-text">开始测评</text>
          </view>
        </view>
        
        <view class="card-body">
          <view class="card-title">{{ title }}</view>
          <view class="card-subtitle">{{ subtitle }}</view>
        </view>
        
        <view class="card-footer">
          <view class="action-btn">
            <text class="btn-text">立即开始</text>
            <text class="btn-arrow">→</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  name: 'AssessmentCard',
  props: {
    // 卡片图标
    icon: {
      type: String,
      default: '📋'
    },
    // 卡片标题
    title: {
      type: String,
      default: '心理健康测评'
    },
    // 卡片副标题
    subtitle: {
      type: String,
      default: '了解你的心理健康状况，获取专业建议'
    }
  },
  methods: {
    onCardClick() {
      this.$emit('click')
    }
  }
}
</script>

<style lang="scss" scoped>
@import "@/static/scss/theme.scss";

.assessment-wrapper {
  margin: 0 $spacing-lg $spacing-lg;
  position: relative;
  z-index: 2;
}

.assessment-card {
  position: relative;
  min-height: 180rpx;
  border-radius: $radius-xl;
  overflow: hidden;
  box-shadow: $shadow-lg;
  transition: all $transition-base $ease-out;
  
  &:active {
    transform: scale(0.98);
    box-shadow: $shadow-md;
  }
}

.card-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, #6ee7b7 0%, #a78bfa 50%, #fda4af 100%);
  z-index: 0;
}

// 装饰圆圈
.deco-circle {
  position: absolute;
  border-radius: $radius-full;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.25) 0%, transparent 70%);
  
  &.circle-1 {
    width: 200rpx;
    height: 200rpx;
    top: -80rpx;
    right: -60rpx;
    animation: float1 6s ease-in-out infinite;
  }
  
  &.circle-2 {
    width: 150rpx;
    height: 150rpx;
    bottom: -50rpx;
    left: -40rpx;
    animation: float2 5s ease-in-out infinite;
  }
  
  &.circle-3 {
    width: 100rpx;
    height: 100rpx;
    top: 50%;
    left: 20%;
    animation: float3 7s ease-in-out infinite;
  }
}

@keyframes float1 {
  0%, 100% { transform: translate(0, 0) scale(1); }
  50% { transform: translate(-10rpx, 10rpx) scale(1.1); }
}

@keyframes float2 {
  0%, 100% { transform: translate(0, 0) scale(1); }
  50% { transform: translate(10rpx, -10rpx) scale(1.15); }
}

@keyframes float3 {
  0%, 100% { transform: translate(0, 0) scale(1); }
  50% { transform: translate(-5rpx, -5rpx) scale(0.9); }
}

.card-content {
  position: relative;
  z-index: 1;
  padding: $spacing-lg;
  display: flex;
  flex-direction: column;
  gap: $spacing-md;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.icon-wrapper {
  width: 64rpx;
  height: 64rpx;
  background: rgba(255, 255, 255, 0.25);
  backdrop-filter: blur(10rpx);
  border-radius: $radius-lg;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1rpx solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.card-icon {
  font-size: 40rpx;
  filter: drop-shadow(0 2rpx 4rpx rgba(0, 0, 0, 0.1));
}

.badge {
  padding: 8rpx 16rpx;
  background: rgba(255, 255, 255, 0.9);
  border-radius: $radius-full;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.08);
}

.badge-text {
  font-size: $font-xs;
  font-weight: $font-bold;
  background: $gradient-primary;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: 0.5rpx;
}

.card-body {
  display: flex;
  flex-direction: column;
  gap: $spacing-xs;
}

.card-title {
  font-size: $font-xl;
  font-weight: $font-bold;
  color: $bg-white;
  letter-spacing: -0.5rpx;
  text-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.15);
}

.card-subtitle {
  font-size: $font-sm;
  color: rgba(255, 255, 255, 0.92);
  line-height: $line-height-relaxed;
  font-weight: $font-medium;
  text-shadow: 0 1rpx 4rpx rgba(0, 0, 0, 0.1);
}

.card-footer {
  display: flex;
  justify-content: flex-end;
  margin-top: $spacing-xs;
}

.action-btn {
  padding: 12rpx 24rpx;
  background: rgba(255, 255, 255, 0.95);
  border-radius: $radius-full;
  display: flex;
  align-items: center;
  gap: $spacing-xs;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.12);
  transition: all $transition-base $ease-out;
}

.assessment-card:active .action-btn {
  background: rgba(255, 255, 255, 1);
  transform: translateX(4rpx);
}

.btn-text {
  font-size: $font-sm;
  font-weight: $font-bold;
  background: $gradient-primary;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: 0.5rpx;
}

.btn-arrow {
  font-size: $font-base;
  font-weight: $font-bold;
  color: $primary-color;
  line-height: 1;
}
</style>

