<template>
  <view class="pwd-retrieve-page">
    <view class="ambient-background"></view>

    <view class="glass-header" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="navbar-content">
        <view class="navbar-left" @tap="goBack">
          <view class="nav-icon-glass">
            <uni-icons type="left" size="22" color="#1D1D1F"></uni-icons>
          </view>
        </view>
        <view class="navbar-title">修改密码</view>
        <view class="navbar-right"></view>
      </view>
    </view>

    <scroll-view class="content-scroll" scroll-y>
      
      <view class="security-hero">
        <view class="shield-glass-box">
          <uni-icons type="locked-filled" size="48" color="#2CB5A0"></uni-icons>
        </view>
        <text class="hero-title">账号安全升级</text>
        <text class="hero-desc">定期修改密码可以有效保护您的账号安全</text>
      </view>

      <view class="glass-card form-group">
        <view class="input-row">
          <text class="input-label">旧密码</text>
          <input 
            class="ios-input" 
            :password="!showOldPwd" 
            v-model="user.oldPassword" 
            placeholder="请输入当前密码" 
            placeholder-class="ios-placeholder"
          />
          <view class="eye-btn" @tap="showOldPwd = !showOldPwd">
            <uni-icons :type="showOldPwd ? 'eye-filled' : 'eye-slash-filled'" size="20" :color="showOldPwd ? '#2CB5A0' : '#C7C7CC'"></uni-icons>
          </view>
        </view>

        <view class="divider"></view>

        <view class="input-row">
          <text class="input-label">新密码</text>
          <input 
            class="ios-input" 
            :password="!showNewPwd" 
            v-model="user.newPassword" 
            placeholder="6-20个字符" 
            placeholder-class="ios-placeholder"
            maxlength="20"
          />
          <view class="eye-btn" @tap="showNewPwd = !showNewPwd">
            <uni-icons :type="showNewPwd ? 'eye-filled' : 'eye-slash-filled'" size="20" :color="showNewPwd ? '#2CB5A0' : '#C7C7CC'"></uni-icons>
          </view>
        </view>

        <view class="divider"></view>

        <view class="input-row">
          <text class="input-label">确认密码</text>
          <input 
            class="ios-input" 
            :password="!showConfirmPwd" 
            v-model="user.confirmPassword" 
            placeholder="请再次输入新密码" 
            placeholder-class="ios-placeholder"
            maxlength="20"
          />
          <view class="eye-btn" @tap="showConfirmPwd = !showConfirmPwd">
            <uni-icons :type="showConfirmPwd ? 'eye-filled' : 'eye-slash-filled'" size="20" :color="showConfirmPwd ? '#2CB5A0' : '#C7C7CC'"></uni-icons>
          </view>
        </view>
      </view>

      <view class="submit-section">
        <view class="action-btn" :class="{ 'is-active': canSubmit }" @tap="submit">
          <text>{{ loading ? '提交中...' : '确认修改' }}</text>
        </view>
      </view>

    </scroll-view>
  </view>
</template>

<script>
import {updateUserPwd} from "@/api/system/user"

export default {
  data() {
    return {
      statusBarHeight: 0,
      loading: false,
      showOldPwd: false,
      showNewPwd: false,
      showConfirmPwd: false,
      user: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      }
    }
  },
  computed: {
    // 监听所有字段是否已填，用于点亮按钮
    canSubmit() {
      return this.user.oldPassword.trim().length > 0 && 
             this.user.newPassword.trim().length > 0 && 
             this.user.confirmPassword.trim().length > 0
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
    
    // 纯手工轻量验证，完美适配高级 UI 交互
    validateForm() {
      if (!this.user.oldPassword) {
        uni.showToast({ title: '请输入旧密码', icon: 'none' })
        return false
      }
      if (!this.user.newPassword) {
        uni.showToast({ title: '请输入新密码', icon: 'none' })
        return false
      }
      if (this.user.newPassword.length < 6 || this.user.newPassword.length > 20) {
        uni.showToast({ title: '新密码长度需在6-20位之间', icon: 'none' })
        return false
      }
      if (this.user.newPassword === this.user.oldPassword) {
        uni.showToast({ title: '新密码不能与旧密码相同', icon: 'none' })
        return false
      }
      if (this.user.confirmPassword !== this.user.newPassword) {
        uni.showToast({ title: '两次输入的密码不一致', icon: 'none' })
        return false
      }
      return true
    },

    submit() {
      if (!this.canSubmit || this.loading) return
      if (!this.validateForm()) return

      this.loading = true
      uni.showLoading({ title: '安全验证中...', mask: true })

      updateUserPwd(this.user.oldPassword, this.user.newPassword).then(response => {
        this.loading = false
        uni.hideLoading()
        
        // 兼容原来的 this.$modal.msgSuccess 或使用原生 API
        uni.showToast({ title: '密码修改成功', icon: 'success' })
        
        setTimeout(() => {
          this.goBack()
        }, 1500)
      }).catch(() => {
        this.loading = false
        uni.hideLoading()
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

.pwd-retrieve-page {
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
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.04);
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

/* ==================== 1. 顶部安全图形 ==================== */
.security-hero {
  padding: 60rpx 40rpx 40rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.shield-glass-box {
  width: 140rpx; height: 140rpx;
  border-radius: 40rpx;
  background: rgba(44, 181, 160, 0.15);
  backdrop-filter: blur(10px);
  display: flex; align-items: center; justify-content: center;
  margin-bottom: 32rpx;
  box-shadow: 0 12rpx 32rpx rgba(44, 181, 160, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.6);
}

.hero-title {
  font-size: 40rpx;
  font-weight: 700;
  color: $ios-text-primary;
  margin-bottom: 16rpx;
  letter-spacing: 1rpx;
}

.hero-desc {
  font-size: 26rpx;
  color: $ios-text-secondary;
  max-width: 80%;
  line-height: 1.5;
}

/* ==================== 2. 表单卡片 ==================== */
.form-group {
  margin: 0 32rpx;
  padding: 0 32rpx;
  display: flex;
  flex-direction: column;
}

.input-row {
  display: flex;
  align-items: center;
  padding: 36rpx 0;
}

.input-label {
  width: 160rpx; /* 固定标签宽度，让输入框对齐 */
  font-size: 30rpx;
  font-weight: 500;
  color: $ios-text-primary;
}

.ios-input {
  flex: 1;
  font-size: 30rpx;
  color: $ios-text-primary;
}

.ios-placeholder {
  color: #C7C7CC;
}

.eye-btn {
  padding: 10rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.divider {
  height: 1px;
  background: rgba(0, 0, 0, 0.05);
  margin-left: 160rpx; /* 内凹分割线，iOS 标准做法 */
}

/* ==================== 3. 底部提交栏 ==================== */
.submit-section {
  padding: 80rpx 40rpx;
}

.action-btn {
  width: 100%;
  height: 96rpx;
  border-radius: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32rpx;
  font-weight: 600;
  background: rgba(0, 0, 0, 0.05);
  color: #A1A1A6;
  transition: all 0.3s cubic-bezier(0.2, 0.8, 0.2, 1);
  box-shadow: none;
}

/* 激活状态的高亮渐变 */
.action-btn.is-active {
  background: linear-gradient(135deg, #48D1CC 0%, #2CB5A0 100%);
  color: #FFFFFF;
  box-shadow: 0 8rpx 24rpx rgba(44, 181, 160, 0.3);
  
  &:active {
    transform: scale(0.96);
    box-shadow: 0 4rpx 12rpx rgba(44, 181, 160, 0.2);
  }
}
</style>