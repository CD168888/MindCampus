<template>
  <view class="article-list-page">
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

    <view class="page-content" :style="{ marginTop: (statusBarHeight + 44) + 'px' }">
      
      <view class="search-section">
        <view class="search-box glass-pill">
          <uni-icons type="search" size="20" color="#86868B"></uni-icons>
          <input class="search-input" v-model="searchKeyword" placeholder="搜索文章、作者" placeholder-class="input-placeholder" @confirm="handleSearch" />
        </view>
      </view>

      <scroll-view class="article-scroll" scroll-y @scrolltolower="loadMore" :lower-threshold="100" :enable-back-to-top="true">
        <view class="article-list">
          <view class="glass-card article-card" v-for="item in articleList" :key="item.articleId" @tap="viewArticle(item)">
            
            <view class="article-header">
              <view class="article-category-tag" v-if="item.category">
                <text>{{ item.category }}</text>
              </view>
              <view class="read-count">
                <uni-icons type="eye" size="14" color="#86868B"></uni-icons>
                <text class="count-text">{{ formatReadCount(item.readCount) }}</text>
              </view>
            </view>

            <view class="article-body">
              <text class="article-title">{{ item.title }}</text>
              <text class="article-summary" v-if="item.summary">{{ item.summary }}</text>
            </view>

            <view class="article-footer">
              <view class="author-info">
                <view class="author-avatar-placeholder">
                  <uni-icons type="person-filled" size="12" color="#2CB5A0"></uni-icons>
                </view>
                <text class="author-name">{{ item.author || '匿名' }}</text>
              </view>
              <text class="article-time">{{ formatTime(item.createTime) }}</text>
            </view>
            
            <view class="card-arrow">
              <uni-icons type="right" size="14" color="#C7C7CC"></uni-icons>
            </view>
          </view>
        </view>

        <view class="status-footer" v-if="articleList.length > 0">
          <view v-if="loadStatus === 'loading'" class="loading-box">
            <uni-icons type="spinner-cycle" size="18" color="#86868B" class="spin-icon"></uni-icons>
            <text class="status-text">正在翻阅...</text>
          </view>
          <text v-else-if="!hasMore" class="status-text">已经读到最后了</text>
          <text v-else class="status-text">上拉探索更多深度内容</text>
        </view>

        <view v-if="articleList.length === 0 && !loading" class="empty-state glass-card">
          <view class="empty-icon-box">
            <uni-icons type="compose" size="48" color="#2CB5A0"></uni-icons>
          </view>
          <text class="empty-title">暂无文章</text>
          <text class="empty-desc">换个搜索词试试，或者静待心灵的馈赠</text>
        </view>
      </scroll-view>
    </view>
  </view>
</template>

<script>
import {listArticles} from '@/api/article';

export default {
  data() {
    return {
      statusBarHeight: 0,
      loading: false,
      searchKeyword: '',
      articleList: [],
      pageNum: 1,
      pageSize: 10,
      total: 0,
      hasMore: true,
      loadStatus: 'more',
    };
  },
  onLoad() {
    const systemInfo = uni.getSystemInfoSync();
    this.statusBarHeight = systemInfo.statusBarHeight || 0;
    this.loadArticleList(true);
  },
  methods: {
    goBack() {
      uni.navigateBack();
    },

    handleSearch() {
      this.pageNum = 1;
      this.articleList = [];
      this.hasMore = true;
      this.loadArticleList(true);
    },

    loadArticleList(reset = false) {
      if (this.loading) return;

      this.loading = true;
      this.loadStatus = 'loading';

      const params = {
        pageNum: this.pageNum,
        pageSize: this.pageSize,
        status: '0',
      };

      if (this.searchKeyword) {
        params.keyword = this.searchKeyword;
      }

      listArticles(params)
        .then((res) => {
          this.loading = false;
          if (res.code === 200) {
            const newList = res.rows || [];
            if (reset) {
              this.articleList = newList;
            } else {
              this.articleList = this.articleList.concat(newList);
            }
            this.total = res.total || 0;
            if (this.articleList.length >= this.total) {
              this.hasMore = false;
              this.loadStatus = 'noMore';
            } else {
              this.hasMore = true;
              this.loadStatus = 'more';
            }
          } else {
            this.$modal.showToast(res.msg || '加载失败');
            this.loadStatus = 'more';
          }
        })
        .catch((err) => {
          this.loading = false;
          this.loadStatus = 'more';
          this.$modal.showToast('加载失败，请重试');
        });
    },

    loadMore() {
      if (!this.hasMore || this.loading) return;
      this.pageNum++;
      this.loadArticleList(false);
    },

    viewArticle(item) {
      uni.navigateTo({
        url: `/pages/article/detail?articleId=${item.articleId}`,
      });
    },

    formatReadCount(count) {
      if (!count) return '0';
      if (count >= 10000) return (count / 10000).toFixed(1) + 'w';
      return count.toString();
    },

    formatTime(time) {
      if (!time) return '';
      const date = new Date(time);
      const now = new Date();
      const diff = now - date;
      const day = 24 * 60 * 60 * 1000;
      if (diff < day) {
        const hours = Math.floor(diff / (60 * 60 * 1000));
        return hours === 0 ? '刚刚' : `${hours}小时前`;
      } else if (diff < 7 * day) {
        return `${Math.floor(diff / day)}天前`;
      } else {
        return `${date.getMonth() + 1}月${date.getDate()}日`;
      }
    },
  },
};
</script>

<style lang="scss" scoped>
/* ==================== iOS 风格核心变量 ==================== */
$ios-text-primary: #1D1D1F;
$ios-text-secondary: #86868B;
$theme-cyan: #2CB5A0;

.article-list-page {
  min-height: 100vh;
  position: relative;
  display: flex;
  flex-direction: column;
  background-color: #F5F5F7;
  font-family: -apple-system, BlinkMacSystemFont, "SF Pro Text", "Helvetica Neue", Arial, sans-serif;
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

/* --- 毛玻璃通用卡片 --- */
.glass-card {
  background: rgba(255, 255, 255, 0.65);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.04);
  border-radius: 32rpx;
}

/* ==================== 顶部导航 ==================== */
.glass-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  background: rgba(245, 245, 247, 0.6);
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

.nav-icon-glass {
  width: 64rpx;
  height: 64rpx;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.04);
  transition: transform 0.2s ease;
  &:active { transform: scale(0.9); opacity: 0.8; }
}

.navbar-title {
  font-size: 32rpx;
  font-weight: 600;
  color: $ios-text-primary;
  letter-spacing: 1rpx;
}

/* ==================== 页面内容 ==================== */
.page-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  position: relative;
  z-index: 1;
  height: 100vh;
}

/* 搜索栏 */
.search-section {
  padding: 20rpx 32rpx;
  flex-shrink: 0;
}

.glass-pill {
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.9);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.03);
  border-radius: 50rpx;
  padding: 16rpx 32rpx;
  display: flex;
  align-items: center;
  gap: 16rpx;
  transition: all 0.3s ease;

  &:focus-within {
    background: rgba(255, 255, 255, 0.9);
    box-shadow: 0 8px 32px rgba(44, 181, 160, 0.15);
    border-color: rgba(44, 181, 160, 0.3);
  }
}

.search-input {
  flex: 1;
  font-size: 28rpx;
  color: $ios-text-primary;
  height: 48rpx;
}

.input-placeholder {
  color: #A1A1A6;
}

/* 文章列表区 */
.article-scroll {
  flex: 1;
  height: 0;
}

.article-list {
  padding: 10rpx 32rpx 40rpx;
  display: flex;
  flex-direction: column;
  gap: 32rpx;
}

.article-card {
  padding: 36rpx;
  display: flex;
  flex-direction: column;
  position: relative;
  transition: transform 0.25s cubic-bezier(0.2, 0.8, 0.2, 1);
  
  &:active {
    transform: scale(0.97);
    background: rgba(255, 255, 255, 0.85);
  }
}

.article-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24rpx;
}

.article-category-tag {
  background: rgba(167, 139, 250, 0.1); /* 浅紫色透明底 */
  padding: 6rpx 20rpx;
  border-radius: 12rpx;
  
  text {
    font-size: 22rpx;
    color: #a78bfa;
    font-weight: 600;
  }
}

.read-count {
  display: flex;
  align-items: center;
  gap: 6rpx;
  
  .count-text {
    font-size: 24rpx;
    color: $ios-text-secondary;
    font-weight: 500;
  }
}

.article-body {
  margin-bottom: 32rpx;
}

.article-title {
  font-size: 34rpx;
  font-weight: 700;
  color: $ios-text-primary;
  line-height: 1.4;
  margin-bottom: 16rpx;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.article-summary {
  font-size: 28rpx;
  color: $ios-text-secondary;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.article-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 24rpx;
  border-top: 1rpx solid rgba(0, 0, 0, 0.04);
}

.author-info {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.author-avatar-placeholder {
  width: 40rpx;
  height: 40rpx;
  background: rgba(44, 181, 160, 0.1);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.author-name {
  font-size: 24rpx;
  color: $ios-text-primary;
  font-weight: 600;
}

.article-time {
  font-size: 24rpx;
  color: $ios-text-secondary;
  font-weight: 500;
}

.card-arrow {
  position: absolute;
  top: 40rpx;
  right: 24rpx;
  opacity: 0.5;
}

/* 加载与状态 */
.status-footer {
  padding: 40rpx 0 calc(40rpx + env(safe-area-inset-bottom));
  display: flex;
  justify-content: center;
}

.loading-box {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.spin-icon {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.status-text {
  font-size: 24rpx;
  color: $ios-text-secondary;
  font-weight: 500;
}

/* 空状态 */
.empty-state {
  margin: 60rpx 32rpx;
  padding: 120rpx 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.empty-icon-box {
  width: 120rpx;
  height: 120rpx;
  border-radius: 50%;
  background: rgba(44, 181, 160, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 40rpx;
}

.empty-title {
  font-size: 32rpx;
  font-weight: 600;
  color: $ios-text-primary;
  margin-bottom: 12rpx;
}

.empty-desc {
  font-size: 26rpx;
  color: $ios-text-secondary;
  text-align: center;
  padding: 0 60rpx;
}
</style>