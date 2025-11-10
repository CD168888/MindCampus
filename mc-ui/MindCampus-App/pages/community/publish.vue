<template>
  <view class="publish-page">
    <!-- 自定义导航栏 -->
    <view class="custom-navbar" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="navbar-content">
        <view class="navbar-left" @tap="goBack">
          <text class="back-icon">←</text>
        </view>
        <view class="navbar-title">发布帖子</view>
        <view class="navbar-right" @tap="handlePublish">
          <text class="publish-text" :class="{ 'disabled': !canPublish }">发布</text>
        </view>
      </view>
    </view>

    <!-- 表单内容 -->
    <scroll-view class="form-scroll" scroll-y>
      <view class="form-content">
        <!-- 标题输入 -->
        <view class="form-item">
          <textarea class="title-input" v-model="form.title" placeholder="请输入标题（必填）" maxlength="100"
            :auto-height="true" :show-confirm-bar="false" />
          <view class="char-count">{{ form.title.length }}/100</view>
        </view>

        <!-- 内容输入 -->
        <view class="form-item">
          <textarea class="content-input" v-model="form.content" placeholder="分享你的想法..." maxlength="2000"
            :auto-height="true" :show-confirm-bar="false" />
          <view class="char-count">{{ form.content.length }}/2000</view>
        </view>

        <!-- 图片上传 -->
        <view class="form-item">
          <view class="image-upload">
            <view class="image-list">
              <view class="image-item" v-for="(img, index) in form.images" :key="index">
                <image class="upload-image" :src="img" mode="aspectFill"></image>
                <view class="delete-btn" @tap="deleteImage(index)">
                  <uni-icons type="closeempty" size="16" color="#FFFFFF"></uni-icons>
                </view>
              </view>

              <!-- 上传按钮 -->
              <view v-if="form.images.length < 9" class="upload-btn" @tap="chooseImage">
                <uni-icons type="camera" size="32" color="#CCCCCC"></uni-icons>
                <text class="upload-text">{{ form.images.length }}/9</text>
              </view>
            </view>
          </view>
        </view>

        <!-- 提示信息 -->
        <view class="tips">
          <view class="tip-item">
            <uni-icons type="info" size="14" color="#999999"></uni-icons>
            <text class="tip-text">请文明发言，共建和谐社区</text>
          </view>
        </view>
      </view>
    </scroll-view>

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
import config from '@/config'

export default {
  data() {
    return {
      statusBarHeight: 0,
      publishing: false,
      form: {
        title: '',
        content: '',
        images: []
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
        return new Promise((resolve, reject) => {
          uni.uploadFile({
            url: config.baseUrl + '/common/upload',
            filePath: filePath,
            name: 'file',
            header: {
              'Authorization': 'Bearer ' + uni.getStorageSync('token')
            },
            success: (res) => {
              try {
                const data = JSON.parse(res.data)
                if (data.code === 200) {
                  resolve(data.url)
                } else {
                  reject(data.msg || '上传失败')
                }
              } catch (e) {
                reject('上传失败')
              }
            },
            fail: (err) => {
              reject('上传失败')
            }
          })
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
        images: this.form.images.join(',')
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
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(20rpx);
  box-shadow: 0 2rpx 16rpx rgba(167, 139, 250, 0.08);
}

.navbar-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 88rpx;
  padding: 0 30rpx;
}

.navbar-left {
  width: 80rpx;
}

.back-icon {
  font-size: $font-2xl;
  color: $text-primary;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 64rpx;
  height: 64rpx;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.6);
  transition: all 0.2s ease;

  &:active {
    transform: scale(0.9);
    background: rgba(255, 255, 255, 0.8);
  }
}

.navbar-title {
  flex: 1;
  text-align: center;
  font-size: $font-base;
  font-weight: $font-semibold;
  color: $text-primary;
  font-family: $font-family-base;
}

.navbar-right {
  width: 80rpx;
  display: flex;
  justify-content: flex-end;
}

.publish-text {
  font-size: $font-sm;
  color: $primary-color;
  font-weight: $font-semibold;
  padding: $spacing-xs $spacing-md;
  border-radius: $radius-sm;
  transition: all 0.2s ease;
  font-family: $font-family-base;

  &:active {
    opacity: $opacity-hover;
    background: rgba(22, 119, 255, 0.05);
  }

  &.disabled {
    color: $text-quaternary;
    opacity: 0.5;
  }
}

/* 表单内容 */
.form-scroll {
  height: calc(100vh - 88rpx - env(safe-area-inset-top));
  margin-top: calc(88rpx + env(safe-area-inset-top));
}

.form-content {
  padding: $spacing-lg;
}

.form-item {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10rpx);
  border-radius: $radius-lg;
  padding: $spacing-lg;
  margin-bottom: $spacing-base;
  box-shadow:
    0 4rpx 16rpx rgba(167, 243, 208, 0.08),
    0 2rpx 8rpx rgba(196, 181, 253, 0.08);
  border: 1rpx solid rgba(255, 255, 255, 0.6);
  position: relative;
}

/* 标题输入 */
.title-input {
  width: 100%;
  font-size: $font-md;
  font-weight: $font-semibold;
  color: $text-primary;
  line-height: $line-height-normal;
  min-height: 80rpx;
  font-family: $font-family-base;
}

/* 内容输入 */
.content-input {
  width: 100%;
  font-size: $font-sm;
  color: $text-secondary;
  line-height: $line-height-normal;
  min-height: 300rpx;
  font-family: $font-family-base;
}

/* 字数统计 */
.char-count {
  position: absolute;
  right: $spacing-lg;
  bottom: $spacing-lg;
  font-size: $font-xs;
  color: $text-quaternary;
  font-family: $font-family-base;
}

/* 图片上传 */
.image-upload {
  padding-top: $spacing-xs;
}

.image-list {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: $spacing-md;
}

.image-item {
  position: relative;
  width: 100%;
  padding-bottom: 100%;
}

.upload-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  border-radius: $radius-base;
  background: $bg-gray-100;
}

.delete-btn {
  position: absolute;
  top: -8rpx;
  right: -8rpx;
  width: 40rpx;
  height: 40rpx;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;

  &:active {
    transform: scale(0.9);
  }
}

.upload-btn {
  width: 100%;
  padding-bottom: 100%;
  position: relative;
  border-radius: $radius-base;
  background: rgba(0, 0, 0, 0.02);
  border: 2rpx dashed $border-dark;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;

  &:active {
    background: rgba(0, 0, 0, 0.04);
  }

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
  }
}

.upload-text {
  position: absolute;
  bottom: 20rpx;
  left: 50%;
  transform: translateX(-50%);
  font-size: $font-xs;
  color: $text-quaternary;
  font-family: $font-family-base;
}

/* 提示信息 */
.tips {
  padding: $spacing-lg;
}

.tip-item {
  display: flex;
  align-items: center;
  gap: $spacing-xs;
}

.tip-text {
  font-size: $font-xs;
  color: $text-tertiary;
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
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20rpx);
  border-radius: $radius-lg;
  padding: $spacing-2xl $spacing-3xl;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: $spacing-lg;
}

.loading-spinner {
  width: 60rpx;
  height: 60rpx;
  border: 4rpx solid rgba(22, 119, 255, 0.2);
  border-top-color: $primary-color;
  border-radius: 50%;
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



