# MindCampus 首页快速使用指南

## 🚀 立即开始

### 1. 运行项目

```bash
# H5 端（推荐先用这个测试）
npm run dev:h5

# 浏览器访问
http://localhost:9090
```

### 2. 查看效果

首页包含以下模块：
- ✅ 英雄区域（渐变背景 + 个性化问候）
- ✅ 心理测评入口（大卡片按钮）
- ✅ 心理状态仪表盘（圆形进度 + 三项指标）
- ✅ 今日建议卡片
- ✅ 音乐疗愈模块（播放器 + 音乐列表）
- ✅ 文章推荐模块（三篇推荐文章）

## 🎨 设计亮点

### 1. 温暖治愈系配色方案
```
主品牌色: #10b981 (翡翠绿 - 生命、希望、疗愈)
辅助色: #fb923c (温暖橙 - 活力、温暖、积极)
中性色: #1f2937, #6b7280, #f9fafb
```
💡 **为什么选择绿色？** 摒弃传统蓝色的冷冰冰医疗感，绿色代表自然、生命与希望，更符合心理健康平台的温暖基调。

### 2. 精致的交互动画
- 卡片点击缩放反馈
- 按钮状态变化
- 平滑过渡效果（0.25s）

### 3. 统一的视觉语言
- 圆角: 12-16rpx
- 阴影: rgba(0,0,0,0.06-0.10)
- 间距: 24-32rpx

## 📝 自定义修改

### 修改心理评分

编辑 `pages/index.vue`：

```javascript
data() {
  return {
    mentalScore: 85,        // 修改评分（0-100）
    mentalLevel: '优秀',    // 修改等级
    // ...
  }
}
```

仪表盘会自动根据评分显示对应颜色：
- **≥80分**: 翡翠绿 `#10b981`（优秀）
- **60-79分**: 薄荷绿 `#34d399`（良好）
- **<60分**: 金黄色 `#fbbf24`（需关注）

### 修改心理指标

```javascript
indicators: [
  { value: '8.5', label: '情绪指数' },
  { value: '7.2', label: '压力水平' },
  { value: '9.0', label: '社交质量' }
]
```

### 添加音乐

```javascript
musicList: [
  { 
    icon: '🎵',              // emoji 图标
    title: '新音乐名称', 
    artist: '分类名称', 
    duration: '10:00'       // 时长
  },
  // ... 更多音乐
]
```

### 添加文章

```javascript
articleList: [
  {
    cover: '📖',            // emoji 封面
    title: '文章标题',
    excerpt: '文章摘要内容...',
    views: '1.5k',          // 浏览量
    likes: '120'            // 点赞数
  },
  // ... 更多文章
]
```

## 🔗 对接后端接口

### 1. 获取心理状态数据

在 `api/` 目录创建 `mental.js`：

```javascript
import request from '@/utils/request'

// 获取心理状态
export function getMentalStatus() {
  return request({
    url: '/mental/status',
    method: 'get'
  })
}
```

在 `pages/index.vue` 中调用：

```javascript
import { getMentalStatus } from '@/api/mental'

onLoad() {
  this.getUserInfo()
  this.getMentalData()  // 新增
},

methods: {
  // 获取心理数据
  async getMentalData() {
    try {
      const res = await getMentalStatus()
      this.mentalScore = res.score
      this.mentalLevel = res.level
      this.indicators = res.indicators
    } catch (error) {
      console.error('获取心理数据失败', error)
    }
  }
}
```

### 2. 获取音乐列表

```javascript
// api/music.js
export function getMusicList() {
  return request({
    url: '/music/list',
    method: 'get'
  })
}

// pages/index.vue
async getMusicData() {
  const res = await getMusicList()
  this.musicList = res.data
}
```

### 3. 获取文章列表

```javascript
// api/article.js
export function getArticleList(params) {
  return request({
    url: '/article/list',
    method: 'get',
    params
  })
}

// pages/index.vue
async getArticleData() {
  const res = await getArticleList({ pageNum: 1, pageSize: 3 })
  this.articleList = res.rows
}
```

## 🎯 页面跳转实现

### 1. 创建测评页面

```bash
# 在 pages 目录创建
pages/
  assessment/
    index.vue          # 测评列表页
    detail.vue         # 测评详情页
    result.vue         # 测评结果页
```

在 `pages.json` 添加路由：

```json
{
  "path": "pages/assessment/index",
  "style": {
    "navigationBarTitleText": "心理测评"
  }
}
```

更新 `pages/index.vue` 跳转逻辑：

```javascript
openAssessment() {
  this.$tab.navigateTo('/pages/assessment/index')
}
```

### 2. 创建音乐页面

```bash
pages/
  music/
    list.vue           # 音乐列表
    player.vue         # 播放器页面
```

### 3. 创建文章页面

```bash
pages/
  article/
    list.vue           # 文章列表
    detail.vue         # 文章详情
```

## 🎨 主题变量使用

所有主题变量都定义在 `static/scss/theme.scss`。

### 在组件中使用

```vue
<style lang="scss" scoped>
@import '@/static/scss/theme.scss';

.my-card {
  background: $bg-white;
  color: $text-primary;
  border-radius: $radius-md;
  padding: $spacing-md;
  box-shadow: $shadow-sm;
  
  &:active {
    transform: scale(0.98);
    transition: all $transition-base $ease-out;
  }
}

.my-button {
  background: $primary-color;
  color: $bg-white;
  border-radius: $radius-full;
  padding: $spacing-sm $spacing-md;
  
  &:hover {
    opacity: $opacity-hover;
  }
}
</style>
```

### 常用变量速查

```scss
// 颜色
$primary-color         // 主品牌色
$text-primary          // 主文字色
$text-secondary        // 副文字色
$bg-white              // 白色背景
$bg-gray-50            // 浅灰背景

// 间距
$spacing-xs: 8rpx
$spacing-sm: 12rpx
$spacing-base: 16rpx
$spacing-md: 24rpx
$spacing-lg: 32rpx
$spacing-xl: 48rpx

// 圆角
$radius-sm: 8rpx
$radius-base: 12rpx
$radius-md: 16rpx
$radius-lg: 20rpx
$radius-full: 9999rpx

// 阴影
$shadow-xs             // 最轻
$shadow-sm             // 轻微
$shadow-base           // 基础
$shadow-md             // 中等
$shadow-lg             // 较强
$shadow-xl             // 最强

// 字体
$font-xs: 20rpx
$font-sm: 24rpx
$font-base: 28rpx
$font-lg: 32rpx
$font-xl: 36rpx
$font-2xl: 40rpx

// 过渡
$transition-fast: 0.15s
$transition-base: 0.25s
$transition-slow: 0.35s
```

## 🔍 调试技巧

### 1. 查看状态数据

在浏览器控制台：

```javascript
// 查看首页数据
this.$vm.$children[0].$data
```

### 2. 修改配色方案

如需调整配色，编辑 `static/scss/theme.scss`：

```scss
// 当前方案：翡翠绿（温暖治愈系）
$primary-color: #10b981;

// 可选方案 A：更鲜艳的绿色
$primary-color: #22c55e;

// 可选方案 B：青绿色（Teal）
$primary-color: #14b8a6;

// 可选方案 C：柔和紫色
$primary-color: #a78bfa;
```

详细配色说明请查看：[配色方案文档](./COLOR_SCHEME.md)

### 3. H5 端实时调试

使用 Chrome DevTools：
1. F12 打开开发者工具
2. 切换到手机模拟模式（iPhone 12 Pro）
3. 实时查看效果

## ⚠️ 常见问题

### Q1: 样式变量不生效？

**解决方案**：确保在 `<style>` 标签中导入主题文件

```vue
<style lang="scss" scoped>
@import '@/static/scss/theme.scss';
/* 你的样式 */
</style>
```

### Q2: 渐变色显示不正确？

**解决方案**：检查是否使用了正确的渐变语法

```scss
// ✅ 正确
background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);

// ❌ 错误
background: linear-gradient(135deg, #667eea, #764ba2);
```

### Q3: TabBar 颜色未更新？

**解决方案**：重新编译项目

```bash
# 清除缓存
rm -rf unpackage/

# 重新运行
npm run dev:h5
```

### Q4: 小程序端样式差异？

**解决方案**：部分 CSS 属性在小程序端不支持，需要替代方案

```scss
// H5 支持，小程序不支持
gap: 16rpx;

// 改为（兼容方案）
margin-bottom: 16rpx;
```

## 📚 扩展阅读

- [uni-app 官方文档](https://uniapp.dcloud.net.cn/)
- [Vue.js 官方文档](https://v2.cn.vuejs.org/)
- [SCSS 语法指南](https://sass-lang.com/guide)
- [设计规范参考](./HOME_OPTIMIZATION.md)

## 💡 最佳实践

1. **保持代码整洁**: 使用统一的命名规范
2. **复用组件**: 将重复的 UI 提取为组件
3. **性能优化**: 避免过度渲染
4. **注释清晰**: 关键代码添加注释
5. **版本控制**: 使用 Git 管理代码

## 🎉 下一步

1. ✅ 首页界面已完成
2. ⏭️ 开发心理测评模块
3. ⏭️ 开发音乐播放器
4. ⏭️ 开发文章详情页
5. ⏭️ 对接后端接口

---

**祝您开发顺利！** 🚀

如有问题，请查看 [详细文档](./HOME_OPTIMIZATION.md) 或联系开发团队。

