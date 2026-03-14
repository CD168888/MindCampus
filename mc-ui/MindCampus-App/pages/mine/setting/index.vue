<template>
  <view class="setting-page">
    <view class="ambient-background"></view>

    <view class="glass-header" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="navbar-content">
        <view class="navbar-left" @tap="goBack">
          <view class="nav-icon-glass">
            <uni-icons type="left" size="22" color="#1D1D1F"></uni-icons>
          </view>
        </view>
        <view class="navbar-title">应用设置</view>
        <view class="navbar-right"></view>
      </view>
    </view>

    <scroll-view class="content-scroll" scroll-y>
      <view class="settings-wrapper">
        
        <view class="section-title">账号与安全</view>
        <view class="glass-card list-group">
          <view class="list-item" @tap="handleToPwd">
            <view class="item-left">
              <view class="icon-bg bg-blue">
                <uni-icons type="locked-filled" size="18" color="#FFFFFF"></uni-icons>
              </view>
              <text class="item-text">修改密码</text>
            </view>
            <view class="item-right">
              <uni-icons type="right" size="16" color="#C7C7CC"></uni-icons>
            </view>
          </view>
        </view>

        <view class="section-title">通用</view>
        <view class="glass-card list-group">
          <view class="list-item" @tap="handleToUpgrade">
            <view class="item-left">
              <view class="icon-bg bg-orange">
                <uni-icons type="reload" size="18" color="#FFFFFF"></uni-icons>
              </view>
              <text class="item-text">检查更新</text>
            </view>
            <view class="item-right">
              <text class="item-value">v1.0.2</text>
              <uni-icons type="right" size="16" color="#C7C7CC"></uni-icons>
            </view>
          </view>
          
          <view class="divider"></view>
          
          <view class="list-item" @tap="handleCleanTmp">
            <view class="item-left">
              <view class="icon-bg bg-gray">
                <uni-icons type="trash-filled" size="18" color="#FFFFFF"></uni-icons>
              </view>
              <text class="item-text">清理缓存</text>
            </view>
            <view class="item-right">
              <text class="item-value">{{ cacheSize }}</text>
              <uni-icons type="right" size="16" color="#C7C7CC"></uni-icons>
            </view>
          </view>
        </view>

        <view class="glass-card logout-card" @tap="handleLogout">
          <text class="logout-text">退出登录</text>
        </view>

      </view>
      
      <view class="bottom-spacer"></view>
    </scroll-view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      statusBarHeight: 0,
      cacheSize: '128.5 MB' // 模拟初始缓存大小
    }
  },
  onLoad() {
    const sys = uni.getSystemInfoSync()
    this.statusBarHeight = sys.statusBarHeight || 0
  },
  methods: {
    goBack() {
      uni.navigateBack()
    },
    
    handleToPwd() {
      // 兼容原有的路由跳转方式或使用原生 uni API
      if (this.$tab) {
        this.$tab.navigateTo('/pages/mine/pwd/index')
      } else {
        uni.navigateTo({ url: '/pages/mine/pwd/index' })
      }
    },
    
    handleToUpgrade() {
      uni.showToast({ title: '当前已是最新版本', icon: 'none' })
    },
    
    handleCleanTmp() {
      if (this.cacheSize === '0 B') {
        uni.showToast({ title: '缓存已清理', icon: 'none' })
        return
      }
      
      uni.showLoading({ title: '正在清理...', mask: true })
      
      // 模拟清理过程
      setTimeout(() => {
        uni.hideLoading()
        this.cacheSize = '0 B'
        uni.vibrateShort() // 触觉反馈
        uni.showToast({ title: '清理完成', icon: 'success' })
      }, 800)
    },
    
    handleLogout() {
      // iOS 标准的底部弹出动作菜单 (ActionSheet) 体验更好
      uni.showActionSheet({
        itemList: ['退出登录'],
        itemColor: '#FF3B30', // 红色警示
        success: (res) => {
          if (res.tapIndex === 0) {
            this.executeLogout()
          }
        }
      })
    },
    
    executeLogout() {
      uni.showLoading({ title: '正在退出...' })
      this.$store.dispatch('LogOut').then(() => {
        uni.hideLoading()
        if (this.$tab) {
          this.$tab.reLaunch('/pages/index')
        } else {
          uni.reLaunch({ url: '/pages/index' })
        }
      }).catch(() => {
        uni.hideLoading()
        uni.showToast({ title: '退出失败，请重试', icon: 'none' })
      })
    }
  }
}
</script>

<style lang="scss" scoped>
/* ==================== iOS 风格核心变量 ==================== */
$ios-text-primary: #1D1D1F;
$ios-text-secondary: #86868B;
$ios-danger: #FF3B30;

.setting-page {
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
  border-radius: 32rpx;
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
  height: 100vh;
}

.settings-wrapper {
  padding: 20rpx 32rpx;
  display: flex;
  flex-direction: column;
}

/* 分组标题 */
.section-title {
  font-size: 26rpx;
  color: $ios-text-secondary;
  margin: 32rpx 0 16rpx 24rpx;
  text-transform: uppercase;
}

/* 列表卡片组 */
.list-group {
  padding: 0 32rpx;
  margin-bottom: 16rpx;
}

.list-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 32rpx 0;
  transition: opacity 0.2s ease;
  
  &:active { opacity: 0.5; }
}

.item-left {
  display: flex;
  align-items: center;
  gap: 24rpx;
}

/* 图标底座 (多巴胺色系) */
.icon-bg {
  width: 56rpx; height: 56rpx;
  border-radius: 14rpx;
  display: flex; align-items: center; justify-content: center;
}

.bg-blue { background: #007AFF; }
.bg-orange { background: #FF9500; }
.bg-gray { background: #8E8E93; }

.item-text {
  font-size: 30rpx;
  color: $ios-text-primary;
  font-weight: 500;
}

.item-right {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.item-value {
  font-size: 28rpx;
  color: $ios-text-secondary;
}

/* 苹果风格内凹分割线 */
.divider {
  height: 1px;
  background: rgba(0, 0, 0, 0.05);
  margin-left: 80rpx; 
}

/* ==================== 退出登录卡片 ==================== */
.logout-card {
  margin-top: 60rpx;
  padding: 36rpx;
  display: flex;
  justify-content: center;
  align-items: center;
  transition: all 0.2s ease;
  background: rgba(255, 255, 255, 0.8);
  
  &:active { 
    transform: scale(0.96); 
    background: rgba(255, 255, 255, 0.9);
  }
}

.logout-text {
  font-size: 32rpx;
  font-weight: 600;
  color: $ios-danger; /* iOS 标准警告红 */
  letter-spacing: 1rpx;
}

/* 底部防遮挡留白 */
.bottom-spacer {
  height: calc(120rpx + env(safe-area-inset-bottom));
}
</style>