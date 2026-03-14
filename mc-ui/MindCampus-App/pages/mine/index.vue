<template>
  <view class="mine-container">
    <view class="ambient-background"></view>

    <view class="glass-header" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="navbar-content">
        <text class="navbar-title">个人中心</text>
      </view>
    </view>

    <scroll-view class="content-scroll" scroll-y>
      
      <view class="glass-card user-card" @tap="name ? handleToInfo() : handleToLogin()">
        <view class="user-info-box">
          <view class="avatar-ring">
            <image v-if="avatar" :src="avatar" mode="aspectFill" class="avatar-img" @tap.stop="handleToAvatar" />
            <view v-else class="avatar-placeholder" @tap.stop="handleToLogin">
              <uni-icons type="person-filled" size="36" color="#2CB5A0"></uni-icons>
            </view>
          </view>
          <view class="user-text-box">
            <text class="user-name">{{ name || '点击登录' }}</text>
            <text class="user-desc">{{ name ? '欢迎来到 MindCampus' : '登录体验更多治愈功能' }}</text>
          </view>
        </view>
        <view class="user-edit-btn">
          <text>主页</text>
          <uni-icons type="right" size="14" color="#86868B"></uni-icons>
        </view>
      </view>

      <view class="glass-card grid-card">
        <view class="grid-item" @tap="handleJiaoLiuQun">
          <view class="grid-icon-box icon-pink">
            <uni-icons type="chatboxes-filled" size="26" color="#FF2D55"></uni-icons>
          </view>
          <text class="grid-text">交流群</text>
        </view>
        <view class="grid-item" @tap="handleBuilding">
          <view class="grid-icon-box icon-blue">
            <uni-icons type="headphones" size="26" color="#007AFF"></uni-icons>
          </view>
          <text class="grid-text">在线客服</text>
        </view>
        <view class="grid-item" @tap="handleBuilding">
          <view class="grid-icon-box icon-purple">
            <uni-icons type="paperplane-filled" size="26" color="#AF52DE"></uni-icons>
          </view>
          <text class="grid-text">反馈社区</text>
        </view>
        <view class="grid-item" @tap="handleBuilding">
          <view class="grid-icon-box icon-green">
            <uni-icons type="heart-filled" size="26" color="#34C759"></uni-icons>
          </view>
          <text class="grid-text">点赞我们</text>
        </view>
      </view>

      <view class="glass-card list-card">
        <view class="list-item" @tap="handleToMyLikes">
          <view class="item-left">
            <view class="list-icon-bg bg-cyan">
              <uni-icons type="heart-filled" size="18" color="#FFFFFF"></uni-icons>
            </view>
            <text class="item-text">我的点赞</text>
          </view>
          <uni-icons type="right" size="16" color="#C7C7CC"></uni-icons>
        </view>

        <view class="divider"></view>

        <view class="list-item" @tap="handleToEditInfo">
          <view class="item-left">
            <view class="list-icon-bg bg-gray">
              <uni-icons type="person-filled" size="18" color="#1D1D1F"></uni-icons>
            </view>
            <text class="item-text">编辑资料</text>
          </view>
          <uni-icons type="right" size="16" color="#C7C7CC"></uni-icons>
        </view>
        
        <view class="divider"></view>
        
        <view class="list-item" @tap="handleHelp">
          <view class="item-left">
            <view class="list-icon-bg bg-orange">
              <uni-icons type="help-filled" size="18" color="#FFFFFF"></uni-icons>
            </view>
            <text class="item-text">常见问题</text>
          </view>
          <uni-icons type="right" size="16" color="#C7C7CC"></uni-icons>
        </view>
        
        <view class="divider"></view>
        
        <view class="list-item" @tap="handleAbout">
          <view class="item-left">
            <view class="list-icon-bg bg-blue-light">
              <uni-icons type="info-filled" size="18" color="#FFFFFF"></uni-icons>
            </view>
            <text class="item-text">关于我们</text>
          </view>
          <uni-icons type="right" size="16" color="#C7C7CC"></uni-icons>
        </view>
        
        <view class="divider"></view>
        
        <view class="list-item" @tap="handleToSetting">
          <view class="item-left">
            <view class="list-icon-bg bg-gray-dark">
              <uni-icons type="gear-filled" size="18" color="#FFFFFF"></uni-icons>
            </view>
            <text class="item-text">应用设置</text>
          </view>
          <uni-icons type="right" size="16" color="#C7C7CC"></uni-icons>
        </view>
      </view>

      <view class="bottom-spacer"></view>
    </scroll-view>

    <custom-tab-bar currentPath="/pages/mine/index"></custom-tab-bar>
  </view>
</template>

<script>
import CustomTabBar from '@/components/custom-tab-bar/custom-tab-bar.vue'

export default {
  components: {
    CustomTabBar
  },
  data() {
    return {
      statusBarHeight: 0
    }
  },
  computed: {
    // 采用 computed 保持数据响应式
    name() {
      return this.$store.state.user.name
    },
    avatar() {
      return this.$store.state.user.avatar
    }
  },
  onLoad() {
    const sys = uni.getSystemInfoSync()
    this.statusBarHeight = sys.statusBarHeight || 0
  },
  onShow() {
    // 强制隐藏系统原生底部栏
    uni.hideTabBar({ animation: false })
  },
  methods: {
    handleToInfo() {
      uni.navigateTo({ url: '/pages/mine/info/index' })
    },
    handleToMyLikes() {
      uni.navigateTo({ url: '/pages/mine/likes/index' })
    },
    handleToEditInfo() {
      uni.navigateTo({ url: '/pages/mine/info/edit' })
    },
    handleToSetting() {
      uni.navigateTo({ url: '/pages/mine/setting/index' })
    },
    handleToLogin() {
      uni.reLaunch({ url: '/pages/login' })
    },
    handleToAvatar() {
      uni.navigateTo({ url: '/pages/mine/avatar/index' })
    },
    handleHelp() {
      uni.navigateTo({ url: '/pages/mine/help/index' })
    },
    handleAbout() {
      uni.navigateTo({ url: '/pages/mine/about/index' })
    },
    handleJiaoLiuQun() {
      uni.showToast({ title: 'QQ群：①133713780(满)、②146013835(满)', icon: 'none', duration: 3000 })
    },
    handleBuilding() {
      uni.showToast({ title: '模块建设中，敬请期待~', icon: 'none' })
    }
  }
}
</script>

<style lang="scss" scoped>
/* ==================== iOS 风格核心变量 ==================== */
$ios-text-primary: #1D1D1F;
$ios-text-secondary: #86868B;
$theme-cyan: #2CB5A0;

.mine-container {
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
  border-radius: 40rpx;
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
  justify-content: center;
  height: 88rpx;
  padding: 0 32rpx;
}

.navbar-title {
  font-size: 34rpx;
  font-weight: 600;
  color: $ios-text-primary;
  letter-spacing: 1rpx;
}

/* ==================== 内容滚动区 ==================== */
.content-scroll {
  position: relative;
  z-index: 1;
  padding: 32rpx;
  display: flex;
  flex-direction: column;
}

/* --- 1. 个人名片 --- */
.user-card {
  padding: 48rpx 36rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 32rpx;
  transition: transform 0.2s;
  
  &:active { transform: scale(0.98); background: rgba(255,255,255,0.8); }
}

.user-info-box {
  display: flex;
  align-items: center;
  gap: 32rpx;
}

.avatar-ring {
  width: 120rpx; height: 120rpx;
  border-radius: 50%;
  padding: 6rpx;
  background: linear-gradient(135deg, #48D1CC 0%, #2CB5A0 100%);
  box-shadow: 0 8rpx 20rpx rgba(44, 181, 160, 0.3);
}

.avatar-img {
  width: 100%; height: 100%;
  border-radius: 50%;
  border: 4rpx solid #FFFFFF;
  box-sizing: border-box;
}

.avatar-placeholder {
  width: 100%; height: 100%;
  border-radius: 50%;
  border: 4rpx solid #FFFFFF;
  background: rgba(255, 255, 255, 0.9);
  display: flex; align-items: center; justify-content: center;
  box-sizing: border-box;
}

.user-text-box {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.user-name {
  font-size: 40rpx;
  font-weight: 700;
  color: $ios-text-primary;
  font-family: "SF Pro Display", sans-serif;
}

.user-desc {
  font-size: 26rpx;
  color: $ios-text-secondary;
}

.user-edit-btn {
  display: flex;
  align-items: center;
  gap: 4rpx;
  background: rgba(0, 0, 0, 0.04);
  padding: 12rpx 24rpx;
  border-radius: 30rpx;
  
  text { font-size: 24rpx; color: $ios-text-secondary; font-weight: 500; }
}

/* --- 2. 快捷操作网格 --- */
.grid-card {
  padding: 40rpx 20rpx;
  display: flex;
  justify-content: space-around;
  margin-bottom: 32rpx;
}

.grid-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16rpx;
  transition: transform 0.2s cubic-bezier(0.2, 0.8, 0.2, 1);
  
  &:active { transform: scale(0.9); }
}

.grid-icon-box {
  width: 96rpx; height: 96rpx;
  border-radius: 32rpx;
  display: flex; align-items: center; justify-content: center;
}

.icon-pink { background: rgba(255, 45, 85, 0.1); }
.icon-blue { background: rgba(0, 122, 255, 0.1); }
.icon-purple { background: rgba(175, 82, 222, 0.1); }
.icon-green { background: rgba(52, 199, 89, 0.1); }

.grid-text {
  font-size: 24rpx;
  color: $ios-text-primary;
  font-weight: 500;
}

/* --- 3. iOS 设置风格菜单列表 --- */
.list-card {
  padding: 0 32rpx;
  margin-bottom: 40rpx;
}

.list-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 32rpx 0;
  transition: opacity 0.2s ease;
  
  &:active { opacity: 0.6; }
}

.item-left {
  display: flex;
  align-items: center;
  gap: 24rpx;
}

.list-icon-bg {
  width: 56rpx; height: 56rpx;
  border-radius: 14rpx;
  display: flex; align-items: center; justify-content: center;
}

.bg-gray { background: #E5E5EA; }
.bg-orange { background: #FF9500; }
.bg-blue-light { background: #5AC8FA; }
.bg-gray-dark { background: #8E8E93; }
.bg-cyan { background: #2CB5A0; }

.item-text {
  font-size: 30rpx;
  color: $ios-text-primary;
  font-weight: 500;
}

/* 苹果风格内凹分割线 */
.divider {
  height: 1px;
  background: rgba(0, 0, 0, 0.05);
  margin-left: 80rpx; 
}

/* 底部防遮挡留白 */
.bottom-spacer {
  height: calc(160rpx + env(safe-area-inset-bottom));
  width: 100%;
}
</style>