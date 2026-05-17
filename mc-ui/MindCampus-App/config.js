// 根据环境动态设置 baseUrl
const isProd = process.env.NODE_ENV === 'production';
const baseUrl = isProd ? 'https://your-domain.com/api' : '/dev-api';

module.exports = {
  baseUrl: baseUrl,
  appInfo: {
    name: "MindCampus",
    version: "1.2.0",
    logo: "/static/logo.png",
    site_url: "https://mindcampus.com",
    description: "专业的大学生心理健康服务平台",
    agreements: [{
        title: "隐私政策",
        url: "https://mindcampus.com/privacy.html"
      },
      {
        title: "用户服务协议",
        url: "https://mindcampus.com/terms.html"
      }
    ]
  }
}