<template>
  <view class="banner-carousel">
    <swiper class="banner-swiper" :indicator-dots="true" :autoplay="true" :interval="4000" :duration="500"
      :circular="true" :indicator-color="'rgba(255, 255, 255, 0.3)'"
      :indicator-active-color="'rgba(255, 255, 255, 0.8)'">
      <swiper-item v-for="(banner, index) in bannerList" :key="banner.bannerId || index">
        <view class="swiper-item" @tap="onBannerClick(banner)">
          <!-- 如果有图片，显示图片 -->
          <image v-if="banner.imageUrl" class="banner-image" :src="getImageUrl(banner.imageUrl)" mode="aspectFill"
            :lazy-load="true" />
          <!-- 如果没有图片，显示默认渐变背景 -->
          <view v-else class="banner-content" :style="{ background: getBannerGradient(index) }">
            <view class="banner-title">{{ banner.title || '轮播图' }}</view>
          </view>
          <!-- 图片上的标题遮罩（如果有图片且有标题） -->
          <view v-if="banner.imageUrl && banner.title" class="banner-overlay">
            <view class="banner-title-overlay">{{ banner.title }}</view>
          </view>
        </view>
      </swiper-item>
    </swiper>
  </view>
</template>

<script>
import {getBannerList} from '@/api/banner'
import config from '@/config'

export default {
  name: 'BannerCarousel',
  data() {
    return {
      bannerList: []
    }
  },
  mounted() {
    this.loadBannerList()
  },
  methods: {
    // 加载轮播图列表
    loadBannerList() {
      getBannerList().then(res => {
        if (res.code === 200 && res.data) {
          this.bannerList = res.data
        }
      }).catch(err => {
        console.error('加载轮播图失败:', err)
      })
    },

    // 获取图片完整URL
    getImageUrl(url) {
      if (!url) return ''
      if (url.startsWith('http')) return url
      const baseUrl = config.baseUrl || 'http://localhost:8080'
      return url.startsWith('/') ? baseUrl + url : baseUrl + '/' + url
    },

    // 获取轮播图渐变背景（根据索引循环使用三种颜色）
    getBannerGradient(index) {
      const gradients = [
        'linear-gradient(135deg, rgba(167, 243, 208, 0.8) 0%, rgba(110, 231, 183, 0.8) 100%)',
        'linear-gradient(135deg, rgba(196, 181, 253, 0.8) 0%, rgba(167, 139, 250, 0.8) 100%)',
        'linear-gradient(135deg, rgba(254, 205, 211, 0.8) 0%, rgba(253, 164, 175, 0.8) 100%)'
      ]
      return gradients[index % gradients.length]
    },

    // 轮播图点击事件
    onBannerClick(banner) {
      if (banner.linkUrl) {
        // 如果有跳转链接，处理跳转
        this.handleLinkClick(banner.linkUrl)
      } else {
        // 如果没有链接，可以触发事件让父组件处理
        this.$emit('click', banner)
      }
    },

    // 处理链接点击
    handleLinkClick(linkUrl) {
      if (!linkUrl) return

      // 如果是内部页面链接（以 /pages 开头）
      if (linkUrl.startsWith('/pages')) {
        uni.navigateTo({
          url: linkUrl,
          fail: (err) => {
            console.error('页面跳转失败:', err)
            uni.showToast({
              title: '页面不存在',
              icon: 'none'
            })
          }
        })
      }
      // 如果是外部链接（http/https）
      else if (linkUrl.startsWith('http')) {
        // 可以跳转到 webview 页面
        uni.navigateTo({
          url: `/pages/common/webview/index?url=${encodeURIComponent(linkUrl)}`,
          fail: (err) => {
            console.error('跳转失败:', err)
          }
        })
      }
      // 其他情况，触发事件
      else {
        this.$emit('link-click', linkUrl)
      }
    }
  }
}
</script>

<style lang="scss" scoped>
@import "@/static/scss/theme.scss";

.banner-carousel {
  position: relative;
  z-index: 1;
  margin-top: $spacing-sm;
}

.banner-swiper {
  height: 320rpx;
  border-radius: $radius-lg;
  overflow: hidden;
  position: relative;
}

.swiper-item {
  height: 100%;
  position: relative;
  border-radius: $radius-lg;
  overflow: hidden;
}

.banner-image {
  width: 100%;
  height: 100%;
  display: block;
}

.banner-content {
  height: 100%;
  border-radius: $radius-lg;
  padding: $spacing-xl;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;

  // 装饰光晕
  &::before {
    content: '';
    position: absolute;
    top: -30%;
    right: -10%;
    width: 200rpx;
    height: 200rpx;
    background: radial-gradient(circle, rgba(255, 255, 255, 0.3) 0%, transparent 70%);
    border-radius: $radius-full;
  }
}

.banner-title {
  font-size: $font-2xl;
  font-weight: $font-bold;
  color: $bg-white;
  letter-spacing: -0.5rpx;
  text-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.15);
  position: relative;
  z-index: 1;
}

.banner-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.6) 0%, transparent 100%);
  padding: $spacing-lg $spacing-xl;
  z-index: 2;
}

.banner-title-overlay {
  font-size: $font-lg;
  font-weight: $font-semibold;
  color: $bg-white;
  text-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.5);
}
</style>
