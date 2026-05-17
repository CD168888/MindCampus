/**
 * SSE (Server-Sent Events) 解析工具
 * 提供公共的 SSE 流解析逻辑，避免代码重复
 */

const END_MARKER = ''

/**
 * 创建 SSE 流解析器
 * @param {Object} options - 配置选项
 * @param {Function} options.onMessage - 接收消息回调 (content, fullContent)
 * @param {Function} options.onComplete - 完成回调 (fullContent)
 * @param {Function} options.onError - 错误回调 (error)
 * @returns {Object} 解析器对象，包含 processChunk 方法
 */
export function createSSEParser(options) {
    const { onMessage, onComplete, onError } = options
    let fullContent = ''
    let buffer = ''
    let eventDataLines = []

    /**
     * 处理接收到的数据块
     * @param {string} text - 接收到的文本数据
     * @param {boolean} isDone - 是否是最后一块数据
     */
    function processChunk(text, isDone = false) {
        if (isDone) {
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

        buffer += text

        // 按行分割处理 SSE 数据
        const lines = buffer.split('\n')
        // 保留最后一行（可能不完整）
        buffer = lines.pop() || ''

        for (const line of lines) {
            // 移除行末的 \r（Windows换行符）
            const cleanLine = line.endsWith('\r') ? line.slice(0, -1) : line

            if (cleanLine.startsWith('data:')) {
                // 获取data:后面的内容
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

                // 收集data行
                eventDataLines.push(data)
            } else if (cleanLine === '') {
                // 空行表示一个SSE事件结束
                if (eventDataLines.length > 0) {
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
    }

    /**
     * 获取当前累积的完整内容
     * @returns {string}
     */
    function getFullContent() {
        return fullContent
    }

    /**
     * 重置解析器状态
     */
    function reset() {
        fullContent = ''
        buffer = ''
        eventDataLines = []
    }

    return {
        processChunk,
        getFullContent,
        reset
    }
}
