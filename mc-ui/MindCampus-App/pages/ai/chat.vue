<template>
  <view class="ai-chat-container">
    <!-- 浮动历史记录按钮 -->
    <view class="floating-history-btn" @click="toggleHistoryDrawer">
      <view class="btn-content">
        <uni-icons type="list" size="24" color="#fff"></uni-icons>
      </view>
      <view class="btn-glow"></view>
    </view>

    <!-- 聊天区域 -->
    <scroll-view class="chat-area" scroll-y :scroll-top="scrollTop" :scroll-with-animation="true"
      @scrolltoupper="loadMoreMessages" upper-threshold="100" ref="chatScrollView">
      <view class="loading-more" v-if="isLoadingMore">
        <uni-icons type="spinner-cycle" size="16" color="#999"></uni-icons>
        <text class="loading-text">加载更多消息...</text>
      </view>

      <!-- 聊天消息列表 -->
      <view class="messages-wrapper">
        <view v-for="(msg, index) in currentMessages" :key="index"
          :class="['message-item', msg.role === 'user' ? 'user-message' : 'ai-message']">
          <view class="message-container">
            <image class="avatar" :src="msg.role === 'user' ? userAvatar : aiAvatar" mode="aspectFill" />
            <view class="message-content">
              <view class="message-header">
                <text class="sender-name">{{ msg.role === 'user' ? '我' : 'AI 助手' }}</text>
                <text class="message-time">{{ formatTime(msg.timestamp) }}</text>
              </view>
              <view class="message-body">
                <!-- AI消息使用markdown解析 -->
                <ua-markdown v-if="msg.role === 'assistant' && msg.content" :source="msg.content" :showLine="false" />
                <!-- 用户消息直接显示文本 -->
                <text v-else-if="msg.role === 'user' && msg.content">{{ msg.content }}</text>
                <!-- AI正在输入的动画 -->
                <view v-else-if="msg.role === 'assistant' && msg.content === ''" class="typing-indicator">
                  <view class="dot"></view>
                  <view class="dot"></view>
                  <view class="dot"></view>
                </view>
              </view>
              <!-- 附件列表 -->
              <view class="attachments" v-if="msg.attachments && msg.attachments.length > 0">
                <view v-for="(file, fileIndex) in msg.attachments" :key="fileIndex" class="attachment-item">
                  <view class="attachment-icon">
                    <uni-icons :type="getFileIcon(file.name)" size="20" color="#666"></uni-icons>
                  </view>
                  <view class="attachment-info">
                    <text class="attachment-name">{{ getFileName(file.name) }}</text>
                    <text class="attachment-size">{{ formatFileSize(file.size) }}</text>
                  </view>
                </view>
              </view>
            </view>
          </view>
        </view>
      </view>

      <!-- 底部空间，确保最后一条消息显示完整 -->
      <view class="bottom-space"></view>
    </scroll-view>

    <!-- 底部输入区域 -->
    <view class="input-area" :style="{ paddingBottom: `${safeAreaBottom}px` }">
      <!-- 附件预览区 -->
      <view class="attachment-preview" v-if="selectedFiles.length > 0">
        <scroll-view scroll-x class="attachment-scroll">
          <view v-for="(file, index) in selectedFiles" :key="index" class="preview-item">
            <view class="preview-content">
              <uni-icons :type="getFileIcon(file.name)" size="20" color="#666"></uni-icons>
              <text class="preview-name">{{ getFileName(file.name) }}</text>
            </view>
            <view class="remove-btn" @click="removeFile(index)">
              <uni-icons type="close" size="14" color="#fff"></uni-icons>
            </view>
          </view>
        </scroll-view>
      </view>

      <!-- 输入控制区 -->
      <view class="input-controls">
        <view class="upload-btn" @click="handleUpload">
          <uni-icons type="paperclip" size="24" color="#666"></uni-icons>
        </view>

        <view class="input-wrapper">
          <textarea class="input-box" v-model="userInput" placeholder="发送消息给 AI 助手..." :disable-default-padding="true"
            :cursor-spacing="20" confirm-type="send" :maxlength="-1" @confirm="sendMessage" :auto-height="true"
            :style="{ height: `${textareaHeight}px` }" @input="handleAdjustHeight" @focus="handleInputFocus"
            @blur="handleInputBlur" :show-confirm-bar="false" :hold-keyboard="true" :adjust-position="false"></textarea>
        </view>

        <!-- 发送/停止按钮 -->
        <view v-if="!isStreaming" class="send-btn" :class="{ active: canSend }" @click="sendMessage">
          <uni-icons type="paperplane-filled" size="24" color="#fff"></uni-icons>
        </view>
        <view v-else class="stop-btn" @click="handleCancelStream">
          <uni-icons type="closeempty" size="28" color="#fff"></uni-icons>
        </view>
      </view>
    </view>

    <!-- 历史对话抽屉 -->
    <view class="history-drawer-mask" v-if="showHistoryDrawer" @click="toggleHistoryDrawer"></view>
    <view class="history-drawer" :class="{ open: showHistoryDrawer }">
      <view class="drawer-header">
        <text class="drawer-title">历史对话</text>
        <view class="drawer-close" @click="toggleHistoryDrawer">
          <uni-icons type="close" size="22" color="#333"></uni-icons>
        </view>
      </view>

      <view class="drawer-content">
        <!-- 新增：新建对话按钮 -->
        <view class="new-chat-button" @click="createNewChat">
          <view class="button-content">
            <uni-icons type="refresh" size="20" color="#fff"></uni-icons>
            <text class="new-chat-text">开启新对话</text>
          </view>
        </view>

        <view class="history-loading" v-if="isLoadingHistory">
          <uni-icons type="spinner-cycle" size="24" color="#999"></uni-icons>
          <text class="loading-text">加载历史记录中...</text>
        </view>

        <view class="empty-history" v-else-if="chatHistory.length === 0">
          <uni-icons type="chat" size="40" color="#ccc"></uni-icons>
          <text class="empty-text">暂无历史对话</text>
        </view>

        <scroll-view class="history-list" scroll-y v-else>
          <view v-for="(chat, index) in chatHistory" :key="chat.chatId" class="history-item">
            <view class="history-item-content" @click="loadChatHistory(chat.chatId)">
              <view class="history-icon">
                <uni-icons type="chat" size="24" color="#666"></uni-icons>
              </view>
              <view class="history-info">
                <text class="history-title">
                  {{ (chat.firstMessage || `对话 ${index + 1}`).length > 20 
                    ? (chat.firstMessage || `对话 ${index + 1}`).substring(0, 20) + '...' 
                    : (chat.firstMessage || `对话 ${index + 1}`) 
                  }}
                </text>
              </view>
            </view>
            <view class="history-actions">
              <view @click="handleIconClick($event, () => startEditTitle(chat))">
                <uni-icons type="compose" size="20" color="#10b981" style="margin-right: 8px;"></uni-icons>
              </view>
              <view @click="handleIconClick($event, () => handleDeleteChat(chat.chatId))">
                <uni-icons type="trash" size="20" color="#ff6b6b"></uni-icons>
              </view>
            </view>
          </view>
        </scroll-view>
      </view>
    </view>
  </view>
</template>

<script>
import { getSessions, getMessages, deleteSession, updateSessionTitle, streamChat, cancelStream, createSession } from '@/api/ai.js';
import { adjustHeight, formatTime, generateUUID, goBack, onInputBlur, onInputFocus, scrollToBottom } from './ai.js';
import UaMarkdown from '@/components/ua2-markdown/ua-markdown.vue';

export default {
  components: {
    UaMarkdown
  },
  data() {
    return {
      // 会话ID（从后端获取）
      sessionId: null,

      // 响应式状态
      userInput: '',
      currentMessages: [],
      selectedFiles: [],
      scrollTop: 0,
      isStreaming: false,
      isLoadingMore: false,
      fullResponse: '',

      // SSE连接对象
      eventSource: null,

      // 历史对话状态
      showHistoryDrawer: false,
      chatHistory: [],
      isLoadingHistory: false,
      editingChatId: null,
      editingTitle: '',

      // UI 相关状态
      textareaHeight: 24,
      keyboardHeight: 0,
      inputAreaHeight: 60,
      safeAreaBottom: 34,

      // 头像设置
      userAvatar: uni.getStorageSync('avatar') || '/static/images/profile.jpg',
      aiAvatar: 'https://wallpaper-web-pro.oss-cn-beijing.aliyuncs.com/images/ai.jpg',

      // 聊天类型
      chatType: 'chat'
    }
  },

  computed: {
    canSend() {
      return this.userInput.trim().length > 0 || this.selectedFiles.length > 0;
    }
  },

  onLoad() {
    // 设置导航栏
    uni.setNavigationBarTitle({
      title: 'AI 助手'
    });

    const systemInfo = uni.getSystemInfoSync();
    this.safeAreaBottom = systemInfo.safeAreaInsets?.bottom || 34;

    uni.$on('keyboardHeightChange', async (res) => {
      this.keyboardHeight = res.height;
      await this.$nextTick();
      this.scrollToBottom();
    });

    // 添加一条欢迎消息（不自动滚动到底部）
    this.addWelcomeMessage();
  },

  // 右上角按钮
  onNavigationBarButtonTap(e) {
    if (e.index === 0) {
      // 点击历史记录按钮
      this.toggleHistoryDrawer();
    }
  },

  onUnload() {
    uni.$off('keyboardHeightChange');
    // 关闭SSE连接
    this.closeEventSource();
  },

  methods: {
    // 添加欢迎消息
    addWelcomeMessage() {
      this.currentMessages.push({
        role: 'assistant',
        content: '你好！我是 AI 助手，有什么我可以帮助你的吗？',
        timestamp: Date.now()
      });
    },

    // 滚动到底部
    scrollToBottom() {
      scrollToBottom(this, 'scrollTop');
    },

    // 输入框聚焦
    handleInputFocus() {
      onInputFocus(() => this.scrollToBottom());
    },

    // 输入框失焦
    handleInputBlur() {
      onInputBlur((height) => {
        this.keyboardHeight = height;
      });
    },

    // 调整输入框高度
    handleAdjustHeight(e) {
      adjustHeight(e, {
        setTextareaHeight: (height) => {
          this.textareaHeight = height;
        },
        setInputAreaHeight: (height) => {
          this.inputAreaHeight = height;
        },
        scrollToBottom: () => this.scrollToBottom()
      });
    },

    // 加载更多消息
    loadMoreMessages() {
      if (this.currentMessages.length < 5) return;

      this.isLoadingMore = true;
      setTimeout(() => {
        this.isLoadingMore = false;
      }, 1000);
    },

    // 处理文件上传
    handleUpload() {
      if (this.selectedFiles.length >= 5) {
        uni.showToast({
          title: '最多只能上传5个文件',
          icon: 'none'
        });
        return;
      }

      uni.chooseFile({
        count: 5 - this.selectedFiles.length,
        extension: ['jpg', 'jpeg', 'png', 'pdf', 'doc', 'docx', 'xls', 'xlsx', 'txt'],
        success: (res) => {
          const files = res.tempFiles.map(item => ({
            name: item.name || `file_${Date.now()}`,
            size: item.size,
            path: item.path,
            file: item
          }));

          this.selectedFiles = [...this.selectedFiles, ...files];
        },
        fail: (err) => {
          if (err.errMsg !== 'chooseFile:fail cancel') {
            uni.showToast({
              title: '选择文件失败',
              icon: 'none'
            });
          }
        }
      });
    },

    // 移除文件
    removeFile(index) {
      this.selectedFiles.splice(index, 1);
    },

    // 获取文件图标
    getFileIcon(fileName) {
      if (!fileName) return 'paperclip';

      const ext = fileName.split('.').pop().toLowerCase();
      const iconMap = {
        pdf: 'file-pdf',
        doc: 'file-word',
        docx: 'file-word',
        xls: 'file-excel',
        xlsx: 'file-excel',
        jpg: 'image',
        jpeg: 'image',
        png: 'image',
        gif: 'image'
      };

      return iconMap[ext] || 'file';
    },

    // 获取文件名
    getFileName(fileName) {
      if (!fileName) return '未命名文件';
      return fileName.length > 15 ? fileName.substring(0, 12) + '...' + fileName.split('.').pop() : fileName;
    },

    // 格式化文件大小
    formatFileSize(size) {
      if (!size) return '';

      if (size < 1024) {
        return size + 'B';
      } else if (size < 1024 * 1024) {
        return (size / 1024).toFixed(1) + 'KB';
      } else {
        return (size / (1024 * 1024)).toFixed(1) + 'MB';
      }
    },

    // 格式化时间
    formatTime(timestamp) {
      return formatTime(timestamp);
    },

    // 切换历史对话抽屉
    toggleHistoryDrawer() {
      this.showHistoryDrawer = !this.showHistoryDrawer;

      if (this.showHistoryDrawer) {
        this.fetchChatHistory();
      }
    },

    // 关闭SSE连接
    closeEventSource() {
      if (this.eventSource) {
        if (typeof this.eventSource.close === 'function') {
          this.eventSource.close();
        }
        this.eventSource = null;
      }
    },

    // 中断流式输出
    async handleCancelStream() {
      if (!this.isStreaming) return;
      
      try {
        // 先关闭EventSource连接，停止接收数据
        this.closeEventSource();
        
        // 如果sessionId存在，调用后端接口停止生成
        if (this.sessionId) {
          await cancelStream(this.sessionId);
          console.log('已调用后端停止接口 - sessionId:', this.sessionId);
        }
        
        this.isStreaming = false;
        
        // 保存当前AI回复（即使被中断也保存部分内容）
        const lastIndex = this.currentMessages.length - 1;
        if (lastIndex >= 0 && this.currentMessages[lastIndex].role === 'assistant') {
          const content = this.currentMessages[lastIndex].content || '（已停止生成）';
          this.currentMessages[lastIndex].content = content;
        }
        
        uni.showToast({
          title: '已停止生成',
          icon: 'success',
          duration: 1500
        });
      } catch (error) {
        console.error('中断流式输出失败:', error);
        this.isStreaming = false;
        uni.showToast({
          title: '停止失败，请重试',
          icon: 'none'
        });
      }
    },

    // 获取历史对话列表
    async fetchChatHistory() {
      this.isLoadingHistory = true;

      try {
        const response = await getSessions();
        if (response.code === 200) {
          // 转换数据格式以适配现有UI
          this.chatHistory = (response.data || []).map((session, idx) => ({
            chatId: session.sessionId,
            sessionId: session.sessionId,
            firstMessage: session.sessionName || `新对话 ${idx + 1}`,
            timestamp: session.createTime ? new Date(session.createTime).getTime() : Date.now(),
            messageCount: session.messageCount || 0
          }));
        } else {
          uni.showToast({
            title: response.msg || '获取历史记录失败',
            icon: 'none'
          });
        }
      } catch (error) {
        console.error('获取历史对话失败:', error);
        uni.showToast({
          title: '获取历史记录失败',
          icon: 'none'
        });
      } finally {
        this.isLoadingHistory = false;
      }
    },

    // 加载特定对话的历史消息
    async loadChatHistory(chatId) {
      try {
        const response = await getMessages(chatId);

        if (response.code === 200) {
          this.sessionId = chatId;

          const messages = response.data || [];
          this.currentMessages = messages.map(msg => ({
            role: msg.messageType === 1 ? 'user' : 'assistant',  // 1=用户消息, 0=AI消息
            content: msg.content,
            timestamp: msg.createTime ? new Date(msg.createTime).getTime() : Date.now(),
            attachments: []
          }));

          this.toggleHistoryDrawer();

          this.$nextTick(() => {
            this.scrollToBottom();
          });
        } else {
          uni.showToast({
            title: response.msg || '加载对话失败',
            icon: 'none'
          });
        }
      } catch (error) {
        console.error('加载对话失败:', error);
        uni.showToast({
          title: '加载对话失败',
          icon: 'none'
        });
      }
    },

    // 创建新对话
    async createNewChat() {
      // 重置会话ID，让后端在第一次发消息时创建新会话
      this.sessionId = null;
      this.currentMessages = [];
      this.fullResponse = '';
      this.closeEventSource();
      this.addWelcomeMessage();
      this.toggleHistoryDrawer();

      this.$nextTick(() => {
        this.scrollToBottom();
      });

      uni.showToast({
        title: '已创建新对话',
        icon: 'success',
        duration: 1500
      });
    },

    // 发送消息
    async sendMessage() {
      if (!this.canSend || this.isStreaming) return;

      const messageText = this.userInput.trim();
      const attachments = [...this.selectedFiles];

      if (!messageText && attachments.length === 0) return;

      // 添加用户消息到列表
      this.currentMessages.push({
        role: 'user',
        content: messageText,
        attachments: attachments,
        timestamp: Date.now()
      });

      // 添加AI消息占位
      this.currentMessages.push({
        role: 'assistant',
        content: '',
        timestamp: Date.now()
      });

      // 清空输入和附件
      this.userInput = '';
      this.selectedFiles = [];
      this.textareaHeight = 24;
      this.inputAreaHeight = 60;
      this.fullResponse = '';

      await this.$nextTick();
      await this.scrollToBottom();

      // 发送请求并处理流式响应（SSE）
      try {
        this.isStreaming = true;
        let lastScrollTime = 0;
        const that = this;

        // 如果是第一次发送消息（sessionId为null），先创建会话
        if (!this.sessionId) {
          console.log('首次发送消息，先创建会话');
          try {
            const createResponse = await createSession(messageText.substring(0, 50)); // 使用消息前50个字符作为会话名称
            if (createResponse.code === 200 && createResponse.data) {
              // 后端返回的是AiChatSession对象，需要从中获取sessionId
              this.sessionId = createResponse.data.sessionId;
              console.log('创建会话成功，会话ID:', this.sessionId);
            } else {
              throw new Error(createResponse.msg || '创建会话失败');
            }
          } catch (createError) {
            console.error('创建会话失败:', createError);
            // 如果创建失败，移除占位消息并提示用户
            const lastIndex = this.currentMessages.length - 1;
            if (lastIndex >= 0) {
              this.currentMessages[lastIndex].content = '抱歉，创建会话失败，请稍后再试。';
            }
            this.isStreaming = false;
            uni.showToast({
              title: '创建会话失败',
              icon: 'none'
            });
            return;
          }
        }

        // 创建SSE连接
        const sseConnection = streamChat({
          message: messageText,
          sessionId: this.sessionId,
          onSessionId: (newSessionId) => {
            // 更新sessionId（当初次发送消息时）
            that.sessionId = parseInt(newSessionId);
            console.log('更新 sessionId:', that.sessionId);
          },
          onMessage: async (chunk, fullContent) => {
            that.fullResponse = fullContent;
            const lastIndex = that.currentMessages.length - 1;
            if (lastIndex >= 0) {
              that.currentMessages[lastIndex].content = fullContent;
            }

            const now = Date.now();
            if (now - lastScrollTime > 100) {
              lastScrollTime = now;
              await that.$nextTick();
              await that.scrollToBottom();
            }
          },
          onError: (error) => {
            console.error('SSE连接错误:', error);
            const lastIndex = that.currentMessages.length - 1;
            if (lastIndex >= 0 && !that.currentMessages[lastIndex].content) {
              that.currentMessages[lastIndex].content = '抱歉，发生了错误，请稍后再试。';
            }
            that.isStreaming = false;
            that.closeEventSource();

            uni.showToast({
              title: '连接出错，请重试',
              icon: 'none'
            });
          },
          onComplete: async (fullContent) => {
            console.log('流式响应完成');
            that.isStreaming = false;
            that.closeEventSource();
            
            await that.$nextTick();
            await that.scrollToBottom();
          }
        });

        // 保存eventSource引用以便后续关闭
        this.eventSource = sseConnection.eventSource;

      } catch (error) {
        console.error('发送消息失败:', error);
        const lastIndex = this.currentMessages.length - 1;
        if (lastIndex >= 0) {
          this.currentMessages[lastIndex].content = '抱歉，发生了错误，请稍后再试。';
        }

        uni.showToast({
          title: '发送消息失败',
          icon: 'none'
        });
        
        this.isStreaming = false;
        this.closeEventSource();
      }
    },

    // 删除对话
    async handleDeleteChat(chatId) {
      try {
        uni.showModal({
          title: '确认删除',
          content: '确定要删除这个对话吗？',
          success: async (res) => {
            if (res.confirm) {
              const response = await deleteSession(chatId);
              if (response.code === 200) {
                this.chatHistory = this.chatHistory.filter(chat => chat.chatId !== chatId);
                uni.showToast({
                  title: '删除成功',
                  icon: 'success'
                });

                if (chatId === this.sessionId) {
                  this.createNewChat();
                }
              } else {
                uni.showToast({
                  title: response.msg || '删除失败',
                  icon: 'none'
                });
              }
            }
          }
        });
      } catch (error) {
        console.error('删除对话失败:', error);
        uni.showToast({
          title: '删除失败',
          icon: 'none'
        });
      }
    },

    // 开始编辑对话标题
    startEditTitle(chat) {
      this.editingChatId = chat.chatId;
      this.editingTitle = chat.firstMessage || '';

      uni.showModal({
        title: '修改对话标题',
        editable: true,
        placeholderText: '请输入新的标题',
        content: chat.firstMessage || '',
        success: async (res) => {
          if (res.confirm && res.content.trim()) {
            try {
              const newTitle = res.content.trim();
              const response = await updateSessionTitle(this.editingChatId, newTitle);
              if (response.code === 200) {
                const targetChat = this.chatHistory.find(c => c.chatId === this.editingChatId);
                if (targetChat) {
                  targetChat.firstMessage = newTitle;
                }

                uni.showToast({
                  title: '修改成功',
                  icon: 'success'
                });
              } else {
                uni.showToast({
                  title: response.msg || '修改失败',
                  icon: 'none'
                });
              }
            } catch (error) {
              console.error('修改标题失败:', error);
              uni.showToast({
                title: '修改失败',
                icon: 'none'
              });
            }
          }
          this.editingChatId = null;
          this.editingTitle = '';
        }
      });
    },

    // 处理图标点击事件
    handleIconClick(event, callback) {
      if (event && event.stopPropagation) {
        event.stopPropagation();
      }
      callback();
    },

    // 返回
    goBack() {
      goBack();
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/static/scss/theme.scss';

.ai-chat-container {
  display: flex;
  flex-direction: column;
  width: 100%;
  height: 100vh;
  background: $gradient-soft;
  position: relative;
  overflow: hidden;
}

/* 聊天区域样式 */
.chat-area {
  flex: 1;
  width: 100%;
  overflow-y: auto;
  padding-top: $spacing-sm;
  padding-bottom: 0;
  min-height: 0;

  .loading-more {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 40px;
    font-size: $font-xs;
    color: $text-tertiary;

    .loading-text {
      margin-left: $spacing-xs;
    }
  }

  .messages-wrapper {
    padding: $spacing-sm $spacing-base;
  }

  .message-item {
    margin-bottom: $spacing-lg;

    &:last-child {
      margin-bottom: $spacing-xs;
    }
  }

  .message-container {
    display: flex;
    width: 100%;
  }

  .avatar {
    width: 38px;
    height: 38px;
    border-radius: 19px;
    margin-right: $spacing-sm;
    flex-shrink: 0;
    box-shadow: $shadow-sm;
  }

  .user-message .message-container {
    flex-direction: row-reverse;

    .avatar {
      margin-right: 0;
      margin-left: $spacing-sm;
    }

    .message-content {
      align-items: flex-end;
    }

    .message-header {
      flex-direction: row-reverse;
    }

    .message-body {
      background: linear-gradient(135deg, rgba(16, 185, 129, 0.15) 0%, rgba(22, 119, 255, 0.1) 100%);
      border-top-right-radius: 4px;
      border-bottom-right-radius: 4px;
      border-top-left-radius: $radius-base;
      border-bottom-left-radius: $radius-base;
      color: $text-primary;
    }

    .sender-name {
      color: $accent-color;
      margin-left: 6px;
      margin-right: 0;
    }
  }

  .message-content {
    display: flex;
    flex-direction: column;
    max-width: 78%;
  }

  .message-header {
    display: flex;
    align-items: center;
    margin-bottom: 4px;
  }

  .sender-name {
    font-size: $font-xs;
    font-weight: $font-semibold;
    color: $text-primary;
    margin-right: 6px;
    font-family: $font-family-base;
  }

  .message-time {
    font-size: 11px;
    color: $text-tertiary;
    font-family: $font-family-base;
  }

  .message-body {
    padding: $spacing-sm $spacing-md;
    font-size: $font-sm;
    line-height: $line-height-normal;
    background-color: $bg-white;
    border-radius: $radius-base;
    border-top-left-radius: 4px;
    box-shadow: $shadow-xs;
    font-family: $font-family-base;

    // Markdown 样式优化
    :deep(.ua__markdown) {
      font-size: $font-sm;
      line-height: $line-height-normal;
      color: $text-primary;
      word-wrap: break-word;

      // 代码块样式
      pre {
        background: #f6f8fa;
        border-radius: $radius-sm;
        padding: $spacing-sm;
        margin: $spacing-xs 0;
        overflow-x: auto;

        code {
          font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
          font-size: 13px;
          line-height: 1.5;
        }
      }

      // 行内代码样式
      code {
        background: rgba(0, 0, 0, 0.05);
        padding: 2px 6px;
        border-radius: 4px;
        font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
        font-size: 13px;
      }

      // 段落样式
      p {
        margin: $spacing-xs 0;

        &:first-child {
          margin-top: 0;
        }

        &:last-child {
          margin-bottom: 0;
        }
      }

      // 列表样式
      ul,
      ol {
        margin: $spacing-xs 0;
        padding-left: 20px;
      }

      li {
        margin: 4px 0;
      }

      // 标题样式
      h1,
      h2,
      h3,
      h4,
      h5,
      h6 {
        margin: $spacing-md 0 $spacing-xs;
        font-weight: $font-semibold;

        &:first-child {
          margin-top: 0;
        }
      }

      h1 {
        font-size: 20px;
      }

      h2 {
        font-size: 18px;
      }

      h3 {
        font-size: 16px;
      }

      h4 {
        font-size: 15px;
      }

      // 引用样式
      blockquote {
        border-left: 3px solid $border-base;
        padding-left: $spacing-sm;
        margin: $spacing-xs 0;
        color: $text-secondary;
      }

      // 表格样式
      table {
        border-collapse: collapse;
        width: 100%;
        margin: $spacing-xs 0;
        font-size: 13px;
      }

      th,
      td {
        border: 1px solid $border-base;
        padding: 6px 8px;
        text-align: left;
      }

      th {
        background: $bg-gray-50;
        font-weight: $font-semibold;
      }

      // 链接样式
      a {
        color: $primary-color;
        text-decoration: none;
      }

      // 分割线
      hr {
        border: none;
        border-top: 1px solid $border-base;
        margin: $spacing-md 0;
      }
    }
  }

  .typing-indicator {
    display: flex;
    align-items: center;

    .dot {
      width: 8px;
      height: 8px;
      border-radius: 4px;
      background-color: $text-tertiary;
      margin-right: 4px;
      animation: pulse 1.5s infinite ease-in-out;

      &:nth-child(2) {
        animation-delay: 0.2s;
      }

      &:nth-child(3) {
        animation-delay: 0.4s;
        margin-right: 0;
      }
    }
  }

  @keyframes pulse {

    0%,
    100% {
      opacity: 0.4;
      transform: scale(0.8);
    }

    50% {
      opacity: 1;
      transform: scale(1);
    }
  }

  .attachments {
    margin-top: $spacing-sm;
    display: flex;
    flex-direction: column;
    gap: $spacing-xs;

    .attachment-item {
      display: flex;
      align-items: center;
      background: rgba(255, 255, 255, 0.9);
      border-radius: $radius-sm;
      padding: $spacing-sm;
      transition: all $transition-base $ease-out;
      box-shadow: $shadow-xs;
      border: 1px solid $border-base;
      max-width: 280px;

      &:active {
        transform: scale(0.98);
        background: rgba(255, 255, 255, 0.95);
      }
    }

    .attachment-icon {
      margin-right: $spacing-sm;
      display: flex;
      align-items: center;
      justify-content: center;
      width: 36px;
      height: 36px;
      background: linear-gradient(135deg, rgba(16, 185, 129, 0.1) 0%, rgba(22, 119, 255, 0.08) 100%);
      border-radius: $radius-sm;
    }

    .attachment-info {
      display: flex;
      flex-direction: column;
      gap: 4px;
      flex: 1;
      min-width: 0;
    }

    .attachment-name {
      font-size: $font-xs;
      font-weight: $font-medium;
      color: $text-primary;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      font-family: $font-family-base;
    }

    .attachment-size {
      font-size: 12px;
      color: $text-tertiary;
      font-family: $font-family-base;
    }
  }

  // .bottom-space {
  //   height: 50px;
  // }
}

/* 输入区域样式 */
.input-area {
  position: fixed;
  bottom: 20px;
  left: 0;
  right: 0;
  width: 100%;
  background-color: rgba(255, 255, 255, 0.95);
  backdrop-filter: $backdrop-blur;
  border-top: 1px solid $border-light;
  padding: $spacing-xs $spacing-sm;
  box-shadow: 0 -1px 3px rgba(0, 0, 0, 0.05);
  z-index: 100;

  .attachment-preview {
    max-height: 120px;
    margin: $spacing-xs 0;
    padding: 0 4px;

    .attachment-scroll {
      white-space: nowrap;
      padding: 4px 0;
    }

    .preview-item {
      display: inline-flex;
      align-items: center;
      background: linear-gradient(135deg, $bg-gray-50 0%, $bg-gray-100 100%);
      border: 1px solid $border-base;
      border-radius: $radius-sm;
      padding: $spacing-xs $spacing-sm;
      margin-right: $spacing-sm;
      position: relative;
      max-width: 200px;
      transition: all $transition-base $ease-out;
      box-shadow: $shadow-xs;

      &:active {
        transform: scale(0.98);
      }

      .preview-content {
        display: flex;
        align-items: center;
        max-width: 170px;
        gap: $spacing-xs;
      }

      .preview-name {
        font-size: $font-xs;
        color: $text-primary;
        margin-left: 0;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
        font-weight: $font-medium;
        font-family: $font-family-base;
      }

      .remove-btn {
        position: absolute;
        top: -6px;
        right: -6px;
        width: 20px;
        height: 20px;
        border-radius: $radius-full;
        background: $danger-color;
        display: flex;
        align-items: center;
        justify-content: center;
        box-shadow: $shadow-sm;
        border: 2px solid $bg-white;
        transition: all $transition-fast $ease-out;

        &:active {
          transform: scale(0.9);
        }
      }
    }
  }

  .input-controls {
    display: flex;
    align-items: flex-end;
  }

  .upload-btn,
  .send-btn {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 40px;
    height: 40px;
    border-radius: 20px;
    flex-shrink: 0;
    transition: all $transition-base $ease-out;

    &:active {
      opacity: $opacity-hover;
      transform: scale(0.95);
    }
  }

  .upload-btn {
    background: linear-gradient(135deg, rgba(110, 231, 183, 0.15) 0%, rgba(167, 139, 250, 0.15) 100%);
    margin-right: $spacing-xs;
    border: 1px solid rgba(110, 231, 183, 0.2);
    transition: all $transition-base $ease-out;

    &:active {
      background: linear-gradient(135deg, rgba(110, 231, 183, 0.25) 0%, rgba(167, 139, 250, 0.25) 100%);
      transform: scale(0.95);
    }
  }

  .send-btn {
    margin-left: $spacing-xs;
    background-color: $bg-gray-200;
    position: relative;
    overflow: hidden;

    &.active {
      background: linear-gradient(135deg, #6ee7b7 0%, #a78bfa 50%, #fda4af 100%);
      box-shadow: 0 4rpx 16rpx rgba(110, 231, 183, 0.4);
      animation: pulse-glow 2s ease-in-out infinite;
    }

    &.active::before {
      content: '';
      position: absolute;
      top: 50%;
      left: 50%;
      width: 100%;
      height: 100%;
      background: radial-gradient(circle, rgba(255, 255, 255, 0.3) 0%, transparent 70%);
      transform: translate(-50%, -50%);
      animation: rotate-glow 3s linear infinite;
    }
  }

  .stop-btn {
    margin-left: $spacing-xs;
    display: flex;
    align-items: center;
    justify-content: center;
    width: 40px;
    height: 40px;
    border-radius: 20px;
    flex-shrink: 0;
    background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
    box-shadow: 0 4rpx 16rpx rgba(239, 68, 68, 0.4);
    transition: all $transition-base $ease-out;
    position: relative;
    overflow: hidden;
    animation: pulse-stop 1.5s ease-in-out infinite;

    &::before {
      content: '';
      position: absolute;
      top: 50%;
      left: 50%;
      width: 100%;
      height: 100%;
      background: radial-gradient(circle, rgba(255, 255, 255, 0.2) 0%, transparent 70%);
      transform: translate(-50%, -50%);
      animation: rotate-glow 2s linear infinite;
    }

    &:active {
      opacity: $opacity-hover;
      transform: scale(0.95);
      box-shadow: 0 2rpx 12rpx rgba(239, 68, 68, 0.5);
    }
  }

  @keyframes pulse-glow {

    0%,
    100% {
      box-shadow: 0 4rpx 16rpx rgba(110, 231, 183, 0.4);
    }

    50% {
      box-shadow: 0 6rpx 20rpx rgba(167, 139, 250, 0.5);
    }
  }

  @keyframes pulse-stop {

    0%,
    100% {
      box-shadow: 0 4rpx 16rpx rgba(239, 68, 68, 0.4);
    }

    50% {
      box-shadow: 0 6rpx 20rpx rgba(220, 38, 38, 0.6);
    }
  }

  @keyframes rotate-glow {
    0% {
      transform: translate(-50%, -50%) rotate(0deg) scale(1);
    }

    100% {
      transform: translate(-50%, -50%) rotate(360deg) scale(1.2);
    }
  }

  .input-wrapper {
    flex: 1;
    background-color: $bg-gray-100;
    border-radius: 20px;
    padding: $spacing-xs $spacing-sm;
    max-height: 120px;
    overflow-y: auto;
  }

  .input-box {
    width: 100%;
    min-height: 24px;
    font-size: $font-sm;
    line-height: $line-height-normal;
    background-color: transparent;
    color: $text-primary;
    font-family: $font-family-base;

    &::placeholder {
      color: $text-tertiary;
    }
  }
}

/* 历史对话抽屉样式 */
.history-drawer-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.4);
  z-index: 998;
  animation: fadeIn 0.3s ease;
}

.history-drawer {
  position: fixed;
  top: 0;
  right: -80%;
  bottom: 0;
  width: 80%;
  background-color: $bg-white;
  z-index: 999;
  box-shadow: -4px 0 10px rgba(0, 0, 0, 0.1);
  transition: transform $transition-slow $ease-out;
  display: flex;
  flex-direction: column;

  &.open {
    transform: translateX(-100%);
  }

  .drawer-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: $spacing-lg $spacing-base;
    background: linear-gradient(135deg, rgba(110, 231, 183, 0.08) 0%, rgba(167, 139, 250, 0.08) 100%);
    border-bottom: 1px solid rgba(167, 139, 250, 0.15);
    position: relative;
    overflow: hidden;

    // 装饰性渐变
    &::before {
      content: '';
      position: absolute;
      top: -50%;
      right: -20%;
      width: 150rpx;
      height: 150rpx;
      background: radial-gradient(circle, rgba(167, 139, 250, 0.15) 0%, transparent 70%);
      border-radius: $radius-full;
    }

    .drawer-title {
      font-size: $font-lg;
      font-weight: $font-bold;
      background: linear-gradient(135deg, #6ee7b7, #a78bfa);
      background-clip: text;
      -webkit-background-clip: text;
      color: transparent;
      font-family: $font-family-base;
      position: relative;
      z-index: 1;
    }

    .drawer-close {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 36px;
      height: 36px;
      border-radius: $radius-full;
      background: linear-gradient(135deg, rgba(110, 231, 183, 0.1) 0%, rgba(167, 139, 250, 0.1) 100%);
      transition: all $transition-fast $ease-out;
      position: relative;
      z-index: 1;

      &:active {
        background: linear-gradient(135deg, rgba(110, 231, 183, 0.2) 0%, rgba(167, 139, 250, 0.2) 100%);
        transform: scale(0.95);
      }
    }
  }

  .drawer-content {
    flex: 1;
    overflow-y: auto;
    padding: $spacing-sm 0;
  }

  .history-loading {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 200px;

    .loading-text {
      margin-top: $spacing-base;
      font-size: $font-xs;
      color: $text-tertiary;
      font-family: $font-family-base;
    }
  }

  .empty-history {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 200px;

    .empty-text {
      margin-top: $spacing-base;
      font-size: $font-xs;
      color: $text-tertiary;
      font-family: $font-family-base;
    }
  }

  .history-list {
    height: 100%;
  }

  .history-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: $spacing-md $spacing-base;
    border-bottom: 1px solid $border-light;
    transition: all $transition-fast $ease-out;

    &:active {
      background-color: $bg-gray-50;
    }

    .history-item-content {
      display: flex;
      align-items: center;
      flex: 1;
    }

    .history-icon {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 40px;
      height: 40px;
      border-radius: 20px;
      background: linear-gradient(135deg, rgba(16, 185, 129, 0.1) 0%, rgba(22, 119, 255, 0.08) 100%);
      margin-right: $spacing-sm;
    }

    .history-info {
      display: flex;
      flex-direction: column;
      justify-content: center;
      flex: 1;
      min-width: 0;

      .history-title {
        font-size: $font-sm;
        font-weight: $font-medium;
        color: $text-primary;
        line-height: 1.5;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
        font-family: $font-family-base;
      }
    }

    .history-actions {
      display: flex;
      align-items: center;
      padding-left: $spacing-sm;
    }
  }
}

.new-chat-button {
  margin: $spacing-base;
  margin-bottom: $spacing-md;

  .button-content {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    background: linear-gradient(135deg, #6ee7b7 0%, #a78bfa 50%, #fda4af 100%);
    padding: $spacing-md $spacing-lg;
    border-radius: $radius-xl;
    transition: all $transition-base $ease-out;
    box-shadow: 0 4rpx 16rpx rgba(110, 231, 183, 0.3);
    border: 1px solid rgba(255, 255, 255, 0.3);
    position: relative;
    overflow: hidden;

    // 装饰性光晕
    &::before {
      content: '';
      position: absolute;
      top: -50%;
      left: -50%;
      width: 200%;
      height: 200%;
      background: radial-gradient(circle, rgba(255, 255, 255, 0.2) 0%, transparent 70%);
      animation: float-glow 4s ease-in-out infinite;
    }

    &:active {
      transform: scale(0.97);
      box-shadow: 0 2rpx 12rpx rgba(110, 231, 183, 0.4);
    }
  }

  .new-chat-text {
    font-size: $font-base;
    font-weight: $font-semibold;
    color: $bg-white;
    font-family: $font-family-base;
    position: relative;
    z-index: 1;
    text-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.1);
  }

  uni-icons {
    position: relative;
    z-index: 1;
    filter: drop-shadow(0 2rpx 4rpx rgba(0, 0, 0, 0.1));
  }
}

@keyframes float-glow {

  0%,
  100% {
    transform: translate(0, 0) scale(1);
    opacity: 0.6;
  }

  50% {
    transform: translate(-20rpx, -20rpx) scale(1.1);
    opacity: 0.8;
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }

  to {
    opacity: 1;
  }
}

/* 浮动历史记录按钮 */
.floating-history-btn {
  position: fixed;
  top: calc(env(safe-area-inset-top) + 80rpx);
  right: $spacing-lg;
  width: 56px;
  height: 56px;
  z-index: 500;

  .btn-content {
    position: relative;
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    background: linear-gradient(135deg, #6ee7b7 0%, #a78bfa 50%, #fda4af 100%);
    border-radius: $radius-full;
    box-shadow: 0 6rpx 20rpx rgba(110, 231, 183, 0.4);
    transition: all $transition-base $ease-out;
    z-index: 2;

    &:active {
      transform: scale(0.95);
      box-shadow: 0 4rpx 16rpx rgba(167, 139, 250, 0.5);
    }
  }

  .btn-glow {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    border-radius: $radius-full;
    background: linear-gradient(135deg, #6ee7b7 0%, #a78bfa 50%, #fda4af 100%);
    opacity: 0.6;
    animation: pulse-btn 2s ease-in-out infinite;
    z-index: 1;
  }
}

@keyframes pulse-btn {

  0%,
  100% {
    transform: scale(1);
    opacity: 0.6;
  }

  50% {
    transform: scale(1.15);
    opacity: 0.3;
  }
}
</style>
