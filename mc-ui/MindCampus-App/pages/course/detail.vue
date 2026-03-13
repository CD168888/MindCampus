<template>
  <view class="course-detail-page">
    <view class="ambient-background"></view>

    <view class="glass-header" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="navbar-content">
        <view class="navbar-left" @tap="goBack">
          <view class="nav-icon-glass">
            <uni-icons type="left" size="22" color="#1D1D1F"></uni-icons>
          </view>
        </view>
        <view class="navbar-title">课程详情</view>
        <view class="navbar-right"></view>
      </view>
    </view>

    <view v-if="loading" class="loading-container">
      <view class="glass-card loading-card">
        <uni-icons type="spinner-cycle" size="36" color="#2CB5A0" class="spin-icon"></uni-icons>
        <text class="loading-text">正在加载课程内容...</text>
      </view>
    </view>

    <scroll-view class="course-scroll" scroll-y v-else-if="course">
      <view class="course-content">
        
        <view class="video-section shadow-lg">
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
            <uni-icons type="videocam-filled" size="48" color="#FFFFFF"></uni-icons>
            <text class="placeholder-text">视频资源加载中...</text>
          </view>
          <view class="video-highlight"></view>
        </view>

        <view class="glass-card course-detail-card">
          
          <view class="card-top-section">
            <text class="course-title">{{ course.title }}</text>
            
            <view class="course-meta">
              <view class="meta-tag tint-cyan">
                <uni-icons type="person-filled" size="14" color="#2CB5A0"></uni-icons>
                <text>{{ course.lecturer || '特邀导师' }}</text>
              </view>
              
              <view class="meta-tag tint-gray">
                <uni-icons type="clock-filled" size="14" color="#86868B"></uni-icons>
                <text>{{ formatDuration(course.duration) }}</text>
              </view>
              
              <view class="meta-tag tint-gray" v-if="course.chapters">
                <uni-icons type="list" size="14" color="#86868B"></uni-icons>
                <text>{{ course.chapters }} 章</text>
              </view>
              
              <view class="level-tag" v-if="course.level">{{ course.level }}</view>
            </view>
          </view>

          <view class="card-divider" v-if="course.description"></view>

          <view class="card-bottom-section" v-if="course.description">
            <view class="desc-header">
              <uni-icons type="info" size="20" color="#2CB5A0"></uni-icons>
              <text class="header-title">课程简介</text>
            </view>
            <text class="description-text">{{ course.description }}</text>
          </view>

        </view>
        
      </view>
    </scroll-view>

    <view class="empty-container" v-if="!course && !loading">
      <view class="glass-card empty-state">
        <view class="empty-icon-box">
          <uni-icons type="videocam-filled" size="48" color="#86868B"></uni-icons>
        </view>
        <text class="empty-text">该课程可能已被隐藏或删除</text>
      </view>
    </view>
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
    const systemInfo = uni.getSystemInfoSync();
    this.statusBarHeight = systemInfo.statusBarHeight || 0;

    if (options.courseId) {
      this.courseId = parseInt(options.courseId);
      this.loadCourseDetail();
    }
  },
  methods: {
    goBack() {
      uni.navigateBack();
    },

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

    getImageUrl(url) {
      if (!url) return '';
      if (url.startsWith('http')) return url;
      const baseUrl = config.baseUrl || 'http://localhost:8080';
      return url.startsWith('/') ? baseUrl + url : baseUrl + '/' + url;
    },

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
/* ==================== iOS 风格核心变量 ==================== */
$ios-text-primary: #1D1D1F;
$ios-text-secondary: #86868B;
$theme-cyan: #2CB5A0;

.course-detail-page {
  min-height: 100vh;
  position: relative;
  background-color: #F5F5F7;
  font-family: -apple-system, BlinkMacSystemFont, "SF Pro Text", "Helvetica Neue", Arial, sans-serif;
  display: flex;
  flex-direction: column;
}

/* --- 弥散光影背景 --- */
.ambient-background {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
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
  border-radius: 24rpx; /* 修改：缩小了圆角弧度，原为 36rpx */
}

/* ==================== 顶部导航 ==================== */
.glass-header {
  position: sticky;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  background: rgba(245, 245, 247, 0.6);
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

.navbar-left, .navbar-right {
  width: 80rpx;
  display: flex;
  align-items: center;
}

.nav-icon-glass {
  width: 64rpx;
  height: 64rpx;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.04);
  transition: transform 0.2s ease;
  &:active { transform: scale(0.9); opacity: 0.8; }
}

.navbar-title {
  font-size: 32rpx;
  font-weight: 600;
  color: $ios-text-primary;
  letter-spacing: 1rpx;
}

/* ==================== 页面滚动区 ==================== */
.course-scroll {
  flex: 1;
  margin-top: calc(32rpx + env(safe-area-inset-top));
  height: calc(100vh - 88rpx - env(safe-area-inset-top));
}

.course-content {
  position: relative;
  z-index: 1;
  /* 修改：减小了顶部间距 padding-top 从 30rpx 调整为 16rpx */
  padding: 16rpx 32rpx calc(60rpx + env(safe-area-inset-bottom));
  display: flex;
  flex-direction: column;
  /* 修改：减小了元素间距 gap 从 32rpx 调整为 24rpx */
  gap: 24rpx; 
}

/* ==================== 视频播放区域 ==================== */
.video-section {
  width: 100%;
  height: 420rpx;
  border-radius: 24rpx; /* 修改：同步缩小圆角 */
  overflow: hidden;
  position: relative;
  background: #000;
  transform: translateZ(0); 
}

.shadow-lg {
  box-shadow: 
    0 24rpx 48rpx rgba(0, 0, 0, 0.08),
    0 12rpx 24rpx rgba(0, 0, 0, 0.04);
}

.course-video {
  width: 100%;
  height: 100%;
  display: block;
}

.video-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #A8BEEA 0%, #7C98D6 100%);
  gap: 16rpx;
}

.placeholder-text {
  font-size: 26rpx;
  color: rgba(255, 255, 255, 0.9);
  font-weight: 500;
}

.video-highlight {
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  border-radius: 24rpx; /* 修改：同步缩小高光层圆角 */
  box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.2);
  pointer-events: none;
}

/* ==================== 课程信息与简介 (融合版大卡片) ==================== */
.course-detail-card {
  display: flex;
  flex-direction: column;
}

/* 上半部分：信息区 */
.card-top-section {
  padding: 40rpx;
  display: flex;
  flex-direction: column;
}

.course-title {
  font-size: 40rpx;
  font-weight: 700;
  color: $ios-text-primary;
  line-height: 1.35;
  margin-bottom: 24rpx;
  letter-spacing: -0.5rpx;
}

.course-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

/* 标签系统 (Tinted Badge) */
.meta-tag {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 8rpx 20rpx;
  border-radius: 12rpx;
  font-size: 24rpx;
  font-weight: 600;
}

.tint-cyan {
  background: rgba(44, 181, 160, 0.1);
  color: $theme-cyan;
}

.tint-gray {
  background: rgba(0, 0, 0, 0.04);
  color: $ios-text-secondary;
}

.level-tag {
  background: linear-gradient(135deg, #48D1CC 0%, #2CB5A0 100%);
  color: #FFFFFF;
  font-size: 22rpx;
  font-weight: 600;
  padding: 8rpx 20rpx;
  border-radius: 12rpx;
  display: flex;
  align-items: center;
}

/* 中间极简分割线 */
.card-divider {
  height: 1px;
  background: rgba(0, 0, 0, 0.04);
  margin: 0 40rpx;
}

/* 下半部分：简介区 */
.card-bottom-section {
  padding: 40rpx;
  display: flex;
  flex-direction: column;
}

.desc-header {
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin-bottom: 20rpx;
}

.header-title {
  font-size: 32rpx;
  font-weight: 700;
  color: $ios-text-primary;
}

.description-text {
  font-size: 28rpx;
  color: $ios-text-secondary;
  line-height: 1.6;
  letter-spacing: 0.5rpx;
}

/* ==================== 加载/空状态 ==================== */
.loading-container, .empty-container {
  position: relative;
  z-index: 1;
  flex: 1;
  display: flex;
  justify-content: center;
  padding-top: 200rpx;
}

.loading-card, .empty-state {
  padding: 60rpx 80rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 24rpx;
}

.spin-icon {
  animation: spin 1.5s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.loading-text {
  font-size: 28rpx;
  color: $ios-text-secondary;
}

.empty-icon-box {
  width: 120rpx;
  height: 120rpx;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.04);
  display: flex;
  align-items: center;
  justify-content: center;
}

.empty-text {
  font-size: 28rpx;
  color: $ios-text-secondary;
  font-weight: 500;
}
</style>