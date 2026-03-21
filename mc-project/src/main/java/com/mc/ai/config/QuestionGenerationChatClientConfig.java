package com.mc.ai.config;

import com.mc.ai.prompt.AiPrompts;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 题库题目生成专用 ChatClient 配置
 * <p>
 * 使用 Spring AI 标准方式构建 ChatClient，底层 HTTP 请求依赖
 * RestTemplateConfig 中 @Primary 的 RestTemplate（已配置 120 秒超时）。
 *
 * @author caidu
 */
@Configuration
public class QuestionGenerationChatClientConfig {

    /**
     * 题库题目生成专用 ChatClient
     * 所有 AI 请求走 RestTemplateConfig 的 @Primary RestTemplate，
     * 自动继承 connectTimeout=30s、readTimeout=120s 的配置。
     */
    @Bean(name = "questionGenerationChatClient")
    public ChatClient questionGenerationChatClient(ChatClient.Builder builder) {
        return builder
                .defaultSystem(AiPrompts.QUESTION_GENERATION)
                .build();
    }
}
