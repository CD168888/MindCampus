<template>
  <view class="user-info-page">
    <view class="ambient-background"></view>

    <view class="glass-header" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="navbar-content">
        <view class="navbar-left" @tap="goBack">
          <view class="nav-icon-glass">
            <uni-icons type="left" size="22" color="#1D1D1F"></uni-icons>
          </view>
        </view>
        <view class="navbar-title">个人资料</view>
        <view class="navbar-right"></view>
      </view>
    </view>

    <scroll-view class="content-scroll" scroll-y>
      <view class="settings-wrapper">
        
        <view class="glass-card hero-card">
          <view class="avatar-ring">
            <image v-if="avatar" :src="avatar" mode="aspectFill" class="avatar-img" />
            <view v-else class="avatar-placeholder">
              <uni-icons type="person-filled" size="40" color="#2CB5A0"></uni-icons>
            </view>
          </view>
          <text class="hero-name">{{ user.nickName || '未命名用户' }}</text>
          <text class="hero-role">{{ roleGroup || '专属心理学员' }}</text>
        </view>

        <view class="section-title">基本信息</view>
        <view class="glass-card list-group">
          <view class="list-item" @tap="goToEdit">
            <view class="item-left">
              <view class="icon-bg bg-blue">
                <uni-icons type="person-filled" size="18" color="#FFFFFF"></uni-icons>
              </view>
              <text class="item-text">昵称</text>
            </view>
            <view class="item-right">
              <text class="item-value">{{ user.nickName || '未设置' }}</text>
              <uni-icons type="right" size="16" color="#C7C7CC" style="margin-left: 8rpx;"></uni-icons>
            </view>
          </view>

          <view class="divider"></view>

          <view class="list-item" @tap="goToEdit">
            <view class="item-left">
              <view class="icon-bg bg-green">
                <uni-icons type="phone-filled" size="18" color="#FFFFFF"></uni-icons>
              </view>
              <text class="item-text">手机号码</text>
            </view>
            <view class="item-right">
              <text class="item-value">{{ user.phonenumber || '未绑定' }}</text>
              <uni-icons type="right" size="16" color="#C7C7CC" style="margin-left: 8rpx;"></uni-icons>
            </view>
          </view>

          <view class="divider"></view>

          <view class="list-item" @tap="goToEdit">
            <view class="item-left">
              <view class="icon-bg bg-orange">
                <uni-icons type="email-filled" size="18" color="#FFFFFF"></uni-icons>
              </view>
              <text class="item-text">邮箱</text>
            </view>
            <view class="item-right">
              <text class="item-value">{{ user.email || '未绑定' }}</text>
              <uni-icons type="right" size="16" color="#C7C7CC" style="margin-left: 8rpx;"></uni-icons>
            </view>
          </view>
        </view>

        <view class="section-title">系统档案</view>
        <view class="glass-card list-group">
          <view class="list-item">
            <view class="item-left">
              <view class="icon-bg bg-purple">
                <uni-icons type="vip-filled" size="18" color="#FFFFFF"></uni-icons>
              </view>
              <text class="item-text">岗位</text>
            </view>
            <view class="item-right">
              <text class="item-value">{{ postGroup || '暂无' }}</text>
            </view>
          </view>
          
          <view class="divider"></view>
          
          <view class="list-item">
            <view class="item-left">
              <view class="icon-bg bg-cyan">
                <uni-icons type="star-filled" size="18" color="#FFFFFF"></uni-icons>
              </view>
              <text class="item-text">角色</text>
            </view>
            <view class="item-right">
              <text class="item-value">{{ roleGroup || '暂无' }}</text>
            </view>
          </view>

          <view class="divider"></view>

          <view class="list-item">
            <view class="item-left">
              <view class="icon-bg bg-gray">
                <uni-icons type="calendar-filled" size="18" color="#FFFFFF"></uni-icons>
              </view>
              <text class="item-text">注册时间</text>
            </view>
            <view class="item-right">
              <text class="item-value">{{ user.createTime ? formatTime(user.createTime) : '未知' }}</text>
            </view>
          </view>
        </view>

      </view>
      
      <view class="bottom-spacer"></view>
    </scroll-view>
  </view>
</template>

<script>
import {getUserProfile} from "@/api/system/user"

export default {
  data() {
    return {
      statusBarHeight: 0,
      user: {},
      roleGroup: "",
      postGroup: ""
    }
  },
  computed: {
    avatar() {
      return this.$store.state.user.avatar
    }
  },
  onLoad() {
    const sys = uni.getSystemInfoSync()
    this.statusBarHeight = sys.statusBarHeight || 0
    
    this.getUser()
  },
  methods: {
    goBack() {
      uni.navigateBack()
    },

    goToEdit() {
      uni.navigateTo({
        url: '/pages/mine/info/edit'
      })
    },

    getUser() {
      uni.showLoading({ title: '加载中...', mask: true })
      getUserProfile().then(response => {
        uni.hideLoading()
        this.user = response.data || {}
        this.roleGroup = response.roleGroup || ""
        this.postGroup = response.postGroup || ""
      }).catch(() => {
        uni.hideLoading()
        uni.showToast({ title: '获取信息失败', icon: 'none' })
      })
    },

    formatTime(timeStr) {
      if (!timeStr) return ''
      // 如果只需显示年月日，可做简单截取：
      return timeStr.split(' ')[0] 
    }
  }
}
</script>

<style lang="scss" scoped>
/* ==================== iOS 风格核心变量 ==================== */
$ios-text-primary: #1D1D1F;
$ios-text-secondary: #86868B;
$theme-cyan: #2CB5A0;

.user-info-page {
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
}

.settings-wrapper {
  padding: 20rpx 32rpx;
  display: flex;
  flex-direction: column;
}

/* ==================== 1. 顶部视觉锚点 (Hero Card) ==================== */
.hero-card {
  margin-top: 10rpx;
  margin-bottom: 20rpx;
  padding: 60rpx 40rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: linear-gradient(180deg, rgba(255,255,255,0.8) 0%, rgba(255,255,255,0.5) 100%);
}

.avatar-ring {
  width: 160rpx; height: 160rpx;
  border-radius: 50%;
  padding: 6rpx;
  background: linear-gradient(135deg, #48D1CC 0%, #2CB5A0 100%);
  box-shadow: 0 12rpx 32rpx rgba(44, 181, 160, 0.25);
  margin-bottom: 24rpx;
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

.hero-name {
  font-size: 44rpx;
  font-weight: 700;
  color: $ios-text-primary;
  margin-bottom: 8rpx;
  font-family: "SF Pro Display", sans-serif;
}

.hero-role {
  font-size: 26rpx;
  color: $theme-cyan;
  background: rgba(44, 181, 160, 0.12);
  padding: 6rpx 20rpx;
  border-radius: 20rpx;
  font-weight: 500;
}

/* ==================== 分组标题 ==================== */
.section-title {
  font-size: 26rpx;
  color: $ios-text-secondary;
  margin: 32rpx 0 16rpx 24rpx;
  text-transform: uppercase;
}

/* ==================== 列表卡片组 ==================== */
.list-group {
  padding: 0 32rpx;
  margin-bottom: 16rpx;
}

.list-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 32rpx 0;
  transition: background-color 0.2s ease;
  &:active {
    background-color: rgba(0, 0, 0, 0.03);
  }
}

.item-left {
  display: flex;
  align-items: center;
  gap: 24rpx;
}

/* 多巴胺系底色图标 */
.icon-bg {
  width: 56rpx; height: 56rpx;
  border-radius: 14rpx;
  display: flex; align-items: center; justify-content: center;
}

/* 根据功能隐喻定义色彩 */
.bg-blue { background: #007AFF; }
.bg-green { background: #34C759; }
.bg-orange { background: #FF9500; }
.bg-purple { background: #AF52DE; }
.bg-cyan { background: #32ADE6; }
.bg-gray { background: #8E8E93; }

.item-text {
  font-size: 30rpx;
  color: $ios-text-primary;
  font-weight: 500;
}

.item-right {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  max-width: 50%; /* 防止文字过长挤压左侧 */
}

.item-value {
  font-size: 30rpx;
  color: $ios-text-secondary;
  text-align: right;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 苹果风格内凹分割线 */
.divider {
  height: 1px;
  background: rgba(0, 0, 0, 0.05);
  margin-left: 80rpx; 
}

/* 底部防遮挡留白 */
.bottom-spacer {
  height: calc(80rpx + env(safe-area-inset-bottom));
}
</style>