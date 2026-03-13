<template>
  <view class="publish-page">
    <view class="ambient-background"></view>

    <view class="glass-header" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="navbar-content">
        <view class="navbar-left" @tap="goBack">
          <view class="nav-icon-glass">
            <uni-icons type="closeempty" size="22" color="#1D1D1F"></uni-icons>
          </view>
        </view>
        <view class="navbar-title">记录此刻</view>
        <view class="navbar-right"></view>
      </view>
    </view>

    <scroll-view class="form-scroll" scroll-y>
      <view class="content-wrapper">
        
        <view class="glass-card editor-card">
          <input 
            class="title-input" 
            v-model="form.title" 
            placeholder="加个标题，让更多人看到你的故事..." 
            placeholder-class="input-placeholder"
            maxlength="40"
          />
          
          <view class="divider"></view>
          
          <textarea 
            class="content-input" 
            v-model="form.content" 
            placeholder="分享此时此刻的心情与感受..." 
            placeholder-class="input-placeholder"
            maxlength="2000"
            :auto-height="true"
            :show-confirm-bar="false"
          />
          <view class="char-count" v-if="form.content.length > 0">{{ form.content.length }}/2000</view>
          
          <view class="image-grid-section">
            <view class="image-grid">
              <view class="image-item" v-for="(img, index) in form.images" :key="index">
                <image class="uploaded-img" :src="img" mode="aspectFill" @tap="previewImage(index)"></image>
                <view class="delete-badge" @tap.stop="deleteImage(index)">
                  <uni-icons type="closeempty" size="12" color="#FFFFFF"></uni-icons>
                </view>
              </view>
              
              <view class="upload-btn" v-if="form.images.length < 9" @tap="chooseImage">
                <uni-icons type="camera-filled" size="32" color="#86868B"></uni-icons>
                <text class="upload-count">{{ form.images.length }}/9</text>
              </view>
            </view>
          </view>
        </view>

        <view class="glass-card settings-card">
          <view class="setting-row">
            <view class="setting-left">
              <view class="icon-bg bg-blue">
                <uni-icons type="eye-slash-filled" size="18" color="#FFFFFF"></uni-icons>
              </view>
              <view class="setting-text">
                <text class="setting-title">匿名发布</text>
                <text class="setting-desc">开启后，你的头像与昵称将受到保护</text>
              </view>
            </view>
            <switch :checked="form.isAnonymous === '1'" @change="onAnonymousChange" color="#2CB5A0" style="transform: scale(0.85);"/>
          </view>
        </view>

        <view class="glass-card tips-card">
          <view class="tips-header">
            <uni-icons type="info-filled" size="16" color="#FF9500"></uni-icons>
            <text class="tips-title">社区发帖规范</text>
          </view>
          <view class="tips-content">
            <text>• 欢迎分享真实的校园生活与心理感悟。</text>
            <text>• 请友善交流，共同维护治愈和谐的社区环境。</text>
            <text>• 严禁发布违法、色情、人身攻击等不良信息。</text>
          </view>
        </view>

        <view class="bottom-spacer" :style="{ height: (safeAreaBottom + 120) + 'px' }"></view>
      </view>
    </scroll-view>

    <view class="bottom-glass-dock" :style="{ paddingBottom: `calc(24rpx + ${safeAreaBottom}px)` }">
      <view class="publish-btn" :class="{ 'is-active': canPublish }" @tap="handlePublish">
        <uni-icons type="paperplane-filled" size="20" :color="canPublish ? '#FFFFFF' : '#A1A1A6'"></uni-icons>
        <text>{{ publishing ? '正在传递心声...' : '发布瞬间' }}</text>
      </view>
    </view>

    <view v-if="publishing" class="loading-overlay">
       <view class="glass-card loading-modal">
          <uni-icons type="spinner-cycle" size="36" color="#2CB5A0" class="spin-icon"></uni-icons>
          <text class="loading-text">正在发布...</text>
       </view>
    </view>
  </view>
</template>

<script>
import {createPost} from '@/api/community/post'
import upload from '@/utils/upload'

export default {
  data() {
    return {
      statusBarHeight: 0,
      safeAreaBottom: 34,
      publishing: false,
      form: {
        title: '',
        content: '',
        images: [],
        isAnonymous: '0' // 0否 1是
      }
    }
  },
  computed: {
    canPublish() {
      return this.form.title.trim() && this.form.content.trim()
    }
  },
  onLoad() {
    const systemInfo = uni.getSystemInfoSync()
    this.statusBarHeight = systemInfo.statusBarHeight || 0
    this.safeAreaBottom = systemInfo.safeAreaInsets?.bottom || 34
  },
  methods: {
    goBack() {
      if (this.form.title || this.form.content || this.form.images.length > 0) {
        uni.showModal({
          title: '放弃编辑',
          content: '当前有未发布的内容，确定要离开吗？',
          confirmColor: '#FF3B30',
          success: (res) => {
            if (res.confirm) uni.navigateBack()
          }
        })
      } else {
        uni.navigateBack()
      }
    },

    chooseImage() {
      const maxCount = 9 - this.form.images.length
      uni.chooseImage({
        count: maxCount,
        sizeType: ['compressed'],
        sourceType: ['album', 'camera'],
        success: (res) => {
          this.uploadImages(res.tempFilePaths)
        }
      })
    },

    uploadImages(filePaths) {
      uni.showLoading({ title: '处理中...', mask: true })

      const uploadPromises = filePaths.map(filePath => {
        return upload({
          url: '/common/upload',
          filePath: filePath,
          name: 'file'
        }).then(res => res.url)
      })

      Promise.all(uploadPromises).then(urls => {
        uni.hideLoading()
        this.form.images = this.form.images.concat(urls)
      }).catch(err => {
        uni.hideLoading()
        uni.showToast({ title: '图片上传失败', icon: 'none' })
      })
    },

    deleteImage(index) {
      uni.vibrateShort() // 触觉反馈
      this.form.images.splice(index, 1)
    },

    previewImage(index) {
      uni.previewImage({
        current: index,
        urls: this.form.images
      })
    },

    onAnonymousChange(e) {
      this.form.isAnonymous = e.detail.value ? '1' : '0'
      uni.vibrateShort()
    },

    handlePublish() {
      if (!this.canPublish) {
        uni.showToast({ title: '标题和正文是必填的哦', icon: 'none' })
        return
      }

      if (this.publishing) return
      this.publishing = true

      const data = {
        title: this.form.title.trim(),
        content: this.form.content.trim(),
        images: this.form.images.join(','),
        isAnonymous: this.form.isAnonymous
      }

      createPost(data).then(res => {
        this.publishing = false
        if (res.code === 200) {
          uni.showToast({ title: '发布成功', icon: 'success' })
          setTimeout(() => { uni.navigateBack() }, 1500)
        } else {
          uni.showToast({ title: res.msg || '发布失败', icon: 'none' })
        }
      }).catch(err => {
        this.publishing = false
        uni.showToast({ title: '发布失败，请重试', icon: 'none' })
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

.publish-page {
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

/* ==================== 表单滚动区 ==================== */
.form-scroll {
  position: relative;
  z-index: 1;
  height: 100vh;
}

.content-wrapper {
  padding: 30rpx 32rpx 0;
  display: flex;
  flex-direction: column;
  gap: 32rpx;
}

/* ==================== 1. 编辑器画板 (Glass Editor) ==================== */
.editor-card {
  padding: 40rpx 36rpx;
  display: flex;
  flex-direction: column;
}

.title-input {
  font-size: 38rpx;
  font-weight: 700;
  color: $ios-text-primary;
  height: 60rpx;
}

.divider {
  height: 1px;
  background: rgba(0, 0, 0, 0.04);
  margin: 24rpx 0;
}

.content-input {
  width: 100%;
  font-size: 32rpx;
  line-height: 1.6;
  color: #3A3A3C;
  min-height: 240rpx;
}

.input-placeholder {
  color: #C7C7CC;
  font-weight: 400;
}

.char-count {
  text-align: right;
  font-size: 24rpx;
  color: $ios-text-secondary;
  margin-top: 16rpx;
}

/* ==================== 图片网格 (Apple Grid Style) ==================== */
.image-grid-section {
  margin-top: 24rpx;
}

.image-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16rpx;
}

.image-item {
  position: relative;
  aspect-ratio: 1;
  border-radius: 20rpx;
  background: #FFF;
  box-shadow: 0 4rpx 12rpx rgba(0,0,0,0.03);
}

.uploaded-img {
  width: 100%;
  height: 100%;
  border-radius: 20rpx;
  object-fit: cover;
}

.delete-badge {
  position: absolute;
  top: -12rpx;
  right: -12rpx;
  width: 40rpx;
  height: 40rpx;
  background: #FF3B30;
  border-radius: 50%;
  border: 4rpx solid #FFF;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4rpx 8rpx rgba(255, 59, 48, 0.3);
  z-index: 10;
  
  &:active { transform: scale(0.9); }
}

.upload-btn {
  aspect-ratio: 1;
  background: rgba(0, 0, 0, 0.02);
  border-radius: 20rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border: 2rpx dashed rgba(0, 0, 0, 0.1);
  gap: 8rpx;
  transition: all 0.2s ease;
  
  &:active { background: rgba(0, 0, 0, 0.05); }
}

.upload-count {
  font-size: 22rpx;
  color: $ios-text-secondary;
}

/* ==================== 2. 设置部件 (Widget Card) ==================== */
.settings-card {
  padding: 32rpx;
}

.setting-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.setting-left {
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.icon-bg {
  width: 72rpx;
  height: 72rpx;
  border-radius: 20rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.bg-blue {
  background: linear-gradient(135deg, #5AC8FA 0%, #007AFF 100%);
  box-shadow: 0 8rpx 16rpx rgba(0, 122, 255, 0.25);
}

.setting-title {
  font-size: 30rpx;
  font-weight: 600;
  color: $ios-text-primary;
  display: block;
}

.setting-desc {
  font-size: 24rpx;
  color: $ios-text-secondary;
  display: block;
  margin-top: 4rpx;
}

/* ==================== 3. 温馨提示部件 ==================== */
.tips-card {
  background: linear-gradient(135deg, rgba(255, 149, 0, 0.08) 0%, rgba(255, 204, 0, 0.05) 100%);
  border: 1px solid rgba(255, 149, 0, 0.15);
  padding: 32rpx;
}

.tips-header {
  display: flex;
  align-items: center;
  gap: 8rpx;
  margin-bottom: 16rpx;
}

.tips-title {
  font-size: 26rpx;
  font-weight: 600;
  color: #FF9500;
}

.tips-content {
  display: flex;
  flex-direction: column;
  gap: 12rpx;
}

.tips-content text {
  font-size: 26rpx;
  color: #86868B;
  line-height: 1.5;
}

/* ==================== 底部悬浮操作舱 (Glass Dock) ==================== */
.bottom-glass-dock {
  position: fixed;
  bottom: 0; left: 0; right: 0;
  padding: 24rpx 40rpx;
  background: rgba(245, 245, 247, 0.75);
  backdrop-filter: saturate(180%) blur(20px);
  -webkit-backdrop-filter: saturate(180%) blur(20px);
  border-top: 0.5px solid rgba(0, 0, 0, 0.05);
  z-index: 100;
}

.publish-btn {
  width: 100%;
  height: 96rpx;
  border-radius: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
  font-size: 32rpx;
  font-weight: 600;
  background: rgba(0, 0, 0, 0.06);
  color: #A1A1A6;
  transition: all 0.3s ease;
}

.publish-btn.is-active {
  background: linear-gradient(135deg, #48D1CC 0%, #2CB5A0 100%);
  color: #FFFFFF;
  box-shadow: 0 8rpx 24rpx rgba(44, 181, 160, 0.3);
  
  &:active {
    transform: scale(0.98);
    box-shadow: 0 4rpx 12rpx rgba(44, 181, 160, 0.2);
  }
}

/* ==================== 弹窗遮罩 ==================== */
.loading-overlay {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0, 0, 0, 0.3);
  backdrop-filter: blur(4px);
  z-index: 999;
  display: flex;
  align-items: center;
  justify-content: center;
}

.loading-modal {
  padding: 60rpx 80rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 24rpx;
}

.spin-icon { animation: spin 1s linear infinite; }
@keyframes spin { 100% { transform: rotate(360deg); } }

.loading-text {
  font-size: 28rpx;
  color: $ios-text-primary;
  font-weight: 500;
}
</style>