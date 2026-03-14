<template>
  <view class="help-page">
    <view class="ambient-background"></view>

    <view class="glass-header" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="navbar-content">
        <view class="navbar-left" @tap="goBack">
          <view class="nav-icon-glass">
            <uni-icons type="left" size="22" color="#1D1D1F"></uni-icons>
          </view>
        </view>
        <view class="navbar-title">常见问题</view>
        <view class="navbar-right"></view>
      </view>
    </view>

    <scroll-view class="content-scroll" scroll-y>
      
      <view class="help-hero">
        <view class="hero-icon-box">
          <uni-icons type="chatboxes-filled" size="44" color="#2CB5A0"></uni-icons>
        </view>
        <text class="hero-title">需要帮忙吗？</text>
        <text class="hero-desc">我们为您准备了一些常见问题的解答</text>
      </view>

      <view class="faq-wrapper">
        <view v-for="(item, findex) in list" :key="findex" class="faq-group">
          
          <view class="group-header">
            <view class="icon-bg" :class="getGroupColor(findex)">
              <uni-icons :type="getGroupIcon(findex)" size="18" color="#FFFFFF"></uni-icons>
            </view>
            <text class="group-title">{{ item.title }}</text>
          </view>

          <view class="glass-card list-card">
            <view v-for="(child, zindex) in item.childList" :key="zindex" class="faq-item-container">
              
              <view class="list-item" @tap="toggleExpand(child)">
                <text class="item-text" :class="{ 'text-active': child.isOpen }">{{ child.title }}</text>
                
                <view class="icon-arrow" :class="{ 'arrow-down': child.isOpen }">
                  <uni-icons type="right" size="16" :color="child.isOpen ? '#2CB5A0' : '#C7C7CC'"></uni-icons>
                </view>
              </view>

              <view class="answer-box" :class="{ 'is-open': child.isOpen }">
                <view class="answer-content">
                  <text user-select>{{ child.content }}</text>
                </view>
              </view>
              
              <view class="divider" v-if="zindex !== item.childList.length - 1"></view>
            </view>
          </view>

        </view>
      </view>

      <view class="bottom-spacer"></view>
    </scroll-view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      statusBarHeight: 0,
      // FAQ 数据：增加了 isOpen 字段用于独立控制折叠状态
      list: [
        {
          title: '关于项目',
          childList: [
            {
              title: '项目是开源的吗？',
              content: '本项目目前暂未完全开源，你可以访问我的 GitHub 主页：https://github.com/CD168888 关注我，也可以为其他项目点亮 Star 支持。待毕设完成后（6 月），项目将全部开源。',
              isOpen: false
            }
          ]
        },
        {
          title: '核心功能',
          childList: [
            {
              title: 'AI 助手的回复是真人吗？',
              content: '不是的，AI 助手是基于大语言模型驱动的智能干预伴侣。它受过专业的心理辅导语料训练，为您提供24小时的倾听与疏导，但非真人回复。',
              isOpen: false
            },
            {
              title: '社区发帖可以匿名吗？',
              content: '完全可以！在发布瞬间时勾选底部的“匿名发布”开关，系统将严格保护您的隐私，隐藏您的真实头像和昵称。',
              isOpen: false
            }
          ]
        },
        {
          title: '账号管理',
          childList: [
            {
              title: '如何修改密码或退出登录？',
              content: '请点击底部导航的[我的] - 进入[应用设置]，即可进行修改密码、清理缓存或安全退出账号等操作。',
              isOpen: false
            }
          ]
        }
      ]
    }
  },
  onLoad() {
    const sys = uni.getSystemInfoSync()
    this.statusBarHeight = sys.statusBarHeight || 0
  },
  methods: {
    goBack() {
      uni.navigateBack()
    },

    // 核心修复：将 github 替换为系统自带的 info-filled (关于/详情)
    getGroupIcon(index) {
      const icons = ['info-filled', 'chatbubble-filled', 'gear-filled']
      return icons[index % icons.length]
    },

    // 动态获取多巴胺背景色
    getGroupColor(index) {
      const colors = ['bg-gray-dark', 'bg-cyan', 'bg-blue']
      return colors[index % colors.length]
    },

    // 切换折叠/展开状态
    toggleExpand(child) {
      // 触觉反馈增强高级感
      uni.vibrateShort()
      // 切换当前点击项的状态
      child.isOpen = !child.isOpen
    }
  }
}
</script>

<style lang="scss" scoped>
/* ==================== iOS 风格核心变量 ==================== */
$ios-text-primary: #1D1D1F;
$ios-text-secondary: #86868B;
$theme-cyan: #2CB5A0;

.help-page {
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
  border-radius: 32rpx;
}

/* ==================== 顶部导航 (Sticky) ==================== */
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

.navbar-title { font-size: 32rpx; font-weight: 600; color: $ios-text-primary; }

/* ==================== 内容滚动区 ==================== */
.content-scroll {
  position: relative;
  z-index: 1;
  height: calc(100vh - 88rpx - env(safe-area-inset-top));
}

/* ==================== 1. 顶部视觉引导区 (Hero Section) ==================== */
.help-hero {
  padding: 60rpx 40rpx 20rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.hero-icon-box {
  width: 120rpx; height: 120rpx;
  border-radius: 36rpx;
  background: rgba(44, 181, 160, 0.15);
  backdrop-filter: blur(10px);
  display: flex; align-items: center; justify-content: center;
  margin-bottom: 24rpx;
  box-shadow: 0 12rpx 32rpx rgba(44, 181, 160, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.6);
}

.hero-title {
  font-size: 40rpx;
  font-weight: 700;
  color: $ios-text-primary;
  margin-bottom: 12rpx;
  letter-spacing: 1rpx;
}

.hero-desc {
  font-size: 26rpx;
  color: $ios-text-secondary;
}

/* ==================== 2. 问题分组区 ==================== */
.faq-wrapper {
  padding: 20rpx 32rpx;
}

.faq-group {
  margin-bottom: 48rpx;
}

/* 分组头部：图标 + 标题 */
.group-header {
  display: flex;
  align-items: center;
  gap: 16rpx;
  margin-bottom: 20rpx;
  padding-left: 8rpx;
}

.icon-bg {
  width: 48rpx; height: 48rpx;
  border-radius: 12rpx;
  display: flex; align-items: center; justify-content: center;
  box-shadow: 0 4rpx 12rpx rgba(0,0,0,0.1);
}

/* 多巴胺配色类 */
.bg-blue { background: linear-gradient(135deg, #5AC8FA 0%, #007AFF 100%); }
.bg-cyan { background: linear-gradient(135deg, #48D1CC 0%, #2CB5A0 100%); }
.bg-gray-dark { background: linear-gradient(135deg, #8E8E93 0%, #3A3A3C 100%); }

.group-title {
  font-size: 30rpx;
  font-weight: 600;
  color: $ios-text-primary;
}

/* 列表卡片内部 */
.list-card {
  padding: 0 32rpx;
}

.faq-item-container {
  display: flex;
  flex-direction: column;
}

.list-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 36rpx 0;
  transition: opacity 0.2s ease;
  
  &:active {
    opacity: 0.6;
  }
}

.item-text {
  font-size: 28rpx;
  color: $ios-text-primary;
  font-weight: 500;
  line-height: 1.4;
  flex: 1;
  padding-right: 20rpx;
  transition: color 0.3s ease;
}

/* 展开时标题高亮 */
.text-active {
  color: $theme-cyan;
  font-weight: 600;
}

/* 旋转箭头指示器 */
.icon-arrow {
  transition: transform 0.3s cubic-bezier(0.2, 0.8, 0.2, 1);
  display: flex;
  align-items: center;
  justify-content: center;
}

.arrow-down {
  transform: rotate(90deg); /* 展开时向下指 */
}

/* 核心：答案折叠区域动画 */
.answer-box {
  max-height: 0;
  opacity: 0;
  overflow: hidden;
  transition: max-height 0.4s cubic-bezier(0.2, 0.8, 0.2, 1), opacity 0.3s ease;
}

.answer-box.is-open {
  max-height: 500rpx; /* 设定一个足够大的高度以容纳内容 */
  opacity: 1;
}

.answer-content {
  padding: 0 16rpx 36rpx 0;
  
  text {
    font-size: 26rpx;
    color: $ios-text-secondary;
    line-height: 1.6;
    display: block;
    text-align: justify;
  }
}

/* 苹果风格内凹极细分割线 */
.divider {
  height: 1px;
  background: rgba(0, 0, 0, 0.05);
  margin-left: 0;
}

/* 底部防遮挡留白 */
.bottom-spacer {
  height: calc(80rpx + env(safe-area-inset-bottom));
}
</style>