<template>
  <view class="music-player-page">
    <!-- 自定义导航栏 -->
    <view class="custom-navbar" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="navbar-content">
        <view class="navbar-left" @tap="goBack">
          <view class="nav-icon-btn">
            <uni-icons type="back" size="24" color="#333333"></uni-icons>
          </view>
        </view>
        <view class="navbar-title">Now Playing</view>
        <view class="navbar-right" @tap="showMoreOptions">
          <view class="nav-icon-btn">
            <uni-icons type="more-filled" size="24" color="#333333"></uni-icons>
          </view>
        </view>
      </view>
    </view>

    <!-- 主要内容区域 -->
    <view class="player-content">
      <!-- 专辑封面区域 -->
      <view class="album-section">
        <view class="album-cover-wrapper">
          <image v-if="currentMusic.coverUrl" class="album-cover" :src="getImageUrl(currentMusic.coverUrl)"
            mode="aspectFill"></image>
          <view v-else class="album-cover-placeholder">
            <uni-icons type="music-filled" size="80" color="#CCCCCC"></uni-icons>
          </view>
        </view>
      </view>

      <!-- 歌曲信息 -->
      <view class="song-info-section">
        <view class="song-title">{{ currentMusic.title || '未知歌曲' }}</view>
        <view class="song-artist">{{ currentMusic.artist || '未知艺术家' }}</view>
      </view>

      <!-- 进度条区域 -->
      <view class="progress-section">
        <view class="progress-wrapper">
          <text class="time-text time-start">{{ formatTime(currentTime) }}</text>
          <view class="progress-arc-container" @touchstart="seekToPosition" @touchmove="seekToPosition">
            <svg class="progress-svg" viewBox="0 0 300 120" preserveAspectRatio="none">
              <!-- 渐变定义 -->
              <defs>
                <linearGradient id="progressGradient" x1="0%" y1="0%" x2="100%" y2="0%">
                  <stop offset="0%" style="stop-color:#6ee7b7;stop-opacity:0.8" />
                  <stop offset="50%" style="stop-color:#a78bfa;stop-opacity:0.8" />
                  <stop offset="100%" style="stop-color:#fda4af;stop-opacity:0.8" />
                </linearGradient>
              </defs>
              <!-- 背景弧线 -->
              <path d="M 0,10 Q 150,110 300,10" fill="none" stroke="rgba(0, 0, 0, 0.05)" stroke-width="6"
                stroke-linecap="round" />
              <!-- 进度弧线 -->
              <path v-if="progressPercent > 0" :d="getProgressPath(progressPercent)" fill="none"
                stroke="url(#progressGradient)" stroke-width="6" stroke-linecap="round" />
            </svg>
            <!-- 进度点 -->
            <view class="progress-dot-arc" :style="{
              left: progressPercent + '%',
              bottom: getArcHeight(progressPercent) + 'rpx'
            }"></view>
          </view>
          <text class="time-text time-end">{{ formatTime(totalDuration) }}</text>
        </view>
      </view>

      <!-- 主控制按钮 -->
      <view class="main-controls">
        <view class="control-btn" @tap="playPrevious">
          <uni-icons type="back" size="28" color="#666666"></uni-icons>
        </view>
        <view class="play-pause-btn" @tap="togglePlay" :class="{ 'playing': isPlaying }">
          <uni-icons :type="isPlaying ? 'videocam-filled' : 'right'" size="32" color="#FFFFFF"></uni-icons>
        </view>
        <view class="control-btn" @tap="playNext">
          <uni-icons type="forward" size="28" color="#666666"></uni-icons>
        </view>
      </view>

      <!-- 底部操作按钮 -->
      <view class="bottom-actions">
        <view class="action-btn" @tap="togglePlayMode" :class="{ 'active': playMode !== 'loop' }">
          <uni-icons :type="playMode === 'single' ? 'loop' : 'reload'" size="24"
            :color="playMode !== 'loop' ? '#1677FF' : '#999999'"></uni-icons>
        </view>
        <view class="action-btn" @tap="toggleLike" :class="{ 'active': isLiked }">
          <uni-icons :type="isLiked ? 'heart-filled' : 'heart'" size="24"
            :color="isLiked ? '#FF3A3A' : '#999999'"></uni-icons>
        </view>
        <view class="action-btn" @tap="toggleShuffle" :class="{ 'active': isShuffle }">
          <uni-icons type="shuffles" size="24" :color="isShuffle ? '#1677FF' : '#999999'"></uni-icons>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import {getMusicDetail} from '@/api/music'
import config from '@/config'

export default {
  data() {
    return {
      statusBarHeight: 0,
      musicId: null,
      currentMusic: {},
      musicList: [],
      currentIndex: 0,

      isPlaying: false,
      currentTime: 0,
      totalDuration: 0,
      progressPercent: 0,

      audioContext: null,

      playMode: 'loop',
      isShuffle: false,

      isLiked: false,
      likeCount: 0
    }
  },
  onLoad(options) {
    const systemInfo = uni.getSystemInfoSync()
    this.statusBarHeight = systemInfo.statusBarHeight || 0

    if (options.musicId) {
      this.musicId = parseInt(options.musicId)
      this.loadMusicDetail()
    }

    if (options.musicList) {
      try {
        this.musicList = JSON.parse(decodeURIComponent(options.musicList))
        this.currentIndex = this.musicList.findIndex(item => item.musicId === this.musicId)
        if (this.currentIndex === -1) this.currentIndex = 0
      } catch (e) {
        console.error('解析播放列表失败:', e)
      }
    }

    this.initAudioPlayer()
  },
  onUnload() {
    if (this.audioContext) {
      this.audioContext.destroy()
    }
  },
  methods: {
    goBack() {
      uni.navigateBack()
    },

    showMoreOptions() {
      uni.showActionSheet({
        itemList: ['下载', '设为铃声', '查看专辑', '举报'],
        success: (res) => {
          this.$modal.showToast('功能开发中')
        }
      })
    },

    loadMusicDetail() {
      if (!this.musicId) return

      getMusicDetail(this.musicId).then(res => {
        if (res.code === 200 && res.data) {
          this.currentMusic = res.data
          this.totalDuration = res.data.duration || 0

          if (res.data.mp3Url) {
            const audioUrl = this.getAudioUrl(res.data.mp3Url)
            this.audioContext.src = audioUrl
          }
        }
      }).catch(err => {
        console.error('加载音乐详情失败:', err)
        this.$modal.showToast('加载失败')
      })
    },

    initAudioPlayer() {
      this.audioContext = uni.createInnerAudioContext()

      this.audioContext.onPlay(() => {
        this.isPlaying = true
      })

      this.audioContext.onPause(() => {
        this.isPlaying = false
      })

      this.audioContext.onEnded(() => {
        this.isPlaying = false
        this.currentTime = 0
        this.progressPercent = 0
        this.playNext()
      })

      this.audioContext.onTimeUpdate(() => {
        this.currentTime = Math.floor(this.audioContext.currentTime)
        if (this.totalDuration > 0) {
          this.progressPercent = (this.currentTime / this.totalDuration) * 100
        }
      })

      this.audioContext.onError((err) => {
        console.error('音频播放错误:', err)
        this.$modal.showToast('播放失败')
        this.isPlaying = false
      })
    },

    togglePlay() {
      if (!this.currentMusic.mp3Url) {
        this.$modal.showToast('音频文件不存在')
        return
      }

      if (!this.audioContext.src) {
        const audioUrl = this.getAudioUrl(this.currentMusic.mp3Url)
        this.audioContext.src = audioUrl
      }

      if (this.isPlaying) {
        this.audioContext.pause()
      } else {
        this.audioContext.play()
      }
    },

    playPrevious() {
      if (this.musicList.length === 0) {
        this.$modal.showToast('没有上一首')
        return
      }

      if (this.isShuffle) {
        this.currentIndex = Math.floor(Math.random() * this.musicList.length)
      } else {
        this.currentIndex--
        if (this.currentIndex < 0) {
          this.currentIndex = this.musicList.length - 1
        }
      }

      this.switchMusic(this.musicList[this.currentIndex].musicId)
    },

    playNext() {
      if (this.musicList.length === 0) {
        this.$modal.showToast('没有下一首')
        return
      }

      if (this.isShuffle) {
        this.currentIndex = Math.floor(Math.random() * this.musicList.length)
      } else {
        this.currentIndex++
        if (this.currentIndex >= this.musicList.length) {
          if (this.playMode === 'loop') {
            this.currentIndex = 0
          } else {
            this.$modal.showToast('已经是最后一首')
            return
          }
        }
      }

      this.switchMusic(this.musicList[this.currentIndex].musicId)
    },

    switchMusic(musicId) {
      this.musicId = musicId
      this.currentTime = 0
      this.progressPercent = 0
      if (this.audioContext) {
        this.audioContext.stop()
      }
      this.loadMusicDetail()
    },

    toggleShuffle() {
      this.isShuffle = !this.isShuffle
      this.$modal.showToast(this.isShuffle ? '随机播放' : '顺序播放')
    },

    togglePlayMode() {
      const modes = ['loop', 'single', 'random']
      const currentIndex = modes.indexOf(this.playMode)
      this.playMode = modes[(currentIndex + 1) % modes.length]

      const modeText = {
        'loop': '列表循环',
        'single': '单曲循环',
        'random': '随机播放'
      }
      this.$modal.showToast(modeText[this.playMode])
    },

    seekToPosition(e) {
      if (!this.totalDuration || !this.audioContext) return

      const touch = e.touches && e.touches[0] ? e.touches[0] : null
      if (!touch) return

      const query = uni.createSelectorQuery().in(this)
      query.select('.progress-arc-container').boundingClientRect((rect) => {
        if (!rect) return

        const x = touch.clientX - rect.left
        const percent = Math.max(0, Math.min(1, x / rect.width))
        const time = Math.floor(percent * this.totalDuration)

        this.audioContext.seek(time)
        this.currentTime = time
        this.progressPercent = percent * 100
      }).exec()
    },

    toggleLike() {
      this.isLiked = !this.isLiked
      if (this.isLiked) {
        this.likeCount++
        this.$modal.showToast('已喜欢')
      } else {
        this.likeCount = Math.max(0, this.likeCount - 1)
        this.$modal.showToast('取消喜欢')
      }
    },

    formatTime(seconds) {
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
    },

    getAudioUrl(url) {
      if (!url) return ''
      if (url.startsWith('http')) return url
      const baseUrl = config.baseUrl || 'http://localhost:8080'
      return url.startsWith('/') ? baseUrl + url : baseUrl + '/' + url
    },

    getArcHeight(percent) {
      // 计算弧线上点的高度（基于二次贝塞尔曲线）
      // 新弧线：M 0,10 Q 150,110 300,10
      const t = percent / 100
      const y = (1 - t) * (1 - t) * 10 + 2 * (1 - t) * t * 110 + t * t * 10
      // SVG viewBox 是 120 高度，转换为 rpx（140rpx 容器高度）
      return Math.round((120 - y) * 1.1) // 转换为 rpx 单位
    },

    getProgressPath(percent) {
      // 计算进度弧线的路径
      // 新弧线：M 0,10 Q 150,110 300,10
      const endX = (percent / 100) * 300
      const t = percent / 100
      const endY = (1 - t) * (1 - t) * 10 + 2 * (1 - t) * t * 110 + t * t * 10

      // 使用二次贝塞尔曲线绘制进度
      if (percent < 50) {
        // 前半段
        const controlX = 150 * (percent / 50)
        const controlY = 10 + (100 * (percent / 50))
        return `M 0,10 Q ${controlX},${controlY} ${endX},${endY}`
      } else {
        // 后半段
        return `M 0,10 Q 150,110 ${endX},${endY}`
      }
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/static/scss/theme.scss';

.music-player-page {
  min-height: 100vh;
  position: relative;
  display: flex;
  flex-direction: column;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background:
      linear-gradient(to bottom, transparent -30px, #FFFFFF 300px),
      linear-gradient(135deg, rgba(167, 243, 208, 0.15) 0%, rgba(196, 181, 253, 0.15) 50%, rgba(254, 205, 211, 0.15) 100%);
    z-index: 0;
  }

  >* {
    position: relative;
    z-index: 1;
  }
}

/* 导航栏 */
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
  padding: 0 32rpx;
}

.navbar-left,
.navbar-right {
  width: 80rpx;
}

.nav-icon-btn {
  width: 64rpx;
  height: 64rpx;
  border-radius: 50%;
  background: transparent;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;

  &:active {
    transform: scale(0.9);
    opacity: 0.6;
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

/* 主要内容区域 */
.player-content {
  flex: 1;
  margin-top: calc(88rpx + env(safe-area-inset-top));
  padding: 48rpx 48rpx 64rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

/* 专辑封面区域 */
.album-section {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 64rpx;
  padding: 0 32rpx;
}

.album-cover-wrapper {
  width: 560rpx;
  height: 560rpx;
  border-radius: $radius-2xl;
  overflow: hidden;
  box-shadow:
    0 16rpx 48rpx rgba(167, 243, 208, 0.12),
    0 8rpx 24rpx rgba(196, 181, 253, 0.12),
    0 4rpx 12rpx rgba(254, 205, 211, 0.12);
  background: $bg-white;
}

.album-cover {
  width: 100%;
  height: 100%;
  display: block;
}

.album-cover-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: $bg-gray-100;
}

/* 歌曲信息 */
.song-info-section {
  text-align: center;
  margin-bottom: 48rpx;
  width: 100%;
  max-width: 560rpx;
}

.song-title {
  font-size: $font-xl;
  font-weight: $font-bold;
  color: $text-primary;
  margin-bottom: 16rpx;
  line-height: $line-height-tight;
  font-family: $font-family-base;
}

.song-artist {
  font-size: $font-sm;
  color: $text-tertiary;
  font-weight: $font-normal;
  font-family: $font-family-base;
}

/* 进度条 */
.progress-section {
  margin-bottom: 48rpx;
  width: 100%;
  max-width: 600rpx;
}

.progress-wrapper {
  display: flex;
  align-items: flex-end;
  gap: 16rpx;
  position: relative;
}

.time-text {
  font-size: $font-xs;
  color: $text-tertiary;
  min-width: 56rpx;
  text-align: center;
  font-weight: $font-medium;
  font-variant-numeric: tabular-nums;
  font-family: $font-family-english;

  &.time-start {
    text-align: left;
  }

  &.time-end {
    text-align: right;
  }
}

.progress-arc-container {
  flex: 1;
  height: 140rpx;
  position: relative;
  display: flex;
  align-items: flex-end;
  padding-bottom: 10rpx;
}

.progress-svg {
  width: 100%;
  height: 100%;
  display: block;
}

.progress-dot-arc {
  position: absolute;
  width: 32rpx;
  height: 32rpx;
  background: linear-gradient(135deg, #6ee7b7 0%, #a78bfa 50%, #fda4af 100%);
  border: 5rpx solid #FFFFFF;
  border-radius: 50%;
  transform: translateX(-50%);
  box-shadow: 0 2rpx 12rpx rgba(167, 139, 250, 0.3);
  z-index: 3;
  transition: bottom 0.1s ease;
}

/* 主控制按钮 */
.main-controls {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 80rpx;
  margin-bottom: 64rpx;
}

.control-btn {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(10rpx);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;

  &:active {
    transform: scale(0.9);
    background: rgba(255, 255, 255, 0.8);
  }
}

/* 播放/暂停按钮 */
.play-pause-btn {
  width: 128rpx;
  height: 128rpx;
  border-radius: 50%;
  background: linear-gradient(135deg, #6ee7b7 0%, #a78bfa 50%, #fda4af 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8rpx 24rpx rgba(167, 139, 250, 0.3);
  transition: all 0.3s ease;

  &.playing {
    box-shadow: 0 8rpx 32rpx rgba(167, 139, 250, 0.4);
  }

  &:active {
    transform: scale(0.95);
  }
}

/* 底部操作按钮 */
.bottom-actions {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 96rpx;
}

.action-btn {
  width: 72rpx;
  height: 72rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;

  &:active {
    transform: scale(0.9);
    opacity: 0.6;
  }
}
</style>
