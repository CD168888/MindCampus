<template>
  <view class="ai-chat-container">
    <view class="ambient-background"></view>

    <view class="glass-header-fixed" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="navbar-content">
        <!-- 修改：点击左上角按钮跳转到首页 -->
        <view class="navbar-left" @tap="goToIndex">
          <view class="nav-icon-glass">
            <uni-icons type="left" size="22" color="#1D1D1F"></uni-icons>
          </view>
        </view>
        <view class="navbar-title">AI 治愈助手</view>
        <view class="navbar-right" @tap="toggleHistoryDrawer">
          <view class="nav-icon-glass">
            <uni-icons type="list" size="22" color="#1D1D1F"></uni-icons>
          </view>
        </view>
      </view>
    </view>

    <scroll-view class="chat-area" scroll-y :scroll-top="scrollTop" :scroll-with-animation="true"
      @scrolltoupper="loadMoreMessages" upper-threshold="100" ref="chatScrollView">
      
      <view class="header-spacer" :style="{ height: (statusBarHeight + 44) + 'px' }"></view>

      <view class="loading-more" v-if="isLoadingMore">
        <uni-icons type="spinner-cycle" size="16" color="#86868B" class="spin-icon"></uni-icons>
        <text class="loading-text">加载更多记忆...</text>
      </view>

      <view class="messages-wrapper">
        <view v-for="(msg, index) in currentMessages" :key="index"
          :class="['message-item', msg.role === 'user' ? 'user-message' : 'ai-message']">
          
          <view class="message-time-center" v-if="index === 0 || shouldShowTime(msg.timestamp, currentMessages[index-1].timestamp)">
            <text>{{ formatTime(msg.timestamp) }}</text>
          </view>

          <view class="message-container">
            <image class="avatar" :src="msg.role === 'user' ? userAvatar : aiAvatar" mode="aspectFill" />
            
            <view class="message-content-box">
              <text class="sender-name">{{ msg.role === 'user' ? '我' : 'AI 助手' }}</text>
              
              <view class="message-bubble">
                <ua-markdown v-if="msg.role === 'assistant' && msg.content" :source="msg.content" :showLine="false" />
                <text v-else-if="msg.role === 'user' && msg.content" class="user-text">{{ msg.content }}</text>
                
                <view v-else-if="msg.role === 'assistant' && msg.content === ''" class="typing-indicator">
                  <view class="dot"></view>
                  <view class="dot"></view>
                  <view class="dot"></view>
                </view>
              </view>

              <view class="attachments" v-if="msg.attachments && msg.attachments.length > 0">
                <view v-for="(file, fileIndex) in msg.attachments" :key="fileIndex" class="attachment-pill">
                  <uni-icons :type="getFileIcon(file.name)" size="16" color="#2CB5A0"></uni-icons>
                  <text class="attachment-name">{{ getFileName(file.name) }}</text>
                </view>
              </view>
            </view>
          </view>
        </view>
      </view>

      <view class="bottom-spacer" :style="{ height: (textareaHeight + safeAreaBottom + 80) + 'px' }"></view>
    </scroll-view>

    <view class="capsule-wrapper" :style="{ paddingBottom: `${safeAreaBottom + 12}px` }">
      
      <view class="attachment-preview" v-if="selectedFiles.length > 0">
        <scroll-view scroll-x class="attachment-scroll">
          <view v-for="(file, index) in selectedFiles" :key="index" class="preview-item">
            <uni-icons :type="getFileIcon(file.name)" size="16" color="#86868B"></uni-icons>
            <text class="preview-name">{{ getFileName(file.name) }}</text>
            <view class="remove-btn" @click="removeFile(index)">
              <uni-icons type="closeempty" size="12" color="#FFF"></uni-icons>
            </view>
          </view>
        </scroll-view>
      </view>

      <view class="capsule-dock">
        <view class="action-icon-btn" @click="handleUpload">
          <uni-icons type="plus" size="24" color="#86868B"></uni-icons>
        </view>

        <view class="input-wrapper">
          <textarea class="ios-textarea" v-model="userInput" placeholder="分享你的想法..." :disable-default-padding="true"
            :cursor-spacing="20" confirm-type="send" :maxlength="-1" @confirm="sendMessage" :auto-height="true"
            :style="{ height: `${textareaHeight}px` }" @input="handleAdjustHeight" @focus="handleInputFocus"
            @blur="handleInputBlur" :show-confirm-bar="false" :hold-keyboard="true" :adjust-position="false"></textarea>
        </view>

        <view v-if="!isStreaming" class="send-btn" :class="{ 'btn-active': canSend }" @click="sendMessage">
          <uni-icons type="arrow-up" size="20" :color="canSend ? '#FFF' : '#A1A1A6'"></uni-icons>
        </view>
        <view v-else class="stop-btn" @click="handleCancelStream">
          <view class="stop-square"></view>
        </view>
      </view>
    </view>

    <view class="drawer-mask" :class="{ 'mask-show': showHistoryDrawer }" @click="toggleHistoryDrawer"></view>
    <view class="glass-drawer" :class="{ 'drawer-open': showHistoryDrawer }">
      <view class="drawer-header">
        <text class="drawer-title">历史交流</text>
        <view class="nav-icon-glass mini" @click="toggleHistoryDrawer">
          <uni-icons type="closeempty" size="18" color="#1D1D1F"></uni-icons>
        </view>
      </view>

      <view class="drawer-content">
        <view class="new-chat-btn" @click="createNewChat">
          <uni-icons type="plusempty" size="18" color="#FFF"></uni-icons>
          <text>开启新对话</text>
        </view>

        <view class="loading-state" v-if="isLoadingHistory">
          <uni-icons type="spinner-cycle" size="24" color="#86868B" class="spin-icon"></uni-icons>
          <text>加载中...</text>
        </view>

        <view class="empty-state" v-else-if="chatHistory.length === 0">
          <uni-icons type="chatbubble" size="40" color="#C7C7CC"></uni-icons>
          <text>暂无历史对话</text>
        </view>

        <scroll-view class="history-list" scroll-y v-else>
          <view v-for="(chat, index) in chatHistory" :key="chat.chatId" class="history-item">
            <view class="history-item-content" @click="loadChatHistory(chat.chatId)">
              <view class="history-icon">
                <uni-icons type="chat" size="18" color="#2CB5A0"></uni-icons>
              </view>
              <text class="history-title">
                {{ chat.firstMessage || `对话 ${index + 1}` }}
              </text>
            </view>
            <view class="history-actions">
              <view class="action-mini" @click.stop="startEditTitle(chat)">
                <uni-icons type="compose" size="16" color="#86868B"></uni-icons>
              </view>
              <view class="action-mini" @click.stop="handleDeleteChat(chat.chatId)">
                <uni-icons type="trash" size="16" color="#FF3B30"></uni-icons>
              </view>
            </view>
          </view>
        </scroll-view>
      </view>
    </view>
  </view>
</template>

<script>
// 这里保持你的原生业务逻辑完全不变
import { getSessions, getMessages, deleteSession, updateSessionTitle, streamChat, cancelStream, createSession } from '@/api/ai.js';
import { adjustHeight, formatTime, generateUUID, goBack, onInputBlur, onInputFocus, scrollToBottom } from './ai.js';
import UaMarkdown from '@/components/ua2-markdown/ua-markdown.vue';

export default {
  components: { UaMarkdown },
  data() {
    return {
      statusBarHeight: 0, 
      sessionId: null,
      userInput: '',
      currentMessages: [],
      selectedFiles: [],
      scrollTop: 0,
      isStreaming: false,
      isLoadingMore: false,
      fullResponse: '',
      eventSource: null,
      showHistoryDrawer: false,
      chatHistory: [],
      isLoadingHistory: false,
      editingChatId: null,
      editingTitle: '',
      textareaHeight: 20, // 初始高度稍微缩小，适配胶囊
      keyboardHeight: 0,
      inputAreaHeight: 60,
      safeAreaBottom: 34,
      userAvatar: uni.getStorageSync('avatar') || '/static/images/profile.jpg',
      aiAvatar: 'https://wallpaper-web-pro.oss-cn-beijing.aliyuncs.com/images/ai.jpg',
      chatType: 'chat'
    }
  },
  computed: {
    canSend() { return this.userInput.trim().length > 0 || this.selectedFiles.length > 0; }
  },
  onLoad() {
    const systemInfo = uni.getSystemInfoSync();
    this.statusBarHeight = systemInfo.statusBarHeight || 0;
    this.safeAreaBottom = systemInfo.safeAreaInsets?.bottom || 34;

    uni.$on('keyboardHeightChange', async (res) => {
      this.keyboardHeight = res.height;
      await this.$nextTick();
      this.scrollToBottom();
    });

    this.addWelcomeMessage();
  },
  onUnload() {
    uni.$off('keyboardHeightChange');
    this.closeEventSource();
  },
  methods: {
    // 跳转到首页的核心方法
    goToIndex() {
      uni.reLaunch({
        url: '/pages/index'
      })
    },
    shouldShowTime(current, previous) {
      if (!previous) return true;
      return (current - previous) > 5 * 60 * 1000;
    },
    addWelcomeMessage() {
      this.currentMessages.push({ role: 'assistant', content: '你好！我是 AI 治愈助手，无论遇到什么烦心事，都可以和我聊聊。', timestamp: Date.now() });
    },
    scrollToBottom() { scrollToBottom(this, 'scrollTop'); },
    handleInputFocus() { onInputFocus(() => this.scrollToBottom()); },
    handleInputBlur() { onInputBlur((height) => { this.keyboardHeight = height; }); },
    handleAdjustHeight(e) {
      adjustHeight(e, {
        setTextareaHeight: (height) => { this.textareaHeight = height; },
        setInputAreaHeight: (height) => { this.inputAreaHeight = height; },
        scrollToBottom: () => this.scrollToBottom()
      });
    },
    loadMoreMessages() {
      if (this.currentMessages.length < 5) return;
      this.isLoadingMore = true;
      setTimeout(() => { this.isLoadingMore = false; }, 1000);
    },
    handleUpload() {
      if (this.selectedFiles.length >= 5) {
        uni.showToast({ title: '最多只能上传5个文件', icon: 'none' }); return;
      }
      uni.chooseFile({
        count: 5 - this.selectedFiles.length,
        extension: ['jpg', 'jpeg', 'png', 'pdf', 'doc', 'docx', 'xls', 'xlsx', 'txt'],
        success: (res) => {
          const files = res.tempFiles.map(item => ({ name: item.name || `file_${Date.now()}`, size: item.size, path: item.path, file: item }));
          this.selectedFiles = [...this.selectedFiles, ...files];
        },
        fail: (err) => {
          if (err.errMsg !== 'chooseFile:fail cancel') { uni.showToast({ title: '选择文件失败', icon: 'none' }); }
        }
      });
    },
    removeFile(index) { this.selectedFiles.splice(index, 1); },
    getFileIcon(fileName) {
      if (!fileName) return 'paperclip';
      const ext = fileName.split('.').pop().toLowerCase();
      const iconMap = { pdf: 'file-pdf', doc: 'file-word', docx: 'file-word', xls: 'file-excel', xlsx: 'file-excel', jpg: 'image', jpeg: 'image', png: 'image', gif: 'image' };
      return iconMap[ext] || 'file';
    },
    getFileName(fileName) {
      if (!fileName) return '未命名文件';
      return fileName.length > 15 ? fileName.substring(0, 12) + '...' + fileName.split('.').pop() : fileName;
    },
    formatFileSize(size) {
      if (!size) return '';
      if (size < 1024) return size + 'B';
      else if (size < 1024 * 1024) return (size / 1024).toFixed(1) + 'KB';
      else return (size / (1024 * 1024)).toFixed(1) + 'MB';
    },
    formatTime(timestamp) { return formatTime(timestamp); },
    toggleHistoryDrawer() {
      this.showHistoryDrawer = !this.showHistoryDrawer;
      if (this.showHistoryDrawer) this.fetchChatHistory();
    },
    closeEventSource() {
      if (this.eventSource) {
        if (typeof this.eventSource.close === 'function') this.eventSource.close();
        this.eventSource = null;
      }
    },
    async handleCancelStream() {
      if (!this.isStreaming) return;
      try {
        this.closeEventSource();
        if (this.sessionId) await cancelStream(this.sessionId);
        this.isStreaming = false;
        const lastIndex = this.currentMessages.length - 1;
        if (lastIndex >= 0 && this.currentMessages[lastIndex].role === 'assistant') {
          const content = this.currentMessages[lastIndex].content || '（已停止生成）';
          this.currentMessages[lastIndex].content = content;
        }
        uni.showToast({ title: '已停止生成', icon: 'success', duration: 1500 });
      } catch (error) {
        this.isStreaming = false;
        uni.showToast({ title: '停止失败，请重试', icon: 'none' });
      }
    },
    async fetchChatHistory() {
      this.isLoadingHistory = true;
      try {
        const response = await getSessions();
        if (response.code === 200) {
          this.chatHistory = (response.data || []).map((session, idx) => ({
            chatId: session.sessionId, sessionId: session.sessionId,
            firstMessage: session.sessionName || `新对话 ${idx + 1}`,
            timestamp: session.createTime ? new Date(session.createTime).getTime() : Date.now(),
            messageCount: session.messageCount || 0
          }));
        } else { uni.showToast({ title: response.msg || '获取历史记录失败', icon: 'none' }); }
      } catch (error) { uni.showToast({ title: '获取历史记录失败', icon: 'none' }); } finally { this.isLoadingHistory = false; }
    },
    async loadChatHistory(chatId) {
      try {
        const response = await getMessages(chatId);
        if (response.code === 200) {
          this.sessionId = chatId;
          const messages = response.data || [];
          this.currentMessages = messages.map(msg => ({
            role: msg.messageType === 1 ? 'user' : 'assistant',
            content: msg.content,
            timestamp: msg.createTime ? new Date(msg.createTime).getTime() : Date.now(),
            attachments: []
          }));
          this.toggleHistoryDrawer();
          this.$nextTick(() => { this.scrollToBottom(); });
        } else { uni.showToast({ title: response.msg || '加载对话失败', icon: 'none' }); }
      } catch (error) { uni.showToast({ title: '加载对话失败', icon: 'none' }); }
    },
    async createNewChat() {
      this.sessionId = null;
      this.currentMessages = [];
      this.fullResponse = '';
      this.closeEventSource();
      this.addWelcomeMessage();
      this.toggleHistoryDrawer();
      this.$nextTick(() => { this.scrollToBottom(); });
      uni.showToast({ title: '已创建新对话', icon: 'success', duration: 1500 });
    },
    async sendMessage() {
      if (!this.canSend || this.isStreaming) return;
      const messageText = this.userInput.trim();
      const attachments = [...this.selectedFiles];
      if (!messageText && attachments.length === 0) return;

      this.currentMessages.push({ role: 'user', content: messageText, attachments: attachments, timestamp: Date.now() });
      this.currentMessages.push({ role: 'assistant', content: '', timestamp: Date.now() });

      this.userInput = ''; this.selectedFiles = []; this.textareaHeight = 20; this.inputAreaHeight = 60; this.fullResponse = '';
      await this.$nextTick(); await this.scrollToBottom();

      try {
        this.isStreaming = true; let lastScrollTime = 0; const that = this;
        if (!this.sessionId) {
          try {
            const createResponse = await createSession(messageText.substring(0, 50));
            if (createResponse.code === 200 && createResponse.data) { this.sessionId = createResponse.data.sessionId; }
            else { throw new Error(createResponse.msg || '创建会话失败'); }
          } catch (createError) {
            const lastIndex = this.currentMessages.length - 1;
            if (lastIndex >= 0) { this.currentMessages[lastIndex].content = '抱歉，创建会话失败，请稍后再试。'; }
            this.isStreaming = false; uni.showToast({ title: '创建会话失败', icon: 'none' }); return;
          }
        }
        const sseConnection = streamChat({
          message: messageText, sessionId: this.sessionId,
          onSessionId: (newSessionId) => { that.sessionId = parseInt(newSessionId); },
          onMessage: async (chunk, fullContent) => {
            that.fullResponse = fullContent;
            const lastIndex = that.currentMessages.length - 1;
            if (lastIndex >= 0) { that.currentMessages[lastIndex].content = fullContent; }
            const now = Date.now();
            if (now - lastScrollTime > 100) { lastScrollTime = now; await that.$nextTick(); await that.scrollToBottom(); }
          },
          onError: (error) => {
            const lastIndex = that.currentMessages.length - 1;
            if (lastIndex >= 0 && !that.currentMessages[lastIndex].content) { that.currentMessages[lastIndex].content = '抱歉，发生了错误，请稍后再试。'; }
            that.isStreaming = false; that.closeEventSource(); uni.showToast({ title: '连接出错，请重试', icon: 'none' });
          },
          onComplete: async (fullContent) => {
            that.isStreaming = false; that.closeEventSource(); await that.$nextTick(); await that.scrollToBottom();
          }
        });
        this.eventSource = sseConnection.eventSource;
      } catch (error) {
        const lastIndex = this.currentMessages.length - 1;
        if (lastIndex >= 0) { this.currentMessages[lastIndex].content = '抱歉，发生了错误，请稍后再试。'; }
        uni.showToast({ title: '发送消息失败', icon: 'none' }); this.isStreaming = false; this.closeEventSource();
      }
    },
    async handleDeleteChat(chatId) {
      uni.showModal({
        title: '确认删除', content: '确定要删除这个对话吗？', confirmColor: '#FF3B30',
        success: async (res) => {
          if (res.confirm) {
            const response = await deleteSession(chatId);
            if (response.code === 200) {
              this.chatHistory = this.chatHistory.filter(chat => chat.chatId !== chatId);
              uni.showToast({ title: '删除成功', icon: 'success' });
              if (chatId === this.sessionId) this.createNewChat();
            } else { uni.showToast({ title: response.msg || '删除失败', icon: 'none' }); }
          }
        }
      });
    },
    startEditTitle(chat) {
      this.editingChatId = chat.chatId; this.editingTitle = chat.firstMessage || '';
      uni.showModal({
        title: '修改对话标题', editable: true, placeholderText: '请输入新的标题', content: chat.firstMessage || '', confirmColor: '#2CB5A0',
        success: async (res) => {
          if (res.confirm && res.content.trim()) {
            const newTitle = res.content.trim();
            const response = await updateSessionTitle(this.editingChatId, newTitle);
            if (response.code === 200) {
              const targetChat = this.chatHistory.find(c => c.chatId === this.editingChatId);
              if (targetChat) targetChat.firstMessage = newTitle;
              uni.showToast({ title: '修改成功', icon: 'success' });
            } else { uni.showToast({ title: response.msg || '修改失败', icon: 'none' }); }
          }
          this.editingChatId = null; this.editingTitle = '';
        }
      });
    },
    goBack() { uni.navigateBack(); }
  }
}
</script>

<style lang="scss" scoped>
/* ==================== iOS 风格核心变量 ==================== */
$ios-text-primary: #1D1D1F;
$ios-text-secondary: #86868B;
$theme-cyan: #2CB5A0;
$theme-cyan-light: #48D1CC;

.ai-chat-container {
  display: flex;
  flex-direction: column;
  width: 100%;
  height: 100vh;
  position: relative;
  background-color: #F5F5F7;
  font-family: -apple-system, BlinkMacSystemFont, "SF Pro Text", "Helvetica Neue", Arial, sans-serif;
  overflow: hidden;
}

/* --- 弥散光影背景 --- */
.ambient-background {
  position: absolute;
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

/* ==================== 1. 绝对固定的高斯模糊头部 ==================== */
.glass-header-fixed {
  position: fixed;
  top: 0; left: 0; right: 0;
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
.navbar-right { justify-content: flex-end; }

.nav-icon-glass {
  width: 64rpx; height: 64rpx;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.6);
  display: flex; align-items: center; justify-content: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.04);
  transition: transform 0.2s ease;
  &:active { transform: scale(0.9); opacity: 0.8; }
  &.mini { width: 56rpx; height: 56rpx; }
}

.navbar-title {
  font-size: 32rpx; font-weight: 600; color: $ios-text-primary;
}

/* ==================== 聊天滚动区 ==================== */
.chat-area {
  flex: 1;
  width: 100%;
  position: relative;
  z-index: 1;
  height: 100%;
}

.loading-more {
  display: flex; align-items: center; justify-content: center;
  padding: 24rpx 0;
  .spin-icon { animation: spin 1s linear infinite; margin-right: 12rpx; }
  .loading-text { font-size: 24rpx; color: $ios-text-secondary; }
}
@keyframes spin { 100% { transform: rotate(360deg); } }

.messages-wrapper {
  padding: 32rpx;
  display: flex; flex-direction: column; gap: 40rpx;
}

/* 时间戳 */
.message-time-center {
  text-align: center;
  margin-bottom: 24rpx;
  text {
    font-size: 22rpx;
    color: $ios-text-secondary;
    background: rgba(0, 0, 0, 0.04);
    padding: 6rpx 16rpx;
    border-radius: 20rpx;
  }
}

.message-container {
  display: flex;
  align-items: flex-start;
  gap: 20rpx;
}

.avatar {
  width: 64rpx; height: 64rpx;
  border-radius: 50%;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.08);
  flex-shrink: 0;
}

.message-content-box {
  display: flex;
  flex-direction: column;
  max-width: 75%;
}

.sender-name {
  font-size: 24rpx;
  color: $ios-text-secondary;
  margin-bottom: 8rpx;
  margin-left: 8rpx;
}

/* 气泡通用基础 */
.message-bubble {
  border-radius: 36rpx;
  padding: 24rpx 32rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.03);
  overflow: hidden;
}

/* ==================== User 气泡 (治愈青渐变) ==================== */
.user-message .message-container { flex-direction: row-reverse; }
.user-message .message-content-box { align-items: flex-end; }
.user-message .sender-name { margin-right: 8rpx; margin-left: 0; }
.user-message .message-bubble {
  background: linear-gradient(135deg, $theme-cyan-light 0%, $theme-cyan 100%);
  border-top-right-radius: 8rpx; 
  box-shadow: 0 8rpx 20rpx rgba(44, 181, 160, 0.25);
}
.user-text {
  font-size: 30rpx;
  color: #FFFFFF;
  line-height: 1.6;
}

/* ==================== AI 气泡 (Glass Card) ==================== */
.ai-message .message-bubble {
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.9);
  border-top-left-radius: 8rpx; 
}

/* 定制 ua-markdown 的样式以匹配 iOS 质感 */
:deep(.ua__markdown) {
  font-size: 30rpx;
  line-height: 1.6;
  color: $ios-text-primary;
  
  p { margin-bottom: 20rpx; &:last-child { margin-bottom: 0; } }
  strong { font-weight: 600; color: #000; }
  
  pre {
    background: rgba(245, 245, 247, 0.8);
    border-radius: 16rpx;
    padding: 20rpx;
    margin: 20rpx 0;
    overflow-x: auto;
    border: 1px solid rgba(0, 0, 0, 0.05);
    code { font-family: Consolas, monospace; font-size: 26rpx; }
  }
  
  code {
    background: rgba(0, 0, 0, 0.04);
    padding: 4rpx 8rpx;
    border-radius: 8rpx;
    font-size: 26rpx;
    color: #FF3B30;
  }
  
  ul, ol { padding-left: 32rpx; margin: 16rpx 0; }
  li { margin-bottom: 8rpx; }
  
  blockquote {
    border-left: 6rpx solid $theme-cyan;
    padding-left: 20rpx;
    margin: 20rpx 0;
    color: $ios-text-secondary;
    background: rgba(44, 181, 160, 0.05);
    padding: 16rpx 20rpx;
    border-radius: 0 16rpx 16rpx 0;
  }
}

/* 正在输入的呼吸点 */
.typing-indicator {
  display: flex; align-items: center; height: 30rpx; gap: 8rpx;
  .dot {
    width: 10rpx; height: 10rpx; border-radius: 50%;
    background-color: $theme-cyan;
    animation: typing 1.4s infinite ease-in-out both;
    &:nth-child(1) { animation-delay: -0.32s; }
    &:nth-child(2) { animation-delay: -0.16s; }
  }
}
@keyframes typing {
  0%, 80%, 100% { transform: scale(0); opacity: 0.5; }
  40% { transform: scale(1); opacity: 1; }
}

/* 附件小药丸 */
.attachments {
  margin-top: 16rpx; display: flex; flex-direction: column; gap: 12rpx;
}
.attachment-pill {
  display: inline-flex; align-items: center; gap: 12rpx;
  background: rgba(255, 255, 255, 0.8);
  border: 1px solid rgba(0, 0, 0, 0.05);
  padding: 12rpx 24rpx; border-radius: 30rpx;
  box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.02);
  .attachment-name {
    font-size: 24rpx; color: $ios-text-primary; font-weight: 500;
  }
}

/* ==================== 2. 悬浮胶囊输入舱 (Floating Capsule Dock) ==================== */
/* 外部包装器，不阻挡底层事件 */
.capsule-wrapper {
  position: fixed;
  bottom: 0; left: 0; right: 0;
  z-index: 100;
  padding: 0 32rpx; /* 屏幕两侧留白，形成悬浮感 */
  pointer-events: none; /* 让空白区域的点击穿透到下面的聊天记录 */
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
}

/* 附件预览 (悬浮在胶囊上方) */
.attachment-preview {
  pointer-events: auto; /* 恢复点击事件 */
  margin-bottom: 24rpx;
  padding: 0 12rpx;
  
  .attachment-scroll { white-space: nowrap; padding: 4rpx 0; }
  .preview-item {
    display: inline-flex; align-items: center; gap: 8rpx;
    background: rgba(255, 255, 255, 0.9);
    backdrop-filter: blur(10px);
    border: 1px solid rgba(255,255,255,0.8);
    border-radius: 40rpx; /* 圆润的附件胶囊 */
    padding: 14rpx 24rpx; margin-right: 16rpx;
    position: relative; box-shadow: 0 8rpx 20rpx rgba(0,0,0,0.06);
  }
  .preview-name { font-size: 24rpx; color: $ios-text-primary; max-width: 140rpx; overflow: hidden; text-overflow: ellipsis; }
  .remove-btn {
    position: absolute; top: -8rpx; right: -8rpx;
    width: 32rpx; height: 32rpx; border-radius: 50%; background: #FF3B30;
    display: flex; align-items: center; justify-content: center;
    box-shadow: 0 4rpx 10rpx rgba(255,59,48,0.3);
    &:active { transform: scale(0.9); }
  }
}

/* 核心输入胶囊 */
.capsule-dock {
  pointer-events: auto; /* 恢复点击事件 */
  background: rgba(250, 250, 252, 0.85); /* 极浅透明灰底 */
  backdrop-filter: saturate(200%) blur(30px);
  -webkit-backdrop-filter: saturate(200%) blur(30px);
  border: 1px solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 16rpx 40rpx rgba(0, 0, 0, 0.08); /* 深邃悬浮阴影 */
  border-radius: 60rpx; /* 大圆角呈现完美胶囊感 */
  padding: 12rpx 16rpx 12rpx 24rpx;
  display: flex; 
  align-items: flex-end; /* 底部对齐，让 textarea 向上生长 */
  transition: all 0.3s cubic-bezier(0.2, 0.8, 0.2, 1);
}

.action-icon-btn {
  width: 68rpx; height: 68rpx; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0; margin-bottom: 2rpx;
  background: transparent;
  &:active { background: rgba(0,0,0,0.05); }
}

.input-wrapper {
  flex: 1;
  padding: 14rpx 16rpx;
  margin: 0 8rpx;
}

.ios-textarea {
  width: 100%; 
  font-size: 30rpx; 
  line-height: 1.4;
  color: $ios-text-primary; 
  min-height: 42rpx; 
  max-height: 180rpx;
}

.send-btn {
  width: 68rpx; height: 68rpx; border-radius: 50%; flex-shrink: 0;
  display: flex; align-items: center; justify-content: center; margin-bottom: 2rpx;
  background: rgba(0, 0, 0, 0.04); transition: all 0.3s ease;
  &.btn-active {
    background: $theme-cyan;
    box-shadow: 0 6rpx 16rpx rgba(44, 181, 160, 0.3);
    &:active { transform: scale(0.9); }
  }
}

.stop-btn {
  width: 68rpx; height: 68rpx; border-radius: 50%; flex-shrink: 0;
  display: flex; align-items: center; justify-content: center; margin-bottom: 2rpx;
  background: #1D1D1F; box-shadow: 0 6rpx 16rpx rgba(0, 0, 0, 0.2);
  &:active { transform: scale(0.9); }
  .stop-square { width: 22rpx; height: 22rpx; background: #FFF; border-radius: 6rpx; }
}

/* ==================== 侧边抽屉 (Spatial Drawer) ==================== */
.drawer-mask {
  position: fixed; top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0, 0, 0, 0.3);
  backdrop-filter: blur(4px);
  -webkit-backdrop-filter: blur(4px);
  z-index: 998;
  opacity: 0; pointer-events: none; transition: opacity 0.3s ease;
  &.mask-show { opacity: 1; pointer-events: auto; }
}

.glass-drawer {
  position: fixed; top: 0; right: -80%; bottom: 0; width: 80%;
  background: rgba(245, 245, 247, 0.85);
  backdrop-filter: saturate(200%) blur(30px);
  -webkit-backdrop-filter: saturate(200%) blur(30px);
  z-index: 999;
  box-shadow: -8px 0 32px rgba(0, 0, 0, 0.1);
  transition: transform 0.4s cubic-bezier(0.2, 0.8, 0.2, 1);
  display: flex; flex-direction: column;
  &.drawer-open { transform: translateX(-100%); }
}

.drawer-header {
  padding: calc(20rpx + env(safe-area-inset-top)) 32rpx 20rpx;
  display: flex; align-items: center; justify-content: space-between;
  border-bottom: 0.5px solid rgba(0, 0, 0, 0.05);
  .drawer-title { font-size: 34rpx; font-weight: 700; color: $ios-text-primary; }
}

.drawer-content { flex: 1; padding: 32rpx; overflow-y: auto; }

.new-chat-btn {
  background: $theme-cyan; border-radius: 24rpx; padding: 24rpx;
  display: flex; align-items: center; justify-content: center; gap: 12rpx;
  box-shadow: 0 8rpx 20rpx rgba(44, 181, 160, 0.25);
  margin-bottom: 40rpx; transition: transform 0.2s;
  text { font-size: 30rpx; font-weight: 600; color: #FFF; }
  &:active { transform: scale(0.96); }
}

.history-list { height: calc(100% - 120rpx); }

.history-item {
  display: flex; align-items: center; justify-content: space-between;
  padding: 28rpx 24rpx; background: rgba(255,255,255,0.6);
  border-radius: 24rpx; margin-bottom: 20rpx; border: 1px solid rgba(255,255,255,0.5);
  &:active { background: rgba(255,255,255,0.9); }
}

.history-item-content {
  display: flex; align-items: center; gap: 16rpx; flex: 1; overflow: hidden;
}

.history-icon {
  width: 48rpx; height: 48rpx; border-radius: 16rpx;
  background: rgba(44, 181, 160, 0.15);
  display: flex; align-items: center; justify-content: center; flex-shrink: 0;
}

.history-title {
  font-size: 28rpx; font-weight: 500; color: $ios-text-primary;
  white-space: nowrap; overflow: hidden; text-overflow: ellipsis;
}

.history-actions { display: flex; align-items: center; gap: 16rpx; }
.action-mini { padding: 8rpx; }

.loading-state, .empty-state {
  display: flex; flex-direction: column; align-items: center; padding: 100rpx 0; gap: 16rpx;
  text { font-size: 28rpx; color: $ios-text-secondary; }
}
</style>