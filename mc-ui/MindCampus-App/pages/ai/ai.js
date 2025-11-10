/**
 * AI对话页面工具函数
 */

/**
 * 生成UUID
 */
export function generateUUID() {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
    const r = (Math.random() * 16) | 0;
    const v = c === 'x' ? r : (r & 0x3) | 0x8;
    return v.toString(16);
  });
}

/**
 * 格式化时间
 * @param {number} timestamp - 时间戳
 */
export function formatTime(timestamp) {
  if (!timestamp) return '';

  const date = new Date(timestamp);
  const now = new Date();
  const diff = now.getTime() - date.getTime();

  // 如果是今天
  if (diff < 24 * 60 * 60 * 1000 && now.getDate() === date.getDate()) {
    const hours = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');
    return `${hours}:${minutes}`;
  }

  // 如果是昨天
  const yesterday = new Date(now);
  yesterday.setDate(yesterday.getDate() - 1);
  if (yesterday.getDate() === date.getDate()) {
    return '昨天';
  }

  // 其他日期
  const month = (date.getMonth() + 1).toString().padStart(2, '0');
  const day = date.getDate().toString().padStart(2, '0');
  return `${month}-${day}`;
}

/**
 * 滚动到底部
 * @param {Object} vm - Vue实例
 * @param {string} scrollTopKey - scrollTop的key名
 */
export function scrollToBottom(vm, scrollTopKey = 'scrollTop') {
  uni
    .createSelectorQuery()
    .in(vm)
    .select('.chat-area')
    .boundingClientRect((data) => {
      if (data) {
        vm[scrollTopKey] = 999999;
      }
    })
    .exec();
}

/**
 * 输入框聚焦处理
 * @param {Function} callback - 回调函数
 */
export function onInputFocus(callback) {
  setTimeout(() => {
    if (typeof callback === 'function') {
      callback();
    }
  }, 300);
}

/**
 * 输入框失焦处理
 * @param {Function} callback - 回调函数
 */
export function onInputBlur(callback) {
  if (typeof callback === 'function') {
    callback(0);
  }
}

/**
 * 调整输入框高度
 * @param {Object} e - 事件对象
 * @param {Object} callbacks - 回调函数集合
 */
export function adjustHeight(e, callbacks) {
  const { setTextareaHeight, setInputAreaHeight, scrollToBottom } = callbacks;

  const height = e.detail.height;
  const lineHeight = 21;
  const maxLines = 4;
  const maxHeight = lineHeight * maxLines;

  let newHeight = Math.min(height, maxHeight);
  newHeight = Math.max(newHeight, 24);

  if (typeof setTextareaHeight === 'function') {
    setTextareaHeight(newHeight);
  }

  const inputAreaHeight = newHeight + 32;
  if (typeof setInputAreaHeight === 'function') {
    setInputAreaHeight(inputAreaHeight);
  }

  if (typeof scrollToBottom === 'function') {
    setTimeout(scrollToBottom, 100);
  }
}

/**
 * 返回上一页
 */
export function goBack() {
  uni.navigateBack({
    delta: 1,
  });
}

/**
 * 防抖函数
 * @param {Function} func - 要执行的函数
 * @param {number} wait - 等待时间
 */
export function debounce(func, wait = 300) {
  let timeout;
  return function () {
    const context = this;
    const args = arguments;
    clearTimeout(timeout);
    timeout = setTimeout(() => {
      func.apply(context, args);
    }, wait);
  };
}

/**
 * 节流函数
 * @param {Function} func - 要执行的函数
 * @param {number} wait - 等待时间
 */
export function throttle(func, wait = 300) {
  let previous = 0;
  return function () {
    const now = Date.now();
    const context = this;
    const args = arguments;
    if (now - previous > wait) {
      func.apply(context, args);
      previous = now;
    }
  };
}
