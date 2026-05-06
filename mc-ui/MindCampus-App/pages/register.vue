<template>
  <view class="register-page">
    <view class="ambient-background"></view>

    <view class="glass-header" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="navbar-content">
      <!--
        <view class="navbar-left" @tap="handleUserLogin">
          <view class="nav-icon-glass">
            <uni-icons type="left" size="22" color="#1D1D1F"></uni-icons>
          </view>
        </view>
         <view class="navbar-title">注册账号</view> 
        <view class="navbar-right"></view>
      -->
      </view>
    </view>

    <scroll-view class="content-scroll" scroll-y>
      <view class="register-wrapper">
        
        <view class="hero-section">
          <view class="logo-glass-box">
            <image v-if="globalConfig.appInfo && globalConfig.appInfo.logo" :src="globalConfig.appInfo.logo" mode="aspectFill" class="app-logo"></image>
            <uni-icons v-else type="paperplane-filled" size="48" color="#2CB5A0"></uni-icons>
          </view>
          <text class="hero-title">加入 青矜智护</text>
          <text class="hero-desc">创建您的专属账号，开启治愈之旅</text>
        </view>

        <view class="glass-card form-group">
          
          <view class="input-row">
            <view class="icon-bg bg-blue">
              <uni-icons type="person-filled" size="20" color="#FFFFFF"></uni-icons>
            </view>
            <input 
              v-model="registerForm.username" 
              class="ios-input" 
              type="text" 
              placeholder="请输入账号 (如手机号)" 
              placeholder-class="ios-placeholder"
              maxlength="30" 
            />
          </view>

          <view class="divider"></view>

          <view class="input-row">
            <view class="icon-bg bg-orange">
              <uni-icons type="locked-filled" size="20" color="#FFFFFF"></uni-icons>
            </view>
            <input 
              v-model="registerForm.password" 
              type="password" 
              class="ios-input" 
              placeholder="请输入密码" 
              placeholder-class="ios-placeholder"
              maxlength="20" 
            />
          </view>

          <view class="divider"></view>

          <view class="input-row">
            <view class="icon-bg bg-purple">
              <uni-icons type="checkmarkempty" size="20" color="#FFFFFF"></uni-icons>
            </view>
            <input 
              v-model="registerForm.confirmPassword" 
              type="password" 
              class="ios-input" 
              placeholder="请再次输入密码" 
              placeholder-class="ios-placeholder"
              maxlength="20" 
            />
          </view>

          <template v-if="captchaEnabled">
            <view class="divider"></view>
            <view class="input-row captcha-row">
              <view class="icon-bg bg-cyan">
                <uni-icons type="info-filled" size="20" color="#FFFFFF"></uni-icons>
              </view>
              <input 
                v-model="registerForm.code" 
                type="text" 
                class="ios-input captcha-input" 
                placeholder="验证码" 
                placeholder-class="ios-placeholder"
                maxlength="4" 
              />
              <view class="captcha-img-box" @tap="getCode">
                <image :src="codeUrl" class="captcha-img" mode="aspectFit"></image>
              </view>
            </view>
          </template>

        </view>

        <view class="action-section">
          <view class="register-btn" :class="{ 'is-active': canSubmit }" @tap="handleRegister">
            <uni-icons v-if="isRegistering" type="spinner-cycle" size="20" color="#FFFFFF" class="spin-icon"></uni-icons>
            <text>{{ isRegistering ? '正在注册...' : '注 册' }}</text>
          </view>

          <view class="login-link">
            <text class="text-secondary">已有账号？</text>
            <text class="text-primary" @tap="handleUserLogin">立即登录</text>
          </view>
        </view>

      </view>
    </scroll-view>
  </view>
</template>

<script>
import {getCodeImg, register} from '@/api/login'

export default {
  data() {
    return {
      statusBarHeight: 0,
      codeUrl: "",
      captchaEnabled: true,
      isRegistering: false,
      globalConfig: {},
      registerForm: {
        username: "",
        password: "",
        confirmPassword: "",
        code: "",
        uuid: ""
      }
    }
  },
  computed: {
    // 动态判断必填项，点亮按钮
    canSubmit() {
      const userReady = this.registerForm.username.trim().length > 0
      const pwdReady = this.registerForm.password.length > 0
      const confirmReady = this.registerForm.confirmPassword.length > 0
      const codeReady = this.captchaEnabled ? this.registerForm.code.trim().length > 0 : true
      return userReady && pwdReady && confirmReady && codeReady
    }
  },
  created() {
    try {
      this.globalConfig = getApp().globalData.config || {}
    } catch(e) {
      console.log('读取全局配置失败')
    }
    this.getCode()
  },
  onLoad() {
    const sys = uni.getSystemInfoSync()
    this.statusBarHeight = sys.statusBarHeight || 0
  },
  methods: {
    // 返回登录页
    handleUserLogin() {
      uni.navigateBack()
    },
    
    // 获取图形验证码
    getCode() {
      getCodeImg().then(res => {
        this.captchaEnabled = res.captchaEnabled === undefined ? true : res.captchaEnabled
        if (this.captchaEnabled && res.img) {
          this.codeUrl = 'data:image/gif;base64,' + res.img
          this.registerForm.uuid = res.uuid
        }
      }).catch(() => {
        console.log('验证码加载失败')
      })
    },
    
    // 验证并注册
    async handleRegister() {
      if (!this.canSubmit || this.isRegistering) return

      if (this.registerForm.username === "") {
        uni.showToast({ title: "请输入账号", icon: 'none' })
        return
      }
      if (this.registerForm.password === "") {
        uni.showToast({ title: "请输入密码", icon: 'none' })
        return
      }
      if (this.registerForm.password !== this.registerForm.confirmPassword) {
        uni.showToast({ title: "两次输入的密码不一致", icon: 'none' })
        return
      }
      if (this.captchaEnabled && this.registerForm.code === "") {
        uni.showToast({ title: "请输入验证码", icon: 'none' })
        return
      }

      this.isRegistering = true
      this.executeRegister()
    },
    
    // 执行注册请求
    async executeRegister() {
      uni.showLoading({ title: "注册中...", mask: true })
      
      register(this.registerForm).then(res => {
        uni.hideLoading()
        this.isRegistering = false
        
        // 注册成功的 iOS 风格弹窗
        uni.showModal({
          title: "欢迎加入",
          content: `恭喜，您的账号 ${this.registerForm.username} 注册成功！`,
          confirmText: "去登录",
          confirmColor: "#2CB5A0",
          showCancel: false,
          success: (res) => {
            if (res.confirm) {
              uni.redirectTo({ url: `/pages/login` })
            }
          }
        })
      }).catch(() => {
        uni.hideLoading()
        this.isRegistering = false
        if (this.captchaEnabled) {
          this.registerForm.code = ""
          this.getCode()
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

.register-page {
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

/* ==================== 顶部导航 (Sticky) ==================== */
.glass-header {
  position: sticky;
  top: 0;
  z-index: 100;
  background: transparent; /* 注册页同样采用透明顶部，更显沉浸 */
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
  backdrop-filter: blur(20px);
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

.register-wrapper {
  padding: 20rpx 40rpx 60rpx;
  display: flex;
  flex-direction: column;
}

/* ==================== 1. 沉浸式欢迎区 ==================== */
.hero-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  margin-top: 20rpx;
  margin-bottom: 60rpx;
}

.logo-glass-box {
  width: 160rpx; height: 160rpx;
  border-radius: 40rpx;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(20px);
  display: flex; align-items: center; justify-content: center;
  margin-bottom: 40rpx;
  box-shadow: 0 16rpx 40rpx rgba(0, 0, 0, 0.08), inset 0 2rpx 4rpx rgba(255, 255, 255, 0.8);
  border: 1px solid rgba(255, 255, 255, 0.9);
  overflow: hidden;
}

.app-logo { width: 100%; height: 100%; }

.hero-title {
  font-size: 46rpx;
  font-weight: 700;
  color: $ios-text-primary;
  margin-bottom: 16rpx;
  font-family: "SF Pro Display", sans-serif;
  letter-spacing: -0.5rpx;
}

.hero-desc {
  font-size: 28rpx;
  color: $ios-text-secondary;
}

/* ==================== 2. iOS 聚合毛玻璃表单 ==================== */
.glass-card {
  background: rgba(255, 255, 255, 0.65);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.04);
  border-radius: 36rpx;
  overflow: hidden;
  margin-bottom: 60rpx;
}

.input-row {
  display: flex;
  align-items: center;
  padding: 32rpx 40rpx;
  gap: 24rpx;
}

/* 多巴胺系底色图标 */
.icon-bg {
  width: 60rpx; height: 60rpx;
  border-radius: 16rpx;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
}
.bg-blue { background: linear-gradient(135deg, #5AC8FA 0%, #007AFF 100%); }
.bg-orange { background: linear-gradient(135deg, #FFD60A 0%, #FF9500 100%); }
.bg-purple { background: linear-gradient(135deg, #E58BFF 0%, #AF52DE 100%); } /* 确认密码用高贵紫 */
.bg-cyan { background: linear-gradient(135deg, #48D1CC 0%, #2CB5A0 100%); }

.ios-input {
  flex: 1;
  font-size: 30rpx;
  color: $ios-text-primary;
  height: 60rpx;
}

.ios-placeholder { color: #A1A1A6; }

.divider {
  height: 1px;
  background: rgba(0, 0, 0, 0.05);
  margin-left: 120rpx; /* 内凹分割线 */
}

/* 验证码特殊布局 */
.captcha-input { max-width: 200rpx; }

.captcha-img-box {
  width: 180rpx; height: 68rpx;
  border-radius: 16rpx;
  overflow: hidden;
  margin-left: auto;
  background: #E5E5EA;
  transition: transform 0.2s ease;
  &:active { transform: scale(0.95); }
}

.captcha-img { width: 100%; height: 100%; }

/* ==================== 3. 操作区 ==================== */
.action-section {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.register-btn {
  width: 100%;
  height: 96rpx;
  border-radius: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16rpx;
  font-size: 32rpx;
  font-weight: 600;
  background: rgba(0, 0, 0, 0.05);
  color: #A1A1A6;
  transition: all 0.3s cubic-bezier(0.2, 0.8, 0.2, 1);
}

/* 激活状态的高亮渐变 */
.register-btn.is-active {
  background: linear-gradient(135deg, #48D1CC 0%, #2CB5A0 100%);
  color: #FFFFFF;
  box-shadow: 0 8rpx 24rpx rgba(44, 181, 160, 0.3);
  
  &:active {
    transform: scale(0.96);
    box-shadow: 0 4rpx 12rpx rgba(44, 181, 160, 0.2);
  }
}

.spin-icon { animation: spin 1s linear infinite; }
@keyframes spin { 100% { transform: rotate(360deg); } }

.login-link {
  margin-top: 40rpx;
  font-size: 28rpx;
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.text-secondary { color: $ios-text-secondary; }

.text-primary {
  color: #007AFF; /* iOS 原生蓝色链接 */
  font-weight: 600;
  transition: opacity 0.2s;
  &:active { opacity: 0.5; }
}
</style>