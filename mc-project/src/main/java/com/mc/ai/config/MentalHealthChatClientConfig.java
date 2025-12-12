package com.mc.ai.config;

import com.mc.ai.prompt.AiPrompts;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 心理健康评估专用ChatClient配置
 * 创建一个独立的ChatClient实例，避免与现有配置冲突
 *
 * @author caidu
 */
@Configuration
public class MentalHealthChatClientConfig {

    /**
     * 配置心理健康评估专用ChatClient实例
     * 不使用对话记忆和工具，专注于单次评估任务
     *
     * @param builder Spring AI的ChatClient构建器
     * @return 配置完成的ChatClient实例
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