<template>
  <view class="publish-page">
    <!-- 自定义导航栏 -->
    <view class="custom-navbar" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="navbar-content">
        <view class="navbar-left" @tap="goBack">
          <text class="back-icon">✕</text>
        </view>
        <view class="navbar-title">发布帖子</view>
        <view class="navbar-right"></view>
      </view>
    </view>

    <!-- 表单内容 -->
    <scroll-view class="form-scroll" scroll-y>
      <view class="form-content">
        <!-- 标题输入 -->
        <view class="form-item title-item">
          <view class="item-header">
            <view class="header-dot"></view>
            <text class="header-label">标题</text>
            <text class="required-mark">*</text>
          </view>
          <textarea class="title-input" v-model="form.title" placeholder="给你的帖子起个标题吧..." maxlength="100"
            :auto-height="true" :show-confirm-bar="false" />
          <view class="char-count">{{ form.title.length }}/100</view>
        </view>

        <!-- 内容输入 -->
        <view class="form-item content-item">
          <view class="item-header">
            <view class="header-dot"></view>
            <text class="header-label">内容</text>
          </view>
          <textarea class="content-input" v-model="form.content" placeholder="说说你的想法，分享你的故事..." maxlength="2000"
            :auto-height="true" :show-confirm-bar="false" />
          <view class="char-count">{{ form.content.length }}/2000</view>
        </view>

        <!-- 图片上传 -->
        <view class="form-item image-item">
          <view class="item-header">
            <view class="header-dot"></view>
            <text class="header-label">图片</text>
            <text class="header-tip">最多9张</text>
          </view>
          <view class="image-upload">
            <view class="image-list">
              <!-- 已上传的图片 -->
              <view class="image-wrapper" v-for="(img, index) in form.images" :key="index">
                <image class="upload-image" :src="img" mode="aspectFill"></image>
                <view class="image-mask">
                  <view class="delete-btn" @tap.stop="deleteImage(index)">
                    <uni-icons type="trash" size="18" color="#FFFFFF"></uni-icons>
                  </view>
                </view>
              </view>

              <!-- 上传按钮 -->
              <view v-if="form.images.length < 9" class="upload-btn" @tap="chooseImage">
                <view class="upload-icon-wrapper">
                  <uni-icons type="camera-filled" size="40" color="#4D94FF"></uni-icons>
                </view>
                <text class="upload-hint">添加图片</text>
                <text class="upload-count">{{ form.images.length }}/9</text>
              </view>
            </view>
          </view>
        </view>

        <!-- 设置选项 -->
        <view class="form-item settings-item">
          <view class="item-header">
            <view class="header-dot"></view>
            <text class="header-label">设置</text>
          </view>

          <!-- 匿名发布 -->
          <view class="setting-option">
            <view class="option-left">
              <view class="option-icon anonymous-icon">
                <uni-icons type="eye-slash-filled" size="20" color="#1677FF"></uni-icons>
              </view>
              <view class="option-content">
                <text class="option-title">匿名发布</text>
                <text class="option-desc">隐藏真实身份，保护隐私</text>
              </view>
            </view>
            <switch :checked="form.isAnonymous === '1'" @change="onAnonymousChange" color="#1677FF"
              style="transform: scale(0.85);" />
          </view>
        </view>

        <!-- 温馨提示 -->
        <view class="tips-card">
          <view class="tips-header">
            <uni-icons type="info-filled" size="16" color="#FF8D3E"></uni-icons>
            <text class="tips-title">温馨提示</text>
          </view>
          <view class="tips-list">
            <view class="tip-item">
              <text class="tip-dot">•</text>
              <text class="tip-text">请文明发言，尊重他人</text>
            </view>
            <view class="tip-item">
              <text class="tip-dot">•</text>
              <text class="tip-text">禁止发布违法违规内容</text>
            </view>
            <view class="tip-item">
              <text class="tip-dot">•</text>
              <text class="tip-text">共同维护和谐社区环境</text>
            </view>
          </view>
        </view>

        <!-- 底部占位 -->
        <view class="bottom-placeholder"></view>
      </view>
    </scroll-view>

    <!-- 底部发布按钮 -->
    <view class="publish-footer" :style="{ paddingBottom: 'env(safe-area-inset-bottom)' }">
      <view class="publish-btn" :class="{ 'disabled': !canPublish }" @tap="handlePublish">
        <view class="btn-content">
          <uni-icons type="paperplane-filled" size="20" color="#FFFFFF"></uni-icons>
          <text class="btn-text">{{ publishing ? '发布中...' : '发布' }}</text>
        </view>
      </view>
    </view>

    <!-- 加载中遮罩 -->
    <view v-if="publishing" class="loading-mask">
      <view class="loading-content">
        <view class="loading-spinner"></view>
        <text class="loading-text">发布中...</text>
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
      publishing: false,
      form: {
        title: '',
        content: '',
        images: [],
        isAnonymous: '0' // 默认不匿名：0否 1是
      }
    }
  },
  computed: {
    canPublish() {
      return this.form.title.trim() && this.form.content.trim()
    }
  },
  onLoad() {
    // 获取状态栏高度
    const systemInfo = uni.getSystemInfoSync()
    this.statusBarHeight = systemInfo.statusBarHeight || 0
  },
  methods: {
    // 返回
    goBack() {
      if (this.form.title || this.form.content || this.form.images.length > 0) {
        uni.showModal({
          title: '提示',
          content: '确定要放弃编辑吗？',
          success: (res) => {
            if (res.confirm) {
              uni.navigateBack()
            }
          }
        })
      } else {
        uni.navigateBack()
      }
    },

    // 选择图片
    chooseImage() {
      const maxCount = 9 - this.form.images.length
      uni.chooseImage({
        count: maxCount,
        sizeType: ['compressed'],
        sourceType: ['album', 'camera'],
        success: (res) => {
          const tempFilePaths = res.tempFilePaths
          this.uploadImages(tempFilePaths)
        }
      })
    },

    // 上传图片
    uploadImages(filePaths) {
      uni.showLoading({
        title: '上传中...',
        mask: true
      })

      const uploadPromises = filePaths.map(filePath => {
        return upload({
          url: '/common/upload',
          filePath: filePath,
          name: 'file'
        }).then(res => {
          return res.url
        })
      })

      Promise.all(uploadPromises).then(urls => {
        uni.hideLoading()
        this.form.images = this.form.images.concat(urls)
        this.$modal.showToast('上传成功')
      }).catch(err => {
        uni.hideLoading()
        this.$modal.showToast(err || '上传失败')
      })
    },

    // 删除图片
    deleteImage(index) {
      uni.showModal({
        title: '提示',
        content: '确定要删除这张图片吗？',
        success: (res) => {
          if (res.confirm) {
            this.form.images.splice(index, 1)
          }
        }
      })
    },

    // 匿名开关变化
    onAnonymousChange(e) {
      this.form.isAnonymous = e.detail.value ? '1' : '0'
    },

    // 发布帖子
    handlePublish() {
      if (!this.canPublish) {
        this.$modal.showToast('请填写标题和内容')
        return
      }

      if (this.publishing) return

      this.publishing = true

      const data = {
        title: this.form.title.trim(),
        content: this.form.content.trim(),
        images: this.form.images.join(','),
        isAnonymous: this.form.isAnonymous // 传递匿名状态
      }

      createPost(data).then(res => {
        this.publishing = false

        if (res.code === 200) {
          this.$modal.showToast('发布成功')
          setTimeout(() => {
            uni.navigateBack()
          }, 1500)
        } else {
          this.$modal.showToast(res.msg || '发布失败')
        }
      }).catch(err => {
        this.publishing = false
        console.error('发布帖子失败:', err)
        this.$modal.showToast('发布失败，请重试')
      })
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/static/scss/theme.scss';

.publish-page {
  min-height: 100vh;
  position: relative;
  background: $gradient-soft;
}

/* 自定义导航栏 */
.custom-navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 999;
  background: $glass-background;
  backdrop-filter: $backdrop-blur;
  border-bottom: 1rpx solid $border-base;
}

.navbar-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 88rpx;
  padding: 0 32rpx;
}

.navbar-left {
  width: 80rpx;
}

.back-icon {
  font-size: 44rpx;
  color: $text-primary;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 56rpx;
  height: 56rpx;
  border-radius: $radius-full;
  background: $bg-gray-100;
  transition: all $transition-base $ease-out;
  font-weight: 200;

  &:active {
    transform: scale(0.9);
    background: $bg-gray-200;
  }
}

.navbar-title {
  flex: 1;
  text-align: center;
  font-size: 32rpx;
  font-weight: 600;
  color: $text-primary;
}

.navbar-right {
  width: 80rpx;
}

/* 表单内容 */
.form-scroll {
  height: calc(100vh - 88rpx - env(safe-area-inset-top));
  margin-top: calc(88rpx + env(safe-area-inset-top));
}

.form-content {
  padding: $spacing-lg $spacing-xl 160rpx;
}

/* 表单项 */
.form-item {
  background: $bg-white;
  border-radius: $radius-lg;
  padding: $spacing-xl;
  margin-bottom: $spacing-lg;
  box-shadow: $shadow-sm;
  position: relative;
}

/* 表单项头部 */
.item-header {
  display: flex;
  align-items: center;
  gap: $spacing-base;
  margin-bottom: $spacing-md;
}

.header-dot {
  width: 8rpx;
  height: 8rpx;
  border-radius: $radius-full;
  background: $gradient-primary;
}

.header-label {
  font-size: $font-sm;
  font-weight: $font-semibold;
  color: $text-primary;
  font-family: $font-family-base;
}

.required-mark {
  color: $danger-color;
  font-size: $font-sm;
  margin-left: -8rpx;
}

.header-tip {
  font-size: $font-xs;
  color: $text-tertiary;
  margin-left: auto;
  font-family: $font-family-base;
}

/* 标题输入 */
.title-input {
  width: 100%;
  font-size: $font-base;
  font-weight: $font-medium;
  color: $text-primary;
  line-height: $line-height-normal;
  min-height: 80rpx;
  padding-bottom: 40rpx;
  font-family: $font-family-base;
}

/* 内容输入 */
.content-input {
  width: 100%;
  font-size: $font-sm;
  color: $text-secondary;
  line-height: $line-height-relaxed;
  min-height: 300rpx;
  padding-bottom: 40rpx;
  font-family: $font-family-base;
}

/* 字数统计 */
.char-count {
  position: absolute;
  right: $spacing-xl;
  bottom: $spacing-xl;
  font-size: $font-xs;
  color: $text-quaternary;
  font-family: $font-family-base;
}

/* 图片上传 */
.image-upload {
  margin-top: $spacing-xs;
}

.image-list {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: $spacing-md;
}

.image-wrapper {
  position: relative;
  width: 100%;
  padding-bottom: 100%;
  border-radius: $radius-base;
  overflow: hidden;
}

.upload-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: $bg-gray-100;
}

.image-mask {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0);
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.image-wrapper:active .image-mask {
  background: rgba(0, 0, 0, 0.3);
}

.delete-btn {
  width: 56rpx;
  height: 56rpx;
  border-radius: $radius-full;
  background: rgba($danger-color, 0.9);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transform: scale(0.8);
  transition: all $transition-base $ease-out;
}

.image-wrapper:active .delete-btn {
  opacity: 1;
  transform: scale(1);
}

.upload-btn {
  width: 100%;
  padding-bottom: 100%;
  position: relative;
  border-radius: $radius-base;
  background: $gradient-card;
  border: 2rpx dashed $primary-lighter;
  transition: all $transition-base $ease-out;

  &:active {
    background: linear-gradient(135deg, rgba($primary-color, 0.15) 0%, rgba($accent-color, 0.15) 100%);
    border-color: $primary-light;
    transform: scale(0.98);
  }
}

.upload-icon-wrapper {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%) translateY(-20rpx);
}

.upload-hint {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%) translateY(20rpx);
  font-size: $font-xs;
  color: $primary-light;
  font-weight: $font-medium;
  font-family: $font-family-base;
}

.upload-count {
  position: absolute;
  bottom: $spacing-base;
  right: $spacing-base;
  font-size: 20rpx;
  color: $text-quaternary;
  background: rgba($bg-white, 0.9);
  padding: 4rpx $spacing-sm;
  border-radius: $radius-md;
  font-family: $font-family-base;
}

/* 设置选项 */
.setting-option {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: $spacing-lg;
  background: $gradient-card;
  border-radius: $radius-base;
  margin-top: $spacing-xs;
}

.option-left {
  display: flex;
  align-items: center;
  gap: $spacing-md;
  flex: 1;
}

.option-icon {
  width: 72rpx;
  height: 72rpx;
  border-radius: $radius-full;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, rgba($primary-color, 0.1) 0%, rgba($accent-color, 0.1) 100%);
}

.option-content {
  display: flex;
  flex-direction: column;
  gap: 6rpx;
}

.option-title {
  font-size: $font-sm;
  font-weight: $font-semibold;
  color: $text-primary;
  font-family: $font-family-base;
}

.option-desc {
  font-size: $font-xs;
  color: $text-tertiary;
  font-family: $font-family-base;
}

/* 温馨提示 */
.tips-card {
  background: linear-gradient(135deg, rgba($warning-color, 0.1) 0%, rgba($secondary-color, 0.15) 100%);
  border-radius: $radius-md;
  padding: $spacing-lg;
  margin-top: $spacing-xs;
  border: 1rpx solid rgba($warning-color, 0.2);
}

.tips-header {
  display: flex;
  align-items: center;
  gap: $spacing-sm;
  margin-bottom: $spacing-md;
}

.tips-title {
  font-size: $font-sm;
  font-weight: $font-semibold;
  color: $warning-color;
  font-family: $font-family-base;
}

.tips-list {
  display: flex;
  flex-direction: column;
  gap: $spacing-sm;
}

.tip-item {
  display: flex;
  align-items: flex-start;
  gap: $spacing-sm;
}

.tip-dot {
  font-size: $font-xs;
  color: $secondary-color;
  line-height: $line-height-normal;
}

.tip-text {
  flex: 1;
  font-size: $font-xs;
  color: $text-secondary;
  line-height: $line-height-normal;
  font-family: $font-family-base;
}

/* 底部占位 */
.bottom-placeholder {
  height: 40rpx;
}

/* 底部发布按钮 */
.publish-footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 998;
  padding: $spacing-lg $spacing-xl;
  background: linear-gradient(180deg, transparent 0%, rgba($bg-white, 0.98) 20%, $bg-white 100%);
  backdrop-filter: $backdrop-blur;
}

.publish-btn {
  width: 100%;
  height: 96rpx;
  border-radius: $radius-2xl;
  background: linear-gradient(135deg, #6ee7b7 0%, #a78bfa 50%, #fda4af 100%);
  box-shadow: 0 8rpx 32rpx rgba(167, 139, 250, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all $transition-base $ease-out;

  &:active {
    transform: translateY(2rpx);
    box-shadow: 0 4rpx 16rpx rgba(167, 139, 250, 0.3);
  }

  &.disabled {
    background: $bg-gray-200;
    box-shadow: none;
    opacity: $opacity-disabled;
  }
}

.btn-content {
  display: flex;
  align-items: center;
  gap: $spacing-sm;
}

.btn-text {
  font-size: $font-base;
  font-weight: $font-semibold;
  color: $bg-white;
  font-family: $font-family-base;
}

/* 加载中遮罩 */
.loading-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
}

.loading-content {
  background: rgba($bg-white, 0.98);
  backdrop-filter: $backdrop-blur;
  border-radius: $radius-lg;
  padding: $spacing-2xl $spacing-3xl;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: $spacing-lg;
  box-shadow: $shadow-lg;
}

.loading-spinner {
  width: 60rpx;
  height: 60rpx;
  border: 4rpx solid rgba($primary-color, 0.2);
  border-top-color: $primary-color;
  border-radius: $radius-full;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.loading-text {
  font-size: $font-sm;
  color: $text-primary;
  font-weight: $font-semibold;
  font-family: $font-family-base;
}
</style>
