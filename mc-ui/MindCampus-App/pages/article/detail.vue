<template>
  <view class="article-detail-page">
    <!-- 自定义导航栏 -->
    <view class="custom-navbar" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="navbar-content">
        <view class="navbar-left" @tap="goBack">
          <text class="back-icon">←</text>
        </view>
        <view class="navbar-title">文章详情</view>
        <view class="navbar-right"></view>
      </view>
    </view>

    <!-- 文章内容 -->
    <scroll-view class="article-scroll" scroll-y>
      <view class="article-content" v-if="article">
        <!-- 文章头部 -->
        <view class="article-header">
          <view class="article-title">{{ article.title }}</view>
          <view class="article-meta">
            <view class="meta-item">
              <uni-icons type="person" size="14" color="#999999"></uni-icons>
              <text class="meta-text">{{ article.author || '匿名' }}</text>
            </view>
            <view class="meta-item">
              <uni-icons type="eye" size="14" color="#999999"></uni-icons>
              <text class="meta-text">{{ article.readCount || 0 }}</text>
            </view>
            <view class="meta-item">
              <uni-icons type="calendar" size="14" color="#999999"></uni-icons>
              <text class="meta-text">{{ formatDate(article.createTime) }}</text>
            </view>
            <view class="article-category" v-if="article.category">{{ article.category }}</view>
          </view>
        </view>

        <!-- 文章摘要 -->
        <view class="article-summary" v-if="article.summary">
          <view class="summary-label">摘要</view>
          <view class="summary-text">{{ article.summary }}</view>
        </view>

        <!-- 文章正文 -->
        <view class="article-body">
          <rich-text :nodes="formattedContent"></rich-text>
        </view>
      </view>

      <!-- 加载中 -->
      <view class="loading-state" v-if="loading">
        <text class="loading-text">加载中...</text>
      </view>

      <!-- 空状态 -->
      <view class="empty-state" v-if="!article && !loading">
        <uni-icons type="compose" size="80" color="#CCCCCC"></uni-icons>
        <text class="empty-text">文章不存在</text>
      </view>
    </scroll-view>
  </view>
</template>

<script>
import {getArticleDetail} from '@/api/article';

export default {
  data() {
    return {
      statusBarHeight: 0,
      articleId: null,
      article: null,
      loading: false,
    };
  },
  computed: {
    formattedContent() {
      if (!this.article || !this.article.content) return '';
      // 简单的 Markdown 转 HTML（可以使用更完善的 markdown 解析库）
      return this.markdownToHtml(this.article.content);
    },
  },
  onLoad(options) {
    // 获取状态栏高度
    const systemInfo = uni.getSystemInfoSync();
    this.statusBarHeight = systemInfo.statusBarHeight || 0;

    // 获取文章ID
    if (options.articleId) {
      this.articleId = parseInt(options.articleId);
      this.loadArticleDetail();
    }
  },
  methods: {
    // 返回
    goBack() {
      uni.navigateBack();
    },

    // 加载文章详情
    loadArticleDetail() {
      if (!this.articleId) return;

      this.loading = true;

      getArticleDetail(this.articleId)
        .then((res) => {
          this.loading = false;
          if (res.code === 200 && res.data) {
            this.article = res.data;
          } else {
            this.$modal.showToast(res.msg || '加载失败');
          }
        })
        .catch((err) => {
          this.loading = false;
          console.error('加载文章详情失败:', err);
          this.$modal.showToast('加载失败，请重试');
        });
    },

    // 简单的 Markdown 转 HTML
    markdownToHtml(markdown) {
      if (!markdown) return '';

      let html = markdown
        // 标题
        .replace(/^### (.*$)/gim, '<h3>$1</h3>')
        .replace(/^## (.*$)/gim, '<h2>$1</h2>')
        .replace(/^# (.*$)/gim, '<h1>$1</h1>')
        // 粗体
        .replace(/\*\*(.*)\*\*/gim, '<strong>$1</strong>')
        // 斜体
        .replace(/\*(.*)\*/gim, '<em>$1</em>')
        // 换行
        .replace(/\n/gim, '<br/>');

      // 添加样式
      html = `<div style="font-size: 32rpx; line-height: 1.8; color: #333; word-wrap: break-word;">${html}</div>`;

      return html;
    },

    // 格式化日期
    formatDate(time) {
      if (!time) return '';
      const date = new Date(time);
      const year = date.getFullYear();
      const month = (date.getMonth() + 1).toString().padStart(2, '0');
      const day = date.getDate().toString().padStart(2, '0');
      return `${year}-${month}-${day}`;
    },
  },
};
</script>

<style lang="scss" scoped>
@import '@/static/scss/theme.scss';

.article-detail-page {
  min-height: 100vh;
  position: relative;

  &::before {
    content: '';
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(to bottom, transparent -30px, #ffffff 400px),
      linear-gradient(135deg, rgba(167, 243, 208, 0.15) 0%, rgba(196, 181, 253, 0.15) 50%, rgba(254, 205, 211, 0.15) 100%);
    z-index: 0;
  }

  > * {
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

/* 滚动区域 */
.article-scroll {
  height: 100vh;
  margin-top: calc(88rpx + env(safe-area-inset-top));
  padding-bottom: calc(env(safe-area-inset-bottom) + 40rpx);
}

/* 文章内容 */
.article-content {
  padding: $spacing-lg;
}

/* 文章头部 */
.article-header {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10rpx);
  border-radius: $radius-lg;
  padding: $spacing-lg;
  margin-bottom: $spacing-lg;
  box-shadow: 0 4rpx 16rpx rgba(167, 243, 208, 0.08), 0 2rpx 8rpx rgba(196, 181, 253, 0.08);
  border: 1rpx solid rgba(255, 255, 255, 0.6);
}

.article-category {
  display: inline-block;
  font-size: 20rpx;
  color: #a78bfa;
  background: linear-gradient(135deg, rgba(167, 243, 208, 0.15) 0%, rgba(196, 181, 253, 0.15) 100%);
  padding: 6rpx 16rpx;
  border-radius: $radius-full;
  border: 1rpx solid rgba(167, 139, 250, 0.2);
  font-family: $font-family-base;
}

.article-title {
  font-size: $font-2xl;
  font-weight: $font-bold;
  color: $text-primary;
  line-height: $line-height-tight;
  margin-bottom: $spacing-lg;
  font-family: $font-family-base;
}

.article-meta {
  display: flex;
  align-items: center;
  gap: $spacing-lg;
  flex-wrap: wrap;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6rpx;
  font-size: $font-xs;
  color: $text-tertiary;
  font-family: $font-family-base;
}


/* 文章摘要 */
.article-summary {
  background: linear-gradient(135deg, rgba(167, 243, 208, 0.1) 0%, rgba(196, 181, 253, 0.1) 100%);
  border-radius: $radius-base;
  padding: $spacing-lg;
  margin-bottom: $spacing-lg;
  border-left: 4rpx solid #a78bfa;
}

.summary-label {
  font-size: $font-sm;
  font-weight: $font-semibold;
  color: #a78bfa;
  margin-bottom: $spacing-sm;
  font-family: $font-family-base;
}

.summary-text {
  font-size: $font-sm;
  color: $text-secondary;
  line-height: $line-height-normal;
  font-family: $font-family-base;
}

/* 文章正文 */
.article-body {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10rpx);
  border-radius: $radius-lg;
  padding: $spacing-xl;
  box-shadow: 0 4rpx 16rpx rgba(167, 243, 208, 0.08), 0 2rpx 8rpx rgba(196, 181, 253, 0.08);
  border: 1rpx solid rgba(255, 255, 255, 0.6);
}

/* 加载中 */
.loading-state {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 200rpx 0;
}

.loading-text {
  font-size: $font-sm;
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
}

.empty-state {
  uni-icons {
    margin-bottom: $spacing-2xl;
  }
}

.empty-text {
  font-size: $font-sm;
  color: $text-tertiary;
  font-family: $font-family-base;
}
</style>

