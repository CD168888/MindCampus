<template>
  <view class="music-player-page">
    <!-- 自定义导航栏 -->
    <view class="custom-navbar" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="navbar-content">
        <view class="navbar-left" @tap="goBack">
          <view class="nav-icon-btn">
            <uni-icons type="back" size="20" color="#1f2937"></uni-icons>
          </view>
        </view>
        <view class="navbar-title">正在播放</view>
        <view class="navbar-right" @tap="showMoreOptions">
          <view class="nav-icon-btn">
            <uni-icons type="more-filled" size="20" color="#1f2937"></uni-icons>
          </view>
        </view>
      </view>
    </view>

    <!-- 主要内容区域 -->
    <view class="player-content">
      <!-- 唱片/专辑封面区域 -->
      <view class="vinyl-section">
        <view class="vinyl-container" :class="{ 'playing': isPlaying }">
          <!-- 光晕效果 -->
          <view class="glow-effect" :class="{ 'playing': isPlaying }"></view>

          <view class="vinyl-disc">
            <image v-if="currentMusic.coverUrl" class="album-cover" :src="getImageUrl(currentMusic.coverUrl)"
              mode="aspectFill"></image>
            <view v-else class="album-cover-placeholder">
              <uni-icons type="music-filled" size="70" color="rgba(255, 255, 255, 0.8)"></uni-icons>
            </view>
            <view class="vinyl-center">
              <view class="center-inner"></view>
            </view>
          </view>

          <view class="record-arm" :class="{ 'playing': isPlaying }">
            <view class="arm-line"></view>
            <view class="arm-head"></view>
          </view>
        </view>
      </view>

      <!-- 歌曲信息 -->
      <view class="song-info-section">
        <view class="song-title">{{ currentMusic.title || '未知歌曲' }}</view>
        <view class="song-artist">
          <text class="artist-name">{{ currentMusic.artist || '未知艺术家' }}</text>
        </view>
        <view class="song-tags" v-if="currentMusic.genre">
          <view class="tag-item">{{ currentMusic.genre }}</view>
        </view>
      </view>

      <!-- 操作按钮区域 -->
      <view class="action-buttons">
        <view class="action-btn-modern" @tap="toggleLike">
          <view class="action-icon-wrapper" :class="{ 'active': isLiked }">
            <uni-icons :type="isLiked ? 'heart-filled' : 'heart'" size="22"
              :color="isLiked ? '#ff6b6b' : '#6b7280'"></uni-icons>
          </view>
          <text class="action-text">{{ likeCount > 0 ? likeCount : '喜欢' }}</text>
        </view>
        <view class="action-btn-modern" @tap="toggleCollect">
          <view class="action-icon-wrapper" :class="{ 'active': isCollected }">
            <uni-icons :type="isCollected ? 'star-filled' : 'star'" size="22"
              :color="isCollected ? '#fbbf24' : '#6b7280'"></uni-icons>
          </view>
          <text class="action-text">收藏</text>
        </view>
        <view class="action-btn-modern" @tap="showComments">
          <view class="action-icon-wrapper">
            <uni-icons type="chat-filled" size="22" color="#6b7280"></uni-icons>
          </view>
          <text class="action-text">评论</text>
        </view>
        <view class="action-btn-modern" @tap="shareMusic">
          <view class="action-icon-wrapper">
            <uni-icons type="redo" size="22" color="#6b7280"></uni-icons>
          </view>
          <text class="action-text">分享</text>
        </view>
      </view>
    </view>

    <!-- 播放控制栏 -->
    <view class="player-controls">
      <!-- 进度条 -->
      <view class="progress-section">
        <text class="time-text">{{ formatTime(currentTime) }}</text>
        <view class="progress-bar-container" @touchstart="seekToPosition" @touchmove="seekToPosition">
          <view class="progress-bar-bg">
            <view class="progress-bar-fill" :style="{ width: progressPercent + '%' }"></view>
            <view class="progress-dot" :style="{ left: progressPercent + '%' }">
              <view class="dot-inner"></view>
            </view>
          </view>
        </view>
        <text class="time-text">{{ formatTime(totalDuration) }}</text>
      </view>

      <!-- 控制按钮 -->
      <view class="controls-row">
        <view class="control-btn-modern" @tap="toggleShuffle" :class="{ 'active': isShuffle }">
          <uni-icons type="loop" size="20" :color="isShuffle ? '#667eea' : '#6b7280'"></uni-icons>
        </view>
        <view class="control-btn-modern" @tap="playPrevious">
          <uni-icons type="left" size="20" color="#6b7280"></uni-icons>
        </view>
        <view class="play-pause-btn-modern" @tap="togglePlay" :class="{ 'playing': isPlaying }">
          <view class="play-pause-inner">
            <uni-icons :type="isPlaying ? 'videocam-filled' : 'right'" size="26" color="#ffffff"></uni-icons>
          </view>
        </view>
        <view class="control-btn-modern" @tap="playNext">
          <uni-icons type="right" size="20" color="#6b7280"></uni-icons>
        </view>
        <view class="control-btn-modern" @tap="togglePlayMode" :class="{ 'active': playMode === 'loop' }">
          <uni-icons :type="playMode === 'loop' ? 'reload' : playMode === 'single' ? 'loop' : 'shuffles'" size="20"
            :color="playMode === 'loop' ? '#667eea' : '#6b7280'"></uni-icons>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { getMusicDetail } from '@/api/music'
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
      isCollected: false,
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
      query.select('.progress-bar-container').boundingClientRect((rect) => {
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
      } else {
        this.likeCount = Math.max(0, this.likeCount - 1)
      }
    },

    toggleCollect() {
      this.isCollected = !this.isCollected
      this.$modal.showToast(this.isCollected ? '已收藏' : '取消收藏')
    },

    showComments() {
      this.$modal.showToast('评论功能开发中')
    },

    shareMusic() {
      uni.share({
        provider: 'weixin',
        scene: 'WXSceneSession',
        type: 0,
        href: '',
        title: this.currentMusic.title,
        summary: this.currentMusic.artist,
        imageUrl: this.getImageUrl(this.currentMusic.coverUrl),
        success: () => {
          this.$modal.showToast('分享成功')
        },
        fail: () => {
          this.$modal.showToast('分享失败')
        }
      })
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
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/static/scss/theme.scss';

.music-player-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #ffffff 0%, #f9fafb 100%);
  display: flex;
  flex-direction: column;
}

/* 导航栏 */
.custom-navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 999;
  background: #ffffff;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.04);
}

.navbar-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 88rpx;
  padding: 0 24rpx;
}

.navbar-left,
.navbar-right {
  width: 64rpx;
}

.nav-icon-btn {
  width: 64rpx;
  height: 64rpx;
  border-radius: 50%;
  background: $bg-gray-50;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;

  &:active {
    transform: scale(0.95);
    background: $bg-gray-100;
  }
}

.navbar-title {
  flex: 1;
  text-align: center;
  font-size: 30rpx;
  font-weight: 600;
  color: $text-primary;
}

/* 主要内容区域 */
.player-content {
  flex: 1;
  margin-top: calc(88rpx + env(safe-area-inset-top));
  padding: 24rpx 30rpx 0;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 唱片区域 */
.vinyl-section {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 16rpx 0;
  margin-bottom: 24rpx;
  position: relative;
}

.vinyl-container {
  position: relative;
  width: 480rpx;
  height: 480rpx;
  display: flex;
  justify-content: center;
  align-items: center;
}

/* 光晕效果 */
.glow-effect {
  position: absolute;
  width: 480rpx;
  height: 480rpx;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(102, 126, 234, 0.12) 0%, transparent 70%);
  opacity: 0;
  transition: opacity 0.5s ease;
  z-index: 0;

  &.playing {
    opacity: 1;
    animation: pulse 3s ease-in-out infinite;
  }
}

@keyframes pulse {

  0%,
  100% {
    transform: scale(1);
    opacity: 0.25;
  }

  50% {
    transform: scale(1.05);
    opacity: 0.4;
  }
}

.vinyl-disc {
  width: 420rpx;
  height: 420rpx;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  box-shadow: 0 12rpx 32rpx rgba(102, 126, 234, 0.2), 0 6rpx 12rpx rgba(0, 0, 0, 0.06);
  transition: all 0.5s ease;
  z-index: 1;

  &::before {
    content: '';
    position: absolute;
    width: 100%;
    height: 100%;
    border-radius: 50%;
    background:
      radial-gradient(circle at 30% 30%, rgba(255, 255, 255, 0.15) 0%, transparent 50%),
      repeating-conic-gradient(from 0deg,
        rgba(0, 0, 0, 0.08) 0deg 1deg,
        transparent 1deg 2deg);
    opacity: 0.5;
  }

  &.playing {
    animation: rotate 20s linear infinite;
  }
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }

  to {
    transform: rotate(360deg);
  }
}

.album-cover {
  width: 290rpx;
  height: 290rpx;
  border-radius: 50%;
  position: relative;
  z-index: 1;
  box-shadow: inset 0 0 30rpx rgba(0, 0, 0, 0.15);
}

.album-cover-placeholder {
  width: 290rpx;
  height: 290rpx;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(20rpx);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  z-index: 1;
  box-shadow: inset 0 0 30rpx rgba(0, 0, 0, 0.1);
}

.vinyl-center {
  position: absolute;
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  background: linear-gradient(135deg, #1f2937 0%, #111827 100%);
  z-index: 2;
  box-shadow:
    inset 0 3rpx 6rpx rgba(0, 0, 0, 0.4),
    0 3rpx 8rpx rgba(0, 0, 0, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
}

.center-inner {
  width: 16rpx;
  height: 16rpx;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.3);
}

/* 唱臂 */
.record-arm {
  position: absolute;
  top: -16rpx;
  right: 48rpx;
  width: 200rpx;
  height: 200rpx;
  transform-origin: top right;
  transition: transform 0.6s cubic-bezier(0.34, 1.56, 0.64, 1);
  z-index: 10;

  &.playing {
    transform: rotate(-26deg);
  }
}

.arm-line {
  width: 5rpx;
  height: 160rpx;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.9) 0%, rgba(255, 255, 255, 0.6) 100%);
  border-radius: 2.5rpx;
  position: absolute;
  top: 0;
  right: 0;
  box-shadow:
    0 2rpx 6rpx rgba(0, 0, 0, 0.2),
    inset 0 0 3rpx rgba(255, 255, 255, 0.5);
}

.arm-head {
  width: 20rpx;
  height: 20rpx;
  border-radius: 50%;
  background: linear-gradient(135deg, #cbd5e1 0%, #94a3b8 100%);
  position: absolute;
  top: 152rpx;
  right: -8rpx;
  box-shadow:
    0 3rpx 10rpx rgba(0, 0, 0, 0.3),
    inset 0 2rpx 3rpx rgba(255, 255, 255, 0.3);
}

/* 歌曲信息 */
.song-info-section {
  text-align: center;
  margin-bottom: 32rpx;
}

.song-title {
  font-size: 44rpx;
  font-weight: 700;
  color: $text-primary;
  margin-bottom: 10rpx;
  line-height: 1.3;
  letter-spacing: -0.5rpx;
}

.song-artist {
  font-size: 24rpx;
  color: $text-secondary;
  margin-bottom: 14rpx;
  font-weight: 400;
}

.artist-name {
  color: $text-secondary;
}

.song-tags {
  display: flex;
  justify-content: center;
  gap: 10rpx;
}

.tag-item {
  font-size: 18rpx;
  color: $text-tertiary;
  background: $bg-gray-50;
  padding: 5rpx 14rpx;
  border-radius: $radius-full;
  font-weight: 400;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  justify-content: space-around;
  align-items: center;
  padding: 0 16rpx;
  margin-bottom: 32rpx;
}

.action-btn-modern {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6rpx;
  flex: 1;
}

.action-icon-wrapper {
  width: 68rpx;
  height: 68rpx;
  border-radius: $radius-full;
  background: $bg-gray-50;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;

  &.active {
    background: rgba(102, 126, 234, 0.1);
    transform: scale(1.05);
  }

  &:active {
    transform: scale(0.95);
  }
}

.action-text {
  font-size: 18rpx;
  color: $text-tertiary;
  font-weight: 400;
}

/* 播放控制栏 */
.player-controls {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #ffffff;
  border-top: 1rpx solid $border-light;
  padding: 20rpx 30rpx;
  padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
  box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.03);
  z-index: 999;
}

/* 进度条 */
.progress-section {
  display: flex;
  align-items: center;
  gap: 14rpx;
  margin-bottom: 20rpx;
}

.time-text {
  font-size: 20rpx;
  color: $text-secondary;
  min-width: 56rpx;
  text-align: center;
  font-weight: 500;
  font-variant-numeric: tabular-nums;
}

.progress-bar-container {
  flex: 1;
  height: 56rpx;
  display: flex;
  align-items: center;
  padding: 0 8rpx;
}

.progress-bar-bg {
  width: 100%;
  height: 4rpx;
  background: $bg-gray-100;
  border-radius: 2rpx;
  position: relative;
}

.progress-bar-fill {
  height: 100%;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
  border-radius: 2rpx;
  transition: width 0.1s linear;
}

.progress-dot {
  position: absolute;
  top: 50%;
  width: 20rpx;
  height: 20rpx;
  transform: translate(-50%, -50%);
  z-index: 2;
}

.dot-inner {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background: #fff;
  box-shadow:
    0 2rpx 6rpx rgba(102, 126, 234, 0.4),
    0 0 0 3rpx rgba(102, 126, 234, 0.2);
}

/* 控制按钮行 */
.controls-row {
  display: flex;
  align-items: center;
  justify-content: space-around;
  padding: 0 16rpx;
}

.control-btn-modern {
  width: 68rpx;
  height: 68rpx;
  border-radius: $radius-full;
  background: $bg-gray-50;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;

  &.active {
    background: rgba(102, 126, 234, 0.1);
  }

  &:active {
    transform: scale(0.9);
  }
}

/* 播放/暂停按钮 */
.play-pause-btn-modern {
  width: 104rpx;
  height: 104rpx;
  border-radius: $radius-full;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 6rpx 18rpx rgba(102, 126, 234, 0.28);
  transition: all 0.3s ease;
  position: relative;

  &.playing {
    box-shadow: 0 6rpx 18rpx rgba(102, 126, 234, 0.32);
  }

  &:active {
    transform: scale(0.95);
  }
}

.play-pause-inner {
  width: 100%;
  height: 100%;
  border-radius: $radius-full;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
