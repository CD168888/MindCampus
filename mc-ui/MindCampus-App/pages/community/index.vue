<template>
  <view class="community-page">
    <!-- 浮动发布按钮 - 右侧圆形 -->
    <view class="float-publish-btn" @tap="goToPublish">
      <uni-icons type="compose" size="24" color="#FFFFFF"></uni-icons>
    </view>

    <!-- 帖子列表 -->
    <scroll-view class="post-scroll" scroll-y @scrolltolower="loadMore" :lower-threshold="100"
      :enable-back-to-top="true" @refresherrefresh="onRefresh" :refresher-enabled="true"
      :refresher-triggered="refreshing">
      <view class="post-list">
        <view class="post-item" v-for="item in postList" :key="item.postId" @tap="goToDetail(item.postId)">
          <!-- 用户信息头部 -->
          <view class="post-header">
            <view class="user-info">
              <image v-if="item.userAvatar" class="user-avatar" :src="getImageUrl(item.userAvatar)" mode="aspectFill">
              </image>
              <view v-else class="user-avatar-placeholder">
                <uni-icons type="person-filled" size="16" color="#FFFFFF"></uni-icons>
              </view>
              <view class="user-detail">
                <text class="user-name">{{ item.userName || '匿名用户' }}</text>
                <text class="post-time">{{ formatTime(item.createTime) }}</text>
              </view>
            </view>
          </view>

          <!-- 帖子内容 -->
          <view class="post-content">
            <view class="post-text">{{ item.content || item.title }}</view>
          </view>

          <!-- 帖子图片（如果有） -->
          <view v-if="item.images && item.images.length > 0" class="post-images">
            <image v-if="item.images.length === 1" class="post-image single" :src="getImageUrl(item.images[0])"
              mode="aspectFill" @tap.stop="previewImage(item.images, 0)">
            </image>
            <view v-else class="image-grid" :class="'grid-' + Math.min(item.images.length, 3)">
              <image v-for="(img, index) in item.images.slice(0, 9)" :key="index" class="post-image"
                :src="getImageUrl(img)" mode="aspectFill" @tap.stop="previewImage(item.images, index)">
              </image>
            </view>
          </view>

          <!-- 帖子底部操作栏 -->
          <view class="post-footer">
            <view class="action-item" @tap.stop="toggleLike(item)">
              <uni-icons :type="item.isLiked ? 'heart-filled' : 'heart'" size="18"
                :color="item.isLiked ? '#FF3A3A' : '#999999'">
              </uni-icons>
              <text class="action-text" :class="{ 'liked': item.isLiked }">
                {{ formatCount(item.likeCount) }}
              </text>
            </view>
            <view class="action-item">
              <uni-icons type="chat" size="18" color="#999999"></uni-icons>
              <text class="action-text">{{ formatCount(item.commentCount) }}</text>
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
      loading: false,
      refreshing: false,
      postList: [],
      pageNum: 1,
      pageSize: 10,
      total: 0,
      hasMore: true,
      loadStatus: 'more', // more | loading | noMore
      currentTab: 'recommend' // follow | recommend
    }
  },
  onLoad() {
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
  background: $bg-gray-50;
}

/* 浮动发布按钮 - 右侧圆形 */
.float-publish-btn {
  position: fixed;
  right: $spacing-lg;
  bottom: calc(120rpx + env(safe-area-inset-bottom));
  z-index: 998;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 88rpx;
  height: 88rpx;
  background: linear-gradient(135deg, #FF6B9D 0%, #FFA5C0 100%);
  border-radius: 50%;
  box-shadow: 0 8rpx 24rpx rgba(255, 107, 157, 0.4);
  transition: all $transition-base $ease-out;

  &:active {
    transform: scale(0.9);
    box-shadow: 0 4rpx 16rpx rgba(255, 107, 157, 0.3);
  }
}

/* 滚动区域 */
.post-scroll {
  height: 100vh;
}

/* 帖子列表 - 单列卡片布局 */
.post-list {
  padding: $spacing-base $spacing-lg $spacing-3xl;
}

.post-item {
  background: $bg-white;
  border-radius: $radius-lg;
  padding: $spacing-lg;
  margin-bottom: $spacing-lg;
  box-shadow: $shadow-sm;
  transition: all $transition-base $ease-out;

  &:active {
    transform: translateY(-2rpx);
    box-shadow: $shadow-md;
  }
}

/* 帖子头部 - 用户信息 */
.post-header {
  margin-bottom: $spacing-md;
}

.user-info {
  display: flex;
  align-items: center;
  gap: $spacing-sm;
}

.user-avatar {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  background: $bg-gray-100;
  flex-shrink: 0;
}

.user-avatar-placeholder {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  background: $gradient-primary;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.user-detail {
  display: flex;
  flex-direction: column;
  gap: 4rpx;
}

.user-name {
  font-size: $font-sm;
  font-weight: $font-semibold;
  color: $text-primary;
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

.post-text {
  font-size: $font-sm;
  color: $text-primary;
  line-height: $line-height-normal;
  font-family: $font-family-base;
  word-break: break-word;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 4;
  line-clamp: 4;
  -webkit-box-orient: vertical;
}

/* 帖子图片 */
.post-images {
  margin-bottom: $spacing-md;
}

.post-image.single {
  width: 100%;
  max-height: 500rpx;
  border-radius: $radius-base;
  background: $bg-gray-100;
}

.image-grid {
  display: grid;
  gap: $spacing-xs;

  &.grid-2 {
    grid-template-columns: repeat(2, 1fr);

    .post-image {
      width: 100%;
      height: 200rpx;
    }
  }

  &.grid-3 {
    grid-template-columns: repeat(3, 1fr);

    .post-image {
      width: 100%;
      height: 160rpx;
    }
  }
}

.image-grid .post-image {
  border-radius: $radius-sm;
  background: $bg-gray-100;
}

/* 帖子底部 - 操作栏 */
.post-footer {
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
  transition: all $transition-fast $ease-out;

  &:active {
    transform: scale(0.9);
  }
}

.action-text {
  font-size: $font-sm;
  color: $text-tertiary;
  font-family: $font-family-base;

  &.liked {
    color: $danger-color;
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
  padding: 200rpx $spacing-lg;
}

.empty-icon {
  margin-bottom: $spacing-2xl;
  opacity: 0.5;
}

.empty-text {
  font-size: $font-base;
  color: $text-tertiary;
  font-family: $font-family-base;
  margin-bottom: $spacing-xl;
}

.empty-btn {
  padding: $spacing-md $spacing-2xl;
  background: linear-gradient(135deg, #FF6B9D 0%, #FFA5C0 100%);
  border-radius: $radius-full;
  box-shadow: $shadow-sm;
  transition: all $transition-base $ease-out;

  &:active {
    transform: scale(0.95);
    box-shadow: $shadow-xs;
  }
}

.empty-btn-text {
  font-size: $font-sm;
  color: $bg-white;
  font-weight: $font-semibold;
  font-family: $font-family-base;
}
</style>
