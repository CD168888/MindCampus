/**
 * 系统相关工具函数
 */

// 系统信息缓存
let systemInfo = null;

/**
 * 获取系统信息
 */
export function getSystemInfo() {
  if (!systemInfo) {
    systemInfo = uni.getSystemInfoSync();
  }
  return systemInfo;
}

/**
 * 获取状态栏高度
 */
export function getStatusBarHeight() {
  const info = getSystemInfo();
  return info.statusBarHeight || 0;
}

/**
 * 获取导航栏高度（含状态栏）
 */
export function getNavBarHeight() {
  const statusBarHeight = getStatusBarHeight();
  // 导航栏内容高度固定44px
  return statusBarHeight + 44;
}

/**
 * 获取底部安全区域高度
 */
export function getSafeAreaBottom() {
  const info = getSystemInfo();
  return info.safeAreaInsets?.bottom || 0;
}

/**
 * 判断是否为iOS
 */
export function isIOS() {
  const info = getSystemInfo();
  return info.platform === 'ios';
}

/**
 * 判断是否为Android
 */
export function isAndroid() {
  const info = getSystemInfo();
  return info.platform === 'android';
}

/**
 * 判断是否为微信小程序
 */
export function isWeChat() {
  // #ifdef MP-WEIXIN
  return true;
  // #endif
  // #ifndef MP-WEIXIN
  return false;
  // #endif
}

/**
 * 判断是否为H5
 */
export function isH5() {
  // #ifdef H5
  return true;
  // #endif
  // #ifndef H5
  return false;
  // #endif
}

/**
 * 判断是否为App
 */
export function isApp() {
  // #ifdef APP-PLUS
  return true;
  // #endif
  // #ifndef APP-PLUS
  return false;
  // #endif
}

/**
 * 获取屏幕宽度
 */
export function getScreenWidth() {
  const info = getSystemInfo();
  return info.screenWidth || 0;
}

/**
 * 获取屏幕高度
 */
export function getScreenHeight() {
  const info = getSystemInfo();
  return info.screenHeight || 0;
}

/**
 * 获取可用窗口高度
 */
export function getWindowHeight() {
  const info = getSystemInfo();
  return info.windowHeight || 0;
}

/**
 * rpx转px
 * @param {number} rpx - rpx值
 */
export function rpxToPx(rpx) {
  const info = getSystemInfo();
  const screenWidth = info.screenWidth || 375;
  return (rpx / 750) * screenWidth;
}

/**
 * px转rpx
 * @param {number} px - px值
 */
export function pxToRpx(px) {
  const info = getSystemInfo();
  const screenWidth = info.screenWidth || 375;
  return (px / screenWidth) * 750;
}

/**
 * 获取设备像素比
 */
export function getPixelRatio() {
  const info = getSystemInfo();
  return info.pixelRatio || 1;
}

/**
 * 获取系统版本
 */
export function getSystemVersion() {
  const info = getSystemInfo();
  return info.system || '';
}

/**
 * 获取微信版本号
 */
export function getWeChatVersion() {
  // #ifdef MP-WEIXIN
  const info = getSystemInfo();
  return info.version || '';
  // #endif
  // #ifndef MP-WEIXIN
  return '';
  // #endif
}

/**
 * 比较版本号
 * @param {string} v1 - 版本号1
 * @param {string} v2 - 版本号2
 * @returns {number} - 1: v1 > v2, 0: v1 = v2, -1: v1 < v2
 */
export function compareVersion(v1, v2) {
  const arr1 = v1.split('.');
  const arr2 = v2.split('.');
  const len = Math.max(arr1.length, arr2.length);
  
  for (let i = 0; i < len; i++) {
    const num1 = parseInt(arr1[i] || 0);
    const num2 = parseInt(arr2[i] || 0);
    
    if (num1 > num2) {
      return 1;
    } else if (num1 < num2) {
      return -1;
    }
  }
  
  return 0;
}

/**
 * 设置剪贴板内容
 * @param {string} data - 要复制的内容
 */
export function setClipboardData(data) {
  return new Promise((resolve, reject) => {
    uni.setClipboardData({
      data: data,
      success: () => {
        resolve();
      },
      fail: (err) => {
        reject(err);
      }
    });
  });
}

/**
 * 获取剪贴板内容
 */
export function getClipboardData() {
  return new Promise((resolve, reject) => {
    uni.getClipboardData({
      success: (res) => {
        resolve(res.data);
      },
      fail: (err) => {
        reject(err);
      }
    });
  });
}

/**
 * 振动反馈
 * @param {string} type - 振动类型：short | medium | long
 */
export function vibrateShort(type = 'short') {
  if (type === 'short') {
    uni.vibrateShort({
      type: 'light'
    });
  } else if (type === 'medium') {
    uni.vibrateShort({
      type: 'medium'
    });
  } else if (type === 'long') {
    uni.vibrateLong();
  }
}

/**
 * 保持屏幕常亮
 * @param {boolean} keepScreenOn - 是否保持常亮
 */
export function setKeepScreenOn(keepScreenOn = true) {
  uni.setKeepScreenOn({
    keepScreenOn: keepScreenOn
  });
}

/**
 * 获取网络类型
 */
export function getNetworkType() {
  return new Promise((resolve, reject) => {
    uni.getNetworkType({
      success: (res) => {
        resolve(res.networkType);
      },
      fail: (err) => {
        reject(err);
      }
    });
  });
}

/**
 * 拨打电话
 * @param {string} phoneNumber - 电话号码
 */
export function makePhoneCall(phoneNumber) {
  return new Promise((resolve, reject) => {
    uni.makePhoneCall({
      phoneNumber: phoneNumber,
      success: () => {
        resolve();
      },
      fail: (err) => {
        reject(err);
      }
    });
  });
}

/**
 * 扫码
 */
export function scanCode() {
  return new Promise((resolve, reject) => {
    uni.scanCode({
      success: (res) => {
        resolve(res);
      },
      fail: (err) => {
        reject(err);
      }
    });
  });
}

export default {
  getSystemInfo,
  getStatusBarHeight,
  getNavBarHeight,
  getSafeAreaBottom,
  isIOS,
  isAndroid,
  isWeChat,
  isH5,
  isApp,
  getScreenWidth,
  getScreenHeight,
  getWindowHeight,
  rpxToPx,
  pxToRpx,
  getPixelRatio,
  getSystemVersion,
  getWeChatVersion,
  compareVersion,
  setClipboardData,
  getClipboardData,
  vibrateShort,
  setKeepScreenOn,
  getNetworkType,
  makePhoneCall,
  scanCode
};

