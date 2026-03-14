<template>
  <view class="music-player-page">
    <view class="ambient-background"></view>

    <view class="glass-header" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="navbar-content">
        <view class="navbar-left" @tap="goBack">
          <view class="nav-icon-glass">
            <uni-icons type="bottom" size="20" color="#1D1D1F" style="transform: rotate(90deg);"></uni-icons>
          </view>
        </view>
        <view class="navbar-title">正在治愈</view>
        <view class="navbar-right" @tap="showMoreOptions">
          <view class="nav-icon-glass">
            <uni-icons type="more-filled" size="24" color="#1D1D1F"></uni-icons>
          </view>
        </view>
      </view>
    </view>

    <view class="player-content">
      <view class="album-section">
        <view class="album-cover-glass">
          <image v-if="currentMusic.coverUrl" class="album-cover" :src="getImageUrl(currentMusic.coverUrl)" mode="aspectFill"></image>
          <view v-else class="album-cover-placeholder">
            <uni-icons type="headphones" size="80" color="#FFFFFF"></uni-icons>
          </view>
          <view class="album-highlight"></view>
        </view>
      </view>

      <view class="song-info-section">
        <text class="song-title">{{ currentMusic.title || '未知频率' }}</text>
        <text class="song-artist">{{ currentMusic.artist || '愈音工坊' }}</text>
      </view>

      <view class="progress-section">
        <view class="progress-wrapper">
          <text class="time-text time-start">{{ formatTime(currentTime) }}</text>
          
          <view class="progress-arc-container" @touchstart="seekToPosition" @touchmove="seekToPosition">
            <svg class="progress-svg" viewBox="0 0 300 120" preserveAspectRatio="none">
              <defs>
                <linearGradient id="smileGradient" x1="0%" y1="0%" x2="100%" y2="0%">
                  <stop offset="0%" style="stop-color:#A7E6DA;stop-opacity:1" />
                  <stop offset="100%" style="stop-color:#2CB5A0;stop-opacity:1" />
                </linearGradient>
              </defs>
              
              <path d="M 0,15 Q 150,150 300,15" fill="none" stroke="rgba(44, 181, 160, 0.15)" stroke-width="8" stroke-linecap="round" />
              
              <path v-if="progressPercent > 0" :d="getProgressPath(progressPercent)" fill="none" stroke="url(#smileGradient)" stroke-width="8" stroke-linecap="round" />
            </svg>
            
            <view class="progress-dot-arc" :style="{
              left: progressPercent + '%',
              top: getArcTopPercent(progressPercent) + '%'
            }"></view>
          </view>

          <text class="time-text time-end">{{ formatTime(totalDuration) }}</text>
        </view>
      </view>

      <view class="main-controls">
        <view class="control-btn-glass" @tap="playPrevious">
          <uni-icons type="back" size="28" color="#1D1D1F"></uni-icons>
        </view>
        
        <view class="play-pause-btn" @tap="togglePlay" :class="{ 'playing': isPlaying }">
          <view class="play-btn-inner">
            <view v-if="isPlaying" class="pause-icon">
              <view class="pause-bar"></view>
              <view class="pause-bar"></view>
            </view>
            <view v-else class="play-triangle"></view>
          </view>
        </view>

        <view class="control-btn-glass" @tap="playNext">
          <uni-icons type="forward" size="28" color="#1D1D1F"></uni-icons>
        </view>
      </view>

      <view class="bottom-actions glass-pill-bar">
        <view class="action-btn" @tap="togglePlayMode">
          <uni-icons :type="playMode === 'single' ? 'loop' : 'reload'" size="22" :color="playMode !== 'loop' ? '#2CB5A0' : '#86868B'"></uni-icons>
        </view>
        
        <view class="action-btn" @tap="toggleLike" :class="{ 'heart-active': isLiked }">
          <uni-icons :type="isLiked ? 'heart-filled' : 'heart'" size="24" :color="isLiked ? '#2CB5A0' : '#86868B'"></uni-icons>
        </view>

        <view class="action-btn" @tap="downloadMusic">
          <uni-icons type="download" size="24" color="#86868B"></uni-icons>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import {getLikeStatus, getMusicDetail, likeMusic} from '@/api/music'
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
    goBack() { uni.navigateBack() },

    showMoreOptions() {
      uni.showActionSheet({
        itemList: ['设为铃声', '查看专辑', '举报'],
        success: (res) => { this.$modal.showToast('功能开发中') }
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

          // 加载点赞状态
          this.loadLikeStatus()
        }
      }).catch(err => {
        console.error('加载详情失败:', err)
        this.$modal.showToast('加载失败')
      })
    },

    initAudioPlayer() {
      this.audioContext = uni.createInnerAudioContext()
      this.audioContext.onPlay(() => { this.isPlaying = true })
      this.audioContext.onPause(() => { this.isPlaying = false })
      
      // 修复核心：音频播放结束后的处理逻辑
      this.audioContext.onEnded(() => {
        this.isPlaying = false
        this.currentTime = 0
        this.progressPercent = 0
        
        // 增加对“单曲循环”模式的判断
        if (this.playMode === 'single') {
          this.audioContext.play()
        } else {
          this.playNext()
        }
      })
      
      this.audioContext.onTimeUpdate(() => {
        this.currentTime = Math.floor(this.audioContext.currentTime)
        if (this.totalDuration > 0) {
          this.progressPercent = (this.currentTime / this.totalDuration) * 100
        }
      })
      this.audioContext.onError((err) => {
        console.error('播放错误:', err)
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
        this.audioContext.src = this.getAudioUrl(this.currentMusic.mp3Url)
      }
      if (this.isPlaying) {
        this.audioContext.pause()
      } else {
        this.audioContext.play()
      }
    },

    playPrevious() {
      if (this.musicList.length === 0) return
      if (this.playMode === 'random' || this.isShuffle) {
        this.currentIndex = Math.floor(Math.random() * this.musicList.length)
      } else {
        this.currentIndex = this.currentIndex - 1 < 0 ? this.musicList.length - 1 : this.currentIndex - 1
      }
      this.switchMusic(this.musicList[this.currentIndex].musicId)
    },

    playNext() {
      if (this.musicList.length === 0) return
      if (this.playMode === 'random' || this.isShuffle) {
        this.currentIndex = Math.floor(Math.random() * this.musicList.length)
      } else {
        this.currentIndex++
        if (this.currentIndex >= this.musicList.length) {
          if (this.playMode === 'loop') this.currentIndex = 0
          else return this.$modal.showToast('已经是最后一首')
        }
      }
      this.switchMusic(this.musicList[this.currentIndex].musicId)
    },

    switchMusic(musicId) {
      this.musicId = musicId
      this.currentTime = 0
      this.progressPercent = 0
      if (this.audioContext) this.audioContext.stop()
      this.loadMusicDetail()
      this.loadLikeStatus()
    },

    togglePlayMode() {
      const modes = ['loop', 'single', 'random']
      const currentIndex = modes.indexOf(this.playMode)
      this.playMode = modes[(currentIndex + 1) % modes.length]
      const modeText = { 'loop': '列表循环', 'single': '单曲循环', 'random': '随机播放' }
      this.$modal.showToast(modeText[this.playMode])
    },

    toggleLike() {
      if (!this.musicId) return

      likeMusic(this.musicId).then(res => {
        if (res.code === 200) {
          this.isLiked = res.data.liked
          this.likeCount = res.data.likeCount
          this.$modal.showToast(this.isLiked ? '已喜欢' : '取消喜欢')
        } else {
          this.$modal.showToast(res.msg || '操作失败')
        }
      }).catch(err => {
        console.error('点赞失败:', err)
        this.$modal.showToast('操作失败')
      })
    },

    loadLikeStatus() {
      if (!this.musicId) return

      getLikeStatus(this.musicId).then(res => {
        if (res.code === 200) {
          this.isLiked = res.data.liked
          this.likeCount = res.data.likeCount
        }
      }).catch(err => {
        console.error('获取点赞状态失败:', err)
      })
    },

    downloadMusic() {
      if (!this.currentMusic || !this.currentMusic.mp3Url) {
        return this.$modal.showToast('资源地址为空');
      }
      
      uni.showLoading({ title: '开始下载...', mask: true });
      const downloadUrl = this.getAudioUrl(this.currentMusic.mp3Url);

      uni.downloadFile({
        url: downloadUrl,
        success: (res) => {
          if (res.statusCode === 200) {
            uni.saveFile({
              tempFilePath: res.tempFilePath,
              success: (saveRes) => {
                uni.hideLoading();
                uni.showToast({ title: '已成功下载至本地', icon: 'success' });
                console.log('保存路径:', saveRes.savedFilePath);
              },
              fail: () => {
                uni.hideLoading();
                uni.showToast({ title: '文件保存失败', icon: 'none' });
              }
            });
          } else {
            uni.hideLoading();
            uni.showToast({ title: '网络请求失败', icon: 'none' });
          }
        },
        fail: (err) => {
          uni.hideLoading();
          uni.showToast({ title: '下载失败', icon: 'none' });
          console.error('下载异常:', err);
        }
      });
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

    formatTime(seconds) {
      if (!seconds) return '0:00'
      const mins = Math.floor(seconds / 60)
      const secs = seconds % 60
      return `${mins}:${secs.toString().padStart(2, '0')}`
    },

    getImageUrl(url) {
      if (!url) return ''
      if (url.startsWith('http')) return url
      return config.baseUrl + (url.startsWith('/') ? url : '/' + url)
    },

    getAudioUrl(url) {
      if (!url) return ''
      if (url.startsWith('http')) return url
      return config.baseUrl + (url.startsWith('/') ? url : '/' + url)
    },

    /* --- 核心数学：完美的二次贝塞尔曲线计算 --- */
    getArcTopPercent(percent) {
      const t = Math.max(0, Math.min(1, percent / 100));
      // 锚点: P0(0,15), P1(150,150), P2(300,15)
      const y = (1 - t) * (1 - t) * 15 + 2 * (1 - t) * t * 150 + t * t * 15;
      return (y / 120) * 100; // viewBox 恢复为 120
    },

    getProgressPath(percent) {
      if (percent <= 0) return '';
      const t = Math.max(0, Math.min(1, percent / 100));
      
      const p0 = {x: 0, y: 15};
      const p1 = {x: 150, y: 150}; 
      const p2 = {x: 300, y: 15};

      const q1x = p0.x + t * (p1.x - p0.x);
      const q1y = p0.y + t * (p1.y - p0.y);

      const q2x = (1 - t) * (1 - t) * p0.x + 2 * (1 - t) * t * p1.x + t * t * p2.x;
      const q2y = (1 - t) * (1 - t) * p0.y + 2 * (1 - t) * t * p1.y + t * t * p2.y;

      return `M ${p0.x},${p0.y} Q ${q1x},${q1y} ${q2x},${q2y}`;
    }
  }
}
</script>

<style lang="scss" scoped>
$ios-text-primary: #1D1D1F;
$ios-text-secondary: #86868B;
$theme-cyan: #2CB5A0;

.music-player-page {
  min-height: 100vh;
  position: relative;
  display: flex;
  flex-direction: column;
  background-color: #F5F5F7;
  font-family: -apple-system, BlinkMacSystemFont, "SF Pro Text", "Helvetica Neue", Arial, sans-serif;
}

.ambient-background {
  position: absolute;
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

.glass-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  background: rgba(245, 245, 247, 0.4);
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
.navbar-right { justify-content: flex-end; }

.nav-icon-glass {
  width: 64rpx;
  height: 64rpx;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.04);
  transition: transform 0.2s ease;
  &:active { transform: scale(0.9); opacity: 0.8; }
}

.navbar-title {
  font-size: 28rpx;
  font-weight: 600;
  color: $ios-text-primary;
  letter-spacing: 2rpx;
}

.player-content {
  flex: 1;
  z-index: 1;
  margin-top: calc(88rpx + env(safe-area-inset-top));
  padding: 60rpx 40rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
}

/* 修复了之前的错别字，现为正确属性 */
.album-section {
  width: 100%;
  display: flex;
  justify-content: center;
  margin-bottom: 80rpx;
  perspective: 1000px;
}

.album-cover-glass {
  width: 580rpx;
  height: 580rpx;
  border-radius: 48rpx; 
  position: relative;
  background: rgba(255, 255, 255, 0.4);
  box-shadow: 
    0 30px 60px rgba(0, 0, 0, 0.12),
    0 10px 20px rgba(0, 0, 0, 0.05);
  overflow: hidden;
  transform: translateZ(0); 
}

.album-cover {
  width: 100%;
  height: 100%;
  display: block;
}

.album-cover-placeholder {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #A8BEEA 0%, #7C98D6 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.album-highlight {
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  border-radius: 48rpx;
  box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.4);
  pointer-events: none;
}

.song-info-section {
  text-align: center;
  margin-bottom: 60rpx;
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.song-title {
  font-size: 40rpx;
  font-weight: 700;
  color: $ios-text-primary;
  margin-bottom: 16rpx;
  letter-spacing: -0.5rpx;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.song-artist {
  font-size: 26rpx;
  color: $theme-cyan; 
  font-weight: 600;
  background: rgba(44, 181, 160, 0.1);
  padding: 6rpx 24rpx;
  border-radius: 20rpx;
}

.progress-section {
  margin-bottom: 60rpx;
  width: 100%;
  max-width: 620rpx;
}

.progress-wrapper {
  display: flex;
  align-items: flex-end;
  gap: 16rpx;
  position: relative;
}

.time-text {
  font-size: 24rpx;
  color: $ios-text-secondary;
  min-width: 60rpx;
  font-weight: 600;
  font-variant-numeric: tabular-nums;
  &.time-start { text-align: left; }
  &.time-end { text-align: right; }
}

.progress-arc-container {
  flex: 1;
  height: 180rpx; /* 容器高度适配完美弧度排版 */
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
  width: 28rpx;
  height: 28rpx;
  background: #FFFFFF;
  border: 4rpx solid $theme-cyan;
  border-radius: 50%;
  transform: translate(-50%, -50%);
  box-shadow: 0 4px 12px rgba(44, 181, 160, 0.4);
  z-index: 3;
}

.main-controls {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 70rpx;
  margin-bottom: 70rpx;
}

.control-btn-glass {
  width: 90rpx;
  height: 90rpx;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.65);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.8);
  transition: all 0.2s ease;
  &:active { transform: scale(0.9); background: rgba(255, 255, 255, 0.9); }
}

.play-pause-btn {
  width: 140rpx;
  height: 140rpx;
  border-radius: 50%;
  background: $theme-cyan; 
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 12px 32px rgba(44, 181, 160, 0.35);
  transition: transform 0.2s cubic-bezier(0.2, 0.8, 0.2, 1);
  &.playing { box-shadow: 0 8px 24px rgba(44, 181, 160, 0.25); }
  &:active { transform: scale(0.92); }
}

.play-btn-inner {
  display: flex;
  align-items: center;
  justify-content: center;
}

.play-triangle {
  width: 0;
  height: 0;
  border-top: 16rpx solid transparent;
  border-bottom: 16rpx solid transparent;
  border-left: 24rpx solid #FFFFFF;
  margin-left: 8rpx; 
  border-radius: 4rpx;
}

.pause-icon {
  display: flex;
  gap: 12rpx;
}

.pause-bar {
  width: 8rpx;
  height: 32rpx;
  background-color: #FFFFFF;
  border-radius: 4rpx;
}

.glass-pill-bar {
  background: rgba(255, 255, 255, 0.5);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.03);
  border-radius: 50rpx;
  padding: 16rpx 40rpx;
  display: flex;
  align-items: center;
  justify-content: space-around; 
  width: 480rpx; 
}

.action-btn {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
  &:active { transform: scale(0.8); }
}

.heart-active {
  animation: heartBeat 0.3s ease-in-out;
}

@keyframes heartBeat {
  0% { transform: scale(1); }
  50% { transform: scale(1.3); }
  100% { transform: scale(1); }
}
</style>