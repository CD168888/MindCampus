<template>
  <view class="avatar-edit-page">
    <view class="ambient-background"></view>

    <view class="glass-header" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="navbar-content">
        <view class="navbar-left" @tap="goBack">
          <view class="nav-icon-glass">
            <uni-icons type="left" size="22" color="#1D1D1F"></uni-icons>
          </view>
        </view>
        <view class="navbar-title">调整头像</view>
        <view class="navbar-right"></view>
      </view>
    </view>

    <view class="cropper-container" :style="{ paddingTop: `calc(${statusBarHeight}px + 120rpx)` }">
      
      <view v-if="!isShowImg" class="empty-state-glass">
        <view class="empty-icon-box" @tap="getImage">
          <uni-icons type="camera-filled" size="48" color="#2CB5A0"></uni-icons>
        </view>
        <text class="empty-text">选择一张照片作为新头像</text>
      </view>

      <view v-if="isShowImg" class="cropper-content">
        <view class="uni-corpper" :style="'width:'+cropperInitW+'px;height:'+cropperInitH+'px;'">
          <view class="uni-corpper-content" :style="'width:'+cropperW+'px;height:'+cropperH+'px;left:'+cropperL+'px;top:'+cropperT+'px'">
            <image :src="imageSrc" :style="'width:'+cropperW+'px;height:'+cropperH+'px'"></image>
            
            <view class="uni-corpper-crop-box" 
              @touchstart.stop="contentStartMove" 
              @touchmove.stop="contentMoveing" 
              @touchend.stop="contentTouchEnd"
              :style="'left:'+cutL+'px;top:'+cutT+'px;right:'+cutR+'px;bottom:'+cutB+'px'">
              
              <view class="uni-cropper-view-box">
                <view class="uni-cropper-dashed-h"></view>
                <view class="uni-cropper-dashed-v"></view>
                
                <view class="uni-cropper-line-t" data-drag="top" @touchstart.stop="dragStart" @touchmove.stop="dragMove"></view>
                <view class="uni-cropper-line-r" data-drag="right" @touchstart.stop="dragStart" @touchmove.stop="dragMove"></view>
                <view class="uni-cropper-line-b" data-drag="bottom" @touchstart.stop="dragStart" @touchmove.stop="dragMove"></view>
                <view class="uni-cropper-line-l" data-drag="left" @touchstart.stop="dragStart" @touchmove.stop="dragMove"></view>
                
                <view class="uni-cropper-point point-t" data-drag="top" @touchstart.stop="dragStart" @touchmove.stop="dragMove"></view>
                <view class="uni-cropper-point point-tr" data-drag="topTight"></view>
                <view class="uni-cropper-point point-r" data-drag="right" @touchstart.stop="dragStart" @touchmove.stop="dragMove"></view>
                
                <view class="uni-cropper-point point-rb" data-drag="rightBottom" @touchstart.stop="dragStart" @touchmove.stop="dragMove">
                  <view class="handle-inner"></view>
                </view>
                
                <view class="uni-cropper-point point-b" data-drag="bottom" @touchstart.stop="dragStart" @touchmove.stop="dragMove"></view>
                <view class="uni-cropper-point point-bl" data-drag="bottomLeft"></view>
                <view class="uni-cropper-point point-l" data-drag="left" @touchstart.stop="dragStart" @touchmove.stop="dragMove"></view>
                <view class="uni-cropper-point point-lt" data-drag="leftTop"></view>
              </view>
            </view>
          </view>
        </view>
      </view>
    </view>

    <view class="bottom-glass-dock" :style="{ paddingBottom: `calc(${safeAreaBottom}px + 24rpx)` }">
      <view class="dock-inner">
        <view class="action-btn btn-secondary" @tap="getImage">
          <uni-icons type="image" size="20" color="#1D1D1F"></uni-icons>
          <text>重新选择</text>
        </view>
        <view class="action-btn btn-primary" @tap="getImageInfo" :class="{ 'disabled': !isShowImg }">
          <text>保存头像</text>
          <uni-icons type="checkmarkempty" size="18" color="#FFFFFF"></uni-icons>
        </view>
      </view>
    </view>

    <canvas canvas-id="myCanvas" :style="'position:absolute;border: 1px solid red; width:'+imageW+'px;height:'+imageH+'px;top:-9999px;left:-9999px;'"></canvas>
  </view>
</template>

<script>
import config from '@/config'
import store from "@/store"
import {uploadAvatar} from "@/api/system/user"

const baseUrl = config.baseUrl
let sysInfo = uni.getSystemInfoSync()
let SCREEN_WIDTH = sysInfo.screenWidth
let PAGE_X, PAGE_Y, PR = sysInfo.pixelRatio, T_PAGE_X, T_PAGE_Y, CUT_L, CUT_T, CUT_R, CUT_B, CUT_W, CUT_H, IMG_RATIO, IMG_REAL_W, IMG_REAL_H, DRAFG_MOVE_RATIO = 1, INIT_DRAG_POSITION = 100, DRAW_IMAGE_W = sysInfo.screenWidth

export default {
  data() {
    return {
      statusBarHeight: 0,
      safeAreaBottom: 0,
      
      imageSrc: store.getters.avatar,
      isShowImg: false,
      cropperInitW: SCREEN_WIDTH,
      cropperInitH: SCREEN_WIDTH,
      cropperW: SCREEN_WIDTH,
      cropperH: SCREEN_WIDTH,
      cropperL: 0,
      cropperT: 0,
      transL: 0,
      transT: 0,
      scaleP: 0,
      imageW: 0,
      imageH: 0,
      cutL: 0,
      cutT: 0,
      cutB: SCREEN_WIDTH,
      cutR: '100%',
      qualityWidth: DRAW_IMAGE_W,
      innerAspectRadio: DRAFG_MOVE_RATIO
    }
  },
  onLoad() {
    const sys = uni.getSystemInfoSync()
    this.statusBarHeight = sys.statusBarHeight || 0
    this.safeAreaBottom = sys.safeAreaInsets?.bottom || 0
  },
  onReady() {
    if (this.imageSrc) {
      this.loadImage()
    }
  },
  methods: {
    goBack() {
      uni.navigateBack()
    },
    setData(obj) {
      let that = this
      Object.keys(obj).forEach(key => {
        that.$set(that.$data, key, obj[key])
      })
    },
    getImage() {
      var _this = this
      uni.chooseImage({
        count: 1,
        success: function (res) {
          _this.setData({
            imageSrc: res.tempFilePaths[0],
          })
          _this.loadImage()
        },
      })
    },
    loadImage() {
      var _this = this
      uni.getImageInfo({
        src: _this.imageSrc,
        success: function success(res) {
          IMG_RATIO = 1 / 1
          if (IMG_RATIO >= 1) {
            IMG_REAL_W = SCREEN_WIDTH
            IMG_REAL_H = SCREEN_WIDTH / IMG_RATIO
          } else {
            IMG_REAL_W = SCREEN_WIDTH * IMG_RATIO
            IMG_REAL_H = SCREEN_WIDTH
          }
          let minRange = IMG_REAL_W > IMG_REAL_H ? IMG_REAL_W : IMG_REAL_H
          INIT_DRAG_POSITION = minRange > INIT_DRAG_POSITION ? INIT_DRAG_POSITION : minRange
          if (IMG_RATIO >= 1) {
            let cutT = Math.ceil((SCREEN_WIDTH / IMG_RATIO - (SCREEN_WIDTH / IMG_RATIO - INIT_DRAG_POSITION)) / 2)
            let cutB = cutT
            let cutL = Math.ceil((SCREEN_WIDTH - SCREEN_WIDTH + INIT_DRAG_POSITION) / 2)
            let cutR = cutL
            _this.setData({
              cropperW: SCREEN_WIDTH,
              cropperH: SCREEN_WIDTH / IMG_RATIO,
              cropperL: Math.ceil((SCREEN_WIDTH - SCREEN_WIDTH) / 2),
              cropperT: Math.ceil((SCREEN_WIDTH - SCREEN_WIDTH / IMG_RATIO) / 2),
              cutL: cutL, cutT: cutT, cutR: cutR, cutB: cutB,
              imageW: IMG_REAL_W, imageH: IMG_REAL_H,
              scaleP: IMG_REAL_W / SCREEN_WIDTH,
              qualityWidth: DRAW_IMAGE_W, innerAspectRadio: IMG_RATIO
            })
          } else {
            let cutL = Math.ceil((SCREEN_WIDTH * IMG_RATIO - (SCREEN_WIDTH * IMG_RATIO)) / 2)
            let cutR = cutL
            let cutT = Math.ceil((SCREEN_WIDTH - INIT_DRAG_POSITION) / 2)
            let cutB = cutT
            _this.setData({
              cropperW: SCREEN_WIDTH * IMG_RATIO,
              cropperH: SCREEN_WIDTH,
              cropperL: Math.ceil((SCREEN_WIDTH - SCREEN_WIDTH * IMG_RATIO) / 2),
              cropperT: Math.ceil((SCREEN_WIDTH - SCREEN_WIDTH) / 2),
              cutL: cutL, cutT: cutT, cutR: cutR, cutB: cutB,
              imageW: IMG_REAL_W, imageH: IMG_REAL_H,
              scaleP: IMG_REAL_W / SCREEN_WIDTH,
              qualityWidth: DRAW_IMAGE_W, innerAspectRadio: IMG_RATIO
            })
          }
          _this.setData({ isShowImg: true })
          uni.hideLoading()
        }
      })
    },
    contentStartMove(e) {
      PAGE_X = e.touches[0].pageX
      PAGE_Y = e.touches[0].pageY
    },
    contentMoveing(e) {
      var _this = this
      var dragLengthX = (PAGE_X - e.touches[0].pageX) * DRAFG_MOVE_RATIO
      var dragLengthY = (PAGE_Y - e.touches[0].pageY) * DRAFG_MOVE_RATIO
      if (dragLengthX > 0) {
        if (this.cutL - dragLengthX < 0) dragLengthX = this.cutL
      } else {
        if (this.cutR + dragLengthX < 0) dragLengthX = -this.cutR
      }
      if (dragLengthY > 0) {
        if (this.cutT - dragLengthY < 0) dragLengthY = this.cutT
      } else {
        if (this.cutB + dragLengthY < 0) dragLengthY = -this.cutB
      }
      this.setData({
        cutL: this.cutL - dragLengthX,
        cutT: this.cutT - dragLengthY,
        cutR: this.cutR + dragLengthX,
        cutB: this.cutB + dragLengthY
      })
      PAGE_X = e.touches[0].pageX
      PAGE_Y = e.touches[0].pageY
    },
    contentTouchEnd() {},
    getImageInfo() {
      if (!this.isShowImg) return
      var _this = this
      uni.showLoading({ title: '图像处理中...', mask: true })
      const ctx = uni.createCanvasContext('myCanvas')
      ctx.drawImage(_this.imageSrc, 0, 0, IMG_REAL_W, IMG_REAL_H)
      ctx.draw(true, () => {
        var canvasW = ((_this.cropperW - _this.cutL - _this.cutR) / _this.cropperW) * IMG_REAL_W
        var canvasH = ((_this.cropperH - _this.cutT - _this.cutB) / _this.cropperH) * IMG_REAL_H
        var canvasL = (_this.cutL / _this.cropperW) * IMG_REAL_W
        var canvasT = (_this.cutT / _this.cropperH) * IMG_REAL_H
        uni.canvasToTempFilePath({
          x: canvasL, y: canvasT, width: canvasW, height: canvasH,
          destWidth: canvasW, destHeight: canvasH, quality: 0.8,
          canvasId: 'myCanvas',
          success: function (res) {
            uni.hideLoading()
            let data = {name: 'avatarfile', filePath: res.tempFilePath}
            uploadAvatar(data).then(response => {
              store.commit('SET_AVATAR', response.imgUrl)
              uni.showToast({ title: "新头像已保存", icon: 'success' })
              setTimeout(() => { uni.navigateBack() }, 1500)
            })
          }
        })
      })
    },
    dragStart(e) {
      T_PAGE_X = e.touches[0].pageX
      T_PAGE_Y = e.touches[0].pageY
      CUT_L = this.cutL; CUT_R = this.cutR; CUT_B = this.cutB; CUT_T = this.cutT
    },
    dragMove(e) {
      var _this = this
      var dragType = e.target.dataset.drag
      switch (dragType) {
        case 'right':
          var dragLength = (T_PAGE_X - e.touches[0].pageX) * DRAFG_MOVE_RATIO
          if (CUT_R + dragLength < 0) dragLength = -CUT_R
          this.setData({ cutR: CUT_R + dragLength })
          break
        case 'left':
          var dragLength = (T_PAGE_X - e.touches[0].pageX) * DRAFG_MOVE_RATIO
          if (CUT_L - dragLength < 0) dragLength = CUT_L
          if ((CUT_L - dragLength) > (this.cropperW - this.cutR)) dragLength = CUT_L - (this.cropperW - this.cutR)
          this.setData({ cutL: CUT_L - dragLength })
          break
        case 'top':
          var dragLength = (T_PAGE_Y - e.touches[0].pageY) * DRAFG_MOVE_RATIO
          if (CUT_T - dragLength < 0) dragLength = CUT_T
          if ((CUT_T - dragLength) > (this.cropperH - this.cutB)) dragLength = CUT_T - (this.cropperH - this.cutB)
          this.setData({ cutT: CUT_T - dragLength })
          break
        case 'bottom':
          var dragLength = (T_PAGE_Y - e.touches[0].pageY) * DRAFG_MOVE_RATIO
          if (CUT_B + dragLength < 0) dragLength = -CUT_B
          this.setData({ cutB: CUT_B + dragLength })
          break
        case 'rightBottom':
          var dragLengthX = (T_PAGE_X - e.touches[0].pageX) * DRAFG_MOVE_RATIO
          var dragLengthY = (T_PAGE_Y - e.touches[0].pageY) * DRAFG_MOVE_RATIO
          if (CUT_B + dragLengthY < 0) dragLengthY = -CUT_B
          if (CUT_R + dragLengthX < 0) dragLengthX = -CUT_R
          let cutB = CUT_B + dragLengthY
          let cutR = CUT_R + dragLengthX
          this.setData({ cutB: cutB, cutR: cutR })
          break
        default: break
      }
    }
  }
}
</script>

<style lang="scss" scoped>
/* ==================== iOS 风格核心变量 ==================== */
$ios-text-primary: #1D1D1F;
$ios-text-secondary: #86868B;
$theme-cyan: #2CB5A0;

.avatar-edit-page {
  min-height: 100vh;
  position: relative;
  background-color: #F5F5F7; /* 🍎 恢复为明亮的背景色 */
  font-family: -apple-system, BlinkMacSystemFont, "SF Pro Text", sans-serif;
  overflow: hidden;
}

/* --- 弥散光影背景 (明亮版) --- */
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

/* ==================== 顶部导航栏 (Glass Header) ==================== */
.glass-header {
  position: fixed;
  top: 0; left: 0; right: 0;
  z-index: 100;
  background: rgba(245, 245, 247, 0.65); /* 🍎 恢复为浅色毛玻璃 */
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
  background: rgba(255, 255, 255, 0.6); /* 🍎 浅色图标底座 */
  display: flex; align-items: center; justify-content: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.04);
  transition: transform 0.2s ease;
  &:active { transform: scale(0.9); opacity: 0.8; }
}

.navbar-title { font-size: 32rpx; font-weight: 600; color: $ios-text-primary; }

/* ==================== 核心裁剪区域 ==================== */
.cropper-container {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100vh;
  box-sizing: border-box;
}

.empty-state-glass {
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  gap: 32rpx; transform: translateY(-100rpx);
}

.empty-icon-box {
  width: 160rpx; height: 160rpx; border-radius: 50%;
  background: rgba(255, 255, 255, 0.6);
  display: flex; align-items: center; justify-content: center;
  backdrop-filter: blur(20px); border: 1px solid rgba(255,255,255,0.8);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.04);
  transition: transform 0.2s ease;
  &:active { transform: scale(0.95); }
}

.empty-text { font-size: 28rpx; color: $ios-text-secondary; }

/* --- 裁剪组件原生样式深度复写 --- */
.cropper-content {
  width: 100%;
  display: flex; justify-content: center;
  transform: translateY(-80rpx); /* 微调位置给底边栏让路 */
}

.uni-corpper {
  position: relative;
  overflow: hidden;
  user-select: none;
  box-sizing: border-box;
  box-shadow: 0 20rpx 60rpx rgba(0,0,0,0.15); /* 🍎 浅色主题下的投影变柔和 */
  border-radius: 12rpx;
}

.uni-corpper-content { position: relative; }
.uni-corpper-content image { display: block; width: 100%; height: 100%; margin: 0 auto; }

/* 裁剪外围遮罩 */
.uni-corpper-crop-box {
  position: absolute;
  background: transparent !important; /* 🍎 核心修改：这里必须是完全透明，否则会导致里面的图像变暗！ */
  z-index: 2;
}

/* 裁剪主体框 (苹果极简白框) */
.uni-corpper-crop-box .uni-cropper-view-box {
  position: relative;
  display: block; width: 100%; height: 100%;
  outline: 1.5px solid rgba(255, 255, 255, 0.9); /* 高亮白边 */
  box-shadow: 0 0 0 9999px rgba(0, 0, 0, 0.5); /* 🍎 用 CSS 魔法仅让框“外部”变暗，框内完全透明清晰 */
}

/* 内部辅助虚线 */
.uni-cropper-dashed-h, .uni-cropper-dashed-v {
  position: absolute;
  border-color: rgba(255, 255, 255, 0.4) !important;
}
.uni-cropper-dashed-h { top: 33.33%; left: 0; width: 100%; height: 33.33%; border-top: 1px dashed; border-bottom: 1px dashed; }
.uni-cropper-dashed-v { left: 33.33%; top: 0; width: 33.33%; height: 100%; border-left: 1px dashed; border-right: 1px dashed; }

/* 隐藏原有蓝色控制线，改用透明扩大触控区 */
.uni-cropper-line-t, .uni-cropper-line-r, .uni-cropper-line-b, .uni-cropper-line-l {
  background-color: transparent !important; opacity: 1 !important;
}
.uni-cropper-line-t { top: -20rpx; left: 0; width: 100%; height: 40rpx; position: absolute; z-index: 10; }
.uni-cropper-line-b { bottom: -20rpx; left: 0; width: 100%; height: 40rpx; position: absolute; z-index: 10; }
.uni-cropper-line-l { top: 0; left: -20rpx; width: 40rpx; height: 100%; position: absolute; z-index: 10; }
.uni-cropper-line-r { top: 0; right: -20rpx; width: 40rpx; height: 100%; position: absolute; z-index: 10; }

/* 隐藏无用的点 */
.uni-cropper-point { opacity: 0; background: transparent; }

/* 🌟 核心拖动把手 (右下角玉石手柄) */
.point-rb {
  width: 80rpx !important; height: 80rpx !important; /* 扩大触控区 */
  background-color: transparent !important;
  opacity: 1 !important; z-index: 1112;
  position: absolute; right: -40rpx; bottom: -40rpx;
  display: flex; align-items: center; justify-content: center;
}

/* 手柄视觉实体 */
.handle-inner {
  width: 32rpx; height: 32rpx;
  background: #FFFFFF;
  border-radius: 50%;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.2), inset 0 0 0 2rpx rgba(44, 181, 160, 0.5); /* 带有主题色的内发光 */
  pointer-events: none;
}

/* ==================== 底部悬浮操作舱 (明亮 Glass Dock) ==================== */
.bottom-glass-dock {
  position: fixed;
  bottom: 0; left: 0; right: 0;
  z-index: 100;
  padding: 24rpx 40rpx;
  display: flex; justify-content: center;
}

.dock-inner {
  width: 100%;
  max-width: 800rpx;
  background: rgba(250, 250, 252, 0.85); /* 🍎 浅色毛玻璃 */
  backdrop-filter: saturate(200%) blur(30px);
  -webkit-backdrop-filter: saturate(200%) blur(30px);
  border: 1px solid rgba(255, 255, 255, 0.8);
  border-radius: 100rpx;
  padding: 16rpx 20rpx;
  display: flex; align-items: center; gap: 20rpx;
  box-shadow: 0 16rpx 40rpx rgba(0, 0, 0, 0.08);
}

.action-btn {
  flex: 1; height: 88rpx; border-radius: 44rpx;
  display: flex; align-items: center; justify-content: center; gap: 12rpx;
  font-size: 30rpx; font-weight: 600;
  transition: all 0.2s cubic-bezier(0.2, 0.8, 0.2, 1);
  &:active { transform: scale(0.95); }
}

.btn-secondary {
  background: rgba(0, 0, 0, 0.05); color: $ios-text-primary;
}

.btn-primary {
  background: linear-gradient(135deg, #48D1CC 0%, #2CB5A0 100%); color: #FFFFFF;
  box-shadow: 0 8rpx 20rpx rgba(44, 181, 160, 0.25);
  &.disabled { opacity: 0.5; pointer-events: none; box-shadow: none; filter: grayscale(80%); }
}
</style>