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
 * 流式对话接口 - 使用 fetch + ReadableStream
 * @param {Object} options - 配置选项
 * @param {string} options.message - 用户消息
 * @param {number} options.sessionId - 会话ID(可选,后端会自动创建)
 * @param {Function} options.onMessage - 接收消息回调 (chunk, fullContent)
 * @param {Function} options.onError - 错误回调
 * @param {Function} options.onComplete - 完成回调
 * @returns {Object} 返回包含 abortController 和 close 方法的对象
 */
export function streamChat(options) {
  const { message, sessionId, onMessage, onError, onComplete } = options
  
  // 构建URL
  let url = `${baseUrl}/ai/chatStream?message=${encodeURIComponent(message)}`
  if (sessionId) {
    url += `&sessionId=${sessionId}`
  }
  
  const token = getToken()
  console.log('获取到的token:', token ? token.substring(0, 20) + '...' : 'null')
  console.log('请求URL:', url)
  
  let fullContent = ''
  // 结束标记字符 (ETX - End of Text)
  const END_MARKER = '\u0003'
  
  // 创建 AbortController 用于取消请求
  const abortController = new AbortController()
  
  // #ifdef H5
  // H5端使用 fetch API
  fetch(url, {
    method: 'GET',
    headers: {
      'Authorization': token ? 'Bearer ' + token : '',
      'Accept': 'text/event-stream',
      'Cache-Control': 'no-cache'
    },
    signal: abortController.signal
  })
  .then(response => {
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`)
    }
    
    const reader = response.body.getReader()
    const decoder = new TextDecoder('utf-8')
    let buffer = ''
    
    // 递归读取流数据
    const processStream = () => {
      reader.read().then(({ done, value }) => {
        if (done) {
          console.log('流读取完成')
          if (typeof onComplete === 'function') {
            onComplete(fullContent)
          }
          return
        }
        
        // 解码数据块
        const chunk = decoder.decode(value, { stream: true })
        buffer += chunk
        
        // 处理 SSE 格式数据
        const lines = buffer.split('\n')
        buffer = lines.pop() || '' // 保留未完成的行
        
        for (const line of lines) {
          if (line.startsWith('data:')) {
            const data = line.substring(5).trim()
            
            // 跳过心跳消息
            if (!data || data === ':heartbeat') {
              continue
            }
            
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
              reader.cancel()
              return
            }
            
            // 处理正常数据
            fullContent += data
            if (typeof onMessage === 'function') {
              onMessage(data, fullContent)
            }
          } else if (line.startsWith('event:')) {
            // 处理事件类型(如 sessionId)
            const eventType = line.substring(6).trim()
            if (eventType === 'sessionId') {
              // 下一行应该是 data
              const nextLineIndex = lines.indexOf(line) + 1
              if (nextLineIndex < lines.length) {
                const dataLine = lines[nextLineIndex]
                if (dataLine.startsWith('data:')) {
                  const sessionIdData = dataLine.substring(5).trim()
                  if (typeof options.onSessionId === 'function') {
                    options.onSessionId(sessionIdData)
                  }
                }
              }
            }
          }
        }
        
        // 继续读取下一块数据
        processStream()
      }).catch(error => {
        if (error.name === 'AbortError') {
          console.log('流读取已取消')
        } else {
          console.error('流读取错误:', error)
          if (typeof onError === 'function') {
            onError(error)
          }
        }
      })
    }
    
    // 开始处理流
    processStream()
  })
  .catch(error => {
    if (error.name === 'AbortError') {
      console.log('请求已取消')
    } else {
      console.error('请求错误:', error)
      if (typeof onError === 'function') {
        onError(error)
      }
    }
  })
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
  // #endif
  
  return {
    // #ifdef H5
    abortController,
    close: () => {
      abortController.abort()
    },
    // #endif
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
