<template>
  <view class="my-likes-page">
    <view class="ambient-background"></view>

    <view class="glass-header" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="navbar-content">
        <view class="navbar-left" @tap="goBack">
          <view class="nav-icon-glass">
            <uni-icons type="left" size="22" color="#1D1D1F"></uni-icons>
          </view>
        </view>
        <view class="navbar-title">我的点赞</view>
        <view class="navbar-right"></view>
      </view>
    </view>

    <view class="page-content">
      <!-- 标签切换 -->
      <view class="tab-bar glass-card">
        <view
          class="tab-item"
          :class="{ active: currentTab === 0 }"
          @tap="switchTab(0)"
        >
          <uni-icons type="headphones" size="18" :color="currentTab === 0 ? '#2CB5A0' : '#86868B'"></uni-icons>
          <text :class="{ active: currentTab === 0 }">音乐</text>
        </view>
        <view
          class="tab-item"
          :class="{ active: currentTab === 1 }"
          @tap="switchTab(1)"
        >
          <uni-icons type="videocam" size="18" :color="currentTab === 1 ? '#2CB5A0' : '#86868B'"></uni-icons>
          <text :class="{ active: currentTab === 1 }">课程</text>
        </view>
        <view
          class="tab-item"
          :class="{ active: currentTab === 2 }"
          @tap="switchTab(2)"
        >
          <uni-icons type="paper" size="18" :color="currentTab === 2 ? '#2CB5A0' : '#86868B'"></uni-icons>
          <text :class="{ active: currentTab === 2 }">文章</text>
        </view>
      </view>

      <scroll-view class="content-scroll" scroll-y @scrolltolower="loadMore" :lower-threshold="100">
        <!-- 音乐列表 -->
        <view v-show="currentTab === 0" class="list-container">
          <view v-if="loading && musicList.length === 0" class="loading-state">
            <uni-icons type="spinner-cycle" size="24" color="#2CB5A0" class="spin-icon"></uni-icons>
            <text class="loading-text">加载中...</text>
          </view>

          <view v-else-if="musicList.length === 0" class="empty-state glass-card">
            <view class="empty-icon-box">
              <uni-icons type="heart-filled" size="48" color="#2CB5A0"></uni-icons>
            </view>
            <text class="empty-title">暂无点赞音乐</text>
            <text class="empty-desc">去发现喜欢的音乐吧</text>
          </view>

          <view v-else class="item-list">
            <view
              v-for="item in musicList"
              :key="item.musicId"
              class="glass-card item-card music-card"
              @tap="goToMusicDetail(item)"
            >
              <view class="item-cover-wrapper">
                <image v-if="item.coverUrl" class="item-cover" :src="getImageUrl(item.coverUrl)" mode="aspectFill" :lazy-load="true"></image>
                <view v-else class="item-cover-placeholder">
                  <uni-icons type="headphones" size="28" color="#FFFFFF"></uni-icons>
                </view>
              </view>
              <view class="item-info">
                <text class="item-title">{{ item.title }}</text>
                <text class="item-subtitle">{{ item.artist || '未知' }} · {{ formatDuration(item.duration) }}</text>
                <view v-if="item.genre" class="item-tag">
                  <text class="item-tag-text">{{ item.genre }}</text>
                </view>
              </view>
              <view class="item-action">
                <uni-icons type="play-circle-filled" size="28" color="#2CB5A0"></uni-icons>
              </view>
            </view>
          </view>
        </view>

        <!-- 课程列表 -->
        <view v-show="currentTab === 1" class="list-container">
          <view v-if="loading && courseList.length === 0" class="loading-state">
            <uni-icons type="spinner-cycle" size="24" color="#2CB5A0" class="spin-icon"></uni-icons>
            <text class="loading-text">加载中...</text>
          </view>

          <view v-else-if="courseList.length === 0" class="empty-state glass-card">
            <view class="empty-icon-box">
              <uni-icons type="heart-filled" size="48" color="#2CB5A0"></uni-icons>
            </view>
            <text class="empty-title">暂无点赞课程</text>
            <text class="empty-desc">去发现喜欢的课程吧</text>
          </view>

          <view v-else class="item-list">
            <view
              v-for="item in courseList"
              :key="item.courseId"
              class="glass-card item-card course-card"
              @tap="goToCourseDetail(item)"
            >
              <view class="item-cover-wrapper">
                <image v-if="item.coverUrl" class="item-cover" :src="getImageUrl(item.coverUrl)" mode="aspectFill" :lazy-load="true"></image>
                <view v-else class="item-cover-placeholder">
                  <uni-icons type="videocam" size="28" color="#FFFFFF"></uni-icons>
                </view>
              </view>
              <view class="item-info">
                <text class="item-title">{{ item.title }}</text>
                <text class="item-subtitle">{{ item.lecturer || '未知讲师' }} · {{ item.totalDuration || 0 }}分钟</text>
                <view v-if="item.level" class="item-tag">
                  <text class="item-tag-text">{{ item.level }}</text>
                </view>
              </view>
              <view class="item-action">
                <uni-icons type="right" size="20" color="#86868B"></uni-icons>
              </view>
            </view>
          </view>
        </view>

        <!-- 文章列表 -->
        <view v-show="currentTab === 2" class="list-container">
          <view v-if="loading && articleList.length === 0" class="loading-state">
            <uni-icons type="spinner-cycle" size="24" color="#2CB5A0" class="spin-icon"></uni-icons>
            <text class="loading-text">加载中...</text>
          </view>

          <view v-else-if="articleList.length === 0" class="empty-state glass-card">
            <view class="empty-icon-box">
              <uni-icons type="heart-filled" size="48" color="#2CB5A0"></uni-icons>
            </view>
            <text class="empty-title">暂无点赞文章</text>
            <text class="empty-desc">去发现喜欢的文章吧</text>
          </view>

          <view v-else class="item-list">
            <view
              v-for="item in articleList"
              :key="item.articleId"
              class="glass-card item-card article-card"
              @tap="goToArticleDetail(item)"
            >
              <view class="article-content">
                <text class="article-title">{{ item.title }}</text>
                <text class="article-excerpt">{{ item.summary || '暂无摘要' }}</text>

                <view class="article-meta">
                  <view class="meta-tag">
                    <uni-icons :type="getCategoryIcon(item.category)" size="12" color="#007AFF"></uni-icons>
                    <text class="meta-category-text">{{ item.category || '心理健康' }}</text>
                  </view>
                  <view class="meta-info-group">
                    <text class="meta-author">
                      <uni-icons type="person" size="12" color="#86868B"></uni-icons> {{ item.author || '匿名' }}
                    </text>
                    <text class="meta-author">
                      <uni-icons type="eye" size="12" color="#86868B"></uni-icons> {{ item.readCount || 0 }}
                    </text>
                  </view>
                </view>
              </view>
              <view class="article-arrow">
                <uni-icons type="right" size="16" color="#C7C7CC"></uni-icons>
              </view>
            </view>
          </view>
        </view>

        <view class="bottom-spacer"></view>
      </scroll-view>
    </view>
  </view>
</template>

<script>
import {getLikedMusic} from '@/api/music'
import {getLikedCourses} from '@/api/course'
import {getLikedArticles} from '@/api/article'
import config from '@/config'

export default {
  data() {
    return {
      statusBarHeight: 0,
      currentTab: 0,
      loading: false,
      musicList: [],
      courseList: [],
      articleList: []
    }
  },
  onLoad() {
    const sys = uni.getSystemInfoSync()
    this.statusBarHeight = sys.statusBarHeight || 0
    this.loadData()
  },
  methods: {
    goBack() {
      uni.navigateBack()
    },
    switchTab(index) {
      this.currentTab = index
    },
    async loadData() {
      this.loading = true
      try {
        if (this.currentTab === 0) {
          const res = await getLikedMusic()
          this.musicList = res.data || []
        } else if (this.currentTab === 1) {
          const res = await getLikedCourses()
          this.courseList = res.data || []
        } else {
          const res = await getLikedArticles()
          this.articleList = res.data || []
        }
      } catch (e) {
        console.error('加载点赞列表失败:', e)
      } finally {
        this.loading = false
      }
    },
    loadMore() {
      // 暂无限流加载
    },
    getImageUrl(url) {
      if (!url) return ''
      if (url.startsWith('http')) return url
      return config.baseUrl + url
    },
    formatDuration(seconds) {
      if (!seconds) return '00:00'
      const min = Math.floor(seconds / 60)
      const sec = seconds % 60
      return `${String(min).padStart(2, '0')}:${String(sec).padStart(2, '0')}`
    },
    goToMusicDetail(item) {
      uni.navigateTo({
        url: `/pages/music/detail?id=${item.musicId}`
      })
    },
    goToCourseDetail(item) {
      uni.navigateTo({
        url: `/pages/course/detail?id=${item.courseId}`
      })
    },
    goToArticleDetail(item) {
      uni.navigateTo({
        url: `/pages/article/detail?id=${item.articleId}`
      })
    },
    getCategoryIcon(category) {
      const categoryMap = {
        '压力管理': 'fire',
        '情绪调节': 'heart',
        '人际关系': 'hand-up',
        '学习方法': 'book',
        '心理健康': 'staff',
        '正念冥想': 'flower',
        '睡眠改善': 'moon',
        '自我成长': 'seedling'
      }
      return categoryMap[category] || 'folder'
    }
  },
  watch: {
    currentTab() {
      this.loadData()
    }
  }
}
</script>

<style lang="scss" scoped>
$ios-text-primary: #1D1D1F;
$ios-text-secondary: #86868B;
$theme-cyan: #2CB5A0;
$ios-blue: #007AFF;

.my-likes-page {
  min-height: 100vh;
  position: relative;
  background-color: #F5F5F7;
  font-family: -apple-system, BlinkMacSystemFont, "SF Pro Text", "Helvetica Neue", Arial, sans-serif;
}

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

.glass-card {
  background: rgba(255, 255, 255, 0.65);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.03);
  border-radius: 40rpx;
}

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

.navbar-left, .navbar-right {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.nav-icon-glass {
  width: 60rpx; height: 60rpx;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(10px);
  display: flex;
  align-items: center;
  justify-content: center;
}

.navbar-title {
  font-size: 34rpx;
  font-weight: 600;
  color: $ios-text-primary;
  letter-spacing: 1rpx;
}

.page-content {
  position: relative;
  z-index: 1;
  padding: 32rpx;
}

.tab-bar {
  display: flex;
  padding: 12rpx;
  margin-bottom: 24rpx;
}

.tab-item {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10rpx;
  padding: 20rpx 0;
  border-radius: 30rpx;
  transition: all 0.3s ease;

  text {
    font-size: 28rpx;
    color: $ios-text-secondary;
    font-weight: 500;

    &.active {
      color: $theme-cyan;
      font-weight: 600;
    }
  }

  &.active {
    background: rgba(44, 181, 160, 0.1);
  }
}

.content-scroll {
  height: calc(100vh - 300rpx - env(safe-area-inset-bottom));
}

.list-container {
  min-height: 400rpx;
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80rpx 0;
  gap: 20rpx;
}

.spin-icon {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.loading-text {
  font-size: 26rpx;
  color: $ios-text-secondary;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80rpx 40rpx;
  margin-top: 40rpx;
}

.empty-icon-box {
  width: 140rpx;
  height: 140rpx;
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
  margin-bottom: 16rpx;
}

.empty-desc {
  font-size: 26rpx;
  color: $ios-text-secondary;
}

.item-list {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.item-card {
  display: flex;
  align-items: center;
  padding: 24rpx;
  transition: transform 0.2s;

  &:active {
    transform: scale(0.98);
  }
}

.item-cover-wrapper {
  width: 120rpx;
  height: 120rpx;
  border-radius: 20rpx;
  overflow: hidden;
  flex-shrink: 0;
}

.item-cover {
  width: 100%;
  height: 100%;
}

.item-cover-placeholder {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, $theme-cyan 0%, #4FDACE 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.item-info {
  flex: 1;
  margin-left: 24rpx;
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.item-title {
  font-size: 30rpx;
  font-weight: 600;
  color: $ios-text-primary;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.item-subtitle {
  font-size: 24rpx;
  color: $ios-text-secondary;
}

.item-tag {
  display: inline-flex;
  align-items: center;
  background: rgba(44, 181, 160, 0.1);
  padding: 6rpx 16rpx;
  border-radius: 20rpx;
  align-self: flex-start;
  margin-top: 8rpx;
}

.item-tag-text {
  font-size: 20rpx;
  color: $theme-cyan;
  font-weight: 500;
}

.item-action {
  width: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.bottom-spacer {
  height: 40rpx;
}

/* 文章卡片样式 - 匹配首页设计 */
.article-card {
  padding: 24rpx;
  border-radius: 24rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: relative;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.55);

  &:active {
    transform: scale(0.98);
  }
}

.article-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding-right: 16rpx;
  z-index: 1;
  gap: 8rpx;
}

.article-title {
  font-size: 30rpx;
  font-weight: 600;
  color: $ios-text-primary;
  line-height: 1.3;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.article-excerpt {
  font-size: 24rpx;
  color: $ios-text-secondary;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.article-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.meta-tag {
  display: flex;
  align-items: center;
  gap: 4rpx;
  background: rgba(0, 122, 255, 0.08);
  padding: 4rpx 12rpx;
  border-radius: 10rpx;
}

.meta-category-text {
  font-size: 20rpx;
  color: $ios-blue;
  font-weight: 500;
}

.meta-info-group {
  display: flex;
  gap: 16rpx;
}

.meta-author {
  font-size: 20rpx;
  color: $ios-text-secondary;
  display: flex;
  align-items: center;
  gap: 4rpx;
}

.article-arrow {
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
