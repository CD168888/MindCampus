<template>
  <view class="about-page">
    <view class="ambient-background"></view>

    <view class="glass-header" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="navbar-content">
        <view class="navbar-left" @tap="goBack">
          <view class="nav-icon-glass">
            <uni-icons type="left" size="22" color="#1D1D1F"></uni-icons>
          </view>
        </view>
        <view class="navbar-title">关于我们</view>
        <view class="navbar-right"></view>
      </view>
    </view>

    <scroll-view class="content-scroll" scroll-y>
      
      <view class="app-hero">
        <view class="logo-glass-box">
          <image src="/static/logo200.png" mode="aspectFill" class="app-logo"></image>
        </view>
        <text class="app-name">MindCampus</text>
        <view class="version-badge">
          <text>Version {{ version || '1.0.0' }}</text>
        </view>
      </view>

      <view class="info-wrapper">
        <view class="glass-card list-group">
          
          <view class="list-item">
            <view class="item-left">
              <view class="icon-bg bg-orange">
                <uni-icons type="email-filled" size="18" color="#FFFFFF"></uni-icons>
              </view>
              <text class="item-text">官方邮箱</text>
            </view>
            <view class="item-right">
              <text class="item-value">contact@mindcampus.com</text>
            </view>
          </view>
          
          <view class="divider"></view>
          
          <view class="list-item">
            <view class="item-left">
              <view class="icon-bg bg-green">
                <uni-icons type="phone-filled" size="18" color="#FFFFFF"></uni-icons>
              </view>
              <text class="item-text">服务热线</text>
            </view>
            <view class="item-right">
              <text class="item-value">400-999-9999</text>
            </view>
          </view>

          <view class="divider"></view>

          <view class="list-item">
            <view class="item-left">
              <view class="icon-bg bg-blue">
                <uni-icons type="paperplane-filled" size="18" color="#FFFFFF"></uni-icons>
              </view>
              <text class="item-text">官方网站</text>
            </view>
            <view class="item-right">
              <text class="item-value link-text" @tap="copyLink">{{ url || 'www.mindcampus.com' }}</text>
            </view>
          </view>

        </view>
      </view>

      <view class="copyright-section">
        <text class="copyright-text">Copyright &copy; {{ currentYear }} MindCampus.</text>
        <text class="copyright-text">All Rights Reserved.</text>
      </view>

    </scroll-view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      statusBarHeight: 0,
      url: 'www.mindcampus.com',
      version: '1.0.0',
      currentYear: new Date().getFullYear()
    }
  },
  onLoad() {
    const sys = uni.getSystemInfoSync()
    this.statusBarHeight = sys.statusBarHeight || 0
    
    // 兼容原有的全局配置读取（增加容错）
    try {
      const config = getApp().globalData?.config?.appInfo
      if (config) {
        this.url = config.site_url || this.url
        this.version = config.version || this.version
      }
    } catch (e) {
      console.log('读取全局配置失败，使用默认值')
    }
  },
  methods: {
    goBack() {
      uni.navigateBack()
    },
    
    // 点击复制链接体验更好
    copyLink() {
      uni.setClipboardData({
        data: `https://${this.url}`,
        success: () => {
          uni.showToast({
            title: '官网链接已复制',
            icon: 'success'
          })
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
/* ==================== iOS 风格核心变量 ==================== */
$ios-text-primary: #1D1D1F;
$ios-text-secondary: #86868B;
$theme-cyan: #2CB5A0;

.about-page {
  min-height: 100vh;
  position: relative;
  background-color: #F5F5F7;
  font-family: -apple-system, BlinkMacSystemFont, "SF Pro Text", "Helvetica Neue", Arial, sans-serif;
}

/* --- 弥散光影背景 --- */
.ambient-background {
  position: fixed;
  top: 0; left: 0;
  width: 100vw; height: 100vh;
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
  border-radius: 36rpx;
}

/* ==================== 顶部导航 (Sticky) ==================== */
.glass-header {
  position: sticky;
  top: 0;
  z-index: 100;
  background: rgba(245, 245, 247, 0.65);
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

.navbar-left, .navbar-right { width: 80rpx; display: flex; align-items: center; }

.nav-icon-glass {
  width: 64rpx; height: 64rpx;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.6);
  display: flex; align-items: center; justify-content: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.04);
  transition: transform 0.2s ease;
  &:active { transform: scale(0.9); opacity: 0.8; }
}

.navbar-title { font-size: 32rpx; font-weight: 600; color: $ios-text-primary; }

/* ==================== 内容滚动区 ==================== */
.content-scroll {
  position: relative;
  z-index: 1;
  height: calc(100vh - 88rpx - env(safe-area-inset-top));
  display: flex;
  flex-direction: column;
}

/* ==================== 1. 应用标识区 (App Hero) ==================== */
.app-hero {
  padding: 80rpx 40rpx 60rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.logo-glass-box {
  width: 180rpx; height: 180rpx;
  border-radius: 40rpx; /* iOS App Icon 经典圆角 */
  background: #FFFFFF;
  display: flex; align-items: center; justify-content: center;
  margin-bottom: 32rpx;
  box-shadow: 0 16rpx 40rpx rgba(0, 0, 0, 0.08), inset 0 2rpx 4rpx rgba(255, 255, 255, 0.8);
  border: 1px solid rgba(0, 0, 0, 0.04);
  overflow: hidden;
}

.app-logo {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.app-name {
  font-size: 44rpx;
  font-weight: 700;
  color: $ios-text-primary;
  margin-bottom: 16rpx;
  font-family: "SF Pro Display", sans-serif;
  letter-spacing: 1rpx;
}

.version-badge {
  background: rgba(44, 181, 160, 0.12);
  padding: 8rpx 24rpx;
  border-radius: 30rpx;
  
  text {
    font-size: 26rpx;
    font-weight: 600;
    color: $theme-cyan;
    font-family: -apple-system, BlinkMacSystemFont, sans-serif;
  }
}

/* ==================== 2. 官方信息卡片 ==================== */
.info-wrapper {
  padding: 0 32rpx;
  margin-bottom: 60rpx;
}

.list-group {
  padding: 0 32rpx;
}

.list-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 36rpx 0;
}

.item-left {
  display: flex;
  align-items: center;
  gap: 24rpx;
}

/* 果冻色图标底座 */
.icon-bg {
  width: 56rpx; height: 56rpx;
  border-radius: 14rpx;
  display: flex; align-items: center; justify-content: center;
}

.bg-orange { background: #FF9500; }
.bg-green { background: #34C759; }
.bg-blue { background: #007AFF; }

.item-text {
  font-size: 30rpx;
  color: $ios-text-primary;
  font-weight: 500;
}

.item-right {
  display: flex;
  align-items: center;
  justify-content: flex-end;
}

.item-value {
  font-size: 30rpx;
  color: $ios-text-secondary;
  text-align: right;
}

/* 链接样式，点击有反馈 */
.link-text {
  color: #007AFF;
  transition: opacity 0.2s;
  &:active {
    opacity: 0.5;
  }
}

/* 苹果风格内凹极细分割线 */
.divider {
  height: 1px;
  background: rgba(0, 0, 0, 0.05);
  margin-left: 80rpx; 
}

/* ==================== 3. 底部版权信息 ==================== */
.copyright-section {
  margin-top: auto; /* 推到底部 */
  padding: 60rpx 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8rpx;
}

.copyright-text {
  font-size: 24rpx;
  color: #A1A1A6; /* 极浅的灰色 */
  font-family: -apple-system, sans-serif;
}
</style>