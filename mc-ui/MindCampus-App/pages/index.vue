<template>
  <view class="home-container">
    <!-- 英雄区域 -->
    <view class="hero-section">
      <view class="hero-greeting">你好，{{ userName || '同学' }} 👋</view>
      <view class="hero-desc">欢迎来到心理健康服务平台</view>

      <!-- 轮播图 -->
      <banner-carousel @click="onBannerClick" @link-click="onBannerLinkClick"></banner-carousel>

    </view>

    <!-- 心理测评入口 -->
    <assessment-card icon="📋" title="心理健康测评" subtitle="了解你的心理健康状况，获取专业建议" @click="openAssessment"></assessment-card>


    <!-- 心灵鸡汤 -->
    <daily-quote @click="goToQuoteList"></daily-quote>

    <!-- 心理音乐推荐 -->
    <view class="music-section">
      <view class="section-header">
        <view class="section-title">
          <uni-icons type="sound-filled" size="20" color="#1677FF"></uni-icons>
          <text class="title-text">推荐音乐</text>
        </view>
        <view class="section-more" @tap="goToMusicList">更多 ›</view>
      </view>

      <!-- 推荐音乐列表（3个） -->
      <view class="music-list">
        <view class="music-item" v-for="item in recommendedMusicList" :key="item.musicId" @tap="playMusic(item)">
          <view class="music-icon-wrapper">
            <image v-if="item.coverUrl" class="music-cover" :src="getImageUrl(item.coverUrl)" mode="aspectFill"></image>
            <view v-else class="music-cover-placeholder">
              <uni-icons type="sound-filled" size="24" color="#FFFFFF"></uni-icons>
            </view>
          </view>
          <view class="music-info">
            <view class="music-title">{{ item.title }}</view>
            <view class="music-artist">{{ item.artist || '未知' }} · {{ formatDuration(item.duration) }}</view>
          </view>
        </view>
      </view>

      <view v-if="recommendedMusicList.length === 0" class="empty-music">
        <text class="empty-text">暂无推荐音乐</text>
      </view>
    </view>

    <!-- 心理课程推荐 -->
    <view class="course-section">
      <view class="section-header">
        <view class="section-title">
          <uni-icons type="videocam-filled" size="20" color="#1CD07E"></uni-icons>
          <text class="title-text">推荐课程</text>
        </view>
        <view class="section-more" @tap="goToCourseList">更多 ›</view>
      </view>

      <view class="course-list">
        <view class="course-item" v-for="item in courseList" :key="item.courseId" @tap="openCourse(item)">
          <view class="course-cover-wrapper">
            <image v-if="item.coverUrl" class="course-cover" :src="getImageUrl(item.coverUrl)" mode="aspectFill">
            </image>
            <view v-else class="course-cover-placeholder">
              <uni-icons type="videocam-filled" size="28" color="#FFFFFF"></uni-icons>
            </view>
          </view>
          <view class="course-info">
            <view class="course-title">{{ item.title }}</view>
            <view class="course-meta">
              <view class="meta-item">
                <uni-icons type="person" size="12" color="#CCCCCC"></uni-icons>
                <text>{{ item.lecturer || '未知' }}</text>
              </view>
              <view class="meta-item">
                <uni-icons type="clock" size="12" color="#CCCCCC"></uni-icons>
                <text>{{ formatDuration(item.duration) }}</text>
              </view>
            </view>
          </view>
        </view>
      </view>

      <view v-if="courseList.length === 0" class="empty-course">
        <text class="empty-text">暂无推荐课程</text>
      </view>
    </view>

    <!-- 心理文章推荐 -->
    <view class="article-section">
      <view class="section-header">
        <view class="section-title">
          <uni-icons type="compose" size="20" color="#FF8D3E"></uni-icons>
          <text class="title-text">推荐阅读</text>
        </view>
        <view class="section-more" @tap="goToArticleList">更多 ›</view>
      </view>

      <view class="article-list">
        <view class="article-item" v-for="item in articleList" :key="item.articleId" @tap="openArticle(item)">
          <view class="article-icon-wrapper">
            <uni-icons :type="getCategoryIcon(item.category)" size="18" color="#FF8D3E"></uni-icons>
          </view>
          <view class="article-content">
            <view class="article-title">{{ item.title }}</view>
            <view class="article-excerpt">{{ item.summary || '暂无摘要' }}</view>
            <view class="article-meta">
              <view class="meta-item">
                <uni-icons type="eye" size="12" color="#CCCCCC"></uni-icons>
                <text>{{ formatReadCount(item.readCount) }}</text>
              </view>
              <view class="meta-item">
                <uni-icons type="person" size="12" color="#CCCCCC"></uni-icons>
                <text>{{ item.author || '匿名' }}</text>
              </view>
            </view>
          </view>
        </view>
      </view>

      <view v-if="articleList.length === 0" class="empty-article">
        <text class="empty-text">暂无推荐文章</text>
      </view>
    </view>
  </view>
</template>

<script>
import DailyQuote from '@/components/daily-quote/daily-quote.vue'
import AssessmentCard from '@/components/assessment-card/assessment-card.vue'
import BannerCarousel from '@/components/banner-carousel/banner-carousel.vue'
import {getRecommendedMusic} from '@/api/music'
import {getRecommendedArticles} from '@/api/article'
import {getRecommendedCourses} from '@/api/course'
import config from '@/config'

export default {
  components: {
    DailyQuote,
    AssessmentCard,
    BannerCarousel
  },
  data() {
    return {
      userName: '同学',
      isPlaying: false,

      // 当前播放音乐
      currentMusic: {
        title: '',
        artist: '',
        duration: ''
      },

      // 推荐音乐列表（首页显示3个）
      recommendedMusicList: [],

      // 文章列表
      articleList: [],

      // 课程列表
      courseList: []
    }
  },

  onLoad() {
    this.getUserInfo()
    this.loadRecommendedMusic()
    this.loadRecommendedArticles()
    this.loadRecommendedCourses()
  },

  methods: {
    // 获取用户信息
    getUserInfo() {
      const name = this.$store.state.user.name
      if (name) {
        this.userName = name
      }
    },

    // 加载推荐音乐
    loadRecommendedMusic() {
      getRecommendedMusic().then(res => {
        if (res.code === 200 && res.data) {
          this.recommendedMusicList = res.data
          // 如果有推荐音乐，设置第一个为当前播放
          if (this.recommendedMusicList.length > 0) {
            this.currentMusic = {
              title: this.recommendedMusicList[0].title,
              artist: this.recommendedMusicList[0].artist || '未知',
              duration: this.formatDuration(this.recommendedMusicList[0].duration)
            }
          }
        }
      }).catch(err => {
        console.error('加载推荐音乐失败:', err)
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
      // 从 config 获取 baseUrl
      const baseUrl = config.baseUrl || 'http://localhost:8080'
      return url.startsWith('/') ? baseUrl + url : baseUrl + '/' + url
    },

    // 轮播图点击（没有链接时触发）
    onBannerClick(banner) {
      console.log('轮播图点击:', banner)
      // 可以在这里处理没有链接的轮播图点击事件
    },

    // 轮播图链接点击（自定义链接处理）
    onBannerLinkClick(linkUrl) {
      console.log('轮播图链接点击:', linkUrl)
      // 可以在这里处理自定义链接
    },

    // 打开心理测评
    openAssessment() {
      uni.navigateTo({
        url: '/pages/assessment/list'
      })
    },

    // 跳转到心灵鸡汤列表
    goToQuoteList() {
      this.$modal.showToast('查看更多鸡汤')
      // TODO: 跳转到鸡汤列表页面
      // this.$tab.navigateTo('/pages/quotes/list')
    },

    // 切换音乐播放状态
    toggleMusic() {
      this.isPlaying = !this.isPlaying
      this.$modal.showToast(this.isPlaying ? '开始播放' : '暂停播放')
    },

    // 播放指定音乐
    playMusic(item) {
      // 跳转到播放页面
      const musicList = JSON.stringify(this.recommendedMusicList)
      uni.navigateTo({
        url: `/pages/music/player?musicId=${item.musicId}&musicList=${encodeURIComponent(musicList)}`
      })
    },

    // 前往音乐列表
    goToMusicList() {
      uni.navigateTo({
        url: '/pages/music/list'
      })
    },

    // 加载推荐文章
    loadRecommendedArticles() {
      getRecommendedArticles().then(res => {
        if (res.code === 200 && res.data) {
          this.articleList = res.data
        }
      }).catch(err => {
        console.error('加载推荐文章失败:', err)
      })
    },

    // 打开文章详情
    openArticle(item) {
      uni.navigateTo({
        url: `/pages/article/detail?articleId=${item.articleId}`
      })
    },

    // 前往文章列表
    goToArticleList() {
      uni.navigateTo({
        url: '/pages/article/list'
      })
    },

    // 加载推荐课程
    loadRecommendedCourses() {
      getRecommendedCourses().then(res => {
        if (res.code === 200 && res.data) {
          this.courseList = res.data
        }
      }).catch(err => {
        console.error('加载推荐课程失败:', err)
      })
    },

    // 打开课程详情
    openCourse(item) {
      uni.navigateTo({
        url: `/pages/course/detail?courseId=${item.courseId}`
      })
    },

    // 前往课程列表
    goToCourseList() {
      uni.navigateTo({
        url: '/pages/course/list'
      })
    },

    // 根据分类返回对应的图标
    getCategoryIcon(category) {
      const categoryMap = {
        '压力管理': 'fire',
        '情绪调节': 'heart',
        '人际关系': 'hand-up',
        '学习方法': 'book',
        '心理健康': 'staff',
        '正念冥想': 'flower',
        '睡眠改善': 'moon',
        '自我成长': 'seedling',
      }
      return categoryMap[category] || 'book-filled'
    },

    // 格式化阅读量
    formatReadCount(count) {
      if (!count) return '0'
      if (count >= 10000) {
        return (count / 10000).toFixed(1) + 'w'
      }
      if (count >= 1000) {
        return (count / 1000).toFixed(1) + 'k'
      }
      return count.toString()
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/static/scss/theme.scss';

.home-container {
  min-height: 100vh;
  background: $gradient-soft;
  padding-bottom: calc(120rpx + env(safe-area-inset-bottom));
}

/* ==================== 英雄区域（梦幻渐变 + 平滑过渡）==================== */
.hero-section {
  padding: $spacing-xl $spacing-lg $spacing-2xl;
  background: $gradient-hero;
  padding-top: calc($spacing-xl + env(safe-area-inset-top));
  position: relative;
  overflow: visible;

  // 添加装饰性渐变圆圈（缩小）
  &::before {
    content: '';
    position: absolute;
    top: -80rpx;
    right: -80rpx;
    width: 300rpx;
    height: 300rpx;
    background: radial-gradient(circle, rgba(255, 255, 255, 0.3) 0%, transparent 70%);
    border-radius: $radius-full;
    z-index: 0;
  }

  // 底部平滑过渡层（缩短高度）
  &::after {
    content: '';
    position: absolute;
    bottom: -1rpx;
    left: 0;
    right: 0;
    height: 120rpx;
    background: linear-gradient(to bottom,
        transparent 0%,
        rgba(255, 255, 255, 0.3) 30%,
        rgba(255, 255, 255, 0.7) 60%,
        rgba(249, 250, 251, 0.9) 85%,
        #f9fafb 100%);
    z-index: 1;
  }
}

.hero-greeting {
  font-size: $font-3xl;
  font-weight: $font-bold;
  color: $bg-white;
  margin-bottom: $spacing-sm;
  letter-spacing: -0.5rpx;
  position: relative;
  z-index: 1;
}

.hero-desc {
  font-size: $font-base;
  color: rgba(255, 255, 255, 0.90);
  margin-bottom: $spacing-lg;
  position: relative;
  z-index: 1;
  font-weight: $font-medium;
}


/* ==================== 推荐模块通用样式 ==================== */
.music-section,
.article-section,
.course-section {
  padding: 0 $spacing-lg $spacing-lg;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: $spacing-md;
  padding: 0 $spacing-xs;
}

.section-title {
  display: flex;
  align-items: center;
  gap: $spacing-xs;
  font-size: $font-lg;
  font-weight: $font-bold;
  color: $text-primary;
  letter-spacing: -0.5rpx;

  .title-text {
    font-family: $font-family-base;
  }
}

.section-more {
  font-size: $font-sm;
  color: $primary-color;
  font-weight: $font-bold;
  padding: $spacing-xs $spacing-sm;
  border-radius: $radius-sm;
  transition: all $transition-fast $ease-out;

  &:active {
    opacity: $opacity-hover;
    background: rgba(22, 119, 255, 0.05);
  }
}

/* ==================== 音乐推荐（蓝色主题）==================== */
.music-list {
  display: flex;
  flex-direction: column;
  gap: $spacing-md;
}

.music-item {
  display: flex;
  align-items: center;
  background: linear-gradient(135deg, rgba(22, 119, 255, 0.06) 0%, rgba(77, 148, 255, 0.04) 100%);
  backdrop-filter: blur(10rpx);
  border-radius: $radius-lg;
  padding: $spacing-lg;
  transition: all $transition-base $ease-out;
  box-shadow: 0 4rpx 16rpx rgba(22, 119, 255, 0.08), 0 2rpx 8rpx rgba(22, 119, 255, 0.04);
  border: 1rpx solid rgba(22, 119, 255, 0.12);
  position: relative;
  overflow: hidden;

  // 装饰性渐变层
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: linear-gradient(135deg, rgba(22, 119, 255, 0.12) 0%, rgba(77, 148, 255, 0.08) 100%);
    opacity: 0;
    transition: opacity $transition-base $ease-out;
  }

  // 装饰性光晕
  &::after {
    content: '';
    position: absolute;
    top: -30%;
    right: -15%;
    width: 120rpx;
    height: 120rpx;
    background: radial-gradient(circle, rgba(22, 119, 255, 0.12) 0%, transparent 70%);
    border-radius: $radius-full;
    pointer-events: none;
  }

  &:active {
    transform: translateY(-2rpx) scale(0.99);
    box-shadow: 0 8rpx 24rpx rgba(22, 119, 255, 0.12), 0 4rpx 12rpx rgba(22, 119, 255, 0.08);

    &::before {
      opacity: 1;
    }
  }
}

.music-icon-wrapper {
  position: relative;
  z-index: 1;
}

.music-cover {
  width: 100rpx;
  height: 100rpx;
  border-radius: $radius-md;
  margin-right: $spacing-md;
  background: $bg-gray-100;
  flex-shrink: 0;
  box-shadow: 0 2rpx 8rpx rgba(22, 119, 255, 0.15);
}

.music-cover-placeholder {
  width: 100rpx;
  height: 100rpx;
  border-radius: $radius-md;
  margin-right: $spacing-md;
  background: linear-gradient(135deg, #1677FF 0%, #4D94FF 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 2rpx 8rpx rgba(22, 119, 255, 0.25);
}

.music-info {
  flex: 1;
  min-width: 0;
  position: relative;
  z-index: 1;
}

.music-title {
  font-size: $font-base;
  font-weight: $font-semibold;
  color: $text-primary;
  margin-bottom: $spacing-xs;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-family: $font-family-base;
}

.music-artist {
  font-size: $font-sm;
  color: $text-tertiary;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-family: $font-family-base;
}

.empty-music {
  padding: $spacing-2xl;
  text-align: center;
}

/* ==================== 文章推荐（橙色主题）==================== */
.article-list {
  display: flex;
  flex-direction: column;
  gap: $spacing-md;
}

.article-item {
  display: flex;
  background: linear-gradient(135deg, rgba(255, 141, 62, 0.06) 0%, rgba(255, 179, 125, 0.04) 100%);
  backdrop-filter: blur(10rpx);
  border-radius: $radius-lg;
  padding: $spacing-lg;
  box-shadow: 0 4rpx 16rpx rgba(255, 141, 62, 0.08), 0 2rpx 8rpx rgba(255, 141, 62, 0.04);
  transition: all $transition-base $ease-out;
  border: 1rpx solid rgba(255, 141, 62, 0.12);
  position: relative;
  overflow: hidden;

  // 装饰性渐变层
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: linear-gradient(135deg, rgba(255, 141, 62, 0.12) 0%, rgba(255, 179, 125, 0.08) 100%);
    opacity: 0;
    transition: opacity $transition-base $ease-out;
  }

  // 装饰性光晕
  &::after {
    content: '';
    position: absolute;
    bottom: -50%;
    left: -20%;
    width: 120rpx;
    height: 120rpx;
    background: radial-gradient(circle, rgba(255, 141, 62, 0.15) 0%, transparent 70%);
    border-radius: $radius-full;
    pointer-events: none;
  }

  &:active {
    transform: translateY(-2rpx) scale(0.99);
    box-shadow: 0 8rpx 24rpx rgba(255, 141, 62, 0.12), 0 4rpx 12rpx rgba(255, 141, 62, 0.08);

    &::before {
      opacity: 1;
    }
  }
}

.article-icon-wrapper {
  width: 56rpx;
  height: 56rpx;
  border-radius: $radius-md;
  background: linear-gradient(135deg, rgba(255, 141, 62, 0.15) 0%, rgba(255, 179, 125, 0.12) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: $spacing-md;
  flex-shrink: 0;
  position: relative;
  z-index: 1;
  box-shadow: 0 2rpx 8rpx rgba(255, 141, 62, 0.15);
}

.article-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  position: relative;
  z-index: 1;
  min-width: 0;
}

.article-title {
  font-size: $font-base;
  font-weight: $font-semibold;
  color: $text-primary;
  margin-bottom: $spacing-xs;
  letter-spacing: -0.5rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  line-clamp: 1;
  -webkit-box-orient: vertical;
  font-family: $font-family-base;
}

.article-excerpt {
  font-size: $font-xs;
  color: $text-tertiary;
  line-height: $line-height-normal;
  margin-bottom: $spacing-xs;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  line-clamp: 1;
  -webkit-box-orient: vertical;
  font-family: $font-family-base;
}

.article-meta {
  display: flex;
  gap: $spacing-md;
  font-size: $font-xs;
  color: $text-quaternary;
  font-weight: $font-normal;

  .meta-item {
    display: flex;
    align-items: center;
    gap: 4rpx;
    font-family: $font-family-base;
  }
}

.empty-article {
  padding: $spacing-2xl;
  text-align: center;
}

/* ==================== 课程推荐（绿色主题）==================== */
.course-list {
  display: flex;
  flex-direction: column;
  gap: $spacing-md;
}

.course-item {
  display: flex;
  background: linear-gradient(135deg, rgba(28, 208, 126, 0.06) 0%, rgba(82, 224, 157, 0.04) 100%);
  backdrop-filter: blur(10rpx);
  border-radius: $radius-lg;
  padding: $spacing-lg;
  box-shadow: 0 4rpx 16rpx rgba(28, 208, 126, 0.08), 0 2rpx 8rpx rgba(28, 208, 126, 0.04);
  transition: all $transition-base $ease-out;
  border: 1rpx solid rgba(28, 208, 126, 0.12);
  position: relative;
  overflow: hidden;

  // 装饰性渐变层
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: linear-gradient(135deg, rgba(28, 208, 126, 0.12) 0%, rgba(82, 224, 157, 0.08) 100%);
    opacity: 0;
    transition: opacity $transition-base $ease-out;
  }

  // 装饰性光晕
  &::after {
    content: '';
    position: absolute;
    top: -30%;
    left: 50%;
    width: 150rpx;
    height: 150rpx;
    background: radial-gradient(circle, rgba(28, 208, 126, 0.12) 0%, transparent 70%);
    border-radius: $radius-full;
    pointer-events: none;
  }

  &:active {
    transform: translateY(-2rpx) scale(0.99);
    box-shadow: 0 8rpx 24rpx rgba(28, 208, 126, 0.12), 0 4rpx 12rpx rgba(28, 208, 126, 0.08);

    &::before {
      opacity: 1;
    }
  }
}

.course-cover-wrapper {
  position: relative;
  z-index: 1;
}

.course-cover {
  width: 160rpx;
  height: 100rpx;
  border-radius: $radius-md;
  margin-right: $spacing-md;
  background: $bg-gray-100;
  flex-shrink: 0;
  box-shadow: 0 2rpx 8rpx rgba(28, 208, 126, 0.15);
}

.course-cover-placeholder {
  width: 160rpx;
  height: 100rpx;
  border-radius: $radius-md;
  margin-right: $spacing-md;
  background: linear-gradient(135deg, #1CD07E 0%, #52E09D 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 2rpx 8rpx rgba(28, 208, 126, 0.25);
}

.course-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  min-width: 0;
  position: relative;
  z-index: 1;
}

.course-title {
  font-size: $font-base;
  font-weight: $font-semibold;
  color: $text-primary;
  margin-bottom: $spacing-xs;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  font-family: $font-family-base;
  line-height: $line-height-tight;
}

.course-meta {
  display: flex;
  gap: $spacing-md;
  font-size: $font-xs;
  color: $text-quaternary;
  font-weight: $font-normal;

  .meta-item {
    display: flex;
    align-items: center;
    gap: 4rpx;
    font-family: $font-family-base;
  }
}

.empty-course {
  padding: $spacing-2xl;
  text-align: center;
}

.empty-text {
  font-size: $font-sm;
  color: $text-tertiary;
  font-family: $font-family-base;
}
</style>
