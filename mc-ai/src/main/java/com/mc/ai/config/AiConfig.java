package com.mc.ai.config;

import com.alibaba.cloud.ai.memory.redis.RedisChatMemoryRepository;
import com.mc.ai.prompt.AiPrompts;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring AI 配置类
 * 配置 ChatClient 和对话记忆功能
 */
@Configuration
public class AiConfig {

    @Value("${spring.ai.memory.redis.host}")
    private String redisHost;
    @Value("${spring.ai.memory.redis.port}")
    private int redisPort;
    // 若没有设置密码则注释该项
//    @Value("${spring.ai.memory.redis.password}")
//    private String redisPassword;

    @Value("${spring.ai.memory.redis.timeout}")
    private int redisTimeout;

    /**
     * 配置 ChatClient 实例
     * 包含默认系统提示和会话记忆功能
     */
    @Bean
    public ChatClient chatClient(ChatClient.Builder builder, RedisChatMemoryRepository chatMemoryRepository) {
        return builder
                // 设置默认系统提示
                .defaultSystem(AiPrompts.STUDENT_WELL_BEING_PROMPT)
                // 添加默认会话记忆顾问
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(
                                MessageWindowChatMemory.builder()
                                        .chatMemoryRepository(chatMemoryRepository)
                                        .build())
                                .build())
                .build();
    }

    @Bean
    public RedisChatMemoryRepository chatMemoryRepository() {
        return RedisChatMemoryRepository.builder()
                .host(redisHost)
                .port(redisPort)
                // 若没有设置密码则注释该项
//				.password(redisPassword)
                .timeout(redisTimeout)
                .build();
    }
}
