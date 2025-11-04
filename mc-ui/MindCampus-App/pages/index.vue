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

      <!-- 正在播放 -->
      <view class="music-player">
        <view class="music-controls">
          <view class="play-btn" @tap="toggleMusic">
            <text class="play-icon">{{ isPlaying ? '⏸' : '▶' }}</text>
          </view>
          <view class="music-info">
            <view class="music-title">{{ currentMusic.title }}</view>
            <view class="music-artist">{{ currentMusic.artist }} · {{ currentMusic.duration }}</view>
          </view>
        </view>
      </view>

      <!-- 音乐列表 -->
      <view class="music-list">
        <view class="music-item" v-for="(item, index) in musicList" :key="index" @tap="playMusic(item)">
          <view class="music-item-icon">{{ item.icon }}</view>
          <view class="music-info">
            <view class="music-title">{{ item.title }}</view>
            <view class="music-artist">{{ item.artist }} · {{ item.duration }}</view>
          </view>
        </view>
      </view>
    </view>

    <!-- 心理文章推荐 -->
    <view class="article-section">
      <view class="section-header">
        <view class="section-title">📖 推荐阅读</view>
        <view class="section-more" @tap="goToArticleList">更多 ›</view>
      </view>

      <view class="article-list">
        <view class="article-item" v-for="(item, index) in articleList" :key="index" @tap="openArticle(item)">
          <view class="article-cover">{{ item.cover }}</view>
          <view class="article-content">
            <view class="article-title">{{ item.title }}</view>
            <view class="article-excerpt">{{ item.excerpt }}</view>
            <view class="article-meta">
              <text class="meta-item">👁 {{ item.views }}</text>
              <text class="meta-item">❤️ {{ item.likes }}</text>
            </view>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import DailyQuote from '@/components/daily-quote/daily-quote.vue'
import AssessmentCard from '@/components/assessment-card/assessment-card.vue'

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
        title: '雨声冥想',
        artist: '自然音效',
        duration: '12:35'
      },

      // 音乐列表
      musicList: [
        {
          icon: '🌲',
          title: '森林晨曦',
          artist: '白噪音系列',
          duration: '15:20'
        },
        {
          icon: '🌊',
          title: '海浪轻抚',
          artist: '放松音乐',
          duration: '18:45'
        },
        {
          icon: '🎹',
          title: '钢琴轻语',
          artist: '古典音乐',
          duration: '10:15'
        }
      ],

      // 文章列表
      articleList: [
        {
          cover: '📚',
          title: '如何管理考试焦虑',
          excerpt: '考试焦虑是大学生常见的心理问题。通过科学的方法，我们可以有效地管理和缓解这种焦虑情绪...',
          views: '2.3k',
          likes: '156'
        },
        {
          cover: '🤝',
          title: '建立健康的人际关系',
          excerpt: '良好的人际关系是心理健康的重要组成部分。学会有效沟通和设定边界是关键...',
          views: '1.8k',
          likes: '203'
        },
        {
          cover: '🧘',
          title: '正念冥想入门指南',
          excerpt: '正念冥想是一种简单而有效的减压方法。每天10分钟的练习就能带来显著改善...',
          views: '3.1k',
          likes: '287'
        }
      ]
    }
  },

  onLoad() {
    this.getUserInfo()
  },

  methods: {
    // 获取用户信息
    getUserInfo() {
      const name = this.$store.state.user.name
      if (name) {
        this.userName = name
      }
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
      this.currentMusic = item
      this.isPlaying = true
      this.$modal.showToast('正在播放：' + item.title)
    },

    // 前往音乐列表
    goToMusicList() {
      this.$modal.showToast('前往音乐列表')
      // TODO: 跳转到音乐列表页面
    },

    // 打开文章详情
    openArticle(item) {
      this.$modal.showToast('打开文章：' + item.title)
      // TODO: 跳转到文章详情页
    },

    // 前往文章列表
    goToArticleList() {
      this.$modal.showToast('前往文章列表')
      // TODO: 跳转到文章列表页面
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
}

.music-title {
  font-size: $font-lg;
  font-weight: $font-bold;
  color: $bg-white;
  margin-bottom: $spacing-xs;
  letter-spacing: -0.5rpx;
  text-shadow: 0 1rpx 4rpx rgba(0, 0, 0, 0.1);
}

.music-artist {
  font-size: $font-sm;
  color: rgba(255, 255, 255, 0.90);
  font-weight: $font-medium;
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

.music-item-icon {
  font-size: 48rpx;
  margin-right: $spacing-md;
  filter: drop-shadow(0 1rpx 3rpx rgba(0, 0, 0, 0.08));
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
</style>
