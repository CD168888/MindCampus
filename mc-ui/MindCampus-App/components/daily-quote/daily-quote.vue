<template>
  <view class="daily-quote-card" @tap="onCardClick">
    <view class="quote-left">
      <text class="quote-icon">💡</text>
      <text class="quote-label">心灵鸡汤</text>
    </view>
    
    <view class="quote-center">
      <swiper 
        vertical 
        :autoplay="true" 
        :interval="3000" 
        :duration="400" 
        :circular="true" 
        :disable-touch="false"
        class="quote-swiper"
      >
        <swiper-item v-for="(quote, index) in quoteList" :key="index">
          <view class="quote-text">{{ quote.content }}</view>
        </swiper-item>
      </swiper>
    </view>
    
    <view class="quote-right">
      <text class="arrow-icon">›</text>
    </view>
  </view>
</template>

<script>
export default {
  name: 'DailyQuote',
  props: {
    // 自定义鸡汤列表
    quotes: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      // 默认心灵鸡汤列表
      defaultQuotes: [
        { content: '每一次努力都是幸运的伏笔，保持微笑，好运会不期而遇 ✨' },
        { content: '你的心态，决定你看到的风景。积极向上，生活才会充满阳光 🌟' },
        { content: '不要害怕改变，最好的自己往往在舒适区之外等着你 🌈' },
        { content: '压力是生活的调味品，学会与它共处，你会变得更强大 💪' },
        { content: '每个清晨都是新的开始，昨天的烦恼就让它随风而去吧 🌅' },
        { content: '善待自己，你值得拥有世界上所有的温柔与美好 🌸' },
        { content: '人生没有白走的路，每一步都算数，慢慢来比较快 🚶' },
        { content: '保持热爱，奔赴山海。生活总会在某个转角给你惊喜 🎁' }
      ]
    }
  },
  computed: {
    quoteList() {
      // 如果传入了自定义列表就使用自定义的，否则使用默认的
      return this.quotes.length > 0 ? this.quotes : this.defaultQuotes
    }
  },
  methods: {
    onCardClick() {
      this.$emit('click')
      // 可以跳转到鸡汤列表页面
      // uni.navigateTo({
      //   url: '/pages/quotes/list'
      // })
    }
  }
}
</script>

<style lang="scss" scoped>
@import "@/static/scss/theme.scss";

.daily-quote-card {
  margin: 0 $spacing-lg $spacing-lg;
  padding: $spacing-lg $spacing-md;
  background: $bg-white;
  border-radius: $radius-lg;
  box-shadow: $shadow-md;
  border: 1rpx solid rgba(110, 231, 183, 0.08);
  position: relative;
  overflow: hidden;
  z-index: 2;
  display: flex;
  align-items: center;
  gap: $spacing-md;
  transition: all $transition-base $ease-out;
  
  // 背景微妙渐变
  &::after {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: $gradient-card;
    opacity: 0;
    transition: opacity $transition-base $ease-out;
    z-index: -1;
  }
  
  &:active {
    transform: scale(0.98);
    
    &::after {
      opacity: 1;
    }
  }
}

.quote-left {
  display: flex;
  align-items: center;
  gap: $spacing-xs;
  flex-shrink: 0;
}

.quote-icon {
  font-size: 32rpx;
  filter: drop-shadow(0 2rpx 4rpx rgba(110, 231, 183, 0.3));
}

.quote-label {
  font-size: $font-base;
  font-weight: $font-bold;
  background: $gradient-primary;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: 0.5rpx;
}

.quote-center {
  flex: 1;
  height: 44rpx;
  overflow: hidden;
  position: relative;
}

.quote-swiper {
  height: 100%;
  width: 100%;
}

.quote-text {
  font-size: $font-sm;
  color: $text-secondary;
  line-height: 44rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-weight: $font-medium;
  padding-right: $spacing-xs;
}

.quote-right {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40rpx;
  height: 40rpx;
  border-radius: $radius-sm;
  background: rgba(110, 231, 183, 0.08);
  transition: all $transition-base $ease-out;
}

.arrow-icon {
  font-size: 32rpx;
  color: $primary-color;
  font-weight: $font-bold;
  line-height: 1;
}

.daily-quote-card:active .quote-right {
  background: rgba(110, 231, 183, 0.15);
  transform: translateX(4rpx);
}
</style>

