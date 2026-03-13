<template>
  <view class="detail-page">
    <view class="ambient-background"></view>

    <view class="glass-header" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="navbar-content">
        <view class="navbar-left" @tap="goBack">
          <view class="nav-icon-glass">
            <uni-icons type="left" size="22" color="#1D1D1F"></uni-icons>
          </view>
        </view>
        <view class="navbar-title">动态详情</view>
        <view class="navbar-right"></view>
      </view>
    </view>

    <scroll-view class="content-scroll" scroll-y :enable-back-to-top="true">
      
      <view v-if="postDetail" class="glass-card post-master-card">
        <view class="post-header">
          <view class="user-info">
            <view class="avatar-wrapper">
              <image v-if="postDetail.userAvatar" class="user-avatar" :src="getImageUrl(postDetail.userAvatar)" mode="aspectFill"></image>
              <view v-else class="user-avatar-placeholder">
                <uni-icons type="person-filled" size="20" color="#2CB5A0"></uni-icons>
              </view>
            </view>
            <view class="user-detail">
              <text class="user-name">{{ postDetail.userName || '匿名守护者' }}</text>
              <text class="post-time">{{ formatTime(postDetail.createTime) }}</text>
            </view>
          </view>
        </view>

        <view class="post-content">
          <text class="post-title" v-if="postDetail.title">{{ postDetail.title }}</text>
          <text class="post-text">{{ postDetail.content }}</text>

          <view v-if="postDetail.images && postDetail.images.length > 0" class="post-images">
            <view v-if="postDetail.images.length === 1" class="images-layout-1">
              <image class="post-image single-img" :src="getImageUrl(postDetail.images[0])"
                mode="aspectFill" @tap="previewImage(postDetail.images, 0)">
              </image>
            </view>
            
            <view v-else-if="postDetail.images.length === 2" class="images-layout-2">
              <image v-for="(img, index) in postDetail.images" :key="index" class="post-image"
                :src="getImageUrl(img)" mode="aspectFill" @tap="previewImage(postDetail.images, index)">
              </image>
            </view>
            
            <view v-else-if="postDetail.images.length === 3" class="images-layout-3">
              <image v-for="(img, index) in postDetail.images" :key="index" class="post-image"
                :src="getImageUrl(img)" mode="aspectFill" @tap="previewImage(postDetail.images, index)">
              </image>
            </view>
            
            <view v-else-if="postDetail.images.length === 4" class="images-layout-4">
              <image v-for="(img, index) in postDetail.images" :key="index" class="post-image"
                :src="getImageUrl(img)" mode="aspectFill" @tap="previewImage(postDetail.images, index)">
              </image>
            </view>
            
            <view v-else class="images-layout-grid">
              <image v-for="(img, index) in postDetail.images.slice(0, 9)" :key="index" class="post-image"
                :src="getImageUrl(img)" mode="aspectFill" @tap="previewImage(postDetail.images, index)">
              </image>
            </view>
          </view>
        </view>

        <view class="post-actions-bar">
          <view class="action-item" @tap="toggleLike" :class="{'is-active': postDetail.isLiked}">
            <uni-icons :type="postDetail.isLiked ? 'heart-filled' : 'heart'" size="22"
              :color="postDetail.isLiked ? '#FF3B30' : '#86868B'"></uni-icons>
            <text class="action-text" :style="{ color: postDetail.isLiked ? '#FF3B30' : '#86868B' }">
              {{ formatCount(postDetail.likeCount) }}
            </text>
          </view>
          <view class="action-item">
            <uni-icons type="chatbubble" size="22" color="#86868B"></uni-icons>
            <text class="action-text">{{ formatCount(postDetail.commentCount) }}</text>
          </view>
          <view class="action-item view-count">
            <uni-icons type="eye" size="22" color="#C7C7CC"></uni-icons>
            <text class="action-text text-light">{{ formatCount(postDetail.viewCount) }}</text>
          </view>
        </view>
      </view>

      <view class="comment-section">
        <view class="section-title-box">
          <text class="title-text">共 {{ commentList.length }} 条治愈回音</text>
        </view>

        <view class="comment-list" v-if="commentList.length > 0">
          <view class="comment-item" v-for="item in commentList" :key="item.commentId">
            <view class="comment-main">
              <view class="comment-avatar-wrapper">
                <image v-if="item.userAvatar" class="comment-avatar" :src="getImageUrl(item.userAvatar)" mode="aspectFill"></image>
                <view v-else class="comment-avatar-placeholder">
                  <uni-icons type="person-filled" size="14" color="#FFFFFF"></uni-icons>
                </view>
              </view>

              <view class="comment-content">
                <view class="comment-user">{{ item.userName || '匿名用户' }}</view>
                <view class="comment-text">{{ item.content }}</view>
                
                <view class="comment-footer">
                  <text class="comment-time">{{ formatTime(item.createTime) }}</text>
                  <view class="comment-interactions">
                    <view class="interaction-btn" @tap.stop="replyComment(item)">
                      <text>回复</text>
                    </view>
                    <view class="interaction-btn" @tap.stop="toggleCommentLike(item)">
                      <uni-icons :type="item.isLiked ? 'heart-filled' : 'heart'" size="14" :color="item.isLiked ? '#FF3B30' : '#86868B'"></uni-icons>
                      <text :style="{ color: item.isLiked ? '#FF3B30' : '#86868B' }">{{ item.likeCount > 0 ? item.likeCount : '赞' }}</text>
                    </view>
                  </view>
                </view>

                <view v-if="item.replies && item.replies.length > 0" class="reply-container">
                  <view class="reply-item" v-for="reply in item.replies" :key="reply.commentId">
                    <view class="reply-header">
                      <text class="reply-name">{{ reply.userName || '匿名用户' }}</text>
                      <text v-if="reply.replyToUserName" class="reply-to-text">回复</text>
                      <text v-if="reply.replyToUserName" class="reply-name">{{ reply.replyToUserName }}</text>
                    </view>
                    <text class="reply-content-text">{{ reply.content }}</text>
                    
                    <view class="reply-footer">
                      <text class="reply-time">{{ formatTime(reply.createTime) }}</text>
                      <view class="comment-interactions">
                        <view class="interaction-btn" @tap.stop="replyComment(reply, item)">
                          <text>回复</text>
                        </view>
                        <view class="interaction-btn" @tap.stop="toggleCommentLike(reply)">
                          <uni-icons :type="reply.isLiked ? 'heart-filled' : 'heart'" size="12" :color="reply.isLiked ? '#FF3B30' : '#86868B'"></uni-icons>
                          <text :style="{ color: reply.isLiked ? '#FF3B30' : '#86868B' }">{{ reply.likeCount > 0 ? reply.likeCount : '赞' }}</text>
                        </view>
                      </view>
                    </view>
                  </view>
                </view>
              </view>
            </view>
          </view>
        </view>

        <view v-if="commentList.length === 0 && !loading" class="empty-state-glass">
          <uni-icons type="chatbubble" size="48" color="#C7C7CC"></uni-icons>
          <text class="empty-text">留下你的第一个足迹吧</text>
        </view>
      </view>

      <view class="bottom-spacer" :style="{ height: (safeAreaBottom + 160) + 'px' }"></view>
    </scroll-view>

    <view class="capsule-wrapper" :style="{ paddingBottom: `${safeAreaBottom + 12}px` }">
      <view class="capsule-dock">
        
        <view class="cancel-reply-btn" v-if="replyTo" @tap="cancelReply">
          <uni-icons type="closeempty" size="14" color="#86868B"></uni-icons>
        </view>

        <view class="input-wrapper">
          <input 
            class="ios-input" 
            v-model="commentContent" 
            :placeholder="commentPlaceholder"
            placeholder-class="ios-placeholder"
            :cursor-spacing="20"
            confirm-type="send" 
            @confirm="handleComment" 
          />
        </view>

        <view class="send-btn" :class="{ 'btn-active': commentContent.trim() }" @tap="handleComment">
          <uni-icons type="arrow-up" size="20" :color="commentContent.trim() ? '#FFFFFF' : '#A1A1A6'"></uni-icons>
        </view>
      </view>
    </view>

  </view>
</template>

<script>
import {
  createComment, getPostDetail, likeComment, likePost, listComments, unlikeComment, unlikePost
} from '@/api/community/post'
import config from '@/config'

export default {
  data() {
    return {
      statusBarHeight: 0,
      safeAreaBottom: 0,
      loading: false,
      postId: null,
      postDetail: null,
      commentList: [],
      commentContent: '',
      commentPlaceholder: '留下你的感悟...',
      replyTo: null, 
      parentComment: null 
    }
  },
  onLoad(options) {
    const systemInfo = uni.getSystemInfoSync()
    this.statusBarHeight = systemInfo.statusBarHeight || 0
    this.safeAreaBottom = systemInfo.safeAreaInsets?.bottom || 0

    if (options.postId) {
      this.postId = options.postId
      this.loadPostDetail()
      this.loadComments()
    }
  },
  methods: {
    goBack() { uni.navigateBack() },

    loadPostDetail() {
      this.loading = true
      getPostDetail(this.postId).then(res => {
        this.loading = false
        if (res.code === 200 && res.data) {
          this.postDetail = res.data
          if (this.postDetail.images && typeof this.postDetail.images === 'string') {
            this.postDetail.images = this.postDetail.images.split(',').filter(img => img.trim())
          } else if (!this.postDetail.images) {
            this.postDetail.images = []
          }
        } else {
          uni.showToast({ title: res.msg || '加载失败', icon: 'none' })
        }
      }).catch(err => {
        this.loading = false
        uni.showToast({ title: '加载失败，请重试', icon: 'none' })
      })
    },

    loadComments() {
      listComments(this.postId, { pageNum: 1, pageSize: 100 }).then(res => {
        if (res.code === 200) {
          this.commentList = this.buildCommentTree(res.data || [])
        }
      }).catch(err => {
        uni.showToast({ title: '加载评论失败', icon: 'none' })
      })
    },

    buildCommentTree(comments) {
      const commentMap = {}
      const rootComments = []
      comments.forEach(comment => {
        comment.replies = []
        commentMap[comment.commentId] = comment
      })
      comments.forEach(comment => {
        if (comment.parentId && commentMap[comment.parentId]) {
          commentMap[comment.parentId].replies.push(comment)
        } else {
          rootComments.push(comment)
        }
      })
      return rootComments
    },

    toggleLike() {
      uni.vibrateShort() 
      const action = this.postDetail.isLiked ? unlikePost : likePost
      action(this.postId).then(res => {
        if (res.code === 200) {
          this.postDetail.isLiked = !this.postDetail.isLiked
          this.postDetail.likeCount = this.postDetail.isLiked ? this.postDetail.likeCount + 1 : this.postDetail.likeCount - 1
        }
      })
    },

    toggleCommentLike(comment) {
      uni.vibrateShort()
      const action = comment.isLiked ? unlikeComment : likeComment
      action(comment.commentId).then(res => {
        if (res.code === 200) {
          comment.isLiked = !comment.isLiked
          comment.likeCount = comment.isLiked ? (comment.likeCount || 0) + 1 : (comment.likeCount || 1) - 1
        }
      })
    },

    replyComment(comment, parentComment = null) {
      this.replyTo = comment
      this.parentComment = parentComment || comment
      this.commentPlaceholder = `回复 @${comment.userName || '匿名用户'}`
    },

    // UX提升：增加主动取消回复的方法
    cancelReply() {
      this.replyTo = null
      this.parentComment = null
      this.commentPlaceholder = '留下你的感悟...'
      uni.vibrateShort()
    },

    handleComment() {
      if (!this.commentContent.trim()) {
        uni.showToast({ title: '请输入内容', icon: 'none' })
        return
      }

      const data = {
        postId: this.postId,
        content: this.commentContent.trim(),
        parentId: this.replyTo ? this.parentComment.commentId : null,
        replyToUserId: this.replyTo ? this.replyTo.userId : null
      }

      createComment(data).then(res => {
        if (res.code === 200) {
          uni.showToast({ title: '发送成功', icon: 'success' })
          this.commentContent = ''
          this.cancelReply() // 清空回复状态
          this.loadComments()
          if (this.postDetail) this.postDetail.commentCount = (this.postDetail.commentCount || 0) + 1
        }
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
      const minute = 60 * 1000; const hour = 60 * minute; const day = 24 * hour;
      if (diff < minute) return '刚刚'
      else if (diff < hour) return Math.floor(diff / minute) + '分钟前'
      else if (diff < day) return Math.floor(diff / hour) + '小时前'
      else if (diff < 7 * day) return Math.floor(diff / day) + '天前'
      else return `${postTime.getFullYear()}-${(postTime.getMonth() + 1).toString().padStart(2, '0')}-${postTime.getDate().toString().padStart(2, '0')}`
    },

    formatCount(count) {
      if (!count || count === 0) return '0'
      if (count >= 10000) return (count / 10000).toFixed(1) + 'w'
      if (count >= 1000) return (count / 1000).toFixed(1) + 'k'
      return count.toString()
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

.detail-page {
  min-height: 100vh;
  position: relative;
  background-color: #F5F5F7;
  font-family: -apple-system, BlinkMacSystemFont, "SF Pro Text", "Helvetica Neue", Arial, sans-serif;
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
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.03);
  border-radius: 40rpx;
}

/* ==================== 顶部导航栏 (Sticky) ==================== */
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

.navbar-title {
  font-size: 32rpx; font-weight: 600; color: $ios-text-primary;
}

/* ==================== 内容滚动区 ==================== */
.content-scroll {
  height: 100vh;
  position: relative;
  z-index: 1;
}

.post-master-card {
  margin: 32rpx;
  padding: 40rpx 32rpx;
  background: rgba(255, 255, 255, 0.75);
}

/* --- 帖子头部 --- */
.post-header { margin-bottom: 32rpx; }
.user-info { display: flex; align-items: center; gap: 20rpx; }

.avatar-wrapper {
  width: 88rpx; height: 88rpx; border-radius: 50%;
  background: #FFF; box-shadow: 0 4rpx 12rpx rgba(0,0,0,0.06);
  overflow: hidden;
}
.user-avatar { width: 100%; height: 100%; display: block; }
.user-avatar-placeholder {
  width: 100%; height: 100%;
  background: rgba(44, 181, 160, 0.1);
  display: flex; align-items: center; justify-content: center;
}

.user-detail { display: flex; flex-direction: column; justify-content: center; }
.user-name { font-size: 32rpx; font-weight: 600; color: $ios-text-primary; margin-bottom: 4rpx; }
.post-time { font-size: 24rpx; color: $ios-text-secondary; }

/* --- 帖子内容区 --- */
.post-content { margin-bottom: 32rpx; }
.post-title { font-size: 40rpx; font-weight: 700; color: $ios-text-primary; line-height: 1.4; margin-bottom: 16rpx; display: block; }
.post-text { font-size: 32rpx; color: #3A3A3C; line-height: 1.65; white-space: pre-wrap; word-break: break-all; display: block; margin-bottom: 32rpx; text-align: justify; }

/* --- 图片网格排版 --- */
.post-images { margin-bottom: 16rpx; }
.post-image { width: 100%; height: 100%; border-radius: 16rpx; background: rgba(0,0,0,0.03); display: block; }
.images-layout-1 .single-img { max-height: 500rpx; border-radius: 24rpx; object-fit: cover; }
.images-layout-2 { display: grid; grid-template-columns: repeat(2, 1fr); gap: 12rpx; height: 300rpx; }
.images-layout-3 { display: grid; grid-template-columns: repeat(3, 1fr); gap: 12rpx; height: 220rpx; }
.images-layout-4 { display: grid; grid-template-columns: repeat(2, 1fr); gap: 12rpx; height: 360rpx; }
.images-layout-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 12rpx; grid-auto-rows: 220rpx; }

/* --- 数据交互栏 --- */
.post-actions-bar {
  display: flex; align-items: center; gap: 40rpx;
  padding-top: 24rpx; border-top: 1px solid rgba(0,0,0,0.05);
}
.action-item {
  display: flex; align-items: center; gap: 10rpx;
  transition: transform 0.2s;
  &:active { transform: scale(0.85); }
}
.action-text { font-size: 28rpx; font-weight: 500; }
.text-light { color: #C7C7CC; }
.view-count { margin-left: auto; pointer-events: none; }
.is-active { animation: popHeart 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275); }
@keyframes popHeart { 0% { transform: scale(1); } 50% { transform: scale(1.3); } 100% { transform: scale(1); } }


/* ==================== 2. 灵动评论区 ==================== */
.comment-section { padding: 0 32rpx; }

.section-title-box { padding: 24rpx 12rpx; margin-bottom: 16rpx; .title-text { font-size: 32rpx; font-weight: 600; color: $ios-text-primary; } }
.comment-item { margin-bottom: 48rpx; }
.comment-main { display: flex; gap: 24rpx; }

.comment-avatar-wrapper { width: 72rpx; height: 72rpx; border-radius: 50%; background: #FFF; box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.05); flex-shrink: 0; overflow: hidden; }
.comment-avatar { width: 100%; height: 100%; }
.comment-avatar-placeholder { width: 100%; height: 100%; background: rgba(44, 181, 160, 0.1); display: flex; align-items: center; justify-content: center; }

.comment-content { flex: 1; border-bottom: 1px solid rgba(0,0,0,0.03); padding-bottom: 32rpx;}
.comment-user { font-size: 28rpx; font-weight: 600; color: $ios-text-secondary; margin-bottom: 8rpx; }
.comment-text { font-size: 30rpx; color: $ios-text-primary; line-height: 1.5; margin-bottom: 16rpx; word-break: break-all; }
.comment-footer { display: flex; justify-content: space-between; align-items: center; }
.comment-time { font-size: 24rpx; color: #A1A1A6; }

.comment-interactions { display: flex; gap: 32rpx; }
.interaction-btn { display: flex; align-items: center; gap: 6rpx; text { font-size: 24rpx; color: $ios-text-secondary; font-weight: 500; } &:active { opacity: 0.6; } }

/* 二级评论 */
.reply-container {
  margin-top: 24rpx; background: rgba(44, 181, 160, 0.04);
  border-left: 4rpx solid rgba(44, 181, 160, 0.2);
  border-radius: 0 16rpx 16rpx 0; padding: 20rpx 24rpx;
  display: flex; flex-direction: column; gap: 24rpx;
}
.reply-header { margin-bottom: 6rpx; font-size: 26rpx;}
.reply-name { font-weight: 600; color: $ios-text-primary; }
.reply-to-text { margin: 0 8rpx; color: $ios-text-secondary; }
.reply-content-text { font-size: 28rpx; color: #3A3A3C; line-height: 1.5; margin-bottom: 12rpx; }
.reply-footer { display: flex; justify-content: space-between; align-items: center; }
.reply-time { font-size: 22rpx; color: #A1A1A6; }

/* 空状态 */
.empty-state-glass {
  margin: 60rpx 0; padding: 80rpx 0;
  display: flex; flex-direction: column; align-items: center; gap: 16rpx;
  .empty-text { font-size: 28rpx; color: $ios-text-secondary; }
}


/* ==================== 3. 悬浮胶囊输入舱 (Floating Capsule Dock) ==================== */
/* 外部包装器，不阻挡底层事件，仅负责定位 */
.capsule-wrapper {
  position: fixed;
  bottom: 0; left: 0; right: 0;
  z-index: 999;
  padding: 0 32rpx; /* 屏幕两侧留白，形成悬浮感 */
  pointer-events: none; /* 点击穿透，方便列表滑动 */
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
}

/* 核心悬浮胶囊 */
.capsule-dock {
  pointer-events: auto; /* 恢复本身交互 */
  background: rgba(250, 250, 252, 0.85); /* 极浅透明灰底 */
  backdrop-filter: saturate(200%) blur(30px);
  -webkit-backdrop-filter: saturate(200%) blur(30px);
  border: 1px solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 16rpx 40rpx rgba(0, 0, 0, 0.08); /* 悬浮阴影 */
  border-radius: 60rpx; /* 胶囊圆角 */
  padding: 12rpx 16rpx 12rpx 32rpx; /* 内边距，左侧多留点给文字，右侧少点给按钮 */
  display: flex;
  align-items: center;
  transition: all 0.3s cubic-bezier(0.2, 0.8, 0.2, 1);
  margin-bottom: 24rpx; /* 距离底部的悬浮距离，与 paddingBottom 结合 */
}

/* 回复状态的取消小角标 */
.cancel-reply-btn {
  width: 48rpx; height: 48rpx; border-radius: 24rpx;
  background: rgba(0,0,0,0.06);
  display: flex; align-items: center; justify-content: center;
  margin-right: 16rpx; flex-shrink: 0;
  transition: all 0.2s;
  &:active { transform: scale(0.85); background: rgba(0,0,0,0.1); }
}

.input-wrapper {
  flex: 1; height: 68rpx; display: flex; align-items: center;
}

.ios-input {
  width: 100%; font-size: 30rpx; color: $ios-text-primary;
}

.ios-placeholder { color: #A1A1A6; }

.send-btn {
  width: 68rpx; height: 68rpx; border-radius: 34rpx; flex-shrink: 0;
  display: flex; align-items: center; justify-content: center;
  background: rgba(0, 0, 0, 0.04);
  margin-left: 16rpx;
  transition: all 0.3s ease;
  
  &.btn-active {
    background: $theme-cyan;
    box-shadow: 0 6rpx 16rpx rgba(44, 181, 160, 0.3);
    &:active { transform: scale(0.9); }
  }
}
</style>