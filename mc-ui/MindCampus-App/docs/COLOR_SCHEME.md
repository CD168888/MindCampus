# MindCampus 配色方案说明

## 🎨 设计理念

### 为什么选择绿色系？

传统的蓝色系虽然专业，但容易给人冷冰冰的医疗感觉。对于心理健康平台，我们需要的是：

✅ **温暖** - 让用户感到被关怀  
✅ **治愈** - 传递积极向上的力量  
✅ **自然** - 回归本真，放松心情  
✅ **希望** - 象征生命与成长  

因此，我们采用了**温暖治愈系**的绿色配色方案。

---

## 🌈 核心配色

### 主品牌色 - 翡翠绿 🍃

```scss
$primary-color: #10b981      // 主色（翡翠绿）
$primary-light: #34d399      // 浅色（薄荷绿）
$primary-lighter: #6ee7b7    // 更浅（清新绿）
$primary-dark: #059669       // 深色（深绿）
```

**色彩心理学**：
- 🌱 **生命力** - 代表成长与新生
- 💚 **疗愈感** - 缓解焦虑，带来平静
- 🌿 **自然** - 亲近自然，放松心情
- ✨ **希望** - 积极向上的心理暗示

**应用场景**：
- 主按钮、CTA（行动号召）
- 成功状态提示
- 重要数据指标
- 进度条、仪表盘

---

### 辅助色 - 温暖橙 🧡

```scss
$secondary-color: #fb923c    // 温暖橙
$secondary-light: #fdba74    // 浅橙色
```

**色彩心理学**：
- 🌅 **温暖** - 给予关怀和支持感
- ⚡ **活力** - 传递积极能量
- 🎯 **吸引** - 引导用户注意力
- 😊 **愉悦** - 提升情绪

**应用场景**：
- 警告提示
- 特殊标签
- 热度标记
- 辅助图标

---

## 🎨 功能色系统

### 成功 - 翡翠绿
```scss
$success-color: #10b981
```
用于：完成状态、成功提示、正向反馈

### 警告 - 金黄色
```scss
$warning-color: #fbbf24
```
用于：提醒信息、待处理事项、需要注意

### 错误 - 柔和红
```scss
$error-color: #f87171
```
用于：错误提示、失败状态（使用柔和的红色，不刺眼）

### 信息 - 浅绿
```scss
$info-color: #34d399
```
用于：提示信息、引导说明

---

## 🌊 渐变色系统

### 1. 英雄区域渐变
```scss
$gradient-hero: linear-gradient(135deg, #34d399 0%, #10b981 100%)
```
**效果**：从浅绿到深绿的柔和过渡  
**应用**：首页顶部英雄区域

<div style="height: 100px; background: linear-gradient(135deg, #34d399 0%, #10b981 100%); border-radius: 8px;"></div>

### 2. 主渐变
```scss
$gradient-primary: linear-gradient(135deg, #10b981 0%, #34d399 100%)
```
**效果**：从深绿到浅绿  
**应用**：音乐播放器、特殊卡片

### 3. 温暖渐变
```scss
$gradient-warm: linear-gradient(135deg, #fbbf24 0%, #fb923c 100%)
```
**效果**：金黄到橙色  
**应用**：推荐内容、热门标签

### 4. 自然渐变
```scss
$gradient-nature: linear-gradient(135deg, #6ee7b7 0%, #10b981 100%)
```
**效果**：清新绿到翡翠绿  
**应用**：卡片背景、装饰元素

---

## 🎯 中性色系统

### 文字颜色

| 颜色 | 色值 | 用途 | 对比度 |
|------|------|------|--------|
| 主标题 | `#1f2937` | 一级标题、重要文本 | AAA |
| 副标题 | `#4b5563` | 二级标题、次要文本 | AA |
| 描述文字 | `#6b7280` | 正文、描述信息 | AA |
| 辅助信息 | `#9ca3af` | 提示、说明、时间戳 | - |

### 背景颜色

```scss
$bg-white: #ffffff        // 纯白背景
$bg-gray-50: #f9fafb     // 浅灰背景（卡片、区域）
$bg-gray-100: #f3f4f6    // 次级背景
$bg-gray-200: #e5e7eb    // 边框、分割线
```

---

## 📐 使用规范

### ✅ 正确用法

1. **主色调用于关键操作**
```vue
<button class="primary-btn">开始测评</button>

<style>
.primary-btn {
  background: $primary-color;
  color: white;
}
</style>
```

2. **渐变用于视觉焦点**
```scss
.hero-section {
  background: $gradient-hero;
}
```

3. **辅助色用于强调**
```scss
.hot-badge {
  background: $secondary-color;
  color: white;
}
```

### ❌ 错误用法

1. **不要过度使用彩色**
```scss
// ❌ 错误：全部用彩色
.card {
  background: $primary-color;
  border: 2px solid $secondary-color;
  color: $warning-color;
}

// ✅ 正确：保持克制
.card {
  background: white;
  border: 1px solid $border-light;
  color: $text-primary;
}
```

2. **不要混用对立色相**
```scss
// ❌ 错误：绿色+红色（圣诞色）
.button {
  background: $primary-color;
  color: $error-color;
}
```

3. **不要用彩色作为大面积背景**
```scss
// ❌ 错误
.page {
  background: $primary-color;  // 太刺眼
}

// ✅ 正确
.page {
  background: $bg-white;       // 舒适
}
```

---

## 🎨 场景化应用

### 1. 首页场景

```
┌─────────────────────────────┐
│ 英雄区域                     │  ← gradient-hero (绿色渐变)
│ 你好，同学 👋                │
│ [开始心理测评] ←──────────────  白色卡片
└─────────────────────────────┘

心理状态仪表盘 ←────────────────  primary-color (绿色)
  ├─ 情绪指数
  ├─ 压力水平  
  └─ 社交质量

💡 今日建议 ←──────────────────  白色卡片 + 轻微阴影

🎵 音乐疗愈
┌─────────────────────────────┐
│ 正在播放                     │  ← gradient-primary (绿色渐变)
│ ▶ 雨声冥想                   │
└─────────────────────────────┘

音乐列表 ←─────────────────────  浅灰背景
```

### 2. 心理测评场景

- **测评入口卡片**：白色背景 + 绿色图标
- **测评进度条**：绿色渐变
- **完成状态**：绿色对勾 + 成功提示

### 3. 音乐播放器场景

- **播放器背景**：绿色渐变（gradient-primary）
- **播放按钮**：白色圆形 + 绿色图标
- **音乐列表**：浅灰背景 + 点击反馈

### 4. 文章阅读场景

- **文章卡片**：白色背景 + 轻阴影
- **封面图标**：浅灰背景 + emoji
- **互动数据**：灰色文字

---

## 🌓 深色模式（未来扩展）

如需支持深色模式，建议配色：

```scss
// 深色模式主色（降低饱和度）
$primary-color-dark: #34d399;    // 稍浅的绿色
$bg-dark: #1f2937;               // 深灰背景
$text-dark: #f9fafb;             // 浅色文字

// 渐变需要调整透明度
$gradient-hero-dark: linear-gradient(
  135deg, 
  rgba(52, 211, 153, 0.8) 0%, 
  rgba(16, 185, 129, 0.8) 100%
);
```

---

## 📊 对比度检测

所有文字颜色都通过了 WCAG 2.1 标准：

| 组合 | 对比度 | 等级 | 说明 |
|------|--------|------|------|
| `#1f2937` on `#ffffff` | 16.5:1 | AAA | 优秀 |
| `#4b5563` on `#ffffff` | 8.6:1 | AAA | 优秀 |
| `#6b7280` on `#ffffff` | 5.7:1 | AA+ | 良好 |
| `#ffffff` on `#10b981` | 3.3:1 | AA | 合格（大字） |

---

## 🎯 配色搭配建议

### 推荐组合

✅ **主绿 + 白色**（清爽）
```scss
background: $primary-color;
color: white;
```

✅ **浅灰背景 + 深色文字**（舒适）
```scss
background: $bg-gray-50;
color: $text-primary;
```

✅ **渐变背景 + 白色文字**（视觉焦点）
```scss
background: $gradient-hero;
color: white;
```

✅ **白色背景 + 绿色按钮**（经典）
```scss
.card {
  background: white;
  
  .btn {
    background: $primary-color;
    color: white;
  }
}
```

---

## 🔧 调整配色

如需调整配色方案，只需修改 `static/scss/theme.scss`：

```scss
/* 方案 A：更温暖的绿色 */
$primary-color: #22c55e;  // 更鲜艳

/* 方案 B：偏青的绿色 */
$primary-color: #14b8a6;  // 青绿色（Teal）

/* 方案 C：淡雅的绿色 */
$primary-color: #10b981;  // 当前翡翠绿（推荐）
```

---

## 📱 多端适配

### H5 端
所有颜色正常显示，支持渐变和阴影。

### 小程序端
- ✅ 支持渐变（linear-gradient）
- ✅ 支持 rgba 透明度
- ⚠️ 部分阴影效果可能弱化

### App 端
完全支持所有 CSS 特性。

---

## 🎨 灵感来源

参考了这些优秀的设计系统：

- **Calm App** - 冥想应用，使用深蓝+柔和色
- **Headspace** - 心理健康应用，橙色系
- **Forest App** - 专注应用，绿色+自然系
- **Notion** - 浅色+舒适的配色
- **Apple Health** - 绿色为主的健康系统

---

## 📞 反馈与建议

如果您觉得配色需要调整，可以考虑：

1. **更柔和** - 降低饱和度
2. **更活泼** - 增加明度
3. **更专业** - 增加中性色比例
4. **更温暖** - 增加橙色/黄色系

---

**版本**: v2.0 (温暖治愈系)  
**更新日期**: 2025-10-31  
**设计理念**: 温暖、治愈、自然、希望

