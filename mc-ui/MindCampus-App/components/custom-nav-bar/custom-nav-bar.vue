<template>
  <view class="custom-nav-bar" :style="{ paddingTop: statusBarHeight + 'px', height: navBarHeight + 'px' }">
    <view class="nav-bar-content">
      <!-- 左侧插槽 -->
      <view class="nav-left">
        <slot name="left"></slot>
      </view>
      
      <!-- 标题插槽 -->
      <view class="nav-title">
        <slot name="title">
          <text class="title-text">{{ title }}</text>
        </slot>
      </view>
      
      <!-- 右侧插槽 -->
      <view class="nav-right">
        <slot name="right">
          <view class="search-btn" v-if="showSearchBtn" @click="onSearchClick">
            <uni-icons type="search" size="22" color="#333"></uni-icons>
          </view>
        </slot>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  name: 'CustomNavBar',
  props: {
    // 标题
    title: {
      type: String,
      default: ''
    },
    // 是否显示搜索按钮
    showSearchBtn: {
      type: Boolean,
      default: true
    }
  },
  data() {
    return {
      statusBarHeight: 0,
      navBarHeight: 44
    }
  },
  mounted() {
    this.getSystemInfo();
  },
  methods: {
    // 获取系统信息
    getSystemInfo() {
      const systemInfo = uni.getSystemInfoSync();
      this.statusBarHeight = systemInfo.statusBarHeight || 0;
      // 导航栏内容高度固定44px
      this.navBarHeight = this.statusBarHeight + 44;
    },
    
    // 搜索按钮点击
    onSearchClick() {
      this.$emit('search');
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/static/scss/theme.scss';

.custom-nav-bar {
  width: 100%;
  background: $bg-white;
  box-shadow: $shadow-xs;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 999;
}

.nav-bar-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 44px;
  padding: 0 $spacing-base;
}

.nav-left,
.nav-right {
  display: flex;
  align-items: center;
  min-width: 60px;
}

.nav-left {
  justify-content: flex-start;
}

.nav-right {
  justify-content: flex-end;
}

.nav-title {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 $spacing-sm;
  
  .title-text {
    font-size: $font-lg;
    font-weight: $font-bold;
    color: $text-primary;
    font-family: $font-family-base;
  }
}

.search-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border-radius: $radius-sm;
  transition: all $transition-fast $ease-out;
  
  &:active {
    transform: scale(0.95);
    background-color: rgba(0, 0, 0, 0.05);
  }
}
</style>

