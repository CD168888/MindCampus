<template>
  <view class="music-list-page">
    <view class="ambient-background"></view>

    <view class="glass-header" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="navbar-content">
        <view class="navbar-left" @tap="goBack">
          <view class="nav-icon-glass">
            <uni-icons type="left" size="22" color="#1D1D1F"></uni-icons>
          </view>
        </view>
        <view class="navbar-title">愈音工坊</view>
        <view class="navbar-right"></view>
      </view>
    </view>

    <view class="page-content" :style="{ marginTop: (statusBarHeight + 44) + 'px' }">
      
      <view class="search-section">
        <view class="search-box glass-pill">
          <uni-icons type="search" size="20" color="#86868B"></uni-icons>
          <input class="search-input" v-model="searchKeyword" placeholder="搜索音乐、演唱者" placeholder-class="input-placeholder" @confirm="handleSearch" />
        </view>
      </view>

      <scroll-view class="music-scroll" scroll-y @scrolltolower="loadMore" :lower-threshold="100" :enable-back-to-top="true">
        <view class="music-list">
          <view class="glass-card music-card" v-for="item in musicList" :key="item.musicId" @tap="playMusic(item)">
            
            <view class="music-cover-wrapper">
              <image v-if="item.coverUrl" class="music-cover" :src="getImageUrl(item.coverUrl)" mode="aspectFill" :lazy-load="true"></image>
              <view v-else class="music-cover-placeholder">
                <uni-icons type="headphones" size="32" color="#FFFFFF"></uni-icons>
              </view>
              <view class="cover-highlight"></view>
            </view>

            <view class="music-info">
              <text class="music-title">{{ item.title }}</text>
              <text class="music-artist">{{ item.artist || '未知' }} · {{ formatDuration(item.duration) }}</text>
              <view v-if="item.genre" class="meta-tag">
                <text class="meta-category-text">{{ item.genre }}</text>
              </view>
            </view>

            <view class="music-action">
              <view class="play-btn-glass">
                <view class="play-triangle-small"></view>
              </view>
            </view>
          </view>
        </view>

        <view class="load-more" v-if="hasMore && musicList.length > 0">
          <uni-icons v-if="loadStatus === 'loading'" type="spinner-cycle" size="18" color="#86868B" class="spin-icon"></uni-icons>
          <text class="status-text">{{ loadStatus === 'loading' ? '正在获取频率...' : '上拉探索更多' }}</text>
        </view>

        <view class="no-more" v-if="!hasMore && musicList.length > 0">
          <view class="no-more-line"></view>
          <text class="status-text">已经到底啦</text>
          <view class="no-more-line"></view>
        </view>

        <view v-if="musicList.length === 0 && !loading" class="empty-state glass-card">
          <view class="empty-icon-box">
            <uni-icons type="sound-filled" size="48" color="#2CB5A0"></uni-icons>
          </view>
          <text class="empty-title">暂无共鸣频率</text>
          <text class="empty-desc">试着换个搜索词，或者稍后再来吧</text>
        </view>
      </scroll-view>
    </view>
  </view>
</template>

<script>
import {listMusic} from '@/api/music'
import config from '@/config'

export default {
  data() {
    return {
      statusBarHeight: 0,
      loading: false,
      searchKeyword: '',
      musicList: [],
      pageNum: 1,
      pageSize: 10,
      total: 0,
      hasMore: true,
      loadStatus: 'more' // more | loading | noMore
    }
  },
  onLoad() {
    const systemInfo = uni.getSystemInfoSync()
    this.statusBarHeight = systemInfo.statusBarHeight || 0
    this.loadMusicList(true)
  },
  methods: {
    goBack() { uni.navigateBack() },

    handleSearch() {
      this.pageNum = 1
      this.musicList = []
      this.hasMore = true
      this.loadMusicList(true)
    },

    loadMusicList(reset = false) {
      if (this.loading) return
      this.loading = true
      this.loadStatus = 'loading'

      const params = {
        pageNum: this.pageNum,
        pageSize: this.pageSize,
        status: '0' 
      }

      if (this.searchKeyword) {
        params.keyword = this.searchKeyword
      }

      listMusic(params).then(res => {
        this.loading = false
        if (res.code === 200) {
          const newList = res.rows || []
          if (reset) {
            this.musicList = newList
          } else {
            this.musicList = this.musicList.concat(newList)
          }
          this.total = res.total || 0
          if (this.musicList.length >= this.total) {
            this.hasMore = false
            this.loadStatus = 'noMore'
          } else {
            this.hasMore = true
            this.loadStatus = 'more'
          }
        } else {
          this.$modal.showToast(res.msg || '加载失败')
          this.loadStatus = 'more'
        }
      }).catch(err => {
        this.loading = false
        this.loadStatus = 'more'
        console.error('加载音乐列表失败:', err)
        this.$modal.showToast('加载失败，请重试')
      })
    },

    loadMore() {
      if (!this.hasMore || this.loading) return
      this.pageNum++
      this.loadMusicList(false)
    },

    playMusic(item) {
      const musicList = JSON.stringify(this.musicList)
      uni.navigateTo({
        url: `/pages/music/player?musicId=${item.musicId}&musicList=${encodeURIComponent(musicList)}`
      })
    },

    formatDuration(seconds) {
      if (!seconds) return '0:00'
      const mins = Math.floor(seconds / 60)
      const secs = seconds % 60
      return `${mins}:${secs.toString().padStart(2, '0')}`
    },

    getImageUrl(url) {
      if (!url) return ''
      if (url.startsWith('http')) return url
      const baseUrl = config.baseUrl || 'http://localhost:8080'
      return url.startsWith('/') ? baseUrl + url : baseUrl + '/' + url
    }
  }
}
</script>

<style lang="scss" scoped>
/* ==================== iOS 风格核心变量 ==================== */
$ios-text-primary: #1D1D1F;
$ios-text-secondary: #86868B;
$theme-cyan: #2CB5A0;

.music-list-page {
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
  height: 100vh; /* 撑满屏幕高度，交给内部 scroll-view 滚动 */
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
    box-shadow: 0 8px 32px rgba(44, 181, 160, 0.15); /* 聚焦时透出青色光晕 */
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

/* ==================== 音乐列表 ==================== */
.music-scroll {
  flex: 1;
  height: 0; /* 触发 flex 内部的正确滚动 */
}

.music-list {
  padding: 10rpx 32rpx 40rpx;
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.music-card {
  padding: 24rpx;
  display: flex;
  align-items: center;
  transition: transform 0.2s ease;
  
  &:active {
    transform: scale(0.97);
    background: rgba(255, 255, 255, 0.85);
  }
}

.music-cover-wrapper {
  width: 110rpx;
  height: 110rpx;
  border-radius: 24rpx;
  margin-right: 24rpx;
  position: relative;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  transform: translateZ(0); /* 裁剪圆角溢出 */
}

.music-cover {
  width: 100%;
  height: 100%;
  display: block;
}

.music-cover-placeholder {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #A8BEEA 0%, #7C98D6 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.cover-highlight {
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  border-radius: 24rpx;
  box-shadow: inset 0 0 0 0.5px rgba(255, 255, 255, 0.6), inset 0 0 0 1px rgba(0, 0, 0, 0.02);
  pointer-events: none;
}

.music-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.music-title {
  font-size: 32rpx;
  font-weight: 600;
  color: $ios-text-primary;
  margin-bottom: 6rpx;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.music-artist {
  font-size: 26rpx;
  color: $ios-text-secondary;
  margin-bottom: 10rpx;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 治愈青色分类标签 */
.meta-tag {
  align-self: flex-start; /* 避免占满整行 */
  background: rgba(44, 181, 160, 0.1);
  padding: 4rpx 16rpx;
  border-radius: 12rpx;
}

.meta-category-text {
  font-size: 22rpx;
  color: $theme-cyan;
  font-weight: 600;
}

.music-action {
  margin-left: 20rpx;
}

/* 小巧精致的纯手绘播放按钮 */
.play-btn-glass {
  width: 64rpx;
  height: 64rpx;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.8);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.04);
  border: 1px solid rgba(44, 181, 160, 0.15); /* 边缘带一点青色 */
  display: flex;
  align-items: center;
  justify-content: center;
}

.play-triangle-small {
  width: 0;
  height: 0;
  border-top: 10rpx solid transparent;
  border-bottom: 10rpx solid transparent;
  border-left: 14rpx solid $theme-cyan;
  margin-left: 4rpx; /* 居中修正 */
  border-radius: 2rpx;
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