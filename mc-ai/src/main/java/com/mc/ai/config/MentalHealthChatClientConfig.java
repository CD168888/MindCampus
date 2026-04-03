package com.mc.ai.config;

import com.mc.ai.prompt.AiPrompts;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 心理健康评估专用 ChatClient 配置
 * <p>
 * 创建一个独立的 ChatClient 实例，专门用于心理健康评估场景。
 * 该 Client 具有以下特性：
 * - 使用 MENTAL_HEALTH_EVALUATION 系统提示词，专注于心理评估任务
 * - 不使用对话记忆（每次评估独立，不受历史消息影响）
 * - 不注册业务工具（评估过程纯粹由问卷内容驱动）
 * - 单次非流式调用，确保返回稳定的 JSON 结构化输出
 *
 * @author caidu
 */
@Configuration
public class MentalHealthChatClientConfig {

    /**
     * 配置心理健康评估专用 ChatClient 实例
     *
     * @param builder Spring AI 的 ChatClient 构建器
     * @return 配置完成的 ChatClient 实例
     */
    @Bean(name = "mentalHealthChatClient")
    public ChatClient mentalHealthChatClient(ChatClient.Builder builder) {
        return builder
                // 设置心理健康评估专用系统提示词
                .defaultSystem(AiPrompts.MENTAL_HEALTH_EVALUATION)
                // 不使用对话记忆和工具，保持评估的独立性
                .build();
    }
}
