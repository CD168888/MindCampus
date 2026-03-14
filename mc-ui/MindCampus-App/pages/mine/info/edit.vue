<template>
  <view class="edit-profile-page">
    <view class="ambient-background"></view>

    <view class="glass-header" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="navbar-content">
        <view class="navbar-left" @tap="goBack">
          <view class="nav-icon-glass">
            <uni-icons type="left" size="22" color="#1D1D1F"></uni-icons>
          </view>
        </view>
        <view class="navbar-title">编辑资料</view>
        <view class="navbar-right"></view>
      </view>
    </view>

    <scroll-view class="content-scroll" scroll-y>
      
      <view class="form-wrapper">
        <text class="section-title">基础信息</text>
        
        <view class="glass-card form-group">
          <view class="input-row">
            <text class="input-label">昵称</text>
            <input 
              class="ios-input" 
              v-model="user.nickName" 
              placeholder="请输入昵称" 
              placeholder-class="ios-placeholder"
              maxlength="20"
            />
          </view>

          <view class="divider"></view>

          <view class="input-row">
            <text class="input-label">手机号码</text>
            <input 
              class="ios-input" 
              type="number"
              v-model="user.phonenumber" 
              placeholder="请输入手机号码" 
              placeholder-class="ios-placeholder"
              maxlength="11"
            />
          </view>

          <view class="divider"></view>

          <view class="input-row">
            <text class="input-label">邮箱</text>
            <input 
              class="ios-input" 
              type="text"
              v-model="user.email" 
              placeholder="请输入邮箱地址" 
              placeholder-class="ios-placeholder"
            />
          </view>

          <view class="divider"></view>

          <view class="input-row">
            <text class="input-label">性别</text>
            <view class="gender-segment">
              <view 
                class="segment-item" 
                :class="{ 'is-active': user.sex === '0' }" 
                @tap="user.sex = '0'">
                <text>男</text>
              </view>
              <view 
                class="segment-item" 
                :class="{ 'is-active': user.sex === '1' }" 
                @tap="user.sex = '1'">
                <text>女</text>
              </view>
            </view>
          </view>
        </view>

        <view class="submit-section">
          <view class="action-btn" :class="{ 'is-active': canSubmit }" @tap="submit">
            <text>{{ loading ? '保存中...' : '保存修改' }}</text>
          </view>
        </view>

      </view>
    </scroll-view>
  </view>
</template>

<script>
import {getUserProfile, updateUserProfile} from "@/api/system/user"

export default {
  data() {
    return {
      statusBarHeight: 0,
      loading: false,
      user: {
        nickName: "",
        phonenumber: "",
        email: "",
        sex: ""
      }
    }
  },
  computed: {
    // 监听必填项，用于动态点亮保存按钮。增加了空值处理，防止报错
    canSubmit() {
      const name = this.user.nickName || ''
      const phone = this.user.phonenumber || ''
      const email = this.user.email || ''
      const sex = this.user.sex || ''
      
      return name.trim().length > 0 && 
             phone.trim().length > 0 && 
             email.trim().length > 0 && 
             sex !== ""
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
    
    getUser() {
      uni.showLoading({ title: '加载中...', mask: true })
      getUserProfile().then(response => {
        uni.hideLoading()
        if (response.data) {
          // 确保后端返回的 null 被转换为空字符串，保证 v-model 正常回显
          this.user = {
            ...response.data,
            nickName: response.data.nickName || "",
            phonenumber: response.data.phonenumber || "",
            email: response.data.email || "",
            sex: response.data.sex || ""
          }
        }
      }).catch(() => {
        uni.hideLoading()
        uni.showToast({ title: '获取资料失败', icon: 'none' })
      })
    },

    // 轻量级表单校验
    validateForm() {
      const name = this.user.nickName || ''
      if (!name.trim()) {
        uni.showToast({ title: '昵称不能为空', icon: 'none' })
        return false
      }
      
      const phoneReg = /^1[3|4|5|6|7|8|9][0-9]\d{8}$/
      if (!phoneReg.test(this.user.phonenumber)) {
        uni.showToast({ title: '请输入正确的手机号码', icon: 'none' })
        return false
      }
      
      const emailReg = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
      if (!emailReg.test(this.user.email)) {
        uni.showToast({ title: '请输入正确的邮箱地址', icon: 'none' })
        return false
      }
      
      if (!this.user.sex) {
        uni.showToast({ title: '请选择性别', icon: 'none' })
        return false
      }
      
      return true
    },

    submit() {
      if (!this.canSubmit || this.loading) return
      if (!this.validateForm()) return

      this.loading = true
      uni.showLoading({ title: '正在保存...', mask: true })

      updateUserProfile(this.user).then(response => {
        this.loading = false
        uni.hideLoading()
        
        uni.showToast({ title: '资料修改成功', icon: 'success' })
        
        // 延迟返回上一页，让用户看到成功提示
        setTimeout(() => {
          this.goBack()
        }, 1500)
      }).catch(() => {
        this.loading = false
        uni.hideLoading()
        uni.showToast({ title: '修改失败，请重试', icon: 'none' })
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

.edit-profile-page {
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

.form-wrapper {
  padding: 32rpx;
}

.section-title {
  font-size: 26rpx;
  color: $ios-text-secondary;
  margin: 0 0 16rpx 24rpx;
  display: block;
}

/* ==================== 表单区 ==================== */
.form-group {
  padding: 0 32rpx;
  display: flex;
  flex-direction: column;
}

.input-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 36rpx 0;
}

.input-label {
  width: 160rpx;
  font-size: 30rpx;
  font-weight: 500;
  color: $ios-text-primary;
  flex-shrink: 0;
}

.ios-input {
  flex: 1;
  font-size: 30rpx;
  color: $ios-text-primary;
  text-align: right; /* iOS 设置界面标准：输入框文字右对齐 */
}

.ios-placeholder {
  color: #C7C7CC;
}

/* 苹果风格内凹分割线 */
.divider {
  height: 1px;
  background: rgba(0, 0, 0, 0.05);
  margin-left: 160rpx; 
}

/* ==================== 性别分段选择器 (Segmented Control) ==================== */
.gender-segment {
  display: flex;
  background: rgba(0, 0, 0, 0.05); /* 胶囊底座 */
  border-radius: 16rpx;
  padding: 6rpx;
  gap: 4rpx;
}

.segment-item {
  padding: 10rpx 36rpx;
  border-radius: 12rpx;
  font-size: 26rpx;
  color: $ios-text-secondary;
  font-weight: 500;
  transition: all 0.3s cubic-bezier(0.2, 0.8, 0.2, 1);
  
  /* 激活状态 */
  &.is-active {
    background: #FFFFFF;
    color: $theme-cyan;
    font-weight: 600;
    box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.08); /* 纯白高亮+物理阴影 */
  }
}

/* ==================== 底部提交栏 ==================== */
.submit-section {
  padding: 80rpx 0;
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