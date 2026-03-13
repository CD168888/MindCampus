<template>
  <view class="frosted-tabbar-wrapper">
    <view class="frosted-dock">
      <view 
        v-for="(item, index) in tabList" 
        :key="index" 
        class="tab-item" 
        @tap="switchTab(item.pagePath)"
      >
        <view class="tab-content" :class="{ 'is-active': currentPath === item.pagePath }">
          <uni-icons 
            :type="currentPath === item.pagePath ? item.selectedIcon : item.icon" 
            :size="22" 
            :color="currentPath === item.pagePath ? '#2CB5A0' : '#86868B'"
            class="tab-icon"
          ></uni-icons>
          
          <text class="tab-text" :style="{ color: currentPath === item.pagePath ? '#2CB5A0' : '#86868B' }">
            {{ item.text }}
          </text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  name: 'CustomTabBar',
  props: {
    currentPath: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      // 严格对齐你的 4 个核心栏目
      tabList: [
        {
          pagePath: '/pages/index',
          icon: 'home',
          selectedIcon: 'home-filled',
          text: '首页'
        },
        {
          pagePath: '/pages/community/index',
          icon: 'chatboxes',
          selectedIcon: 'chatboxes-filled',
          text: '社区'
        },
        {
          pagePath: '/pages/ai/chat',
          icon: 'chatbubble',
          selectedIcon: 'chatbubble-filled',
          text: 'AI治愈'
        },
        {
          pagePath: '/pages/mine/index',
          icon: 'person',
          selectedIcon: 'person-filled',
          text: '我的'
        }
      ]
    }
  },
  methods: {
    switchTab(path) {
      if (this.currentPath === path) return;
      
      // 苹果级触觉反馈
      uni.vibrateShort();
      
      uni.switchTab({
        url: path
      });
    }
  }
}
</script>

<style scoped>
/* 引入分离的核心样式文件 */
@import url('./custom-tab-bar.css');
</style>