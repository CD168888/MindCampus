<template>
  <view class="detail-page">
    <!-- 自定义导航栏 -->
    <view class="custom-navbar" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="navbar-content">
        <view class="navbar-left" @tap="goBack">
          <text class="back-icon">←</text>
        </view>
        <view class="navbar-title">帖子详情</view>
        <view class="navbar-right"></view>
      </view>
    </view>

    <!-- 内容区域 -->
    <scroll-view class="content-scroll" scroll-y :enable-back-to-top="true">
      <!-- 帖子详情 -->
      <view v-if="postDetail" class="post-detail">
        <!-- 用户信息 -->
        <view class="post-header">
          <view class="user-info">
            <image v-if="postDetail.userAvatar" class="user-avatar" :src="getImageUrl(postDetail.userAvatar)"
              mode="aspectFill"></image>
            <view v-else class="user-avatar-placeholder">
              <uni-icons type="person-filled" size="24" color="#FFFFFF"></uni-icons>
            </view>
            <view class="user-detail">
              <view class="user-name">{{ postDetail.userName || '匿名用户' }}</view>
              <view class="post-time">{{ formatTime(postDetail.createTime) }}</view>
            </view>
          </view>
        </view>

        <!-- 帖子内容 -->
        <view class="post-content">
          <view class="post-title">{{ postDetail.title }}</view>
          <view class="post-text">{{ postDetail.content }}</view>

          <!-- 图片列表 -->
          <view v-if="postDetail.images && postDetail.images.length > 0" class="post-images"
            :class="'images-' + Math.min(postDetail.images.length, 3)">
            <image v-for="(img, index) in postDetail.images" :key="index" class="post-image"
              :src="getImageUrl(img)" mode="aspectFill" @tap="previewImage(postDetail.images, index)"></image>
          </view>
        </view>

        <!-- 帖子操作栏 -->
        <view class="post-actions">
          <view class="action-item" @tap="toggleLike">
            <uni-icons :type="postDetail.isLiked ? 'heart-filled' : 'heart'" size="20"
              :color="postDetail.isLiked ? '#FF3A3A' : '#999999'"></uni-icons>
            <text class="action-text" :class="{ 'liked': postDetail.isLiked }">{{
              formatCount(postDetail.likeCount) }}</text>
          </view>
          <view class="action-item">
            <uni-icons type="chat" size="20" color="#999999"></uni-icons>
            <text class="action-text">{{ formatCount(postDetail.commentCount) }}</text>
          </view>
          <view class="action-item">
            <uni-icons type="eye" size="20" color="#999999"></uni-icons>
            <text class="action-text">{{ formatCount(postDetail.viewCount) }}</text>
          </view>
        </view>
      </view>

      <!-- 评论区 -->
      <view class="comment-section">
        <view class="section-header">
          <view class="section-title">全部评论 ({{ commentList.length }})</view>
        </view>

        <!-- 评论列表 -->
        <view class="comment-list">
          <view class="comment-item" v-for="item in commentList" :key="item.commentId">
            <!-- 一级评论 -->
            <view class="comment-main">
              <image v-if="item.userAvatar" class="comment-avatar" :src="getImageUrl(item.userAvatar)"
                mode="aspectFill"></image>
              <view v-else class="comment-avatar-placeholder">
                <uni-icons type="person-filled" size="16" color="#FFFFFF"></uni-icons>
              </view>

              <view class="comment-content">
                <view class="comment-user">{{ item.userName || '匿名用户' }}</view>
                <view class="comment-text">{{ item.content }}</view>
                <view class="comment-footer">
                  <view class="comment-time">{{ formatTime(item.createTime) }}</view>
                  <view class="comment-actions">
                    <view class="comment-action" @tap.stop="toggleCommentLike(item)">
                      <uni-icons :type="item.isLiked ? 'heart-filled' : 'heart'" size="14"
                        :color="item.isLiked ? '#FF3A3A' : '#CCCCCC'"></uni-icons>
                      <text class="action-count" :class="{ 'liked': item.isLiked }">{{
                        item.likeCount > 0 ? item.likeCount : '' }}</text>
                    </view>
                    <view class="comment-action" @tap.stop="replyComment(item)">
                      <uni-icons type="chatbubble" size="14" color="#CCCCCC"></uni-icons>
                      <text class="action-count">回复</text>
                    </view>
                  </view>
                </view>

                <!-- 二级评论（回复） -->
                <view v-if="item.replies && item.replies.length > 0" class="reply-list">
                  <view class="reply-item" v-for="reply in item.replies" :key="reply.commentId">
                    <view class="reply-user">
                      <text class="reply-name">{{ reply.userName || '匿名用户' }}</text>
                      <text v-if="reply.replyToUserName" class="reply-to">回复</text>
                      <text v-if="reply.replyToUserName" class="reply-name">{{ reply.replyToUserName }}</text>
                    </view>
                    <view class="reply-text">{{ reply.content }}</view>
                    <view class="reply-footer">
                      <view class="reply-time">{{ formatTime(reply.createTime) }}</view>
                      <view class="reply-actions">
                        <view class="reply-action" @tap.stop="toggleCommentLike(reply)">
                          <uni-icons :type="reply.isLiked ? 'heart-filled' : 'heart'" size="12"
                            :color="reply.isLiked ? '#FF3A3A' : '#CCCCCC'"></uni-icons>
                          <text class="action-count" :class="{ 'liked': reply.isLiked }">{{
                            reply.likeCount > 0 ? reply.likeCount : '' }}</text>
                        </view>
                        <view class="reply-action" @tap.stop="replyComment(reply, item)">
                          <uni-icons type="chatbubble" size="12" color="#CCCCCC"></uni-icons>
                          <text class="action-count">回复</text>
                        </view>
                      </view>
                    </view>
                  </view>
                </view>
              </view>
            </view>
          </view>
        </view>

        <!-- 空状态 -->
        <view v-if="commentList.length === 0 && !loading" class="empty-comment">
          <view class="empty-icon">
            <uni-icons type="chatbubble" size="60" color="#CCCCCC"></uni-icons>
          </view>
          <text class="empty-text">暂无评论，快来抢沙发吧~</text>
        </view>
      </view>
    </scroll-view>

    <!-- 底部评论输入框 -->
    <view class="comment-input-bar" :style="{ paddingBottom: safeAreaBottom + 'px' }">
      <view class="input-wrapper">
        <input class="comment-input" v-model="commentContent" :placeholder="commentPlaceholder"
          @focus="onInputFocus" @blur="onInputBlur" confirm-type="send" @confirm="handleComment" />
        <view class="send-btn" @tap="handleComment" :class="{ 'active': commentContent.trim() }">
          <uni-icons type="paperplane" size="20" color="#FFFFFF"></uni-icons>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import {
  createComment,
  getPostDetail,
  likeComment,
  likePost,
  listComments,
  unlikeComment,
  unlikePost
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
      commentPlaceholder: '说点什么...',
      replyTo: null, // 回复的评论对象
      parentComment: null // 父评论对象（用于二级回复）
    }
  },
  onLoad(options) {
    // 获取状态栏高度和安全区域
    const systemInfo = uni.getSystemInfoSync()
    this.statusBarHeight = systemInfo.statusBarHeight || 0
    this.safeAreaBottom = systemInfo.safeAreaInsets?.bottom || 0

    // 获取帖子ID
    if (options.postId) {
      this.postId = options.postId
      this.loadPostDetail()
      this.loadComments()
    }
  },
  methods: {
    // 返回
    goBack() {
      uni.navigateBack()
    },

    // 加载帖子详情
    loadPostDetail() {
      this.loading = true

      getPostDetail(this.postId).then(res => {
        this.loading = false

        if (res.code === 200 && res.data) {
          this.postDetail = res.data
          // 处理图片数组
          if (this.postDetail.images && typeof this.postDetail.images === 'string') {
            this.postDetail.images = this.postDetail.images.split(',').filter(img => img)
          }
        } else {
          this.$modal.showToast(res.msg || '加载失败')
        }
      }).catch(err => {
        this.loading = false
        console.error('加载帖子详情失败:', err)
        this.$modal.showToast('加载失败，请重试')
      })
    },

    // 加载评论列表
    loadComments() {
      const params = {
        pageNum: 1,
        pageSize: 100
      }

      listComments(this.postId, params).then(res => {
        if (res.code === 200) {
          // 构建评论树结构
          const comments = res.rows || []
          this.commentList = this.buildCommentTree(comments)
        }
      }).catch(err => {
        console.error('加载评论列表失败:', err)
      })
    },

    // 构建评论树结构
    buildCommentTree(comments) {
      const commentMap = {}
      const rootComments = []

      // 第一遍：建立映射
      comments.forEach(comment => {
        comment.replies = []
        commentMap[comment.commentId] = comment
      })

      // 第二遍：构建树结构
      comments.forEach(comment => {
        if (comment.parentId && commentMap[comment.parentId]) {
          // 二级评论
          commentMap[comment.parentId].replies.push(comment)
        } else {
          // 一级评论
          rootComments.push(comment)
        }
      })

      return rootComments
    },

    // 切换点赞
    toggleLike() {
      const action = this.postDetail.isLiked ? unlikePost : likePost

      action(this.postId).then(res => {
        if (res.code === 200) {
          this.postDetail.isLiked = !this.postDetail.isLiked
          this.postDetail.likeCount = this.postDetail.isLiked ? this.postDetail.likeCount + 1 : this.postDetail.likeCount - 1
        } else {
          this.$modal.showToast(res.msg || '操作失败')
        }
      }).catch(err => {
        console.error('点赞操作失败:', err)
        this.$modal.showToast('操作失败，请重试')
      })
    },

    // 切换评论点赞
    toggleCommentLike(comment) {
      const action = comment.isLiked ? unlikeComment : likeComment

      action(comment.commentId).then(res => {
        if (res.code === 200) {
          comment.isLiked = !comment.isLiked
          comment.likeCount = comment.isLiked ? (comment.likeCount || 0) + 1 : (comment.likeCount || 1) - 1
        } else {
          this.$modal.showToast(res.msg || '操作失败')
        }
      }).catch(err => {
        console.error('点赞操作失败:', err)
        this.$modal.showToast('操作失败，请重试')
      })
    },

    // 回复评论
    replyComment(comment, parentComment = null) {
      this.replyTo = comment
      this.parentComment = parentComment || comment
      this.commentPlaceholder = `回复 ${comment.userName || '匿名用户'}:`
    },

    // 输入框聚焦
    onInputFocus() {
      // 可以在这里处理聚焦逻辑
    },

    // 输入框失焦
    onInputBlur() {
      // 延迟清除回复对象，避免点击发送按钮时失效
      setTimeout(() => {
        if (!this.commentContent.trim()) {
          this.replyTo = null
          this.parentComment = null
          this.commentPlaceholder = '说点什么...'
        }
      }, 200)
    },

    // 发表评论
    handleComment() {
      if (!this.commentContent.trim()) {
        this.$modal.showToast('请输入评论内容')
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
          this.$modal.showToast('评论成功')
          this.commentContent = ''
          this.replyTo = null
          this.parentComment = null
          this.commentPlaceholder = '说点什么...'

          // 刷新评论列表
          this.loadComments()

          // 更新帖子评论数
          if (this.postDetail) {
            this.postDetail.commentCount = (this.postDetail.commentCount || 0) + 1
          }
        } else {
          this.$modal.showToast(res.msg || '评论失败')
        }
      }).catch(err => {
        console.error('发表评论失败:', err)
        this.$modal.showToast('评论失败，请重试')
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

.detail-page {
  min-height: 100vh;
  position: relative;
  background: $gradient-soft;
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

/* 内容区域 */
.content-scroll {
  height: calc(100vh - 88rpx - 100rpx - env(safe-area-inset-top));
  margin-top: calc(88rpx + env(safe-area-inset-top));
  padding: 0 $spacing-lg $spacing-xl;
}

/* 帖子详情 */
.post-detail {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10rpx);
  border-radius: $radius-lg;
  padding: $spacing-lg;
  margin-bottom: $spacing-base;
  box-shadow:
    0 4rpx 16rpx rgba(167, 243, 208, 0.08),
    0 2rpx 8rpx rgba(196, 181, 253, 0.08);
  border: 1rpx solid rgba(255, 255, 255, 0.6);
}

/* 帖子头部 */
.post-header {
  margin-bottom: $spacing-lg;
}

.user-info {
  display: flex;
  align-items: center;
}

.user-avatar {
  width: 88rpx;
  height: 88rpx;
  border-radius: 50%;
  margin-right: $spacing-md;
  background: $bg-gray-100;
}

.user-avatar-placeholder {
  width: 88rpx;
  height: 88rpx;
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
  font-size: $font-base;
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
  margin-bottom: $spacing-lg;
}

.post-title {
  font-size: $font-lg;
  font-weight: $font-bold;
  color: $text-primary;
  margin-bottom: $spacing-md;
  line-height: $line-height-tight;
  font-family: $font-family-base;
}

.post-text {
  font-size: $font-sm;
  color: $text-secondary;
  line-height: $line-height-normal;
  margin-bottom: $spacing-md;
  white-space: pre-wrap;
  word-break: break-all;
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
      height: 500rpx;
    }
  }

  &.images-2 {
    grid-template-columns: repeat(2, 1fr);

    .post-image {
      height: 300rpx;
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
  font-size: $font-sm;
  color: $text-tertiary;
  font-family: $font-family-base;

  &.liked {
    color: #FF3A3A;
  }
}

/* 评论区 */
.comment-section {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10rpx);
  border-radius: $radius-lg;
  padding: $spacing-lg;
  margin-bottom: $spacing-base;
  box-shadow:
    0 4rpx 16rpx rgba(167, 243, 208, 0.08),
    0 2rpx 8rpx rgba(196, 181, 253, 0.08);
  border: 1rpx solid rgba(255, 255, 255, 0.6);
}

.section-header {
  margin-bottom: $spacing-lg;
  padding-bottom: $spacing-md;
  border-bottom: 1rpx solid $border-light;
}

.section-title {
  font-size: $font-base;
  font-weight: $font-semibold;
  color: $text-primary;
  font-family: $font-family-base;
}

/* 评论列表 */
.comment-list {
  .comment-item {
    margin-bottom: $spacing-lg;

    &:last-child {
      margin-bottom: 0;
    }
  }
}

/* 一级评论 */
.comment-main {
  display: flex;
  gap: $spacing-md;
}

.comment-avatar {
  width: 64rpx;
  height: 64rpx;
  border-radius: 50%;
  background: $bg-gray-100;
  flex-shrink: 0;
}

.comment-avatar-placeholder {
  width: 64rpx;
  height: 64rpx;
  border-radius: 50%;
  background: $gradient-primary;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.comment-content {
  flex: 1;
  min-width: 0;
}

.comment-user {
  font-size: $font-sm;
  font-weight: $font-semibold;
  color: $text-primary;
  margin-bottom: $spacing-xs;
  font-family: $font-family-base;
}

.comment-text {
  font-size: $font-sm;
  color: $text-secondary;
  line-height: $line-height-normal;
  margin-bottom: $spacing-xs;
  white-space: pre-wrap;
  word-break: break-all;
  font-family: $font-family-base;
}

.comment-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.comment-time {
  font-size: $font-xs;
  color: $text-quaternary;
  font-family: $font-family-base;
}

.comment-actions {
  display: flex;
  align-items: center;
  gap: $spacing-lg;
}

.comment-action {
  display: flex;
  align-items: center;
  gap: 4rpx;
  transition: all 0.2s ease;

  &:active {
    transform: scale(0.95);
  }
}

.action-count {
  font-size: $font-xs;
  color: $text-quaternary;
  font-family: $font-family-base;

  &.liked {
    color: #FF3A3A;
  }
}

/* 二级评论（回复） */
.reply-list {
  margin-top: $spacing-md;
  padding: $spacing-md;
  background: rgba(0, 0, 0, 0.02);
  border-radius: $radius-base;
}

.reply-item {
  margin-bottom: $spacing-md;

  &:last-child {
    margin-bottom: 0;
  }
}

.reply-user {
  font-size: $font-xs;
  margin-bottom: $spacing-xs;
  font-family: $font-family-base;
}

.reply-name {
  font-weight: $font-semibold;
  color: $text-primary;
}

.reply-to {
  margin: 0 $spacing-xs;
  color: $text-tertiary;
}

.reply-text {
  font-size: $font-xs;
  color: $text-secondary;
  line-height: $line-height-normal;
  margin-bottom: $spacing-xs;
  white-space: pre-wrap;
  word-break: break-all;
  font-family: $font-family-base;
}

.reply-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.reply-time {
  font-size: 20rpx;
  color: $text-quaternary;
  font-family: $font-family-base;
}

.reply-actions {
  display: flex;
  align-items: center;
  gap: $spacing-md;
}

.reply-action {
  display: flex;
  align-items: center;
  gap: 4rpx;
  transition: all 0.2s ease;

  &:active {
    transform: scale(0.95);
  }
}

/* 空状态 */
.empty-comment {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: $spacing-3xl 0;
}

.empty-icon {
  margin-bottom: $spacing-lg;
  filter: grayscale(30%);
}

.empty-text {
  font-size: $font-sm;
  color: $text-tertiary;
  font-family: $font-family-base;
}

/* 底部评论输入框 */
.comment-input-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20rpx);
  padding: $spacing-md $spacing-lg;
  box-shadow: 0 -2rpx 16rpx rgba(0, 0, 0, 0.05);
  z-index: 999;
}

.input-wrapper {
  display: flex;
  align-items: center;
  gap: $spacing-md;
}

.comment-input {
  flex: 1;
  height: 72rpx;
  padding: 0 $spacing-lg;
  background: $bg-gray-100;
  border-radius: $radius-full;
  font-size: $font-sm;
  color: $text-primary;
  font-family: $font-family-base;
}

.send-btn {
  width: 72rpx;
  height: 72rpx;
  border-radius: 50%;
  background: $bg-gray-200;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;

  &.active {
    background: $gradient-primary;
    box-shadow: 0 4rpx 12rpx rgba(22, 119, 255, 0.3);

    &:active {
      transform: scale(0.95);
    }
  }
}
</style>

