package com.mc.ai.config;

import com.mc.ai.prompt.AiPrompts;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 学生端专用 ChatClient 配置
 * <p>
 * 创建一个专门面向学生用户的 AI 对话 ChatClient，具有以下特性：
 * - 系统提示词：使用 STUDENT_WELL_BEING_PROMPT（大学生心理陪伴伙伴）
 * - 对话记忆：使用 Redis 持久化 ChatMemory，支持跨会话上下文
 * - 业务工具：仅注册 MentalHealthTools 中的学生端查询工具
 * <p>
 * 关键设计：通过 ApplicationContext 运行时获取 MentalHealthTools 实例，
 * 避免 mc-ai 模块在编译期依赖 mc-project 模块，从而规避循环依赖。
 * MentalHealthTools 虽在 mc-project 中，但被 @Component 标记后会被
 * Spring Boot 启动类的包扫描路径（com.mc）自动发现并注册为 Bean。
 *
 * @author MindCampus MCP Team
 */
@Configuration
public class StudentChatClientConfig {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ChatMemory chatMemory;

    /**
     * 学生端专用 ChatClient 实例
     * <p>
     * 该 Client 是学生 AI 陪伴对话的核心组件，通过 .tools() 方法
     * 注入 MentalHealthTools（运行时从 ApplicationContext 获取），
     * 供 AiChatServiceImpl 根据场景选择使用。
     *
     * @param builder Spring AI 的 ChatClient 构建器
     * @return 配置完成的 ChatClient 实例
     */
    @Bean(name = "studentChatClient")
    public ChatClient studentChatClient(ChatClient.Builder builder) {
        // 运行时从 ApplicationContext 获取 MentalHealthTools 实例
        // mc-project 中的 @Component 会被 Spring Boot 启动类（com.mc.*）的包扫描路径自动发现
        Object mentalHealthTools = applicationContext.getBean("mentalHealthTools");

        return builder.clone()
                // 学生心理陪伴专用系统提示词
                .defaultSystem(AiPrompts.STUDENT_WELL_BEING_PROMPT)
                // 对话记忆（Redis 持久化）
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                // 仅注册学生端工具（MentalHealthTools 中的 @Tool 方法）
                .defaultTools(mentalHealthTools)
                .build();
    }
}