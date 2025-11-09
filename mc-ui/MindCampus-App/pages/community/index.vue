<template>
  <view class="community-page">
    <!-- 自定义导航栏 -->
    <view class="custom-navbar" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="navbar-content">
        <view class="navbar-left"></view>
        <view class="navbar-title">校园社区</view>
        <view class="navbar-right" @tap="goToPublish">
          <view class="publish-btn">
            <uni-icons type="compose" size="20" color="#FFFFFF"></uni-icons>
          </view>
        </view>
      </view>
    </view>

    <!-- 帖子列表 -->
    <scroll-view class="post-scroll" scroll-y @scrolltolower="loadMore" :lower-threshold="100"
      :enable-back-to-top="true" @refresherrefresh="onRefresh" :refresher-enabled="true"
      :refresher-triggered="refreshing">
      <view class="post-list">
        <view class="post-item" v-for="item in postList" :key="item.postId" @tap="goToDetail(item.postId)">
          <!-- 用户信息 -->
          <view class="post-header">
            <view class="user-info">
              <image v-if="item.userAvatar" class="user-avatar" :src="getImageUrl(item.userAvatar)"
                mode="aspectFill"></image>
              <view v-else class="user-avatar-placeholder">
                <uni-icons type="person-filled" size="20" color="#FFFFFF"></uni-icons>
              </view>
              <view class="user-detail">
                <view class="user-name">{{ item.userName || '匿名用户' }}</view>
                <view class="post-time">{{ formatTime(item.createTime) }}</view>
              </view>
            </view>
          </view>

          <!-- 帖子内容 -->
          <view class="post-content">
            <view class="post-title">{{ item.title }}</view>
            <view class="post-text">{{ item.content }}</view>

            <!-- 图片列表 -->
            <view v-if="item.images && item.images.length > 0" class="post-images"
              :class="'images-' + Math.min(item.images.length, 3)">
              <image v-for="(img, index) in item.images.slice(0, 9)" :key="index" class="post-image"
                :src="getImageUrl(img)" mode="aspectFill" @tap.stop="previewImage(item.images, index)"></image>
            </view>
          </view>

          <!-- 帖子操作栏 -->
          <view class="post-actions">
            <view class="action-item" @tap.stop="toggleLike(item)">
              <uni-icons :type="item.isLiked ? 'heart-filled' : 'heart'" size="18"
                :color="item.isLiked ? '#FF3A3A' : '#999999'"></uni-icons>
              <text class="action-text" :class="{ 'liked': item.isLiked }">{{ formatCount(item.likeCount) }}</text>
            </view>
            <view class="action-item">
              <uni-icons type="chat" size="18" color="#999999"></uni-icons>
              <text class="action-text">{{ formatCount(item.commentCount) }}</text>
            </view>
            <view class="action-item">
              <uni-icons type="eye" size="18" color="#999999"></uni-icons>
              <text class="action-text">{{ formatCount(item.viewCount) }}</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 加载更多 -->
      <view class="load-more" v-if="hasMore && postList.length > 0">
        <view v-if="loadStatus === 'loading'" class="loading-text">正在加载...</view>
        <view v-else class="load-more-text">上拉加载更多</view>
      </view>

      <!-- 没有更多 -->
      <view class="no-more" v-if="!hasMore && postList.length > 0">
        <text class="no-more-text">没有更多了</text>
      </view>

      <!-- 空状态 -->
      <view v-if="postList.length === 0 && !loading" class="empty-state">
        <view class="empty-icon">
          <uni-icons type="chatbubble" size="80" color="#CCCCCC"></uni-icons>
        </view>
        <text class="empty-text">暂无帖子</text>
        <view class="empty-btn" @tap="goToPublish">
          <text class="empty-btn-text">发布第一条帖子</text>
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<script>
import {likePost, listPosts, unlikePost} from '@/api/community/post'
import config from '@/config'

export default {
  data() {
    return {
      statusBarHeight: 0,
      loading: false,
      refreshing: false,
      postList: [],
      pageNum: 1,
      pageSize: 10,
      total: 0,
      hasMore: true,
      loadStatus: 'more' // more | loading | noMore
    }
  },
  onLoad() {
    // 获取状态栏高度
    const systemInfo = uni.getSystemInfoSync()
    this.statusBarHeight = systemInfo.statusBarHeight || 0

    // 加载数据
    this.loadPostList(true)
  },
  onShow() {
    // 每次显示页面时刷新列表（可能发布了新帖子）
    if (this.postList.length > 0) {
      this.refreshPostList()
    }
  },
  methods: {
    // 加载帖子列表
    loadPostList(reset = false) {
      if (this.loading) return

      this.loading = true
      this.loadStatus = 'loading'

      const params = {
        pageNum: this.pageNum,
        pageSize: this.pageSize,
        status: '0' // 只查询正常状态的帖子
      }

      listPosts(params).then(res => {
        this.loading = false
        this.refreshing = false

        if (res.code === 200) {
          const newList = res.rows || []

          if (reset) {
            this.postList = newList
          } else {
            this.postList = this.postList.concat(newList)
          }

          this.total = res.total || 0

          // 判断是否还有更多
          if (this.postList.length >= this.total) {
            this.hasMore = false
            this.loadStatus = 'noMore'
          } else {
            this.hasMore = true
            this.loadStatus = 'more'
          }
        } else {
          this.$modal.showToast(res.msg || '加载失败')
          this.loadStatus = 'more'
        }
      }).catch(err => {
        this.loading = false
        this.refreshing = false
        this.loadStatus = 'more'
        console.error('加载帖子列表失败:', err)
        this.$modal.showToast('加载失败，请重试')
      })
    },

    // 刷新帖子列表
    refreshPostList() {
      this.pageNum = 1
      this.hasMore = true
      this.loadPostList(true)
    },

    // 下拉刷新
    onRefresh() {
      this.refreshing = true
      this.refreshPostList()
    },

    // 加载更多
    loadMore() {
      if (!this.hasMore || this.loading) return

      this.pageNum++
      this.loadPostList(false)
    },

    // 跳转到发布页面
    goToPublish() {
      uni.navigateTo({
        url: '/pages/community/publish'
      })
    },

    // 跳转到详情页面
    goToDetail(postId) {
      uni.navigateTo({
        url: `/pages/community/detail?postId=${postId}`
      })
    },

    // 切换点赞
    toggleLike(item) {
      const action = item.isLiked ? unlikePost : likePost

      action(item.postId).then(res => {
        if (res.code === 200) {
          item.isLiked = !item.isLiked
          item.likeCount = item.isLiked ? item.likeCount + 1 : item.likeCount - 1
        } else {
          this.$modal.showToast(res.msg || '操作失败')
        }
      }).catch(err => {
        console.error('点赞操作失败:', err)
        this.$modal.showToast('操作失败，请重试')
      })
    },

    // 预览图片
    previewImage(images, current) {
      const urls = images.map(img => this.getImageUrl(img))
      uni.previewImage({
        urls: urls,
        current: current
      })
    },

    // 格式化时间
    formatTime(time) {
      if (!time) return ''

      const now = new Date()
      const postTime = new Date(time)
      const diff = now - postTime

      const minute = 60 * 1000
      const hour = 60 * minute
      const day = 24 * hour

      if (diff < minute) {
        return '刚刚'
      } else if (diff < hour) {
        return Math.floor(diff / minute) + '分钟前'
      } else if (diff < day) {
        return Math.floor(diff / hour) + '小时前'
      } else if (diff < 7 * day) {
        return Math.floor(diff / day) + '天前'
      } else {
        const year = postTime.getFullYear()
        const month = (postTime.getMonth() + 1).toString().padStart(2, '0')
        const date = postTime.getDate().toString().padStart(2, '0')
        return `${year}-${month}-${date}`
      }
    },

    // 格式化数量
    formatCount(count) {
      if (!count || count === 0) return '0'
      if (count >= 10000) {
        return (count / 10000).toFixed(1) + 'w'
      }
      if (count >= 1000) {
        return (count / 1000).toFixed(1) + 'k'
      }
      return count.toString()
    },

    // 获取图片完整URL
    getImageUrl(url) {
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

.community-page {
  min-height: 100vh;
  position: relative;

  &::before {
    content: '';
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background:
      linear-gradient(to bottom, transparent -30px, #FFFFFF 400px),
      linear-gradient(135deg, rgba(167, 243, 208, 0.12) 0%, rgba(196, 181, 253, 0.12) 50%, rgba(254, 205, 211, 0.12) 100%);
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
  display: flex;
  justify-content: flex-end;
}

.publish-btn {
  width: 64rpx;
  height: 64rpx;
  border-radius: 50%;
  background: $gradient-primary;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4rpx 12rpx rgba(22, 119, 255, 0.3);
  transition: all 0.2s ease;

  &:active {
    transform: scale(0.9);
  }
}

/* 滚动区域 */
.post-scroll {
  height: calc(100vh - 88rpx - env(safe-area-inset-top));
  padding: 0 $spacing-lg $spacing-xl;
  margin-top: calc(88rpx + env(safe-area-inset-top));
}

/* 帖子列表 */
.post-list {
  .post-item {
    background: rgba(255, 255, 255, 0.8);
    backdrop-filter: blur(10rpx);
    border-radius: $radius-lg;
    padding: $spacing-lg;
    margin-bottom: $spacing-base;
    box-shadow:
      0 4rpx 16rpx rgba(167, 243, 208, 0.08),
      0 2rpx 8rpx rgba(196, 181, 253, 0.08);
    border: 1rpx solid rgba(255, 255, 255, 0.6);
    transition: all 0.3s ease;

    &:active {
      transform: translateY(-2rpx);
      box-shadow:
        0 8rpx 24rpx rgba(167, 243, 208, 0.12),
        0 4rpx 12rpx rgba(196, 181, 253, 0.12);
    }
  }
}

/* 帖子头部 */
.post-header {
  margin-bottom: $spacing-md;
}

.user-info {
  display: flex;
  align-items: center;
}

.user-avatar {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  margin-right: $spacing-md;
  background: $bg-gray-100;
}

.user-avatar-placeholder {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  margin-right: $spacing-md;
  background: $gradient-primary;
  display: flex;
  align-items: center;
  justify-content: center;
}

.user-detail {
  flex: 1;
}

.user-name {
  font-size: $font-sm;
  font-weight: $font-semibold;
  color: $text-primary;
  margin-bottom: 4rpx;
  font-family: $font-family-base;
}

.post-time {
  font-size: $font-xs;
  color: $text-tertiary;
  font-family: $font-family-base;
}

/* 帖子内容 */
.post-content {
  margin-bottom: $spacing-md;
}

.post-title {
  font-size: $font-md;
  font-weight: $font-semibold;
  color: $text-primary;
  margin-bottom: $spacing-sm;
  line-height: $line-height-tight;
  font-family: $font-family-base;
}

.post-text {
  font-size: $font-sm;
  color: $text-secondary;
  line-height: $line-height-normal;
  margin-bottom: $spacing-md;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  line-clamp: 3;
  -webkit-box-orient: vertical;
  font-family: $font-family-base;
}

/* 图片列表 */
.post-images {
  display: grid;
  gap: $spacing-xs;
  margin-top: $spacing-md;

  &.images-1 {
    grid-template-columns: 1fr;

    .post-image {
      height: 400rpx;
    }
  }

  &.images-2 {
    grid-template-columns: repeat(2, 1fr);

    .post-image {
      height: 240rpx;
    }
  }

  &.images-3 {
    grid-template-columns: repeat(3, 1fr);

    .post-image {
      height: 200rpx;
    }
  }
}

.post-image {
  width: 100%;
  border-radius: $radius-base;
  background: $bg-gray-100;
}

/* 帖子操作栏 */
.post-actions {
  display: flex;
  align-items: center;
  gap: $spacing-2xl;
  padding-top: $spacing-md;
  border-top: 1rpx solid $border-light;
}

.action-item {
  display: flex;
  align-items: center;
  gap: $spacing-xs;
  transition: all 0.2s ease;

  &:active {
    transform: scale(0.95);
  }
}

.action-text {
  font-size: $font-xs;
  color: $text-tertiary;
  font-family: $font-family-base;

  &.liked {
    color: #FF3A3A;
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
  margin-bottom: $spacing-xl;
}

.empty-btn {
  padding: $spacing-md $spacing-2xl;
  background: $gradient-primary;
  border-radius: $radius-full;
  box-shadow: 0 4rpx 12rpx rgba(22, 119, 255, 0.3);
  transition: all 0.2s ease;

  &:active {
    transform: scale(0.95);
  }
}

.empty-btn-text {
  font-size: $font-sm;
  color: #FFFFFF;
  font-weight: $font-semibold;
  font-family: $font-family-base;
}
</style>

