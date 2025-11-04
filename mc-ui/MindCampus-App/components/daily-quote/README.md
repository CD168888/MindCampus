# 心灵鸡汤组件 (DailyQuote)

一个精美的心灵鸡汤轮播卡片组件，采用垂直滚动展示，支持自定义内容。

## 📸 效果预览

```
┌─────────────────────────────────────────┐
│ 💡 心灵鸡汤  每一次努力都是幸运的伏笔... › │
└─────────────────────────────────────────┘
       ↓ 3秒后自动切换
┌─────────────────────────────────────────┐
│ 💡 心灵鸡汤  你的心态，决定你看到的风景... › │
└─────────────────────────────────────────┘
```

## 🚀 使用方法

### 基础用法

```vue
<template>
  <daily-quote @click="handleClick"></daily-quote>
</template>

<script>
import DailyQuote from '@/components/daily-quote/daily-quote.vue'

export default {
  components: {
    DailyQuote
  },
  methods: {
    handleClick() {
      // 点击卡片时的处理
      uni.navigateTo({
        url: '/pages/quotes/list'
      })
    }
  }
}
</script>
```

### 自定义鸡汤内容

```vue
<template>
  <daily-quote :quotes="customQuotes" @click="handleClick"></daily-quote>
</template>

<script>
import DailyQuote from '@/components/daily-quote/daily-quote.vue'

export default {
  components: {
    DailyQuote
  },
  data() {
    return {
      customQuotes: [
        { content: '自定义的心灵鸡汤内容 1' },
        { content: '自定义的心灵鸡汤内容 2' },
        { content: '自定义的心灵鸡汤内容 3' }
      ]
    }
  },
  methods: {
    handleClick() {
      console.log('点击了心灵鸡汤卡片')
    }
  }
}
</script>
```

## 📝 Props

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| quotes | Array | [] | 自定义鸡汤列表，每项需包含 `content` 字段 |

### quotes 数据结构

```javascript
[
  { content: '鸡汤文字内容' },
  { content: '鸡汤文字内容' },
  ...
]
```

## 🎯 Events

| 事件名 | 说明 | 回调参数 |
|--------|------|----------|
| click | 点击卡片时触发 | - |

## 🎨 设计特点

### 视觉效果
- ✨ **渐变装饰条**：顶部彩色渐变线条
- 🌈 **微妙背景渐变**：点击时显示淡淡的渐变背景
- 💫 **图标阴影**：左侧emoji带有柔和投影
- 🎯 **文字渐变色**："心灵鸡汤"标题使用渐变色文字
- 🔘 **圆角按钮**：右侧箭头带有淡色背景和圆角

### 交互效果
- 📱 **垂直轮播**：每3秒自动切换到下一条鸡汤
- 🔄 **循环播放**：播放到最后一条后自动回到第一条
- ⚡ **点击反馈**：点击时卡片轻微缩小，背景渐变显现
- 👉 **箭头动画**：点击时右侧箭头向右移动

### 技术实现
- 📏 **紧凑布局**：卡片高度控制在合理范围
- 🎯 **单行显示**：文字过长自动截断并显示省略号
- 📱 **响应式间距**：使用主题变量保持风格统一
- 🎨 **主题色系**：使用全局 `$gradient-primary` 保持品牌一致性

## 🎁 内置鸡汤内容

组件内置了8条精心挑选的心灵鸡汤：

1. 每一次努力都是幸运的伏笔，保持微笑，好运会不期而遇 ✨
2. 你的心态，决定你看到的风景。积极向上，生活才会充满阳光 🌟
3. 不要害怕改变，最好的自己往往在舒适区之外等着你 🌈
4. 压力是生活的调味品，学会与它共处，你会变得更强大 💪
5. 每个清晨都是新的开始，昨天的烦恼就让它随风而去吧 🌅
6. 善待自己，你值得拥有世界上所有的温柔与美好 🌸
7. 人生没有白走的路，每一步都算数，慢慢来比较快 🚶
8. 保持热爱，奔赴山海。生活总会在某个转角给你惊喜 🎁

## 📐 尺寸规格

| 属性 | 值 | 说明 |
|------|-----|------|
| 卡片高度 | 自适应 (~88rpx) | 根据内容自动调整 |
| 卡片圆角 | 24rpx | $radius-lg |
| 图标大小 | 32rpx | 左侧emoji |
| 标题字号 | 26rpx | $font-base |
| 内容字号 | 24rpx | $font-sm |
| 箭头大小 | 32rpx | 右侧箭头 |
| 轮播高度 | 44rpx | 单行文字高度 |

## 🔧 自定义样式

如需自定义样式，可以通过以下方式：

### 修改主题变量

在 `static/scss/theme.scss` 中调整：

```scss
$gradient-primary: linear-gradient(135deg, #your-color-1, #your-color-2);
$radius-lg: 24rpx;
$font-base: 26rpx;
```

### 覆盖组件样式

```vue
<style>
/* 修改卡片背景色 */
.daily-quote-card {
  background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%) !important;
}

/* 修改标题颜色 */
.quote-label {
  color: #your-color !important;
}
</style>
```

## ⚙️ 轮播配置

当前轮播配置：

```javascript
{
  vertical: true,        // 垂直滚动
  autoplay: true,        // 自动播放
  interval: 3000,        // 3秒间隔
  duration: 400,         // 400ms切换动画
  circular: true,        // 循环播放
  disable-touch: false   // 允许手动滑动
}
```

如需调整，可在组件内修改 `<swiper>` 的属性。

## 💡 使用建议

1. **内容长度**：建议每条鸡汤控制在 30 字以内，避免超出单行显示
2. **更新频率**：可以接入后端API，定期更新鸡汤内容
3. **个性化**：可根据用户的心理状态推送不同类型的鸡汤
4. **时段适配**：早中晚可以展示不同主题的鸡汤内容

## 🌟 进阶功能

### 接入后端API

```vue
<script>
export default {
  data() {
    return {
      quotes: []
    }
  },
  async onLoad() {
    // 从后端获取鸡汤内容
    const res = await this.$http.get('/api/quotes/daily')
    this.quotes = res.data
  }
}
</script>
```

### 添加分享功能

```vue
<template>
  <daily-quote @click="shareQuote"></daily-quote>
</template>

<script>
export default {
  methods: {
    shareQuote() {
      uni.share({
        provider: 'weixin',
        scene: 'WXSceneTimeline',
        title: '今日心灵鸡汤',
        summary: this.currentQuote,
        imageUrl: '/static/share-image.png'
      })
    }
  }
}
</script>
```

