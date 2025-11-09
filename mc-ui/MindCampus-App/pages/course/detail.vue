<template>
  <view class="course-detail-page">
    <!-- 自定义导航栏 -->
    <view class="custom-navbar" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="navbar-content">
        <view class="navbar-left" @tap="goBack">
          <text class="back-icon">←</text>
        </view>
        <view class="navbar-title">课程详情</view>
        <view class="navbar-right"></view>
      </view>
    </view>

    <!-- 课程内容 -->
    <scroll-view class="course-scroll" scroll-y>
      <view class="course-content" v-if="course">
        <!-- 视频播放区域 -->
        <view class="video-section">
          <video
            v-if="course.mp4Url"
            :src="getVideoUrl(course.mp4Url)"
            class="course-video"
            :poster="getImageUrl(course.coverUrl)"
            controls
            :enable-play-gesture="true"
            :show-center-play-btn="true"
            object-fit="contain"
          ></video>
          <view v-else class="video-placeholder">
            <text class="placeholder-icon">🎬</text>
            <text class="placeholder-text">视频加载中...</text>
          </view>
        </view>

        <!-- 课程头部信息 -->
        <view class="course-header">
          <view class="course-title">{{ course.title }}</view>
          <view class="course-meta">
            <view class="meta-item">
              <text class="meta-icon">👤</text>
              <text class="meta-text">{{ course.lecturer || '未知' }}</text>
            </view>
            <view class="meta-item">
              <text class="meta-icon">⏱️</text>
              <text class="meta-text">{{ formatDuration(course.duration) }}</text>
            </view>
            <view class="meta-item" v-if="course.chapters">
              <text class="meta-icon">📚</text>
              <text class="meta-text">{{ course.chapters }}章</text>
            </view>
            <view class="course-level" v-if="course.level">{{ course.level }}</view>
          </view>
        </view>

        <!-- 课程简介 -->
        <view class="course-description" v-if="course.description">
          <view class="description-label">课程简介</view>
          <view class="description-text">{{ course.description }}</view>
        </view>
      </view>

      <!-- 加载中 -->
      <view class="loading-state" v-if="loading">
        <text class="loading-text">加载中...</text>
      </view>

      <!-- 空状态 -->
      <view class="empty-state" v-if="!course && !loading">
        <text class="empty-icon">🎬</text>
        <text class="empty-text">课程不存在</text>
      </view>
    </scroll-view>
  </view>
</template>

<script>
import {getCourseDetail} from '@/api/course';
import config from '@/config';

export default {
  data() {
    return {
      statusBarHeight: 0,
      courseId: null,
      course: null,
      loading: false,
    };
  },
  onLoad(options) {
    // 获取状态栏高度
    const systemInfo = uni.getSystemInfoSync();
    this.statusBarHeight = systemInfo.statusBarHeight || 0;

    // 获取课程ID
    if (options.courseId) {
      this.courseId = parseInt(options.courseId);
      this.loadCourseDetail();
    }
  },
  methods: {
    // 返回
    goBack() {
      uni.navigateBack();
    },

    // 加载课程详情
    loadCourseDetail() {
      if (!this.courseId) return;

      this.loading = true;

      getCourseDetail(this.courseId)
        .then((res) => {
          this.loading = false;
          if (res.code === 200 && res.data) {
            this.course = res.data;
          } else {
            this.$modal.showToast(res.msg || '加载失败');
          }
        })
        .catch((err) => {
          this.loading = false;
          console.error('加载课程详情失败:', err);
          this.$modal.showToast('加载失败，请重试');
        });
    },

    // 格式化时长（秒转时:分:秒）
    formatDuration(seconds) {
      if (!seconds) return '0:00';
      const hours = Math.floor(seconds / 3600);
      const mins = Math.floor((seconds % 3600) / 60);
      const secs = seconds % 60;

      if (hours > 0) {
        return `${hours}:${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
      }
      return `${mins}:${secs.toString().padStart(2, '0')}`;
    },

    // 获取图片完整URL
    getImageUrl(url) {
      if (!url) return '';
      if (url.startsWith('http')) return url;
      const baseUrl = config.baseUrl || 'http://localhost:8080';
      return url.startsWith('/') ? baseUrl + url : baseUrl + '/' + url;
    },

    // 获取视频完整URL
    getVideoUrl(url) {
      if (!url) return '';
      if (url.startsWith('http')) return url;
      const baseUrl = config.baseUrl || 'http://localhost:8080';
      return url.startsWith('/') ? baseUrl + url : baseUrl + '/' + url;
    },
  },
};
</script>

<style lang="scss" scoped>
@import '@/static/scss/theme.scss';

.course-detail-page {
  min-height: 100vh;
  position: relative;

  &::before {
    content: '';
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(to bottom, transparent -30px, #ffffff 400px),
      linear-gradient(135deg, rgba(167, 243, 208, 0.15) 0%, rgba(196, 181, 253, 0.15) 50%, rgba(254, 205, 211, 0.15) 100%);
    z-index: 0;
  }

  > * {
    position: relative;
    z-index: 1;
  }
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
}

/* 滚动区域 */
.course-scroll {
  height: 100vh;
  margin-top: calc(88rpx + env(safe-area-inset-top));
  padding-bottom: calc(env(safe-area-inset-bottom) + 40rpx);
}

/* 课程内容 */
.course-content {
  padding: $spacing-lg;
}

/* 视频播放区域 */
.video-section {
  margin-bottom: $spacing-lg;
  border-radius: $radius-lg;
  overflow: hidden;
  box-shadow: 0 8rpx 24rpx rgba(167, 243, 208, 0.12), 0 4rpx 12rpx rgba(196, 181, 253, 0.12);
  background: #000;
}

.course-video {
  width: 100%;
  height: 420rpx;
  display: block;
}

.video-placeholder {
  width: 100%;
  height: 420rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #6ee7b7 0%, #a78bfa 50%, #fda4af 100%);
}

.placeholder-icon {
  font-size: 80rpx;
  margin-bottom: $spacing-md;
}

.placeholder-text {
  font-size: $font-sm;
  color: rgba(255, 255, 255, 0.9);
  font-family: $font-family-base;
}

/* 课程头部信息 */
.course-header {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10rpx);
  border-radius: $radius-lg;
  padding: $spacing-lg;
  margin-bottom: $spacing-lg;
  box-shadow: 0 4rpx 16rpx rgba(167, 243, 208, 0.08), 0 2rpx 8rpx rgba(196, 181, 253, 0.08);
  border: 1rpx solid rgba(255, 255, 255, 0.6);
}

.course-title {
  font-size: $font-2xl;
  font-weight: $font-bold;
  color: $text-primary;
  line-height: $line-height-tight;
  margin-bottom: $spacing-lg;
  font-family: $font-family-base;
}

.course-meta {
  display: flex;
  align-items: center;
  gap: $spacing-lg;
  flex-wrap: wrap;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6rpx;
  font-size: $font-xs;
  color: $text-tertiary;
  font-family: $font-family-base;
}

.meta-icon {
  font-size: 22rpx;
}

.course-level {
  display: inline-block;
  font-size: 20rpx;
  color: #a78bfa;
  background: linear-gradient(135deg, rgba(167, 243, 208, 0.15) 0%, rgba(196, 181, 253, 0.15) 100%);
  padding: 6rpx 16rpx;
  border-radius: $radius-full;
  border: 1rpx solid rgba(167, 139, 250, 0.2);
  font-family: $font-family-base;
}

/* 课程简介 */
.course-description {
  background: linear-gradient(135deg, rgba(167, 243, 208, 0.1) 0%, rgba(196, 181, 253, 0.1) 100%);
  border-radius: $radius-base;
  padding: $spacing-lg;
  border-left: 4rpx solid #a78bfa;
}

.description-label {
  font-size: $font-sm;
  font-weight: $font-semibold;
  color: #a78bfa;
  margin-bottom: $spacing-sm;
  font-family: $font-family-base;
}

.description-text {
  font-size: $font-sm;
  color: $text-secondary;
  line-height: $line-height-normal;
  font-family: $font-family-base;
}

/* 加载中 */
.loading-state {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 200rpx 0;
}

.loading-text {
  font-size: $font-sm;
  color: $text-tertiary;
  font-family: $font-family-base;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 200rpx 0;
}

.empty-icon {
  font-size: 120rpx;
  margin-bottom: $spacing-2xl;
  filter: grayscale(30%);
}

.empty-text {
  font-size: $font-sm;
  color: $text-tertiary;
  font-family: $font-family-base;
}
</style>

