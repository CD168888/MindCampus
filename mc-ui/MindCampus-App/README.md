# MindCampus 移动端应用

<h4 align="center">基于 UniApp 开发的轻量级移动端框架</h4>

## 平台简介

MindCampus 是一个基于 UniApp 开发的跨平台移动应用，支持 H5、小程序、App 等多端运行。项目采用 Vue.js 2.x 框架，集成了完整的用户认证体系、权限管理和丰富的 UI 组件库。

### 技术栈
- **前端框架**: Vue.js 2.x + UniApp
- **状态管理**: Vuex
- **UI组件**: uni-ui + ColorUI
- **网络请求**: uni.request 封装
- **图标字体**: iconfont
- **样式预处理**: SCSS

## 功能介绍

### 🔐 用户认证
- 用户登录/注册
- 图形验证码验证
- Token 自动刷新
- 登录状态持久化
- 权限路由拦截

### 👤 个人中心
- 个人信息管理
- 头像上传
- 密码修改
- 应用设置
- 常见问题
- 关于我们

### 🏢 系统管理 (工作台)
- 用户管理
- 角色管理  
- 菜单管理
- 部门管理
- 岗位管理
- 字典管理
- 参数设置
- 通知公告
- 日志管理

### 📱 多端支持
- H5 浏览器端
- 微信小程序 
- Android App
- iOS App

## 快速开始

### 环境要求
- Node.js >= 12.0.0
- HBuilderX 或 Vue CLI
- 微信开发者工具（小程序开发）

### 安装运行

1. **克隆项目**
```bash
git clone [项目地址]
cd MindCampus-App
```

2. **安装依赖**
```bash
npm install
```

3. **配置后端接口**
```javascript
// config.js
module.exports = {
  baseUrl: 'https://vue.ruoyi.vip/prod-api', // 修改为你的后端地址
  // 其他配置...
}
```

4. **运行项目**
```bash
# H5端
npm run dev:h5

# 微信小程序
npm run dev:mp-weixin

# App端 (需要 HBuilderX)
使用 HBuilderX 打开项目，运行到手机或模拟器
```

## 目录结构

```
MindCampus-App/
├── api/                    # API接口定义
│   ├── login.js           # 登录相关接口
│   └── system/            # 系统管理接口
├── components/            # 自定义组件
│   └── uni-section/       # 章节组件
├── pages/                 # 页面文件
│   ├── index.vue         # 首页
│   ├── login.vue         # 登录页
│   ├── register.vue      # 注册页
│   ├── work/             # 工作台页面
│   ├── mine/             # 个人中心页面
│   └── common/           # 公共页面
├── static/               # 静态资源
│   ├── images/           # 图片资源
│   ├── font/            # 字体文件
│   └── scss/            # 样式文件
├── store/               # Vuex状态管理
│   ├── index.js         # store入口
│   ├── getters.js       # 全局getters
│   └── modules/         # 模块化store
├── utils/               # 工具类
│   ├── auth.js          # 认证工具
│   ├── request.js       # 网络请求封装
│   ├── common.js        # 公共方法
│   ├── storage.js       # 存储工具
│   └── validate.js      # 验证工具
├── plugins/             # 插件模块
│   ├── auth.js          # 认证插件
│   ├── modal.js         # 模态框插件
│   └── tab.js           # 页签插件
├── uni_modules/         # uni-ui组件库
├── App.vue              # 应用入口组件
├── main.js              # 应用入口文件
├── manifest.json        # 应用配置文件
├── pages.json           # 页面路由配置
├── uni.scss             # 全局样式变量
└── config.js            # 应用配置文件
```

## 核心功能说明

### 🔧 网络请求封装
基于 `uni.request` 封装了完整的网络请求库，支持：
- 自动携带 Token
- 统一错误处理
- 请求/响应拦截器
- 超时处理
- 参数序列化

### 🛡️ 权限管理
- 路由权限控制
- 页面访问拦截
- Token 过期处理
- 白名单机制

### 📦 状态管理
使用 Vuex 管理全局状态：
- 用户信息存储
- 权限数据管理
- 持久化存储

### 🎨 UI组件
集成丰富的 UI 组件库：
- uni-ui 官方组件
- ColorUI 样式框架
- 自定义业务组件

## 常用工具类说明

### 认证工具 (auth.js)
```javascript
import { getToken, setToken, removeToken } from '@/utils/auth'

// 获取token
const token = getToken()

// 设置token  
setToken('your-token')

// 删除token
removeToken()
```

### 网络请求 (request.js)
```javascript
import request from '@/utils/request'

// GET请求
request({
  url: '/api/data',
  method: 'get',
  params: { id: 1 }
})

// POST请求
request({
  url: '/api/data', 
  method: 'post',
  data: { name: 'test' }
})
```

### 插件系统
```javascript
// 页签操作
this.$tab.navigateTo('/pages/demo')
this.$tab.reLaunch('/pages/login')

// 模态框
this.$modal.showToast('操作成功')
this.$modal.confirm('确认删除？')

// 权限验证
this.$auth.hasRole('admin')
this.$auth.hasPermission('system:user:add')
```

## 配置说明

### 应用配置 (config.js)
```javascript
module.exports = {
  // 后端接口地址
  baseUrl: 'https://vue.ruoyi.vip/prod-api',
  
  // 应用信息
  appInfo: {
    name: "ruoyi-app",        // 应用名称
    version: "1.2.0",         // 版本号
    logo: "/static/logo.png", // 应用logo
    site_url: "http://ruoyi.vip", // 官网地址
    agreements: [             // 协议链接
      {
        title: "隐私政策",
        url: "https://ruoyi.vip/protocol.html"
      }
    ]
  }
}
```

### 页面配置 (pages.json)
- 页面路由定义
- 导航栏配置
- tabBar 配置
- 全局样式设置

### 应用配置 (manifest.json)
- 应用基本信息
- 平台特定配置
- 权限申请
- 编译配置

## 开发规范

### 代码规范
- 使用 ES6+ 语法
- 组件名使用 PascalCase
- 文件名使用 kebab-case
- 变量名使用 camelCase

### 样式规范  
- 使用 SCSS 预处理器
- 遵循 BEM 命名规范
- 统一使用 rpx 单位
- 颜色值使用全局变量

### API 规范
- 统一使用 request 封装
- 接口按模块分类管理
- 错误统一处理
- 支持请求取消

## 部署说明

### H5 部署
```bash
npm run build:h5
# 将 dist/build/h5 目录部署到 Web 服务器
```

### 小程序发布
```bash
npm run build:mp-weixin
# 使用微信开发者工具打开 dist/build/mp-weixin 目录
# 上传代码并提交审核
```

### App 打包
使用 HBuilderX：
1. 发行 -> 原生App-云打包
2. 选择打包类型和证书
3. 点击打包

## 更新日志

### v1.2.0 (当前版本)
- ✨ 完善用户认证体系
- 🎨 优化UI界面设计
- 🔧 重构网络请求模块
- 📱 适配多端运行环境
- 🛡️ 加强权限管理机制

---

**注意**: 本项目基于若依(RuoYi)框架开发，是一个功能完整的移动端解决方案。适合作为企业级应用的基础框架使用。