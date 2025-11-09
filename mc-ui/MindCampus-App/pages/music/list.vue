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
    <scroll-view 
      class="music-scroll" 
      scroll-y 
      @scrolltolower="loadMore"
      :lower-threshold="100"
      :enable-back-to-top="true">
      <view class="music-list">
        <view 
          class="music-item" 
          v-for="(item, index) in musicList" 
          :key="item.musicId"
          @tap="playMusic(item)">
          <image 
            v-if="item.coverUrl" 
            class="music-cover" 
            :src="getImageUrl(item.coverUrl)" 
            mode="aspectFill"
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
import { listMusic } from '@/api/music'
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

      // 添加搜索条件
      if (this.searchKeyword) {
        params.title = this.searchKeyword
        params.artist = this.searchKeyword
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
.music-list-page {
  min-height: 100vh;
  background: #f5f5f5;
}

/* 自定义导航栏 */
.custom-navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 999;
  background: #fff;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);
}

.navbar-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 88rpx;
  padding: 0 30rpx;
}

.navbar-left {
  width: 60rpx;
}

.back-icon {
  font-size: 36rpx;
  color: #333;
}

.navbar-title {
  flex: 1;
  text-align: center;
  font-size: 36rpx;
  font-weight: 600;
  color: #333;
}

.navbar-right {
  width: 60rpx;
}

/* 搜索栏 */
.search-section {
  padding: 20rpx 30rpx;
  background: #fff;
  margin-top: calc(88rpx + env(safe-area-inset-top));
}

.search-box {
  display: flex;
  align-items: center;
  background: #f5f5f5;
  border-radius: 50rpx;
  padding: 20rpx 30rpx;
}

.search-input {
  flex: 1;
  font-size: 28rpx;
  color: #333;
}

.search-icon {
  font-size: 32rpx;
  margin-left: 20rpx;
  color: #999;
}

/* 滚动区域 */
.music-scroll {
  height: calc(100vh - 88rpx - 100rpx - env(safe-area-inset-top));
  padding: 20rpx 30rpx;
}

/* 音乐列表 */
.music-list {
  .music-item {
    display: flex;
    align-items: center;
    background: #fff;
    border-radius: 20rpx;
    padding: 30rpx;
    margin-bottom: 20rpx;
    box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
  }

  .music-cover {
    width: 120rpx;
    height: 120rpx;
    border-radius: 12rpx;
    margin-right: 24rpx;
    background: #f0f0f0;
  }

  .music-cover-placeholder {
    width: 120rpx;
    height: 120rpx;
    border-radius: 12rpx;
    margin-right: 24rpx;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 60rpx;
  }

  .music-info {
    flex: 1;
    min-width: 0;
  }

  .music-title {
    font-size: 32rpx;
    font-weight: 600;
    color: #333;
    margin-bottom: 8rpx;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .music-artist {
    font-size: 24rpx;
    color: #999;
    margin-bottom: 8rpx;
  }

  .music-genre {
    font-size: 22rpx;
    color: #667eea;
    background: #f0f4ff;
    padding: 4rpx 12rpx;
    border-radius: 8rpx;
    display: inline-block;
  }

  .music-action {
    margin-left: 20rpx;
  }

  .play-icon {
    font-size: 40rpx;
    color: #667eea;
  }
}

/* 加载更多 */
.load-more {
  padding: 40rpx 0;
  text-align: center;
}

.loading-text,
.load-more-text {
  font-size: 24rpx;
  color: #999;
}

.no-more {
  padding: 40rpx 0;
  text-align: center;
}

.no-more-text {
  font-size: 24rpx;
  color: #999;
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
  margin-bottom: 40rpx;
}

.empty-text {
  font-size: 28rpx;
  color: #999;
}
</style>

