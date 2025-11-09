<template>
  <view class="home-container">
    <!-- 英雄区域 -->
    <view class="hero-section">
      <view class="hero-greeting">你好，{{ userName || '同学' }} 👋</view>
      <view class="hero-desc">欢迎来到心理健康服务平台</view>

      <!-- 轮播图 -->
      <swiper class="hero-swiper" :indicator-dots="true" :autoplay="true" :interval="4000" :duration="500"
        :circular="true">
        <swiper-item v-for="(banner, index) in bannerList" :key="index">
          <view class="swiper-item" @tap="onBannerClick(banner)">
            <view class="banner-content" :style="{ background: banner.gradient }">
              <view class="banner-icon">{{ banner.icon }}</view>
              <view class="banner-info">
                <view class="banner-title">{{ banner.title }}</view>
                <view class="banner-desc">{{ banner.desc }}</view>
              </view>
            </view>
          </view>
        </swiper-item>
      </swiper>

    </view>

    <!-- 心理测评入口 -->
    <assessment-card icon="📋" title="心理健康测评" subtitle="了解你的心理健康状况，获取专业建议" @click="openAssessment"></assessment-card>


    <!-- 心灵鸡汤 -->
    <daily-quote @click="goToQuoteList"></daily-quote>

    <!-- 心理音乐疗愈 -->
    <view class="music-section">
      <view class="section-header">
        <view class="section-title">🎵 音乐疗愈</view>
        <view class="section-more" @tap="goToMusicList">更多 ›</view>
      </view>

      <!-- 推荐音乐列表（3个） -->
      <view class="music-list">
        <view class="music-item" v-for="item in recommendedMusicList" :key="item.musicId" @tap="playMusic(item)">
          <image v-if="item.coverUrl" class="music-cover" :src="getImageUrl(item.coverUrl)" mode="aspectFill"></image>
          <view v-else class="music-cover-placeholder">🎵</view>
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

    <!-- 心理文章推荐 -->
    <view class="article-section">
      <view class="section-header">
        <view class="section-title">📖 推荐阅读</view>
        <view class="section-more" @tap="goToArticleList">更多 ›</view>
      </view>

      <view class="article-list">
        <view class="article-item" v-for="item in articleList" :key="item.articleId" @tap="openArticle(item)">
          <view class="article-cover">{{ getCategoryEmoji(item.category) }}</view>
          <view class="article-content">
            <view class="article-title">{{ item.title }}</view>
            <view class="article-excerpt">{{ item.summary || '暂无摘要' }}</view>
            <view class="article-meta">
              <text class="meta-item">👁 {{ formatReadCount(item.readCount) }}</text>
              <text class="meta-item">👤 {{ item.author || '匿名' }}</text>
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
import {getRecommendedMusic} from '@/api/music'
import {getRecommendedArticles} from '@/api/article'
import config from '@/config'

export default {
  components: {
    DailyQuote,
    AssessmentCard
  },
  data() {
    return {
      userName: '同学',
      isPlaying: false,

      // 轮播图数据
      bannerList: [
        {
          icon: '🧘',
          title: '正念冥想',
          desc: '每日10分钟，放松身心',
          gradient: 'linear-gradient(135deg, #a7f3d0 0%, #6ee7b7 100%)',
          link: '/pages/meditation/index'
        },
        {
          icon: '💪',
          title: '压力管理',
          desc: '科学方法应对压力',
          gradient: 'linear-gradient(135deg, #c4b5fd 0%, #a78bfa 100%)',
          link: '/pages/stress/index'
        },
        {
          icon: '😊',
          title: '情绪日记',
          desc: '记录每一天的心情',
          gradient: 'linear-gradient(135deg, #fecdd3 0%, #fda4af 100%)',
          link: '/pages/diary/index'
        }
      ],

      // 当前播放音乐
      currentMusic: {
        title: '',
        artist: '',
        duration: ''
      },

      // 推荐音乐列表（首页显示3个）
      recommendedMusicList: [],

      // 文章列表
      articleList: []
    }
  },

  onLoad() {
    this.getUserInfo()
    this.loadRecommendedMusic()
    this.loadRecommendedArticles()
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

    // 轮播图点击
    onBannerClick(banner) {
      this.$modal.showToast('跳转：' + banner.title)
      // TODO: 跳转到对应页面
      // this.$tab.navigateTo(banner.link)
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

    // 根据分类返回对应的 emoji
    getCategoryEmoji(category) {
      const categoryMap = {
        '压力管理': '💪',
        '情绪调节': '😊',
        '人际关系': '🤝',
        '学习方法': '📚',
        '心理健康': '💚',
        '正念冥想': '🧘',
        '睡眠改善': '😴',
        '自我成长': '🌱',
      }
      return categoryMap[category] || '📖'
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



/* ==================== 轮播图 ==================== */
.hero-swiper {
  height: 160rpx;
  margin-bottom: $spacing-md;
  border-radius: $radius-lg;
  overflow: hidden;
  position: relative;
  z-index: 1;
}

.swiper-item {
  height: 100%;
}

.banner-content {
  height: 100%;
  border-radius: $radius-lg;
  padding: $spacing-lg;
  display: flex;
  align-items: center;
  position: relative;
  overflow: hidden;

  // 装饰光晕
  &::before {
    content: '';
    position: absolute;
    top: -30%;
    right: -10%;
    width: 120rpx;
    height: 120rpx;
    background: radial-gradient(circle, rgba(255, 255, 255, 0.3) 0%, transparent 70%);
    border-radius: $radius-full;
  }
}

.banner-icon {
  font-size: 56rpx;
  margin-right: $spacing-md;
  filter: drop-shadow(0 2rpx 6rpx rgba(0, 0, 0, 0.1));
  position: relative;
  z-index: 1;
}

.banner-info {
  flex: 1;
  position: relative;
  z-index: 1;
}

.banner-title {
  font-size: $font-xl;
  font-weight: $font-bold;
  color: $bg-white;
  margin-bottom: $spacing-xs;
  letter-spacing: -0.5rpx;
  text-shadow: 0 1rpx 4rpx rgba(0, 0, 0, 0.1);
}

.banner-desc {
  font-size: $font-sm;
  color: rgba(255, 255, 255, 0.90);
  font-weight: $font-medium;
}


/* ==================== 音乐疗愈（玻璃态设计）==================== */
.music-section,
.article-section {
  padding: 0 $spacing-lg $spacing-lg;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: $spacing-md;
}

.section-title {
  font-size: $font-xl;
  font-weight: $font-bold;
  color: $text-primary;
  letter-spacing: -0.5rpx;
}

.section-more {
  font-size: $font-sm;
  color: $primary-color;
  font-weight: $font-semibold;

  &:active {
    opacity: $opacity-hover;
  }
}

.music-player {
  background: $gradient-music;
  border-radius: $radius-xl;
  padding: $spacing-lg;
  margin-bottom: $spacing-md;
  box-shadow: $shadow-secondary;
  position: relative;
  overflow: hidden;

  // 装饰性光晕（缩小）
  &::before {
    content: '';
    position: absolute;
    top: -40%;
    right: -15%;
    width: 150rpx;
    height: 150rpx;
    background: radial-gradient(circle, rgba(255, 255, 255, 0.3) 0%, transparent 70%);
    border-radius: $radius-full;
  }
}

.music-controls {
  display: flex;
  align-items: center;
  position: relative;
  z-index: 1;
}

.play-btn {
  width: 72rpx;
  height: 72rpx;
  border-radius: $radius-full;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: $blur-sm;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: $spacing-md;
  transition: all $transition-base $ease-out;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.12);

  &:active {
    transform: scale(0.95);
    box-shadow: 0 1rpx 6rpx rgba(0, 0, 0, 0.15);
  }
}

.play-icon {
  font-size: $font-xl;
  background: $gradient-primary;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-left: 4rpx;
}

.music-info {
  flex: 1;
  min-width: 0;
}

.music-title {
  font-size: $font-base;
  font-weight: $font-semibold;
  color: $text-primary;
  margin-bottom: $spacing-xs;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.music-artist {
  font-size: $font-sm;
  color: $text-secondary;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.music-list {
  display: flex;
  flex-direction: column;
  gap: $spacing-sm;
}

.music-item {
  display: flex;
  align-items: center;
  background: $bg-white;
  border-radius: $radius-base;
  padding: $spacing-md;
  transition: all $transition-base $ease-out;
  box-shadow: $shadow-xs;
  border: 1rpx solid rgba(0, 0, 0, 0.03);

  &:active {
    transform: translateY(-2rpx);
    box-shadow: $shadow-sm;
  }
}

.music-cover {
  width: 100rpx;
  height: 100rpx;
  border-radius: $radius-base;
  margin-right: $spacing-md;
  background: #f0f0f0;
  flex-shrink: 0;
}

.music-cover-placeholder {
  width: 100rpx;
  height: 100rpx;
  border-radius: $radius-base;
  margin-right: $spacing-md;
  background: $gradient-primary;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 48rpx;
  flex-shrink: 0;
}

.empty-music {
  padding: $spacing-lg;
  text-align: center;
}

.empty-text {
  font-size: $font-sm;
  color: #999;
}

/* ==================== 文章推荐（统一音乐模块样式）==================== */
.article-list {
  display: flex;
  flex-direction: column;
  gap: $spacing-sm;
}

.article-item {
  display: flex;
  background: $bg-white;
  border-radius: $radius-base;
  padding: $spacing-md;
  box-shadow: $shadow-xs;
  transition: all $transition-base $ease-out;
  border: 1rpx solid rgba(0, 0, 0, 0.03);
  position: relative;
  overflow: hidden;

  // 悬停渐变装饰
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: $gradient-card;
    opacity: 0;
    transition: opacity $transition-base $ease-out;
  }

  &:active {
    transform: translateY(-2rpx);
    box-shadow: $shadow-sm;

    &::before {
      opacity: 1;
    }
  }
}

.article-cover {
  font-size: 48rpx;
  width: 48rpx;
  height: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: $spacing-md;
  flex-shrink: 0;
  position: relative;
  z-index: 1;
  filter: drop-shadow(0 1rpx 3rpx rgba(110, 231, 183, 0.12));
}

.article-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  position: relative;
  z-index: 1;
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
}

.article-meta {
  display: flex;
  gap: $spacing-md;
  font-size: $font-xs;
  color: $text-quaternary;
  font-weight: $font-normal;
}

.meta-item {
  display: inline-flex;
  align-items: center;
}

.empty-article {
  padding: $spacing-2xl 0;
  text-align: center;
}

.empty-text {
  font-size: $font-sm;
  color: $text-tertiary;
  font-family: $font-family-base;
}
</style>
