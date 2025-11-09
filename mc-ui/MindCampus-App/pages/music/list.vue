<template>
  <view class="music-list-page">
    <!-- 自定义导航栏 -->
    <view class="custom-navbar" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="navbar-content">
        <view class="navbar-left" @tap="goBack">
          <text class="back-icon">←</text>
        </view>
        <view class="navbar-title">音乐疗愈</view>
        <view class="navbar-right"></view>
      </view>
    </view>

    <!-- 搜索栏 -->
    <view class="search-section">
      <view class="search-box">
        <input class="search-input" v-model="searchKeyword" placeholder="搜索音乐、演唱者" @confirm="handleSearch" />
        <text class="search-icon" @tap="handleSearch">🔍</text>
      </view>
    </view>

    <!-- 音乐列表 -->
    <scroll-view class="music-scroll" scroll-y @scrolltolower="loadMore" :lower-threshold="100"
      :enable-back-to-top="true">
      <view class="music-list">
        <view class="music-item" v-for="item in musicList" :key="item.musicId" @tap="playMusic(item)">
          <image v-if="item.coverUrl" class="music-cover" :src="getImageUrl(item.coverUrl)" mode="aspectFill"
            :lazy-load="true"></image>
          <view v-else class="music-cover-placeholder">🎵</view>
          <view class="music-info">
            <view class="music-title">{{ item.title }}</view>
            <view class="music-artist">{{ item.artist || '未知' }} · {{ formatDuration(item.duration) }}</view>
            <view v-if="item.genre" class="music-genre">{{ item.genre }}</view>
          </view>
          <view class="music-action">
            <text class="play-icon">▶</text>
          </view>
        </view>
      </view>

      <!-- 加载更多 -->
      <view class="load-more" v-if="hasMore && musicList.length > 0">
        <view v-if="loadStatus === 'loading'" class="loading-text">正在加载...</view>
        <view v-else class="load-more-text">上拉加载更多</view>
      </view>

      <!-- 没有更多 -->
      <view class="no-more" v-if="!hasMore && musicList.length > 0">
        <text class="no-more-text">没有更多了</text>
      </view>

      <!-- 空状态 -->
      <view v-if="musicList.length === 0 && !loading" class="empty-state">
        <text class="empty-icon">🎵</text>
        <text class="empty-text">暂无音乐</text>
      </view>
    </scroll-view>
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
    // 获取状态栏高度
    const systemInfo = uni.getSystemInfoSync()
    this.statusBarHeight = systemInfo.statusBarHeight || 0

    // 加载数据
    this.loadMusicList(true)
  },
  methods: {
    // 返回
    goBack() {
      uni.navigateBack()
    },

    // 搜索
    handleSearch() {
      this.pageNum = 1
      this.musicList = []
      this.hasMore = true
      this.loadMusicList(true)
    },

    // 加载音乐列表
    loadMusicList(reset = false) {
      if (this.loading) return

      this.loading = true
      this.loadStatus = 'loading'

      const params = {
        pageNum: this.pageNum,
        pageSize: this.pageSize,
        status: '0' // 只查询正常状态的音乐
      }

      // 添加搜索条件（使用 keyword 参数进行模糊搜索，后端会搜索 title 或 artist）
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

          // 判断是否还有更多
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

    // 加载更多
    loadMore() {
      if (!this.hasMore || this.loading) return

      this.pageNum++
      this.loadMusicList(false)
    },

    // 播放音乐
    playMusic(item) {
      // 跳转到播放页面，传递当前列表
      const musicList = JSON.stringify(this.musicList)
      uni.navigateTo({
        url: `/pages/music/player?musicId=${item.musicId}&musicList=${encodeURIComponent(musicList)}`
      })
    },

    // 格式化时长（秒转分:秒）
    formatDuration(seconds) {
      if (!seconds) return '0:00'
      const mins = Math.floor(seconds / 60)
      const secs = seconds % 60
      return `${mins}:${secs.toString().padStart(2, '0')}`
    },

    // 获取图片完整URL
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
@import '@/static/scss/theme.scss';

.music-list-page {
  min-height: 100vh;
  position: relative;

  &::before {
    content: '';
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background:
      linear-gradient(to bottom, transparent -30px, #FFFFFF 400px),
      linear-gradient(135deg, rgba(167, 243, 208, 0.15) 0%, rgba(196, 181, 253, 0.15) 50%, rgba(254, 205, 211, 0.15) 100%);
    z-index: 0;
  }

  >* {
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

/* 搜索栏 */
.search-section {
  padding: $spacing-lg $spacing-lg;
  background: transparent;
  margin-top: calc(88rpx + env(safe-area-inset-top));
}

.search-box {
  display: flex;
  align-items: center;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10rpx);
  border-radius: $radius-full;
  padding: 24rpx 32rpx;
  box-shadow:
    0 4rpx 16rpx rgba(167, 243, 208, 0.08),
    0 2rpx 8rpx rgba(196, 181, 253, 0.08);
  border: 2rpx solid rgba(167, 243, 208, 0.2);
  transition: all 0.3s ease;

  &:focus-within {
    border-color: rgba(167, 139, 250, 0.4);
    box-shadow:
      0 8rpx 24rpx rgba(167, 243, 208, 0.12),
      0 4rpx 12rpx rgba(196, 181, 253, 0.12);
  }
}

.search-input {
  flex: 1;
  font-size: $font-sm;
  color: $text-primary;
  font-family: $font-family-base;
  background: transparent;
}

.search-icon {
  font-size: $font-md;
  margin-left: $spacing-md;
  color: $text-tertiary;
}

/* 滚动区域 */
.music-scroll {
  height: calc(100vh - 88rpx - 100rpx - env(safe-area-inset-top));
  padding: 0 $spacing-lg $spacing-xl;
}

/* 音乐列表 */
.music-list {
  .music-item {
    display: flex;
    align-items: center;
    background: rgba(255, 255, 255, 0.7);
    backdrop-filter: blur(10rpx);
    border-radius: $radius-lg;
    padding: $spacing-lg;
    margin-bottom: $spacing-base;
    box-shadow:
      0 4rpx 16rpx rgba(167, 243, 208, 0.08),
      0 2rpx 8rpx rgba(196, 181, 253, 0.08);
    border: 1rpx solid rgba(255, 255, 255, 0.6);
    transition: all 0.3s ease;
    position: relative;
    overflow: hidden;

    &::before {
      content: '';
      position: absolute;
      left: 0;
      top: 0;
      width: 100%;
      height: 100%;
      background: $gradient-card;
      opacity: 0;
      transition: opacity 0.3s ease;
    }

    &:active {
      transform: translateY(-2rpx);
      box-shadow:
        0 8rpx 24rpx rgba(167, 243, 208, 0.12),
        0 4rpx 12rpx rgba(196, 181, 253, 0.12);

      &::before {
        opacity: 1;
      }
    }
  }

  .music-cover {
    width: 120rpx;
    height: 120rpx;
    border-radius: $radius-base;
    margin-right: $spacing-lg;
    background: $bg-gray-100;
    position: relative;
    z-index: 1;
  }

  .music-cover-placeholder {
    width: 120rpx;
    height: 120rpx;
    border-radius: $radius-base;
    margin-right: $spacing-lg;
    background: linear-gradient(135deg, #6ee7b7 0%, #a78bfa 50%, #fda4af 100%);
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 60rpx;
    position: relative;
    z-index: 1;
  }

  .music-info {
    flex: 1;
    min-width: 0;
    position: relative;
    z-index: 1;
  }

  .music-title {
    font-size: $font-md;
    font-weight: $font-semibold;
    color: $text-primary;
    margin-bottom: $spacing-xs;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    font-family: $font-family-base;
  }

  .music-artist {
    font-size: $font-xs;
    color: $text-tertiary;
    margin-bottom: $spacing-xs;
    font-family: $font-family-base;
  }

  .music-genre {
    font-size: 20rpx;
    color: #a78bfa;
    background: linear-gradient(135deg, rgba(167, 243, 208, 0.15) 0%, rgba(196, 181, 253, 0.15) 100%);
    padding: 6rpx 16rpx;
    border-radius: $radius-full;
    display: inline-block;
    border: 1rpx solid rgba(167, 139, 250, 0.2);
    font-family: $font-family-base;
  }

  .music-action {
    margin-left: $spacing-md;
    position: relative;
    z-index: 1;
  }

  .play-icon {
    font-size: 48rpx;
    width: 72rpx;
    height: 72rpx;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    border-radius: 50%;
    background: linear-gradient(135deg, #6ee7b7 0%, #a78bfa 50%, #fda4af 100%);
    color: #FFFFFF;
    box-shadow: 0 4rpx 12rpx rgba(167, 139, 250, 0.3);
    transition: all 0.2s ease;

    &:active {
      transform: scale(0.95);
    }
  }
}

/* 加载更多 */
.load-more {
  padding: $spacing-2xl 0;
  text-align: center;
}

.loading-text,
.load-more-text {
  font-size: $font-xs;
  color: $text-tertiary;
  font-family: $font-family-base;
}

.no-more {
  padding: $spacing-2xl 0;
  text-align: center;
}

.no-more-text {
  font-size: $font-xs;
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
  position: relative;

  &::before {
    content: '';
    position: absolute;
    width: 200rpx;
    height: 200rpx;
    border-radius: 50%;
    background: radial-gradient(circle, rgba(167, 243, 208, 0.2) 0%, transparent 70%);
    z-index: 0;
  }
}

.empty-icon {
  font-size: 120rpx;
  margin-bottom: $spacing-2xl;
  filter: grayscale(30%);
  position: relative;
  z-index: 1;
}

.empty-text {
  font-size: $font-sm;
  color: $text-tertiary;
  font-family: $font-family-base;
  position: relative;
  z-index: 1;
}
</style>
