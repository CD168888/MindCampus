/**
 * AI对话API接口
 */
import request from '@/utils/request'
import config from '@/config'
import { getToken } from '@/utils/auth'

const baseUrl = config.baseUrl

/**
 * 创建新会话
 * @param {string} sessionName - 会话名称
 */
export function createSession(sessionName) {
  return request({
    url: '/ai/createSession',
    method: 'post',
    params: { sessionName }
  })
}

/**
 * 获取用户的所有会话列表
 */
export function getSessions() {
  return request({
    url: '/ai/sessions',
    method: 'get'
  })
}

/**
 * 获取会话的历史消息
 * @param {number} sessionId - 会话ID
 */
export function getMessages(sessionId) {
  return request({
    url: `/ai/messages/${sessionId}`,
    method: 'get'
  })
}

/**
 * 删除会话
 * @param {number} sessionId - 会话ID
 */
export function deleteSession(sessionId) {
  return request({
    url: `/ai/session/${sessionId}`,
    method: 'delete'
  })
}

/**
 * 更新会话标题
 * @param {number} sessionId - 会话ID
 * @param {string} title - 新标题
 */
export function updateSessionTitle(sessionId, title) {
  return request({
    url: `/ai/session/${sessionId}/title`,
    method: 'put',
    params: { title }
  })
}

/**
 * 中断流式输出
 * @param {number} sessionId - 会话ID
 */
export function cancelStream(sessionId) {
  return request({
    url: '/ai/cancelStream',
    method: 'post',
    params: { sessionId }
  })
}

/**
 * 流式对话接口 - 使用SSE (Server-Sent Events)
 * @param {Object} options - 配置选项
 * @param {string} options.message - 用户消息
 * @param {number} options.sessionId - 会话ID（可选，后端会自动创建）
 * @param {Function} options.onMessage - 接收消息回调 (chunk, fullContent)
 * @param {Function} options.onError - 错误回调
 * @param {Function} options.onComplete - 完成回调
 * @returns {Object} 返回包含 eventSource 和 close 方法的对象
 */
export function streamChat(options) {
  const { message, sessionId, onMessage, onError, onComplete } = options
  
  // 构建URL
  let url = `${baseUrl}/ai/chatStream?message=${encodeURIComponent(message)}`
  if (sessionId) {
    url += `&sessionId=${sessionId}`
  }
  
  // EventSource不支持自定义header，需要通过URL传递token
  const token = getToken()
  console.log('获取到的token:', token ? token.substring(0, 20) + '...' : 'null')
  if (token) {
    url += `&token=${encodeURIComponent(token)}`
  }
  console.log('最终URL:', url.substring(0, 100) + '...')
  
  let eventSource = null
  let fullContent = ''
  // 结束标记字符 (ETX - End of Text)
  const END_MARKER = '\u0003'
  
  // #ifdef H5
  // H5端使用原生EventSource
  // 注意：EventSource会自动携带cookie，不需要显式设置withCredentials
  eventSource = new EventSource(url)
  
  eventSource.onopen = function(event) {
    console.log('SSE连接已建立')
  }
  
  eventSource.onmessage = function(event) {
    try {
      const data = event.data
      
      // 检查是否为心跳消息或空消息
      if (!data || data === ':heartbeat') {
        return
      }
      
      // 检查是否为结束标记
      if (data === END_MARKER || data.includes(END_MARKER)) {
        // 移除结束标记并处理剩余内容
        const cleanData = data.replace(END_MARKER, '')
        if (cleanData) {
          fullContent += cleanData
          if (typeof onMessage === 'function') {
            onMessage(cleanData, fullContent)
          }
        }
        // 触发完成回调
        if (typeof onComplete === 'function') {
          onComplete(fullContent)
        }
        eventSource.close()
        return
      }
      
      // 后端直接发送纯文本内容
      fullContent += data
      if (typeof onMessage === 'function') {
        onMessage(data, fullContent)
      }
    } catch (error) {
      console.error('SSE消息处理错误:', error)
    }
  }
  
  // 监听 sessionId 事件
  eventSource.addEventListener('sessionId', function(event) {
    const newSessionId = event.data
    console.log('接收到 sessionId:', newSessionId)
    if (typeof options.onSessionId === 'function') {
      options.onSessionId(newSessionId)
    }
  })
  
  eventSource.onerror = function(error) {
    console.error('SSE连接错误:', error)
    // 如果已经有内容，可能是正常结束
    if (fullContent && eventSource.readyState === EventSource.CLOSED) {
      if (typeof onComplete === 'function') {
        onComplete(fullContent)
      }
    } else if (typeof onError === 'function' && eventSource.readyState !== EventSource.CLOSED) {
      // 只有在非CLOSED状态才报错
      onError(error)
    }
    eventSource.close()
  }
  // #endif
  
  // #ifndef H5
  // 非H5端使用uni.request
  const requestTask = uni.request({
    url: url,
    method: 'GET',
    header: {
      'Authorization': token ? 'Bearer ' + token : '',
      'Accept': 'text/event-stream',
      'Cache-Control': 'no-cache'
    },
    enableChunked: true,
    success: (res) => {
      if (res.statusCode === 200) {
        if (typeof res.data === 'string') {
          const lines = res.data.split('\n')
          for (const line of lines) {
            if (line.startsWith('data:')) {
              const data = line.substring(5).trim()
              if (data && data !== END_MARKER && !data.includes(END_MARKER)) {
                fullContent += data
                if (typeof onMessage === 'function') {
                  onMessage(data, fullContent)
                }
              }
            }
          }
        }
        if (typeof onComplete === 'function') {
          onComplete(fullContent)
        }
      } else {
        if (typeof onError === 'function') {
          onError(new Error(`请求失败: ${res.statusCode}`))
        }
      }
    },
    fail: (error) => {
      console.error('请求失败:', error)
      if (typeof onError === 'function') {
        onError(error)
      }
    }
  })
  
  // 监听分块数据
  if (requestTask.onChunkReceived) {
    requestTask.onChunkReceived((res) => {
      try {
        const decoder = new TextDecoder('utf-8')
        const text = decoder.decode(res.data)
        
        const lines = text.split('\n')
        for (const line of lines) {
          if (line.startsWith('data:')) {
            const data = line.substring(5).trim()
            if (data && data !== ':heartbeat') {
              if (data === END_MARKER || data.includes(END_MARKER)) {
                const cleanData = data.replace(END_MARKER, '')
                if (cleanData) {
                  fullContent += cleanData
                  if (typeof onMessage === 'function') {
                    onMessage(cleanData, fullContent)
                  }
                }
                if (typeof onComplete === 'function') {
                  onComplete(fullContent)
                }
                return
              }
              
              fullContent += data
              if (typeof onMessage === 'function') {
                onMessage(data, fullContent)
              }
            }
          }
        }
      } catch (error) {
        console.error('解析分块数据错误:', error)
      }
    })
  }
  
  eventSource = {
    close: () => {
      if (requestTask && requestTask.abort) {
        requestTask.abort()
      }
    }
  }
  // #endif
  
  return {
    eventSource,
    close: () => {
      if (eventSource && eventSource.close) {
        eventSource.close()
      }
    }
  }
}

/**
 * 流式对话接口 - 使用RequestTask (适用于小程序)
 * 注意：此方法为备选方案，当SSE不可用时使用
 * @param {Object} options - 配置选项
 */
export function streamChatByRequest(options) {
  const { message, sessionId, onMessage, onError, onComplete } = options
  
  let url = `${baseUrl}/ai/chatStream?message=${encodeURIComponent(message)}`
  if (sessionId) {
    url += `&sessionId=${sessionId}`
  }
  
  const token = getToken()
  let fullContent = ''
  const END_MARKER = '\u0003'
  
  const requestTask = uni.request({
    url: url,
    method: 'GET',
    header: {
      'Authorization': token ? 'Bearer ' + token : '',
      'Accept': 'text/event-stream'
    },
    enableChunked: true,
    responseType: 'text',
    success: () => {},
    fail: (error) => {
      if (typeof onError === 'function') {
        onError(error)
      }
    }
  })
  
  // 监听分块数据
  requestTask.onChunkReceived && requestTask.onChunkReceived((res) => {
    try {
      // 将ArrayBuffer转换为字符串
      const decoder = new TextDecoder('utf-8')
      const text = decoder.decode(res.data)
      
      // 解析SSE格式的数据
      const lines = text.split('\n')
      for (const line of lines) {
        if (line.startsWith('data:')) {
          const data = line.substring(5).trim()
          if (data && data !== ':heartbeat') {
            // 检查结束标记
            if (data === END_MARKER || data.includes(END_MARKER)) {
              const cleanData = data.replace(END_MARKER, '')
              if (cleanData) {
                fullContent += cleanData
                if (typeof onMessage === 'function') {
                  onMessage(cleanData, fullContent)
                }
              }
              if (typeof onComplete === 'function') {
                onComplete(fullContent)
              }
              return
            }
            
            fullContent += data
            if (typeof onMessage === 'function') {
              onMessage(data, fullContent)
            }
          }
        }
      }
    } catch (error) {
      console.error('解析分块数据错误:', error)
    }
  })
  
  return {
    requestTask,
    abort: () => {
      if (requestTask && requestTask.abort) {
        requestTask.abort()
      }
    }
  }
}
