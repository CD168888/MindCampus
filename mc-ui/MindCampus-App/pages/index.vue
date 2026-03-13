<template>
  <view class="home-container">
    <view class="ambient-background"></view>

    <view class="glass-header">
      <view class="greeting-box">
        <text class="greeting-date">今日推荐</text>
        <text class="greeting-text">你好，{{ userName || '同学' }}</text>
      </view>
      <view class="user-avatar-glass">
        <uni-icons type="person-filled" size="24" color="#1D1D1F"></uni-icons>
      </view>
    </view>

    <view class="scroll-content">
      
      <view class="banner-section">
        <banner-carousel @click="onBannerClick" @link-click="onBannerLinkClick"></banner-carousel>
      </view>

      <view class="quote-pill-container" @tap="goToQuoteList">
        <view class="glass-pill quote-bar">
          <view class="quote-icon-box">
            <uni-icons type="sound-filled" size="16" color="#007AFF"></uni-icons>
            <text class="quote-label">寄语</text>
          </view>
          
          <swiper class="quote-swiper" vertical="true" autoplay="true" circular="true" interval="4000">
            <swiper-item class="quote-swiper-item" v-for="(quote, index) in quoteList" :key="index">
              <text class="quote-text">{{ quote }}</text>
            </swiper-item>
          </swiper>

          <uni-icons type="right" size="14" color="#86868B"></uni-icons>
        </view>
      </view>

      <view class="task-section">
        <view class="glass-card task-card" @tap="openAssessment">
          <view class="task-left">
            <view class="task-icon-bg">
              <uni-icons type="compose" size="28" color="#FFFFFF"></uni-icons>
            </view>
            <view class="task-info">
              <text class="task-title">校园心理问卷</text>
              <text class="task-desc">有新的测评任务待完成</text>
            </view>
          </view>
          <view class="task-right">
            <view class="task-btn">
              去完成 <uni-icons type="right" size="12" color="#2CB5A0"></uni-icons>
            </view>
          </view>
        </view>
      </view>

      <view class="section-container" v-if="recommendedMusicList.length > 0">
        <view class="section-header">
          <view class="header-left">
            <uni-icons type="headphones" size="22" color="#1D1D1F"></uni-icons>
            <text class="section-title">愈音工坊</text>
          </view>
          <view class="header-right" @tap="goToMusicList">
            <text class="section-more">更多</text>
            <uni-icons type="right" size="14" color="#86868B"></uni-icons>
          </view>
        </view>

        <scroll-view scroll-x="true" class="horizontal-scroll" :show-scrollbar="false">
          <view class="music-scroll-content">
            <view class="music-item" v-for="item in recommendedMusicList" :key="item.musicId" @tap="playMusic(item)">
              <view class="music-cover-wrapper">
                <image v-if="item.coverUrl" class="music-cover" :src="getImageUrl(item.coverUrl)" mode="aspectFill"></image>
                <view v-else class="music-cover-placeholder">
                  <uni-icons type="headphones" size="36" color="#FFFFFF"></uni-icons>
                </view>
                <view class="cover-gradient-overlay"></view>
                <view class="music-play-btn-glass">
                  <view class="play-triangle"></view>
                </view>
              </view>
              <view class="music-text-box">
                <text class="music-title">{{ item.title }}</text>
                <text class="music-artist">{{ item.artist || '未知' }}</text>
              </view>
            </view>
          </view>
        </scroll-view>
      </view>

      <view class="section-container" v-if="courseList.length > 0">
        <view class="section-header">
          <view class="header-left">
            <uni-icons type="videocam-filled" size="22" color="#1D1D1F"></uni-icons>
            <text class="section-title">精品课程</text>
          </view>
          <view class="header-right" @tap="goToCourseList">
            <text class="section-more">更多</text>
            <uni-icons type="right" size="14" color="#86868B"></uni-icons>
          </view>
        </view>

        <scroll-view scroll-x="true" class="horizontal-scroll" :show-scrollbar="false">
          <view class="course-scroll-content">
            <view class="course-card" v-for="item in courseList" :key="item.courseId" @tap="openCourse(item)">
              <image v-if="item.coverUrl" class="course-cover-img" :src="getImageUrl(item.coverUrl)" mode="aspectFill"></image>
              <view v-else class="course-cover-placeholder">
                <uni-icons type="videocam" size="48" color="#FFFFFF"></uni-icons>
              </view>

              <view class="glass-badge course-badge">
                <uni-icons type="clock" size="12" color="#1D1D1F" style="margin-right: 4rpx;"></uni-icons>
                {{ formatDuration(item.duration) }}
              </view>

              <view class="course-gradient-overlay"></view>

              <view class="course-info-overlay">
                <text class="course-title-overlay">{{ item.title }}</text>
                <text class="course-lecturer-overlay">
                  <uni-icons type="person" size="14" color="#E5E5EA" style="margin-right: 6rpx;"></uni-icons>
                  {{ item.lecturer || '特邀导师' }}
                </text>
              </view>
            </view>
          </view>
        </scroll-view>
      </view>

      <view class="section-container">
        <view class="section-header">
          <view class="header-left">
            <uni-icons type="map-filled" size="22" color="#1D1D1F"></uni-icons>
            <text class="section-title">深度阅读</text>
          </view>
          <view class="header-right" @tap="goToArticleList">
            <text class="section-more">更多</text>
            <uni-icons type="right" size="14" color="#86868B"></uni-icons>
          </view>
        </view>

        <view class="article-list" v-if="articleList.length > 0">
          <view class="glass-card article-card" v-for="item in articleList" :key="item.articleId" @tap="openArticle(item)">
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
                    <uni-icons type="eye" size="12" color="#86868B"></uni-icons> {{ formatReadCount(item.readCount) }}
                  </text>
                </view>
              </view>
            </view>
            <view class="article-arrow">
              <uni-icons type="right" size="16" color="#C7C7CC"></uni-icons>
            </view>
          </view>
        </view>

        <view v-if="articleList.length === 0" class="empty-state glass-card">
          <uni-icons type="info" size="24" color="#86868B"></uni-icons>
          <text class="empty-text">正在准备优质内容...</text>
        </view>
      </view>

    </view>
  </view>
</template>

<script>
import BannerCarousel from '@/components/banner-carousel/banner-carousel.vue'
import {getRecommendedMusic} from '@/api/music'
import {getRecommendedArticles} from '@/api/article'
import {getRecommendedCourses} from '@/api/course'
import config from '@/config'

export default {
  components: {
    BannerCarousel
  },
  data() {
    return {
      userName: '同学',
      isPlaying: false,
      currentMusic: {
        title: '',
        artist: '',
        duration: ''
      },
      recommendedMusicList: [],
      articleList: [],
      courseList: [],
      
      quoteList: [
        '接受自己的不完美，是治愈的开始。',
        '万物皆有裂痕，那是光照进来的地方。',
        '今天也要好好照顾自己的情绪呀。',
        '慢慢来，允许自己偶尔停下脚步。'
      ]
    }
  },
  onLoad() {
    this.getUserInfo()
    this.loadRecommendedMusic()
    this.loadRecommendedArticles()
    this.loadRecommendedCourses()
  },
  methods: {
    getUserInfo() {
      const name = this.$store.state.user.name
      if (name) this.userName = name
    },
    loadRecommendedMusic() {
      getRecommendedMusic().then(res => {
        if (res.code === 200 && res.data) {
          this.recommendedMusicList = res.data
          if (this.recommendedMusicList.length > 0) {
            this.currentMusic = {
              title: this.recommendedMusicList[0].title,
              artist: this.recommendedMusicList[0].artist || '未知',
              duration: this.formatDuration(this.recommendedMusicList[0].duration)
            }
          }
        }
      }).catch(err => console.error('加载推荐音乐失败:', err))
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
    },
    onBannerClick(banner) { console.log('轮播图点击:', banner) },
    onBannerLinkClick(linkUrl) { console.log('轮播图链接点击:', linkUrl) },
    openAssessment() { uni.navigateTo({ url: '/pages/assessment/list' }) },
    goToQuoteList() { uni.navigateTo({ url: '/pages/quotes/list' }) }, 
    toggleMusic() {
      this.isPlaying = !this.isPlaying
      this.$modal.showToast(this.isPlaying ? '开始播放' : '暂停播放')
    },
    playMusic(item) {
      const musicList = JSON.stringify(this.recommendedMusicList)
      uni.navigateTo({ url: `/pages/music/player?musicId=${item.musicId}&musicList=${encodeURIComponent(musicList)}` })
    },
    goToMusicList() { uni.navigateTo({ url: '/pages/music/list' }) },
    loadRecommendedArticles() {
      getRecommendedArticles().then(res => {
        if (res.code === 200 && res.data) this.articleList = res.data
      }).catch(err => console.error('加载推荐文章失败:', err))
    },
    openArticle(item) { uni.navigateTo({ url: `/pages/article/detail?articleId=${item.articleId}` }) },
    goToArticleList() { uni.navigateTo({ url: '/pages/article/list' }) },
    loadRecommendedCourses() {
      getRecommendedCourses().then(res => {
        if (res.code === 200 && res.data) this.courseList = res.data
      }).catch(err => console.error('加载推荐课程失败:', err))
    },
    openCourse(item) { uni.navigateTo({ url: `/pages/course/detail?courseId=${item.courseId}` }) },
    goToCourseList() { uni.navigateTo({ url: '/pages/course/list' }) },
    getCategoryIcon(category) {
      const categoryMap = { '压力管理': 'fire', '情绪调节': 'heart', '人际关系': 'hand-up', '学习方法': 'book', '心理健康': 'staff', '正念冥想': 'flower', '睡眠改善': 'moon', '自我成长': 'seedling' }
      return categoryMap[category] || 'folder'
    },
    formatReadCount(count) {
      if (!count) return '0'
      if (count >= 10000) return (count / 10000).toFixed(1) + 'w'
      if (count >= 1000) return (count / 1000).toFixed(1) + 'k'
      return count.toString()
    }
  }
}
</script>

<style lang="scss" scoped>
/* ==================== iOS 风格核心变量 ==================== */
$ios-text-primary: #1D1D1F;
$ios-text-secondary: #86868B;
$ios-blue: #007AFF;
$radius-xl: 40rpx;
$radius-lg: 32rpx;

.home-container {
  min-height: 100vh;
  position: relative;
  background-color: #F5F5F7; 
  font-family: -apple-system, BlinkMacSystemFont, "SF Pro Text", "Helvetica Neue", Arial, sans-serif;
  padding-bottom: calc(120rpx + env(safe-area-inset-bottom));
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
  border-radius: $radius-lg;
}

/* ==================== 顶部导航 ==================== */
.glass-header {
  position: sticky;
  top: 0;
  z-index: 100;
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  padding: calc(20rpx + env(safe-area-inset-top)) 40rpx 20rpx;
  background: rgba(245, 245, 247, 0.7);
  backdrop-filter: saturate(180%) blur(20px);
  -webkit-backdrop-filter: saturate(180%) blur(20px);
  border-bottom: 0.5px solid rgba(0, 0, 0, 0.05);
}

.greeting-date {
  font-size: 24rpx;
  color: $ios-text-secondary;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 1rpx;
  margin-bottom: 4rpx;
  display: block;
}

.greeting-text {
  font-size: 56rpx;
  font-weight: 700;
  color: $ios-text-primary;
  letter-spacing: -1rpx;
}

.user-avatar-glass {
  width: 72rpx;
  height: 72rpx;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 10rpx;
}

/* ==================== 页面滚动区 ==================== */
.scroll-content {
  position: relative;
  z-index: 1;
  padding-top: 30rpx;
}

/* ==================== 轮播图区域 ==================== */
.banner-section {
  padding: 0 30rpx 30rpx;
  border-radius: $radius-xl;
  overflow: hidden;
  transform: translateZ(0); 
}

/* ==================== 每日一言胶囊 ==================== */
.quote-pill-container {
  padding: 0 30rpx 40rpx;
  transform: translateZ(0);
}

.glass-pill {
  background: rgba(255, 255, 255, 0.65);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.03);
  border-radius: 50rpx;
  padding: 16rpx 24rpx;
  display: flex;
  align-items: center;
  
  &:active {
    transform: scale(0.98);
    opacity: 0.8;
  }
}

.quote-icon-box {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding-right: 16rpx;
  border-right: 1px solid rgba(0, 0, 0, 0.1);
  margin-right: 16rpx;
  flex-shrink: 0;
}

.quote-label {
  font-size: 24rpx;
  font-weight: 600;
  color: $ios-blue;
}

.quote-swiper {
  flex: 1;
  height: 36rpx;
  margin-right: 16rpx;
}

.quote-swiper-item {
  display: flex;
  align-items: center;
}

.quote-text {
  font-size: 26rpx;
  color: $ios-text-primary;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  width: 100%;
}

/* ==================== 问卷调查任务卡片 (色彩调优) ==================== */
.task-section {
  padding: 0 30rpx 50rpx;
}

.task-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 32rpx;
  transition: transform 0.2s ease;
  background: rgba(255, 255, 255, 0.7); /* 让卡片主体稍亮一些 */
  
  &:active {
    transform: scale(0.97);
    background: rgba(255, 255, 255, 0.9);
  }
}

.task-left {
  display: flex;
  align-items: center;
  gap: 24rpx;
}

/* 焕新：使用柔和的治愈青色渐变，更契合心理健康主题 */
.task-icon-bg {
  width: 96rpx;
  height: 96rpx;
  border-radius: 28rpx;
  background: linear-gradient(135deg, #48D1CC 0%, #2CB5A0 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8rpx 20rpx rgba(44, 181, 160, 0.25);
}

.task-info {
  display: flex;
  flex-direction: column;
}

.task-title {
  font-size: 34rpx;
  font-weight: 700;
  color: $ios-text-primary;
  margin-bottom: 8rpx;
  letter-spacing: -0.5rpx;
}

.task-desc {
  font-size: 26rpx;
  color: $ios-text-secondary;
}

.task-right {
  flex-shrink: 0;
}

/* 焕新：iOS 风格 Tinted Button，轻盈通透 */
.task-btn {
  background: rgba(44, 181, 160, 0.12); /* 浅色透明底 */
  color: #2CB5A0; /* 主题色文字 */
  font-size: 26rpx;
  font-weight: 600;
  padding: 14rpx 26rpx;
  border-radius: 40rpx;
  display: flex;
  align-items: center;
  gap: 6rpx;
}

/* ==================== 模块标题通用 ==================== */
.section-container {
  margin-bottom: 60rpx;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 40rpx;
  margin-bottom: 30rpx;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.section-title {
  font-size: 38rpx;
  font-weight: 700;
  color: $ios-text-primary;
  letter-spacing: -0.5rpx;
}

.header-right {
  display: flex;
  align-items: center;
  background: rgba(0, 0, 0, 0.04);
  padding: 8rpx 20rpx;
  border-radius: 20rpx;
  
  &:active {
    background: rgba(0, 0, 0, 0.08);
  }
}

.section-more {
  font-size: 26rpx;
  color: $ios-text-secondary;
  font-weight: 500;
  margin-right: 4rpx;
}

/* ==================== 愈音工坊 ==================== */
.horizontal-scroll {
  width: 100%;
  white-space: nowrap;
}

.music-scroll-content {
  display: inline-flex;
  padding: 0 30rpx 10rpx; 
  gap: 28rpx;
}

.music-item {
  width: 260rpx; 
  display: inline-flex;
  flex-direction: column;
  transition: transform 0.25s cubic-bezier(0.2, 0.8, 0.2, 1);
  
  &:active {
    transform: scale(0.93); 
    opacity: 0.9;
  }
}

.music-cover-wrapper {
  width: 260rpx;
  height: 260rpx;
  border-radius: 36rpx; 
  margin-bottom: 20rpx;
  position: relative;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.4);
  box-shadow: 
    0 8px 24px rgba(0, 0, 0, 0.05),
    inset 0 0 0 0.5px rgba(255, 255, 255, 0.8),
    inset 0 0 0 1px rgba(0, 0, 0, 0.03);
  transform: translateZ(0); 
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

.cover-gradient-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 40%;
  background: linear-gradient(to bottom, rgba(0,0,0,0) 0%, rgba(0,0,0,0.15) 100%);
  pointer-events: none; 
}

.music-play-btn-glass {
  position: absolute;
  bottom: 16rpx;
  right: 16rpx;
  width: 64rpx;
  height: 64rpx;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.5); 
  backdrop-filter: saturate(180%) blur(20px);
  -webkit-backdrop-filter: saturate(180%) blur(20px);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border: 0.5px solid rgba(255, 255, 255, 0.6);
}

.play-triangle {
  width: 0;
  height: 0;
  border-top: 10rpx solid transparent;
  border-bottom: 10rpx solid transparent;
  border-left: 14rpx solid $ios-text-primary; 
  margin-left: 4rpx; 
  border-radius: 2rpx; 
}

.music-text-box {
  display: flex;
  flex-direction: column;
  padding: 0 4rpx; 
}

.music-title {
  font-size: 28rpx;
  font-weight: 600;
  color: $ios-text-primary;
  margin-bottom: 6rpx;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.music-artist {
  font-size: 24rpx;
  color: $ios-text-secondary;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* ==================== 精品课程 ==================== */
.course-scroll-content {
  display: inline-flex;
  padding: 0 30rpx 20rpx;
  gap: 30rpx;
}

.course-card {
  width: 580rpx; 
  height: 360rpx; 
  border-radius: 40rpx; 
  overflow: hidden;
  position: relative;
  display: inline-flex;
  flex-direction: column;
  background: transparent;
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.08);
  transform: translateZ(0); 
  transition: transform 0.25s cubic-bezier(0.2, 0.8, 0.2, 1);
  
  &:active {
    transform: scale(0.96); 
  }
}

.course-cover-img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
}

.course-cover-placeholder {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #81ECEC 0%, #00CEC9 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1;
}

.glass-badge.course-badge {
  position: absolute;
  top: 24rpx;
  right: 24rpx;
  padding: 8rpx 20rpx;
  border-radius: 24rpx;
  font-size: 24rpx;
  font-weight: 600;
  color: #1D1D1F;
  background: rgba(255, 255, 255, 0.75);
  backdrop-filter: saturate(180%) blur(12px);
  -webkit-backdrop-filter: saturate(180%) blur(12px);
  display: flex;
  align-items: center;
  z-index: 3;
}

.course-gradient-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 60%; 
  background: linear-gradient(to bottom, rgba(0,0,0,0) 0%, rgba(0,0,0,0.65) 100%);
  z-index: 2;
  pointer-events: none;
}

.course-info-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 36rpx 40rpx;
  z-index: 3;
  display: flex;
  flex-direction: column;
}

.course-title-overlay {
  font-size: 36rpx;
  font-weight: 700;
  color: #FFFFFF; 
  margin-bottom: 12rpx;
  white-space: normal;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-shadow: 0 4rpx 12rpx rgba(0,0,0,0.3); 
}

.course-lecturer-overlay {
  font-size: 26rpx;
  color: #E5E5EA; 
  font-weight: 500;
  display: flex;
  align-items: center;
}

/* ==================== 深度阅读列表 (排版焕新) ==================== */
.article-list {
  padding: 0 30rpx;
  display: flex;
  flex-direction: column;
  gap: 28rpx;
}

.article-card {
  padding: 36rpx; /* 增加内边距，让排版更加透气 */
  border-radius: 36rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: relative;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.55); /* 稍微增加白底透明度 */
  
  &:active {
    opacity: 0.8;
    transform: scale(0.98);
    transition: all 0.2s ease;
  }
  
  /* 点睛之笔：卡片右上角微弱的环境光晕伪元素 */
  &::before {
    content: '';
    position: absolute;
    top: -40rpx;
    right: -40rpx;
    width: 160rpx;
    height: 160rpx;
    background: radial-gradient(circle, rgba(0, 122, 255, 0.06) 0%, transparent 70%);
    border-radius: 50%;
    pointer-events: none;
  }
}

.article-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding-right: 24rpx;
  z-index: 1; /* 确保文字在光晕上方 */
}

.article-title {
  font-size: 34rpx;
  font-weight: 700; /* 加粗标题，建立明显的视觉层级 */
  color: $ios-text-primary;
  line-height: 1.4;
  margin-bottom: 14rpx;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.article-excerpt {
  font-size: 28rpx; /* 稍微放大摘要文字 */
  color: $ios-text-secondary;
  line-height: 1.6; /* 增大行高，提升阅读舒适度 */
  margin-bottom: 28rpx;
  display: -webkit-box;
  -webkit-line-clamp: 2;
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
  gap: 6rpx;
  background: rgba(0, 122, 255, 0.08);
  padding: 6rpx 16rpx;
  border-radius: 12rpx;
}

.meta-category-text {
  font-size: 24rpx;
  color: $ios-blue;
  font-weight: 600;
}

.meta-info-group {
  display: flex;
  gap: 24rpx;
}

.meta-author {
  font-size: 24rpx;
  color: $ios-text-secondary;
  display: flex;
  align-items: center;
  gap: 6rpx;
}

.article-arrow {
  flex-shrink: 0;
  width: 48rpx;
  height: 48rpx;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  z-index: 1;
}

.empty-state {
  margin: 0 30rpx;
  padding: 60rpx 0;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 12rpx;
}

.empty-text {
  color: $ios-text-secondary;
  font-size: 28rpx;
}
</style>