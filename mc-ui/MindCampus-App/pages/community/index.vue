<template>
  <view class="community-page">
    <view class="ambient-background"></view>

    <view class="glass-header" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="navbar-content">
        <view class="navbar-left"></view>
        <view class="navbar-title">治愈社区</view>
        <view class="navbar-right"></view>
      </view>
    </view>

    <view class="float-publish-btn glass-publish" @tap="goToPublish">
      <uni-icons type="compose" size="24" color="#FFFFFF"></uni-icons>
    </view>

    <scroll-view class="post-scroll" scroll-y @scrolltolower="loadMore" :lower-threshold="100"
      :enable-back-to-top="true" @refresherrefresh="onRefresh" :refresher-enabled="true"
      :refresher-triggered="refreshing">
      
      <view class="post-list">
        <view class="post-item glass-card" v-for="item in postList" :key="item.postId" @tap="goToDetail(item.postId)">
          
          <view class="post-header">
            <view class="user-info">
              <view class="avatar-wrapper">
                <image v-if="item.userAvatar && item.userAvatar.trim()" class="user-avatar"
                  :src="getImageUrl(item.userAvatar)" mode="aspectFill" @error="handleImageError">
                </image>
                <view v-else class="user-avatar-placeholder">
                  <uni-icons type="person-filled" size="20" color="#2CB5A0"></uni-icons>
                </view>
              </view>
              
              <view class="user-detail">
                <text class="user-name">{{ item.userName || '匿名守护者' }}</text>
                <text class="post-time">{{ formatTime(item.createTime) }}</text>
              </view>
            </view>
            
            <view class="more-action-btn">
              <uni-icons type="more-filled" size="18" color="#C7C7CC"></uni-icons>
            </view>
          </view>

          <view class="post-content">
            <text class="post-text">{{ item.content || item.title }}</text>
          </view>

          <view v-if="item.images && item.images.length > 0" class="post-images">
            
            <view v-if="item.images.length === 1" class="images-layout-1">
              <image class="post-image single-img" :src="getImageUrl(item.images[0])" mode="aspectFill"
                @tap.stop="previewImage(item.images, 0)">
              </image>
            </view>

            <view v-else-if="item.images.length === 2" class="images-layout-2">
              <image v-for="(img, index) in item.images" :key="index" class="post-image" :src="getImageUrl(img)"
                mode="aspectFill" @tap.stop="previewImage(item.images, index)">
              </image>
            </view>

            <view v-else class="images-layout-grid" :class="{'grid-3': item.images.length >= 3}">
              <image v-for="(img, index) in item.images.slice(0, 9)" :key="index" class="post-image"
                :src="getImageUrl(img)" mode="aspectFill" @tap.stop="previewImage(item.images, index)">
              </image>
              <view v-if="item.images.length > 9 && index === 8" class="more-images-overlay">
                <text>+{{ item.images.length - 9 }}</text>
              </view>
            </view>
          </view>

          <view class="post-footer">
            <view class="action-group">
              <view class="action-btn" :class="{'is-active': item.isLiked}" @tap.stop="toggleLike(item)">
                <uni-icons :type="item.isLiked ? 'heart-filled' : 'heart'" size="20"
                  :color="item.isLiked ? '#FF3B30' : '#86868B'">
                </uni-icons>
                <text class="action-text" :style="{ color: item.isLiked ? '#FF3B30' : '#86868B' }">
                  {{ formatCount(item.likeCount) }}
                </text>
              </view>
              <view class="action-btn">
                <uni-icons type="chatbubble" size="20" color="#86868B"></uni-icons>
                <text class="action-text">{{ formatCount(item.commentCount) }}</text>
              </view>
            </view>
            
            <view class="action-share">
              <uni-icons type="paperplane" size="20" color="#86868B"></uni-icons>
            </view>
          </view>
        </view>
      </view>

      <view class="load-more-dock" v-if="hasMore && postList.length > 0">
        <uni-icons v-if="loadStatus === 'loading'" type="spinner-cycle" size="16" color="#86868B" class="spin-icon"></uni-icons>
        <text class="dock-text">{{ loadStatus === 'loading' ? '正在加载更多内容...' : '上拉加载更多' }}</text>
      </view>

      <view class="no-more-dock" v-if="!hasMore && postList.length > 0">
        <view class="divider-line"></view>
        <text class="dock-text">已经到底啦</text>
        <view class="divider-line"></view>
      </view>

      <view v-if="postList.length === 0 && !loading" class="empty-state-glass">
        <view class="glass-icon-bg">
          <uni-icons type="chatboxes" size="48" color="#2CB5A0"></uni-icons>
        </view>
        <text class="empty-title">这里很安静</text>
        <text class="empty-desc">成为第一个分享心情的人吧</text>
        <view class="empty-action-btn" @tap="goToPublish">
          发布我的瞬间
        </view>
      </view>

      <view class="bottom-spacer"></view>

    </scroll-view>

    <custom-tab-bar currentPath="/pages/community/index"></custom-tab-bar>
  </view>
</template>

<script>
import {likePost, listPosts, unlikePost} from '@/api/community/post'
import CustomTabBar from '@/components/custom-tab-bar/custom-tab-bar.vue'
import config from '@/config'

export default {
  components: {
    CustomTabBar
  },
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
      loadStatus: 'more', // more | loading | noMore
    }
  },
  onLoad() {
    const systemInfo = uni.getSystemInfoSync()
    this.statusBarHeight = systemInfo.statusBarHeight || 0
    this.loadPostList(true)
  },
  onShow() {
    // 隐藏原生导航栏，为 custom-tab-bar 让路
    uni.hideTabBar({ animation: false })
    
    if (this.postList.length > 0) {
      this.refreshPostList()
    }
  },
  methods: {
    loadPostList(reset = false) {
      if (this.loading) return

      this.loading = true
      this.loadStatus = 'loading'

      const params = {
        pageNum: this.pageNum,
        pageSize: this.pageSize,
        status: '0' 
      }

      listPosts(params).then(res => {
        this.loading = false
        this.refreshing = false

        if (res.code === 200) {
          let newList = res.rows || []

          newList = newList.map(post => {
            if (post.images && typeof post.images === 'string') {
              post.images = post.images.split(',').filter(img => img.trim())
            } else if (!post.images) {
              post.images = []
            }
            return post
          })

          if (reset) {
            this.postList = newList
          } else {
            this.postList = this.postList.concat(newList)
          }

          this.total = res.total || 0

          if (this.postList.length >= this.total) {
            this.hasMore = false
            this.loadStatus = 'noMore'
          } else {
            this.hasMore = true
            this.loadStatus = 'more'
          }
        } else {
          uni.showToast({ title: res.msg || '加载失败', icon: 'none' })
          this.loadStatus = 'more'
        }
      }).catch(err => {
        this.loading = false
        this.refreshing = false
        this.loadStatus = 'more'
        uni.showToast({ title: '加载失败，请重试', icon: 'none' })
      })
    },

    refreshPostList() {
      this.pageNum = 1
      this.hasMore = true
      this.loadPostList(true)
    },

    onRefresh() {
      this.refreshing = true
      this.refreshPostList()
    },

    loadMore() {
      if (!this.hasMore || this.loading) return
      this.pageNum++
      this.loadPostList(false)
    },

    goToPublish() {
      uni.navigateTo({ url: '/pages/community/publish' })
    },

    goToDetail(postId) {
      uni.navigateTo({ url: `/pages/community/detail?postId=${postId}` })
    },

    toggleLike(item) {
      // 触觉反馈增强高级感
      uni.vibrateShort()
      
      const action = item.isLiked ? unlikePost : likePost

      action(item.postId).then(res => {
        if (res.code === 200) {
          item.isLiked = !item.isLiked
          item.likeCount = item.isLiked ? item.likeCount + 1 : item.likeCount - 1
        } else {
          uni.showToast({ title: res.msg || '操作失败', icon: 'none' })
        }
      }).catch(err => {
        uni.showToast({ title: '操作失败，请重试', icon: 'none' })
      })
    },

    previewImage(images, current) {
      const urls = images.map(img => this.getImageUrl(img))
      uni.previewImage({ urls: urls, current: current })
    },

    formatTime(time) {
      if (!time) return ''
      const now = new Date()
      const postTime = new Date(time)
      const diff = now - postTime
      const minute = 60 * 1000
      const hour = 60 * minute
      const day = 24 * hour

      if (diff < minute) return '刚刚'
      else if (diff < hour) return Math.floor(diff / minute) + '分钟前'
      else if (diff < day) return Math.floor(diff / hour) + '小时前'
      else if (diff < 7 * day) return Math.floor(diff / day) + '天前'
      else {
        const month = (postTime.getMonth() + 1).toString().padStart(2, '0')
        const date = postTime.getDate().toString().padStart(2, '0')
        return `${month}-${date}`
      }
    },

    formatCount(count) {
      if (!count || count === 0) return '分享'
      if (count >= 10000) return (count / 10000).toFixed(1) + 'w'
      if (count >= 1000) return (count / 1000).toFixed(1) + 'k'
      return count.toString()
    },

    handleImageError(e) {
      console.error('头像加载失败:', e)
    },

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
/* ==================== iOS 风格核心变量 ==================== */
$ios-text-primary: #1D1D1F;
$ios-text-secondary: #86868B;
$theme-cyan: #2CB5A0;
$theme-cyan-light: #48D1CC;

.community-page {
  min-height: 100vh;
  position: relative;
  background-color: #F5F5F7;
  font-family: -apple-system, BlinkMacSystemFont, "SF Pro Text", "Helvetica Neue", Arial, sans-serif;
}

/* --- 防遮挡底部占位 --- */
.bottom-spacer {
  height: calc(160rpx + env(safe-area-inset-bottom));
  width: 100%;
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

/* ==================== 顶部导航 (Glass Header) ==================== */
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

.navbar-left, .navbar-right { width: 80rpx; }
.navbar-title {
  font-size: 34rpx;
  font-weight: 600;
  color: $ios-text-primary;
  letter-spacing: 1rpx;
}

/* ==================== 滚动区域 ==================== */
.post-scroll {
  height: 100vh;
  position: relative;
  z-index: 1;
}

/* ==================== 悬浮发布按钮 ==================== */
.float-publish-btn {
  position: fixed;
  right: 40rpx;
  bottom: calc(180rpx + env(safe-area-inset-bottom)); /* 避开自定义 tabbar */
  z-index: 90;
  width: 100rpx;
  height: 100rpx;
  border-radius: 50rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, $theme-cyan-light 0%, $theme-cyan 100%);
  box-shadow: 
    0 12rpx 32rpx rgba(44, 181, 160, 0.3),
    inset 0 2rpx 4rpx rgba(255, 255, 255, 0.3);
  transition: transform 0.25s cubic-bezier(0.2, 0.8, 0.2, 1);

  &:active {
    transform: scale(0.9) translateY(4rpx);
    box-shadow: 0 6rpx 16rpx rgba(44, 181, 160, 0.2);
  }
}

/* ==================== 信息流卡片 (Feed Glass Card) ==================== */
.post-list {
  padding: 24rpx 24rpx 0;
  display: flex;
  flex-direction: column;
  gap: 28rpx;
}

.glass-card {
  background: rgba(255, 255, 255, 0.65);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.03);
  border-radius: 40rpx; /* 极致的大圆角 */
  padding: 32rpx;
  transition: background 0.3s ease;
  
  &:active {
    background: rgba(255, 255, 255, 0.85);
  }
}

/* --- 帖子头部：用户元信息 --- */
.post-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24rpx;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.avatar-wrapper {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  box-shadow: 0 4rpx 12rpx rgba(0,0,0,0.06);
  background: #FFF;
  overflow: hidden;
}

.user-avatar {
  width: 100%;
  height: 100%;
  display: block;
}

.user-avatar-placeholder {
  width: 100%;
  height: 100%;
  background: rgba(44, 181, 160, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
}

.user-detail {
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.user-name {
  font-size: 30rpx;
  font-weight: 600;
  color: $ios-text-primary;
  margin-bottom: 4rpx;
}

.post-time {
  font-size: 24rpx;
  color: $ios-text-secondary;
  font-weight: 500;
}

.more-action-btn {
  width: 60rpx; height: 60rpx;
  display: flex; align-items: center; justify-content: flex-end;
}

/* --- 帖子内容 --- */
.post-content {
  margin-bottom: 24rpx;
}

.post-text {
  font-size: 32rpx;
  color: #3A3A3C;
  line-height: 1.6;
  text-align: justify;
  display: -webkit-box;
  -webkit-line-clamp: 5;
  -webkit-box-orient: vertical;
  overflow: hidden;
  word-break: break-all;
}

/* --- 图片网格 (Apple Grid Style) --- */
.post-images {
  margin-bottom: 24rpx;
}

.post-image {
  width: 100%;
  height: 100%;
  border-radius: 16rpx; /* 内部小圆角 */
  background: rgba(0,0,0,0.03);
  display: block;
}

.images-layout-1 {
  .single-img {
    max-height: 400rpx;
    border-radius: 24rpx;
    object-fit: cover;
  }
}

.images-layout-2 {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12rpx;
  height: 240rpx;
}

.images-layout-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12rpx;
  grid-auto-rows: 200rpx;
  position: relative;
}

.more-images-overlay {
  position: absolute;
  right: 0; bottom: 0;
  width: calc(33.33% - 8rpx); /* 占据最后一格 */
  height: 200rpx;
  background: rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  
  text {
    color: #FFF;
    font-size: 40rpx;
    font-weight: 600;
    font-family: "SF Pro Display", sans-serif;
  }
}

/* --- 帖子底部操作栏 --- */
.post-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 12rpx;
}

.action-group {
  display: flex;
  align-items: center;
  gap: 32rpx;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 12rpx 20rpx 12rpx 0;
  transition: transform 0.2s cubic-bezier(0.2, 0.8, 0.2, 1);

  &:active { transform: scale(0.85); }
}

.action-text {
  font-size: 26rpx;
  font-weight: 500;
  font-family: -apple-system, sans-serif;
}

.is-active {
  animation: popHeart 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

@keyframes popHeart {
  0% { transform: scale(1); }
  50% { transform: scale(1.3); }
  100% { transform: scale(1); }
}

.action-share {
  padding: 12rpx;
  &:active { transform: scale(0.85); }
}

/* ==================== 底部状态提示栏 ==================== */
.load-more-dock, .no-more-dock {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40rpx 0 20rpx;
  gap: 16rpx;
}

.spin-icon { animation: spin 1s linear infinite; }
@keyframes spin { 100% { transform: rotate(360deg); } }

.dock-text {
  font-size: 24rpx;
  color: $ios-text-secondary;
  font-weight: 500;
}

.divider-line {
  height: 1px;
  width: 60rpx;
  background: rgba(0, 0, 0, 0.06);
}

/* ==================== 绝美空状态 ==================== */
.empty-state-glass {
  margin: 100rpx 40rpx;
  padding: 80rpx 40rpx;
  background: rgba(255, 255, 255, 0.5);
  backdrop-filter: blur(30px);
  border-radius: 48rpx;
  border: 1px solid rgba(255, 255, 255, 0.8);
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.glass-icon-bg {
  width: 120rpx; height: 120rpx;
  border-radius: 40rpx;
  background: rgba(44, 181, 160, 0.15);
  display: flex; align-items: center; justify-content: center;
  margin-bottom: 32rpx;
}

.empty-title {
  font-size: 34rpx; font-weight: 700; color: $ios-text-primary; margin-bottom: 12rpx;
}

.empty-desc {
  font-size: 28rpx; color: $ios-text-secondary; margin-bottom: 40rpx;
}

.empty-action-btn {
  background: #1D1D1F;
  color: #FFFFFF;
  font-size: 28rpx; font-weight: 600;
  padding: 20rpx 48rpx;
  border-radius: 40rpx;
  transition: transform 0.2s ease;
  &:active { transform: scale(0.95); }
}
</style>