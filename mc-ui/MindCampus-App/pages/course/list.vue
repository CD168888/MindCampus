<template>
  <view class="course-list-page">
    <view class="ambient-background"></view>

    <view class="glass-header" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="navbar-content">
        <view class="navbar-left" @tap="goBack">
          <view class="nav-icon-glass">
            <uni-icons type="left" size="22" color="#1D1D1F"></uni-icons>
          </view>
        </view>
        <view class="navbar-title">精品课程</view>
        <view class="navbar-right"></view>
      </view>
    </view>

    <view class="page-content" :style="{ marginTop: (statusBarHeight + 44) + 'px' }">
      
      <view class="search-section">
        <view class="search-box glass-pill">
          <uni-icons type="search" size="20" color="#86868B"></uni-icons>
          <input class="search-input" v-model="searchKeyword" placeholder="搜索课程、导师" placeholder-class="input-placeholder" @confirm="handleSearch" />
        </view>
      </view>

      <scroll-view class="course-scroll" scroll-y @scrolltolower="loadMore" :lower-threshold="100" :enable-back-to-top="true">
        <view class="course-list">
          <view class="glass-card course-card" v-for="item in courseList" :key="item.courseId" @tap="viewCourse(item)">
            
            <view class="course-cover-wrapper">
              <image v-if="item.coverUrl" class="course-cover" :src="getImageUrl(item.coverUrl)" mode="aspectFill" :lazy-load="true"></image>
              <view v-else class="course-cover-placeholder">
                <uni-icons type="videocam-filled" size="36" color="#FFFFFF"></uni-icons>
              </view>
              <view class="cover-highlight"></view>
              <view class="duration-badge">
                <text>{{ formatDuration(item.duration) }}</text>
              </view>
            </view>

            <view class="course-info">
              <view class="info-top">
                <text class="course-title">{{ item.title }}</text>
                <text class="course-description" v-if="item.description">{{ item.description }}</text>
              </view>
              
              <view class="course-footer">
                <view class="meta-tags">
                  <view class="level-tag" v-if="item.level">{{ item.level }}</view>
                  <view class="chapter-tag" v-if="item.chapters">{{ item.chapters }}章</view>
                </view>
                
                <view class="course-lecturer">
                  <uni-icons type="person" size="14" color="#86868B"></uni-icons>
                  <text class="lecturer-text">{{ item.lecturer || '特邀导师' }}</text>
                </view>
              </view>
            </view>
            
          </view>
        </view>

        <view class="load-more" v-if="hasMore && courseList.length > 0">
          <uni-icons v-if="loadStatus === 'loading'" type="spinner-cycle" size="18" color="#86868B" class="spin-icon"></uni-icons>
          <text class="status-text">{{ loadStatus === 'loading' ? '正在获取课程...' : '上拉探索更多' }}</text>
        </view>

        <view class="no-more" v-if="!hasMore && courseList.length > 0">
          <view class="no-more-line"></view>
          <text class="status-text">已经到底啦</text>
          <view class="no-more-line"></view>
        </view>

        <view v-if="courseList.length === 0 && !loading" class="empty-state glass-card">
          <view class="empty-icon-box">
            <uni-icons type="videocam-filled" size="48" color="#2CB5A0"></uni-icons>
          </view>
          <text class="empty-title">暂无相关课程</text>
          <text class="empty-desc">试着换个搜索词，或者稍后再来吧</text>
        </view>
      </scroll-view>
    </view>
  </view>
</template>

<script>
import {listCourses} from '@/api/course';
import config from '@/config';

export default {
  data() {
    return {
      statusBarHeight: 0,
      loading: false,
      searchKeyword: '',
      courseList: [],
      pageNum: 1,
      pageSize: 10,
      total: 0,
      hasMore: true,
      loadStatus: 'more', // more | loading | noMore
    };
  },
  onLoad() {
    const systemInfo = uni.getSystemInfoSync();
    this.statusBarHeight = systemInfo.statusBarHeight || 0;
    this.loadCourseList(true);
  },
  methods: {
    goBack() {
      uni.navigateBack();
    },

    handleSearch() {
      this.pageNum = 1;
      this.courseList = [];
      this.hasMore = true;
      this.loadCourseList(true);
    },

    loadCourseList(reset = false) {
      if (this.loading) return;

      this.loading = true;
      this.loadStatus = 'loading';

      const params = {
        pageNum: this.pageNum,
        pageSize: this.pageSize,
        status: '0', 
      };

      if (this.searchKeyword) {
        params.keyword = this.searchKeyword;
      }

      listCourses(params)
        .then((res) => {
          this.loading = false;

          if (res.code === 200) {
            const newList = res.rows || [];

            if (reset) {
              this.courseList = newList;
            } else {
              this.courseList = this.courseList.concat(newList);
            }

            this.total = res.total || 0;

            if (this.courseList.length >= this.total) {
              this.hasMore = false;
              this.loadStatus = 'noMore';
            } else {
              this.hasMore = true;
              this.loadStatus = 'more';
            }
          } else {
            this.$modal.showToast(res.msg || '加载失败');
            this.loadStatus = 'more';
          }
        })
        .catch((err) => {
          this.loading = false;
          this.loadStatus = 'more';
          console.error('加载课程列表失败:', err);
          this.$modal.showToast('加载失败，请重试');
        });
    },

    loadMore() {
      if (!this.hasMore || this.loading) return;
      this.pageNum++;
      this.loadCourseList(false);
    },

    viewCourse(item) {
      uni.navigateTo({
        url: `/pages/course/detail?courseId=${item.courseId}`,
      });
    },

    formatDuration(seconds) {
      if (!seconds) return '0:00';
      const mins = Math.floor(seconds / 60);
      const secs = seconds % 60;
      return `${mins}:${secs.toString().padStart(2, '0')}`;
    },

    getImageUrl(url) {
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

.course-list-page {
  min-height: 100vh;
  position: relative;
  display: flex;
  flex-direction: column;
  background-color: #F5F5F7;
  font-family: -apple-system, BlinkMacSystemFont, "SF Pro Text", "Helvetica Neue", Arial, sans-serif;
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
  border-radius: 32rpx;
}

/* ==================== 顶部导航 ==================== */
.glass-header {
  position: fixed;
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

/* ==================== 页面内容容器 ==================== */
.page-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  position: relative;
  z-index: 1;
  height: 100vh;
}

/* ==================== 搜索栏 ==================== */
.search-section {
  padding: 20rpx 32rpx;
  flex-shrink: 0;
}

.glass-pill {
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.9);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.03);
  border-radius: 50rpx;
  padding: 16rpx 32rpx;
  display: flex;
  align-items: center;
  gap: 16rpx;
  transition: all 0.3s ease;

  &:focus-within {
    background: rgba(255, 255, 255, 0.9);
    box-shadow: 0 8px 32px rgba(44, 181, 160, 0.15);
    border-color: rgba(44, 181, 160, 0.3);
  }
}

.search-input {
  flex: 1;
  font-size: 28rpx;
  color: $ios-text-primary;
  height: 48rpx;
}

.input-placeholder {
  color: #A1A1A6;
}

/* ==================== 课程列表 ==================== */
.course-scroll {
  flex: 1;
  height: 0; 
}

.course-list {
  padding: 10rpx 32rpx 40rpx;
  display: flex;
  flex-direction: column;
  gap: 28rpx;
}

.course-card {
  padding: 24rpx;
  display: flex;
  transition: transform 0.25s cubic-bezier(0.2, 0.8, 0.2, 1);
  
  &:active {
    transform: scale(0.97);
    background: rgba(255, 255, 255, 0.85);
  }
}

/* 左侧封面设计 */
.course-cover-wrapper {
  width: 240rpx;
  height: 160rpx;
  border-radius: 20rpx;
  margin-right: 24rpx;
  position: relative;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  transform: translateZ(0); 
  flex-shrink: 0;
}

.course-cover {
  width: 100%;
  height: 100%;
  display: block;
}

.course-cover-placeholder {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #81ECEC 0%, #00CEC9 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.cover-highlight {
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  border-radius: 20rpx;
  box-shadow: inset 0 0 0 0.5px rgba(255, 255, 255, 0.6), inset 0 0 0 1px rgba(0, 0, 0, 0.02);
  pointer-events: none;
}

/* iOS 风格半透明黑色视频角标 */
.duration-badge {
  position: absolute;
  bottom: 12rpx;
  right: 12rpx;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  padding: 4rpx 10rpx;
  border-radius: 8rpx;
  
  text {
    color: #FFFFFF;
    font-size: 20rpx;
    font-weight: 600;
    font-variant-numeric: tabular-nums;
  }
}

/* 右侧信息设计 */
.course-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 4rpx 0;
}

.info-top {
  display: flex;
  flex-direction: column;
}

.course-title {
  font-size: 32rpx;
  font-weight: 600;
  color: $ios-text-primary;
  margin-bottom: 8rpx;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.course-description {
  font-size: 24rpx;
  color: $ios-text-secondary;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.course-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 12rpx;
}

.meta-tags {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

/* 治愈青色 Tinted Badge */
.level-tag {
  background: rgba(44, 181, 160, 0.1);
  color: $theme-cyan;
  font-size: 20rpx;
  font-weight: 600;
  padding: 4rpx 12rpx;
  border-radius: 8rpx;
}

/* 次要标签 */
.chapter-tag {
  background: rgba(0, 0, 0, 0.04);
  color: $ios-text-secondary;
  font-size: 20rpx;
  font-weight: 500;
  padding: 4rpx 12rpx;
  border-radius: 8rpx;
}

.course-lecturer {
  display: flex;
  align-items: center;
  gap: 6rpx;
}

.lecturer-text {
  font-size: 24rpx;
  color: $ios-text-secondary;
}

/* ==================== 状态栏 (加载、空状态) ==================== */
.load-more, .no-more {
  padding: 40rpx 0 calc(40rpx + env(safe-area-inset-bottom));
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
}

.spin-icon {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.status-text {
  font-size: 24rpx;
  color: $ios-text-secondary;
  font-weight: 500;
}

.no-more-line {
  width: 60rpx;
  height: 1px;
  background: rgba(0, 0, 0, 0.05);
}

.empty-state {
  margin: 60rpx 32rpx;
  padding: 100rpx 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.empty-icon-box {
  width: 120rpx;
  height: 120rpx;
  border-radius: 50%;
  background: rgba(44, 181, 160, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 32rpx;
}

.empty-title {
  font-size: 32rpx;
  font-weight: 600;
  color: $ios-text-primary;
  margin-bottom: 12rpx;
}

.empty-desc {
  font-size: 26rpx;
  color: $ios-text-secondary;
}
</style>