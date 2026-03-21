package com.mc.ai.config;

import com.mc.ai.prompt.AiPrompts;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * 题库题目生成专用 ChatClient 配置
 * 通过 RestTemplate 配置 120 秒超时，避免 AI 生成多道题目时请求超时
 *
 * @author caidu
 */
@Configuration
public class QuestionGenerationChatClientConfig {

    /**
     * 题库题目生成专用 ChatClient
     * 依赖底层 RestTemplate 的超时配置
     */
    @Bean(name = "questionGenerationChatClient")
    public ChatClient questionGenerationChatClient(ChatClient.Builder builder) {
        return builder
                .defaultSystem(AiPrompts.QUESTION_GENERATION)
                .build();
    }

    /**
     * 全局 RestTemplate 超时配置
     * Spring AI Alibaba 底层使用 RestTemplate（Apache HttpClient 5）发送请求
     * 直接在 HttpComponentsClientHttpRequestFactory 上设置超时，无需额外依赖
     */
    @Bean
    @Primary
    public RestTemplate dashscopeRestTemplate() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(30));
        factory.setReadTimeout(Duration.ofSeconds(120));
        return new RestTemplate(factory);
    }
}
