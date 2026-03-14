<template>
  <view class="article-detail-page">
    <view class="ambient-background"></view>

    <view class="glass-header" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="navbar-content">
        <view class="navbar-left" @tap="goBack">
          <view class="nav-icon-glass">
            <uni-icons type="left" size="22" color="#1D1D1F"></uni-icons>
          </view>
        </view>
        <view class="navbar-title">深度阅读</view>
        <view class="navbar-right"></view>
      </view>
    </view>

    <view v-if="loading" class="loading-container">
      <view class="glass-card loading-card">
        <uni-icons type="spinner-cycle" size="36" color="#2CB5A0" class="spin-icon"></uni-icons>
        <text class="loading-text">正在为您精心排版内容...</text>
      </view>
    </view>

    <scroll-view v-else-if="article" class="article-scroll-view" scroll-y>
      
      <view class="hero-section">
        <view class="hero-glow"></view>

        <text class="hero-title">{{ article.title }}</text>

        <view class="hero-info-row">
          <view class="hero-meta">
            <view class="meta-item">
              <view class="author-avatar">
                <uni-icons type="person-filled" size="14" color="#2CB5A0"></uni-icons>
              </view>
              <text class="meta-text author-name">{{ article.author || '特邀导师' }}</text>
            </view>
            <view class="meta-divider"></view>
            <view class="meta-item">
              <uni-icons type="calendar" size="14" color="#86868B"></uni-icons>
              <text class="meta-text" style="margin-left: 6rpx;">{{ formatDate(article.createTime) }}</text>
            </view>
            <view class="meta-divider"></view>
            <view class="meta-item like-item" @tap="handleLike">
              <uni-icons :type="liked ? 'heart-filled' : 'heart'" size="14" :color="liked ? '#FF6B6B' : '#86868B'"></uni-icons>
              <text class="meta-text" :style="{ color: liked ? '#FF6B6B' : '#86868B', marginLeft: '6rpx' }">{{ likeCount }}</text>
            </view>
            <view class="meta-divider"></view>
            <view class="meta-item">
              <uni-icons type="eye" size="14" color="#86868B"></uni-icons>
              <text class="meta-text" style="margin-left: 6rpx;">{{ formatReadCount(article.readCount) }}</text>
            </view>
          </view>

          <view class="category-pill" v-if="article.category">
            <view class="dot"></view>
            <text>{{ article.category || '心理治愈' }}</text>
          </view>
        </view>
      </view>

      <view class="glass-card summary-card" v-if="article.summary">
        <view class="quote-mark">“</view>
        <text class="summary-text">{{ article.summary }}</text>
      </view>

      <view class="glass-paper body-content">
        <rich-text :nodes="formattedContent" class="rich-text-wrapper"></rich-text>
      </view>
      
      <view class="bottom-spacer"></view>
    </scroll-view>

    <view v-if="!loading && !article" class="empty-state">
      <view class="glass-card empty-card">
        <uni-icons type="info" size="48" color="#86868B"></uni-icons>
        <text class="empty-title">内容已飞走</text>
        <text class="empty-text">这篇内容可能已经被删除或隐藏</text>
        <view class="action-btn btn-secondary" @tap="goBack">返回上一页</view>
      </view>
    </view>

    <!-- 
    <view class="action-dock" v-if="!loading && article">
      <view class="dock-inner">
        <view class="comment-trigger" @tap="openComment">
          <uni-icons type="compose" size="18" color="#86868B"></uni-icons>
          <text>留下你的感悟...</text>
        </view>
        
        <view class="action-group">
          <view class="action-icon-btn" :class="{'is-active': isLiked}" @tap="toggleLike">
            <uni-icons :type="isLiked ? 'heart-filled' : 'heart'" size="24" :color="isLiked ? '#FF3B30' : '#1D1D1F'"></uni-icons>
          </view>
          <view class="action-icon-btn" :class="{'is-active': isCollected}" @tap="toggleCollect">
            <uni-icons :type="isCollected ? 'star-filled' : 'star'" size="24" :color="isCollected ? '#FF9500' : '#1D1D1F'"></uni-icons>
          </view>
          <view class="action-icon-btn" @tap="shareArticle">
            <uni-icons type="paperplane" size="24" color="#1D1D1F"></uni-icons>
          </view>
        </view>
      </view>
    </view>
    -->
  </view>
</template>

<script>
import {getArticleDetail, getArticleLikeStatus, likeArticle} from '@/api/article'
import {marked} from 'marked'

export default {
  data() {
    return {
      statusBarHeight: 0,
      loading: true,
      articleId: null,
      article: null,
      liked: false,
      likeCount: 0,
      isCollected: false
    }
  },
  computed: {
    formattedContent() {
      if (!this.article || !this.article.content) return ''
      // 1. 调用 marked 将 Markdown 语法（如 #, **, -, ```）解析为标准 HTML
      const rawHtml = marked.parse(this.article.content)
      // 2. 将解析出的标准 HTML 送入排版引擎，注入高级内联样式
      return this.injectAppleTypography(rawHtml)
    }
  },
  onLoad(options) {
    const systemInfo = uni.getSystemInfoSync()
    this.statusBarHeight = systemInfo.statusBarHeight || 0
    
    this.articleId = options.id || options.articleId
    this.loadData()
  },
  methods: {
    async loadData() {
      try {
        this.loading = true
        // 这里替换为你的实际接口调用
        const res = await getArticleDetail(this.articleId)
        if (res.code === 200) {
          this.article = res.data
          // 加载点赞状态
          this.loadLikeStatus()
        } else {
          uni.showToast({ title: res.msg || '加载失败', icon: 'none' })
        }
      } catch (error) {
        console.error('加载文章失败:', error)
      } finally {
        this.loading = false
      }
    },

    loadLikeStatus() {
      getArticleLikeStatus(this.articleId)
        .then((res) => {
          if (res.code === 200) {
            this.liked = res.data.liked
            this.likeCount = res.data.likeCount
          }
        })
        .catch((err) => {
          console.error('加载点赞状态失败:', err)
        })
    },

    handleLike() {
      likeArticle(this.articleId)
        .then((res) => {
          if (res.code === 200) {
            this.liked = res.data.liked
            this.likeCount = res.data.likeCount
          }
        })
        .catch((err) => {
          console.error('点赞操作失败:', err)
        })
    },

    goBack() {
      uni.navigateBack()
    },

    // ✨ 核心引擎：在标准 HTML 标签上注入 iOS 级内联排版样式 ✨
    injectAppleTypography(html) {
      if (!html) return ''
      
      // 预定义各层级行内样式
      const styles = {
        h1: 'font-size: 44rpx; font-weight: 800; color: #1D1D1F; margin: 56rpx 0 24rpx; line-height: 1.35; letter-spacing: -0.5rpx;',
        h2: 'font-size: 38rpx; font-weight: 700; color: #1D1D1F; margin: 48rpx 0 20rpx; line-height: 1.4;',
        h3: 'font-size: 34rpx; font-weight: 600; color: #1D1D1F; margin: 40rpx 0 16rpx; line-height: 1.4;',
        p: 'font-size: 32rpx; line-height: 1.85; color: #3A3A3C; margin-bottom: 32rpx; text-align: justify; word-break: break-all;',
        strong: 'font-weight: 600; color: #1D1D1F;',
        em: 'font-style: italic; color: #1D1D1F;',
        blockquote: 'display: block; margin: 32rpx 0; padding: 24rpx 32rpx; background: rgba(44, 181, 160, 0.08); border-left: 8rpx solid #2CB5A0; border-radius: 0 16rpx 16rpx 0; font-size: 30rpx; color: #555555; line-height: 1.6;',
        img: 'max-width: 100%; height: auto; border-radius: 20rpx; box-shadow: 0 8rpx 24rpx rgba(0,0,0,0.06); margin: 24rpx 0; display: block;',
        ul: 'margin: 24rpx 0; padding-left: 48rpx;',
        ol: 'margin: 24rpx 0; padding-left: 48rpx;',
        li: 'font-size: 32rpx; line-height: 1.85; color: #3A3A3C; margin-bottom: 12rpx;',
        code: 'font-family: Consolas, Monaco, Courier, monospace; background: rgba(0,0,0,0.05); padding: 4rpx 12rpx; border-radius: 8rpx; font-size: 28rpx; color: #FF3B30;',
        pre: 'background: rgba(245, 245, 247, 0.8); padding: 24rpx; border-radius: 16rpx; overflow-x: auto; margin: 32rpx 0; border: 1px solid rgba(0,0,0,0.05);'
      }

      // 运用正则精确替换，不破坏富文本结构
      let styledHtml = html
        .replace(/<h1/g, `<h1 style="${styles.h1}"`)
        .replace(/<h2/g, `<h2 style="${styles.h2}"`)
        .replace(/<h3/g, `<h3 style="${styles.h3}"`)
        .replace(/<p/g, `<p style="${styles.p}"`)
        .replace(/<strong/g, `<strong style="${styles.strong}"`)
        .replace(/<em/g, `<em style="${styles.em}"`)
        .replace(/<blockquote/g, `<blockquote style="${styles.blockquote}"`)
        .replace(/<img/g, `<img style="${styles.img}"`)
        .replace(/<ul/g, `<ul style="${styles.ul}"`)
        .replace(/<ol/g, `<ol style="${styles.ol}"`)
        .replace(/<li/g, `<li style="${styles.li}"`)
        // 匹配独立的 <code> 标签（行内代码）
        .replace(/<code/g, `<code style="${styles.code}"`)
        // 匹配 <pre> 标签（代码块）
        .replace(/<pre/g, `<pre style="${styles.pre}"`);

      // 外层包裹系统字体栈容器
      return `<div style="font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Text', 'Helvetica Neue', Arial, sans-serif;">${styledHtml}</div>`
    },

    formatDate(dateStr) {
      if (!dateStr) return '刚刚'
      const d = new Date(dateStr)
      return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')}`
    },
    
    formatReadCount(count) {
      if (!count) return '0'
      return count > 9999 ? (count / 10000).toFixed(1) + 'w' : count
    },

    // 交互操作与震动反馈
    toggleLike() { 
      this.isLiked = !this.isLiked; 
      uni.vibrateShort(); 
    },
    toggleCollect() { 
      this.isCollected = !this.isCollected; 
      uni.vibrateShort(); 
    },
    shareArticle() { 
      uni.showToast({ title: '调用分享面板', icon: 'none' }); 
    },
    openComment() { 
      uni.showToast({ title: '展开评论键盘', icon: 'none' }); 
    }
  }
}
</script>

<style lang="scss" scoped>
/* ==================== iOS 风格核心变量 ==================== */
$ios-text-primary: #1D1D1F;
$ios-text-secondary: #86868B;
$theme-cyan: #2CB5A0;

.article-detail-page {
  min-height: 100vh;
  position: relative;
  background-color: #F5F5F7;
  font-family: -apple-system, BlinkMacSystemFont, "SF Pro Text", "Helvetica Neue", Arial, sans-serif;
  padding-bottom: calc(180rpx + env(safe-area-inset-bottom)); /* 预留底部悬浮舱空间 */
}

/* --- 弥散光影背景 --- */
.ambient-background {
  position: fixed;
  top: 0; left: 0;
  width: 100vw; height: 100vh;
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
  border-radius: 36rpx;
}

/* ==================== 顶部导航 (Sticky) ==================== */
.glass-header {
  position: sticky;
  top: 0;
  z-index: 100;
  background: rgba(245, 245, 247, 0.65);
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

.navbar-left, .navbar-right { width: 80rpx; display: flex; align-items: center; }

.nav-icon-glass {
  width: 64rpx; height: 64rpx;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.6);
  display: flex; align-items: center; justify-content: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.04);
  transition: transform 0.2s ease;
  &:active { transform: scale(0.9); opacity: 0.8; }
}

.navbar-title { font-size: 32rpx; font-weight: 600; color: $ios-text-primary; }

/* ==================== 加载状态 ==================== */
.loading-container { position: relative; z-index: 1; padding: 120rpx 40rpx; display: flex; justify-content: center; }
.loading-card { padding: 60rpx 80rpx; display: flex; flex-direction: column; align-items: center; gap: 24rpx; }
.spin-icon { animation: spin 1.5s linear infinite; }
@keyframes spin { from { transform: rotate(0deg); } to { transform: rotate(360deg); } }
.loading-text { font-size: 28rpx; color: $ios-text-secondary; }

/* ==================== 文章容器 ==================== */
.article-scroll-view { position: relative; z-index: 1; }

/* 1. 沉浸式 Hero 头区 */
.hero-section {
  padding: 40rpx 40rpx 20rpx;
  position: relative;
}

.hero-glow {
  position: absolute;
  top: 0; left: 10%;
  width: 200rpx; height: 200rpx;
  background: radial-gradient(circle, rgba(44, 181, 160, 0.2) 0%, transparent 70%);
  border-radius: 50%;
  pointer-events: none;
}

.hero-title {
  font-size: 56rpx;
  font-weight: 800;
  color: $ios-text-primary;
  line-height: 1.35;
  letter-spacing: -1rpx;
  margin-bottom: 32rpx;
  display: block;
  font-family: "SF Pro Display", -apple-system, sans-serif;
}

/* 横向排版：左边 Meta 数据，右边分类胶囊 */
.hero-info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.hero-meta {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 16rpx;
  flex: 1;
}

.meta-item { display: flex; align-items: center; }

.like-item {
  cursor: pointer;
}

.author-avatar {
  width: 40rpx; height: 40rpx;
  border-radius: 50%;
  background: rgba(44, 181, 160, 0.15);
  display: flex; align-items: center; justify-content: center;
  margin-right: 12rpx;
}

.meta-text { font-size: 26rpx; color: $ios-text-secondary; font-weight: 500; }
.author-name { color: $ios-text-primary; font-weight: 600; }
.meta-divider { width: 6rpx; height: 6rpx; border-radius: 50%; background: #D1D1D6; }

.category-pill {
  display: inline-flex;
  align-items: center;
  gap: 10rpx;
  background: linear-gradient(135deg, rgba(44, 181, 160, 0.15) 0%, rgba(44, 181, 160, 0.08) 100%);
  padding: 10rpx 24rpx;
  border-radius: 40rpx;
  flex-shrink: 0;
  border: 1rpx solid rgba(44, 181, 160, 0.2);
  box-shadow: 0 2rpx 8rpx rgba(44, 181, 160, 0.1);

  .dot {
    width: 10rpx;
    height: 10rpx;
    border-radius: 50%;
    background: linear-gradient(135deg, $theme-cyan 0%, #4FDACE 100%);
    box-shadow: 0 0 6rpx rgba(44, 181, 160, 0.5);
  }
  text {
    font-size: 22rpx;
    font-weight: 600;
    color: $theme-cyan;
    letter-spacing: 1rpx;
  }
}

/* 2. 导读/摘要卡片 */
.summary-card {
  margin: 30rpx 40rpx;
  padding: 32rpx 40rpx;
  background: linear-gradient(135deg, rgba(255,255,255,0.7) 0%, rgba(240,250,248,0.6) 100%);
  position: relative;
  overflow: hidden;
}

.quote-mark {
  position: absolute;
  top: -10rpx; left: 16rpx;
  font-size: 100rpx;
  font-family: serif;
  color: rgba(44, 181, 160, 0.15);
  line-height: 1;
  font-weight: bold;
}

.summary-text {
  font-size: 30rpx;
  color: #555555;
  line-height: 1.6;
  position: relative;
  z-index: 1;
  font-weight: 500;
}

/* 3. 正文纸张 (Glass Paper) */
.glass-paper {
  margin: 20rpx 32rpx 40rpx; 
  padding: 60rpx 56rpx; 
  background: rgba(255, 255, 255, 0.75);
  backdrop-filter: blur(30px);
  -webkit-backdrop-filter: blur(30px);
  border: 1px solid rgba(255, 255, 255, 0.9);
  box-shadow: 
    0 20px 40px rgba(0, 0, 0, 0.03),
    0 1px 3px rgba(0, 0, 0, 0.02);
  border-radius: 40rpx;
}

/* 确保 rich-text 容器内部内容正确换行 */
.rich-text-wrapper {
  width: 100%;
  word-wrap: break-word;
  overflow: hidden;
  display: block;
}

/* ==================== 空状态 ==================== */
.empty-state { padding: 100rpx 40rpx; position: relative; z-index: 1;}
.empty-card { padding: 80rpx 40rpx; display: flex; flex-direction: column; align-items: center; text-align: center; }
.empty-title { font-size: 34rpx; font-weight: 600; color: $ios-text-primary; margin: 32rpx 0 16rpx; }
.empty-text { font-size: 28rpx; color: $ios-text-secondary; margin-bottom: 48rpx; }
.action-btn { padding: 16rpx 40rpx; border-radius: 40rpx; font-size: 28rpx; font-weight: 600; background: rgba(0,0,0,0.05); }

/* ==================== 底部悬浮社交舱 (Action Dock) ==================== */
.action-dock {
  position: fixed;
  bottom: 0; left: 0; right: 0;
  padding: 24rpx 40rpx calc(24rpx + env(safe-area-inset-bottom));
  z-index: 100;
}

.dock-inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: saturate(200%) blur(24px);
  -webkit-backdrop-filter: saturate(200%) blur(24px);
  border: 1px solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.08);
  border-radius: 100rpx;
  padding: 12rpx 16rpx 12rpx 32rpx;
  height: 96rpx;
  box-sizing: border-box;
}

.comment-trigger {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 12rpx;
  background: rgba(0, 0, 0, 0.04);
  padding: 16rpx 24rpx;
  border-radius: 30rpx;
  margin-right: 24rpx;
  
  text { font-size: 28rpx; color: $ios-text-secondary; }
}

.action-group {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.action-icon-btn {
  width: 72rpx; height: 72rpx;
  border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  transition: all 0.2s cubic-bezier(0.2, 0.8, 0.2, 1);
  background: transparent;
  
  &:active { transform: scale(0.8); background: rgba(0,0,0,0.05); }
  
  &.is-active {
    animation: pop 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  }
}

@keyframes pop {
  0% { transform: scale(1); }
  50% { transform: scale(1.3); }
  100% { transform: scale(1); }
}

/* 底部占位符，防止内容被悬浮舱遮挡 */
.bottom-spacer {
  height: 120rpx;
}
</style>