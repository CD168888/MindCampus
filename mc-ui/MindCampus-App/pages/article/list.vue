<template>
  <view class="article-list-page">
    <!-- 自定义导航栏 -->
    <view class="custom-navbar" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="navbar-content">
        <view class="navbar-left" @tap="goBack">
          <text class="back-icon">←</text>
        </view>
        <view class="navbar-title">心理文章</view>
        <view class="navbar-right"></view>
      </view>
    </view>

    <!-- 搜索栏 -->
    <view class="search-section">
      <view class="search-box">
        <input class="search-input" v-model="searchKeyword" placeholder="搜索文章、作者" @confirm="handleSearch" />
        <view class="search-icon" @tap="handleSearch">
          <uni-icons type="search" size="18" color="#999999"></uni-icons>
        </view>
      </view>
    </view>

    <!-- 文章列表 -->
    <scroll-view class="article-scroll" scroll-y @scrolltolower="loadMore" :lower-threshold="100"
      :enable-back-to-top="true">
      <view class="article-list">
        <view class="article-item" v-for="item in articleList" :key="item.articleId" @tap="viewArticle(item)">
          <view class="article-header">
            <view class="article-category" v-if="item.category">{{ item.category }}</view>
            <view class="article-read-count">
              <uni-icons type="eye" size="12" color="#999999"></uni-icons>
              <text class="read-text">{{ formatReadCount(item.readCount) }}</text>
            </view>
          </view>
          <view class="article-title">{{ item.title }}</view>
          <view class="article-summary" v-if="item.summary">{{ item.summary }}</view>
          <view class="article-footer">
            <view class="article-author">
              <uni-icons type="person" size="12" color="#999999"></uni-icons>
              <text class="author-text">{{ item.author || '匿名' }}</text>
            </view>
            <view class="article-time">{{ formatTime(item.createTime) }}</view>
          </view>
        </view>
      </view>

      <!-- 加载更多 -->
      <view class="load-more" v-if="hasMore && articleList.length > 0">
        <view v-if="loadStatus === 'loading'" class="loading-text">正在加载...</view>
        <view v-else class="load-more-text">上拉加载更多</view>
      </view>

      <!-- 没有更多 -->
      <view class="no-more" v-if="!hasMore && articleList.length > 0">
        <text class="no-more-text">没有更多了</text>
      </view>

      <!-- 空状态 -->
      <view v-if="articleList.length === 0 && !loading" class="empty-state">
        <view class="empty-icon">
          <uni-icons type="compose" size="80" color="#CCCCCC"></uni-icons>
        </view>
        <text class="empty-text">暂无文章</text>
      </view>
    </scroll-view>
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
      loadStatus: 'more', // more | loading | noMore
    };
  },
  onLoad() {
    // 获取状态栏高度
    const systemInfo = uni.getSystemInfoSync();
    this.statusBarHeight = systemInfo.statusBarHeight || 0;

    // 加载数据
    this.loadArticleList(true);
  },
  methods: {
    // 返回
    goBack() {
      uni.navigateBack();
    },

    // 搜索
    handleSearch() {
      this.pageNum = 1;
      this.articleList = [];
      this.hasMore = true;
      this.loadArticleList(true);
    },

    // 加载文章列表
    loadArticleList(reset = false) {
      if (this.loading) return;

      this.loading = true;
      this.loadStatus = 'loading';

      const params = {
        pageNum: this.pageNum,
        pageSize: this.pageSize,
        status: '0', // 只查询正常状态的文章
      };

      // 添加搜索条件（使用 keyword 参数进行模糊搜索，后端会搜索 title 或 author）
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

            // 判断是否还有更多
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
          console.error('加载文章列表失败:', err);
          this.$modal.showToast('加载失败，请重试');
        });
    },

    // 加载更多
    loadMore() {
      if (!this.hasMore || this.loading) return;

      this.pageNum++;
      this.loadArticleList(false);
    },

    // 查看文章
    viewArticle(item) {
      uni.navigateTo({
        url: `/pages/article/detail?articleId=${item.articleId}`,
      });
    },

    // 格式化阅读量
    formatReadCount(count) {
      if (!count) return '0';
      if (count >= 10000) {
        return (count / 10000).toFixed(1) + 'w';
      }
      return count.toString();
    },

    // 格式化时间
    formatTime(time) {
      if (!time) return '';
      const date = new Date(time);
      const now = new Date();
      const diff = now - date;
      const day = 24 * 60 * 60 * 1000;

      if (diff < day) {
        const hours = Math.floor(diff / (60 * 60 * 1000));
        if (hours === 0) {
          const minutes = Math.floor(diff / (60 * 1000));
          return minutes === 0 ? '刚刚' : `${minutes}分钟前`;
        }
        return `${hours}小时前`;
      } else if (diff < 7 * day) {
        return `${Math.floor(diff / day)}天前`;
      } else {
        const month = date.getMonth() + 1;
        const day = date.getDate();
        return `${month}月${day}日`;
      }
    },
  },
};
</script>

<style lang="scss" scoped>
@import '@/static/scss/theme.scss';

.article-list-page {
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

  >* {
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

/* 搜索栏 */
.search-section {
  padding: $spacing-lg $spacing-lg;
  background: transparent;
  margin-top: calc(88rpx + env(safe-area-inset-top));
}

.search-box {
  display: flex;
  align-items: center;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10rpx);
  border-radius: $radius-full;
  padding: 24rpx 32rpx;
  box-shadow: 0 4rpx 16rpx rgba(167, 243, 208, 0.08), 0 2rpx 8rpx rgba(196, 181, 253, 0.08);
  border: 2rpx solid rgba(167, 243, 208, 0.2);
  transition: all 0.3s ease;

  &:focus-within {
    border-color: rgba(167, 139, 250, 0.4);
    box-shadow: 0 8rpx 24rpx rgba(167, 243, 208, 0.12), 0 4rpx 12rpx rgba(196, 181, 253, 0.12);
  }
}

.search-input {
  flex: 1;
  font-size: $font-sm;
  color: $text-primary;
  font-family: $font-family-base;
  background: transparent;
}

.search-icon {
  margin-left: $spacing-md;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 滚动区域 */
.article-scroll {
  height: calc(100vh - 88rpx - 100rpx - env(safe-area-inset-top));
  padding: 0 $spacing-lg $spacing-xl;
}

/* 文章列表 */
.article-list {
  .article-item {
    background: rgba(255, 255, 255, 0.7);
    backdrop-filter: blur(10rpx);
    border-radius: $radius-lg;
    padding: $spacing-lg;
    margin-bottom: $spacing-base;
    box-shadow: 0 4rpx 16rpx rgba(167, 243, 208, 0.08), 0 2rpx 8rpx rgba(196, 181, 253, 0.08);
    border: 1rpx solid rgba(255, 255, 255, 0.6);
    transition: all 0.3s ease;
    position: relative;
    overflow: hidden;

    &::before {
      content: '';
      position: absolute;
      left: 0;
      top: 0;
      width: 100%;
      height: 100%;
      background: $gradient-card;
      opacity: 0;
      transition: opacity 0.3s ease;
    }

    &:active {
      transform: translateY(-2rpx);
      box-shadow: 0 8rpx 24rpx rgba(167, 243, 208, 0.12), 0 4rpx 12rpx rgba(196, 181, 253, 0.12);

      &::before {
        opacity: 1;
      }
    }
  }

  .article-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: $spacing-sm;
    position: relative;
    z-index: 1;
  }

  .article-category {
    font-size: 20rpx;
    color: #a78bfa;
    background: linear-gradient(135deg, rgba(167, 243, 208, 0.15) 0%, rgba(196, 181, 253, 0.15) 100%);
    padding: 6rpx 16rpx;
    border-radius: $radius-full;
    border: 1rpx solid rgba(167, 139, 250, 0.2);
    font-family: $font-family-base;
  }

  .article-read-count {
    display: flex;
    align-items: center;
    gap: 4rpx;
    font-size: $font-xs;
    color: $text-tertiary;
    font-family: $font-family-english;
  }

  .article-title {
    font-size: $font-lg;
    font-weight: $font-bold;
    color: $text-primary;
    margin-bottom: $spacing-sm;
    line-height: $line-height-tight;
    font-family: $font-family-base;
    position: relative;
    z-index: 1;
  }

  .article-summary {
    font-size: $font-sm;
    color: $text-tertiary;
    line-height: $line-height-normal;
    margin-bottom: $spacing-md;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    line-clamp: 2;
    -webkit-box-orient: vertical;
    font-family: $font-family-base;
    position: relative;
    z-index: 1;
  }

  .article-footer {
    display: flex;
    align-items: center;
    justify-content: space-between;
    position: relative;
    z-index: 1;
  }

  .article-author {
    display: flex;
    align-items: center;
    gap: 4rpx;
    font-size: $font-xs;
    color: $text-tertiary;
    font-family: $font-family-base;
  }

  .article-time {
    font-size: $font-xs;
    color: $text-quaternary;
    font-family: $font-family-english;
  }
}

/* 加载更多 */
.load-more {
  padding: $spacing-2xl 0;
  text-align: center;
}

.loading-text,
.load-more-text {
  font-size: $font-xs;
  color: $text-tertiary;
  font-family: $font-family-base;
}

.no-more {
  padding: $spacing-2xl 0;
  text-align: center;
}

.no-more-text {
  font-size: $font-xs;
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
  position: relative;

  &::before {
    content: '';
    position: absolute;
    width: 200rpx;
    height: 200rpx;
    border-radius: 50%;
    background: radial-gradient(circle, rgba(167, 243, 208, 0.2) 0%, transparent 70%);
    z-index: 0;
  }
}

.empty-icon {
  margin-bottom: $spacing-2xl;
  filter: grayscale(30%);
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.empty-text {
  font-size: $font-sm;
  color: $text-tertiary;
  font-family: $font-family-base;
  position: relative;
  z-index: 1;
}
</style>
