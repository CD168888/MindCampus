package com.mc.ai.config;

import com.mc.ai.prompt.AiPrompts;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 题库题目生成专用 ChatClient 配置
 * 与心理健康评估的 ChatClient 分离，使用不同的系统提示词
 *
 * @author caidu
 */
@Configuration
public class QuestionGenerationChatClientConfig {

    /**
     * 题库题目生成专用 ChatClient
     * 使用 QUESTION_GENERATION 系统提示词，不使用对话记忆和工具
     */
    @Bean(name = "questionGenerationChatClient")
    public ChatClient questionGenerationChatClient(ChatClient.Builder builder) {
        return builder
                .defaultSystem(AiPrompts.QUESTION_GENERATION)
                .build();
    }
}
