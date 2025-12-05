/**
 * AI对话API接口
 */
import request from '@/utils/request'
import config from '@/config'
import {getToken} from '@/utils/auth'

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
    const {message, sessionId, onMessage, onError, onComplete} = options

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
            let buffer = '' // 用于处理跨 chunk 的不完整行
            let eventDataLines = [] // 收集同一个SSE事件中的所有data行

            function processChunk({done, value}) {
                if (done) {
                    // 处理最后可能残留的事件数据
                    if (eventDataLines.length > 0) {
                        const content = eventDataLines.join('\n')
                        if (content && !content.includes(END_MARKER)) {
                            fullContent += content
                            if (typeof onMessage === 'function') {
                                onMessage(content, fullContent)
                            }
                        }
                    }
                    // 流结束
                    if (typeof onComplete === 'function') {
                        onComplete(fullContent)
                    }
                    return
                }

                // 解码二进制数据
                const text = decoder.decode(value, {stream: true})
                buffer += text

                // 按行分割处理 SSE 数据
                const lines = buffer.split('\n')
                // 保留最后一行（可能不完整）
                buffer = lines.pop() || ''

                for (const line of lines) {
                    // 移除行末的 \r（Windows换行符）
                    const cleanLine = line.endsWith('\r') ? line.slice(0, -1) : line

                    if (cleanLine.startsWith('data:')) {
                        // 获取data:后面的内容，不使用trim()以保留空白
                        const data = cleanLine.substring(5)

                        // 检查结束标记
                        if (data === END_MARKER || data.includes(END_MARKER)) {
                            // 先处理之前收集的数据
                            if (eventDataLines.length > 0) {
                                const content = eventDataLines.join('\n')
                                fullContent += content
                                if (typeof onMessage === 'function') {
                                    onMessage(content, fullContent)
                                }
                                eventDataLines = []
                            }
                            // 处理结束标记
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

                        // 收集data行（包括空字符串，因为空字符串表示换行）
                        eventDataLines.push(data)
                    } else if (cleanLine === '') {
                        // 空行表示一个SSE事件结束
                        if (eventDataLines.length > 0) {
                            // 将同一事件的多个data行用换行符连接
                            const content = eventDataLines.join('\n')
                            eventDataLines = []

                            if (content !== ':heartbeat') {
                                fullContent += content
                                if (typeof onMessage === 'function') {
                                    onMessage(content, fullContent)
                                }
                            }
                        }
                    }
                }

                // 继续读取下一个 chunk
                return reader.read().then(processChunk)
            }

            // 开始读取流
            return reader.read().then(processChunk)
        })
        .catch(error => {
            if (error.name === 'AbortError') {
                console.log('请求被中断')
                if (typeof onComplete === 'function') {
                    onComplete(fullContent)
                }
            } else {
                console.error('Fetch 错误:', error)
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
    let chunkBuffer = '' // 用于处理跨chunk的不完整行
    let chunkEventDataLines = [] // 收集同一个SSE事件中的所有data行

    if (requestTask.onChunkReceived) {
        requestTask.onChunkReceived((res) => {
            try {
                const decoder = new TextDecoder('utf-8')
                const text = decoder.decode(res.data)
                chunkBuffer += text

                // 按行分割
                const lines = chunkBuffer.split('\n')
                // 保留最后一行（可能不完整）
                chunkBuffer = lines.pop() || ''

                for (const line of lines) {
                    // 移除行末的 \r（Windows换行符）
                    const cleanLine = line.endsWith('\r') ? line.slice(0, -1) : line

                    if (cleanLine.startsWith('data:')) {
                        // 获取data:后面的内容，不使用trim()以保留空白
                        const data = cleanLine.substring(5)

                        // 检查结束标记
                        if (data === END_MARKER || data.includes(END_MARKER)) {
                            // 先处理之前收集的数据
                            if (chunkEventDataLines.length > 0) {
                                const content = chunkEventDataLines.join('\n')
                                fullContent += content
                                if (typeof onMessage === 'function') {
                                    onMessage(content, fullContent)
                                }
                                chunkEventDataLines = []
                            }
                            // 处理结束标记
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

                        // 收集data行（包括空字符串，因为空字符串表示换行）
                        chunkEventDataLines.push(data)
                    } else if (cleanLine === '') {
                        // 空行表示一个SSE事件结束
                        if (chunkEventDataLines.length > 0) {
                            // 将同一事件的多个data行用换行符连接
                            const content = chunkEventDataLines.join('\n')
                            chunkEventDataLines = []

                            if (content !== ':heartbeat') {
                                fullContent += content
                                if (typeof onMessage === 'function') {
                                    onMessage(content, fullContent)
                                }
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

    // #ifdef H5
    // H5端返回 abortController 作为 eventSource（包含 close 方法）
    const eventSourceWrapper = {
        abortController,
        close: () => {
            abortController.abort()
        }
    }
    return {
        eventSource: eventSourceWrapper,
        close: () => {
            abortController.abort()
        }
    }
    // #endif

    // #ifndef H5
    // 非H5端返回 requestTask
    return {
        eventSource: requestTask,
        requestTask,
        close: () => {
            if (requestTask && requestTask.abort) {
                requestTask.abort()
            }
        }
    }
    // #endif
}

/**
 * 流式对话接口 - 使用RequestTask (适用于小程序)
 * 注意：此方法为备选方案，当SSE不可用时使用
 * @param {Object} options - 配置选项
 */
export function streamChatByRequest(options) {
    const {message, sessionId, onMessage, onError, onComplete} = options

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
        success: () => {
        },
        fail: (error) => {
            if (typeof onError === 'function') {
                onError(error)
            }
        }
    })

    // 监听分块数据
    let chunkBuffer = '' // 用于处理跨chunk的不完整行
    let eventDataLines = [] // 收集同一个SSE事件中的所有data行

    requestTask.onChunkReceived && requestTask.onChunkReceived((res) => {
        try {
            // 将ArrayBuffer转换为字符串
            const decoder = new TextDecoder('utf-8')
            const text = decoder.decode(res.data)
            chunkBuffer += text

            // 按行分割
            const lines = chunkBuffer.split('\n')
            // 保留最后一行（可能不完整）
            chunkBuffer = lines.pop() || ''

            for (const line of lines) {
                // 移除行末的 \r（Windows换行符）
                const cleanLine = line.endsWith('\r') ? line.slice(0, -1) : line

                if (cleanLine.startsWith('data:')) {
                    // 获取data:后面的内容，不使用trim()以保留空白
                    const data = cleanLine.substring(5)

                    // 检查结束标记
                    if (data === END_MARKER || data.includes(END_MARKER)) {
                        // 先处理之前收集的数据
                        if (eventDataLines.length > 0) {
                            const content = eventDataLines.join('\n')
                            fullContent += content
                            if (typeof onMessage === 'function') {
                                onMessage(content, fullContent)
                            }
                            eventDataLines = []
                        }
                        // 处理结束标记
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

                    // 收集data行（包括空字符串，因为空字符串表示换行）
                    eventDataLines.push(data)
                } else if (cleanLine === '') {
                    // 空行表示一个SSE事件结束
                    if (eventDataLines.length > 0) {
                        // 将同一事件的多个data行用换行符连接
                        const content = eventDataLines.join('\n')
                        eventDataLines = []

                        if (content !== ':heartbeat') {
                            fullContent += content
                            if (typeof onMessage === 'function') {
                                onMessage(content, fullContent)
                            }
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
