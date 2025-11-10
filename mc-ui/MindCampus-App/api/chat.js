/**
 * AI对话API接口 - 模拟数据
 */

// 模拟延迟函数
const delay = (ms) => new Promise(resolve => setTimeout(resolve, ms));

// 模拟历史对话数据
const mockChatHistory = [
  {
    chatId: '12345678-1234-4abc-8abc-123456789abc',
    firstMessage: '如何缓解压力',
    timestamp: Date.now() - 3600000,
    messageCount: 8
  },
  {
    chatId: '87654321-4321-4def-8def-987654321def',
    firstMessage: '情绪管理技巧',
    timestamp: Date.now() - 7200000,
    messageCount: 12
  },
  {
    chatId: 'abcdefgh-5678-4ghi-8ghi-abcdefghijkl',
    firstMessage: '如何提高学习效率',
    timestamp: Date.now() - 86400000,
    messageCount: 6
  }
];

// 模拟对话消息数据
const mockChatMessages = {
  '12345678-1234-4abc-8abc-123456789abc': [
    {
      role: 'user',
      content: '如何缓解压力',
      timestamp: Date.now() - 3600000,
      attachments: []
    },
    {
      role: 'assistant',
      content: '缓解压力的方法有很多，以下是一些常见且有效的方法：\n\n1. 深呼吸和冥想：通过深呼吸和冥想可以帮助你放松身心，减少焦虑。\n\n2. 运动锻炼：适度的运动可以释放内啡肽，这是一种天然的"快乐激素"。\n\n3. 保持良好的作息：充足的睡眠对于缓解压力非常重要。\n\n4. 与他人交流：与朋友、家人或专业人士分享你的感受。\n\n5. 培养兴趣爱好：做一些自己喜欢的事情可以转移注意力。',
      timestamp: Date.now() - 3590000,
      attachments: []
    },
    {
      role: 'user',
      content: '深呼吸具体应该怎么做？',
      timestamp: Date.now() - 3580000,
      attachments: []
    },
    {
      role: 'assistant',
      content: '深呼吸练习的正确方法：\n\n1. 找一个安静舒适的地方坐下或躺下\n2. 闭上眼睛，放松身体\n3. 用鼻子慢慢吸气，数到4，让空气充满腹部\n4. 屏住呼吸，数到4\n5. 用嘴慢慢呼气，数到6-8\n6. 重复这个过程5-10分钟\n\n建议每天练习1-2次，坚持下去会有很好的效果！',
      timestamp: Date.now() - 3570000,
      attachments: []
    }
  ],
  '87654321-4321-4def-8def-987654321def': [
    {
      role: 'user',
      content: '情绪管理技巧',
      timestamp: Date.now() - 7200000,
      attachments: []
    },
    {
      role: 'assistant',
      content: '情绪管理是一项重要的生活技能。以下是一些有效的情绪管理技巧：\n\n1. 识别情绪：首先要能够准确识别自己的情绪状态。\n\n2. 接纳情绪：允许自己有各种情绪，不要压抑。\n\n3. 表达情绪：用健康的方式表达情绪，如写日记、画画等。\n\n4. 调节情绪：通过运动、音乐、冥想等方式调节情绪。\n\n5. 寻求支持：必要时向朋友、家人或专业人士寻求帮助。',
      timestamp: Date.now() - 7190000,
      attachments: []
    }
  ]
};

// 模拟AI响应内容
const mockAIResponses = [
  '这是一个很好的问题。让我来帮你分析一下...',
  '根据你的描述，我建议你可以尝试以下几点：\n\n1. 保持积极的心态\n2. 制定合理的计划\n3. 适当的休息和放松',
  '我理解你的感受。在面对这种情况时，我们可以从几个方面来考虑：\n\n首先，要认识到这是一个普遍的问题。\n\n其次，我们需要找到适合自己的解决方法。',
  '很高兴能帮助你！如果你还有其他问题，随时可以问我。',
  '基于心理学研究，我推荐你尝试认知行为疗法的一些技巧...',
  '你提到的这个问题确实需要重视。让我们一起来探讨一下可能的解决方案。',
];

/**
 * 获取历史对话列表
 * @param {string} chatType - 对话类型
 */
export function getChatHistory(chatType = 'chat') {
  return new Promise(async (resolve) => {
    await delay(500); // 模拟网络延迟
    
    resolve({
      code: 200,
      msg: '获取成功',
      data: mockChatHistory
    });
  });
}

/**
 * 获取对话消息列表
 * @param {string} chatType - 对话类型
 * @param {string} chatId - 对话ID
 */
export function getChatMessages(chatType, chatId) {
  return new Promise(async (resolve) => {
    await delay(300);
    
    const messages = mockChatMessages[chatId] || [];
    
    resolve({
      code: 200,
      msg: '获取成功',
      data: messages
    });
  });
}

/**
 * 发送消息（流式响应模拟）
 * @param {Object} data - 消息数据
 */
export function sendChatMessage(data) {
  const { prompt, chatId, files, onMessage } = data;
  
  return new Promise(async (resolve, reject) => {
    try {
      await delay(500); // 模拟初始延迟
      
      // 随机选择一个响应
      const response = mockAIResponses[Math.floor(Math.random() * mockAIResponses.length)];
      
      // 模拟流式输出
      const chars = response.split('');
      for (let i = 0; i < chars.length; i++) {
        await delay(30); // 每个字符30ms延迟
        if (typeof onMessage === 'function') {
          onMessage(chars[i]);
        }
      }
      
      resolve({
        code: 200,
        msg: '发送成功',
        data: {
          content: response
        }
      });
    } catch (error) {
      reject({
        code: 500,
        msg: '发送失败',
        error: error
      });
    }
  });
}

/**
 * 删除对话
 * @param {string} chatType - 对话类型
 * @param {string} chatId - 对话ID
 */
export function deleteChat(chatType, chatId) {
  return new Promise(async (resolve) => {
    await delay(300);
    
    // 从模拟数据中删除
    const index = mockChatHistory.findIndex(chat => chat.chatId === chatId);
    if (index !== -1) {
      mockChatHistory.splice(index, 1);
    }
    
    resolve({
      code: 200,
      msg: '删除成功'
    });
  });
}

/**
 * 重命名对话
 * @param {string} chatType - 对话类型
 * @param {Object} data - 数据对象
 */
export function renameChat(chatType, data) {
  const { chatId, prompt } = data;
  
  return new Promise(async (resolve) => {
    await delay(300);
    
    // 更新模拟数据
    const chat = mockChatHistory.find(c => c.chatId === chatId);
    if (chat) {
      chat.firstMessage = prompt;
    }
    
    resolve({
      code: 200,
      msg: '修改成功'
    });
  });
}

