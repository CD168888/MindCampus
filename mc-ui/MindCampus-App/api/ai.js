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
 * 流式对话接口 - 使用 POST + FormData 支持文件上传
 * @param {Object} options - 配置选项
 * @param {string} options.message - 用户消息
 * @param {number} options.sessionId - 会话ID(可选,后端会自动创建)
 * @param {Array<Object>} options.files - 文件对象数组 [{path, name}] (可选)
 * @param {Array<string>} options.fileUrls - 附件URL列表(可选)
 * @param {boolean} options.enableRag - 是否启用 RAG 向量检索增强(可选,默认false)
 * @param {boolean} options.enableKg - 是否启用知识图谱检索增强(可选,默认false)
 * @param {Function} options.onMessage - 接收消息回调 (chunk, fullContent)
 * @param {Function} options.onError - 错误回调
 * @param {Function} options.onComplete - 完成回调
 * @returns {Object} 返回包含 abortController 和 close 方法的对象
 */
export function streamChat(options) {
    const {message, sessionId, files, fileUrls, enableRag, enableKg, onMessage, onError, onComplete} = options

    const token = getToken()
    let fullContent = ''
    const END_MARKER = '\u0003'

    // 创建 AbortController 用于取消请求
    const abortController = new AbortController()

    // #ifdef H5
    // H5端使用 fetch API + FormData
    const formData = new FormData()
    formData.append('message', message)
    if (sessionId) {
        formData.append('sessionId', sessionId)
    }
    // RAG+KG 增强参数
    if (enableRag) {
        formData.append('enableRag', 'true')
    }
    if (enableKg) {
        formData.append('enableKg', 'true')
    }
    // 添加文件
    if (files && files.length > 0) {
        files.forEach((file, index) => {
            if (file.file && file.file instanceof File) {
                formData.append('files', file.file)
            }
        })
    }
    // 添加文件URL
    if (fileUrls && fileUrls.length > 0) {
        fileUrls.forEach(url => {
            formData.append('fileUrls', url)
        })
    }

    fetch(`${baseUrl}/ai/chatStream`, {
        method: 'POST',
        headers: {
            'Authorization': token ? 'Bearer ' + token : '',
            'Accept': 'text/event-stream',
            'Cache-Control': 'no-cache'
        },
        body: formData,
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
    // 非H5端：如果有文件，先上传获取URL，再发送流式请求
    let uploadedFileUrls = fileUrls ? [...fileUrls] : []
    let filesToSend = files || []
    
    // 先上传文件
    const uploadPromises = []
    if (filesToSend.length > 0) {
        for (const file of filesToSend) {
            if (file.path) {
                uploadPromises.push(
                    new Promise((resolve, reject) => {
                        uni.uploadFile({
                            url: `${baseUrl}/common/upload`,
                            filePath: file.path,
                            name: 'file',
                            header: {
                                'Authorization': 'Bearer ' + token
                            },
                            success: (res) => {
                                try {
                                    const data = JSON.parse(res.data)
                                    if (data.code === 200 && data.url) {
                                        resolve(data.url)
                                    } else {
                                        reject(new Error(data.msg || '上传失败'))
                                    }
                                } catch (e) {
                                    reject(e)
                                }
                            },
                            fail: reject
                        })
                    })
                )
            }
        }
    }
    
    Promise.all(uploadPromises).then(uploadedUrls => {
        uploadedFileUrls = [...uploadedFileUrls, ...uploadedUrls]
        
        // 构建请求URL
        let requestUrl = `${baseUrl}/ai/chatStream?message=${encodeURIComponent(message)}`
        if (sessionId) {
            requestUrl += `&sessionId=${sessionId}`
        }
        // RAG+KG 增强参数
        if (enableRag) {
            requestUrl += '&enableRag=true'
        }
        if (enableKg) {
            requestUrl += '&enableKg=true'
        }
        if (uploadedFileUrls.length > 0) {
            uploadedFileUrls.forEach(url => {
                requestUrl += `&fileUrls=${encodeURIComponent(url)}`
            })
        }
        
        const requestTask = uni.request({
            url: requestUrl,
            method: 'POST',
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
        let chunkBuffer = ''
        let chunkEventDataLines = []

        if (requestTask.onChunkReceived) {
            requestTask.onChunkReceived((res) => {
                try {
                    const decoder = new TextDecoder('utf-8')
                    const text = decoder.decode(res.data)
                    chunkBuffer += text

                    const lines = chunkBuffer.split('\n')
                    chunkBuffer = lines.pop() || ''

                    for (const line of lines) {
                        const cleanLine = line.endsWith('\r') ? line.slice(0, -1) : line

                        if (cleanLine.startsWith('data:')) {
                            const data = cleanLine.substring(5)

                            if (data === END_MARKER || data.includes(END_MARKER)) {
                                if (chunkEventDataLines.length > 0) {
                                    const content = chunkEventDataLines.join('\n')
                                    fullContent += content
                                    if (typeof onMessage === 'function') {
                                        onMessage(content, fullContent)
                                    }
                                    chunkEventDataLines = []
                                }
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

                            chunkEventDataLines.push(data)
                        } else if (cleanLine === '') {
                            if (chunkEventDataLines.length > 0) {
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
    }).catch(error => {
        console.error('文件上传失败:', error)
        if (typeof onError === 'function') {
            onError(error)
        }
    })
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
