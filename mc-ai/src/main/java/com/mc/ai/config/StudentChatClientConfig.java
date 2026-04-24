package com.mc.ai.config;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.mc.ai.prompt.AiPrompts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 学生端专用 ChatClient 配置
 * <p>
 * 创建一个专门面向学生用户的 AI 对话 ChatClient，具有以下特性：
 * - 系统提示词：使用 STUDENT_WELL_BEING_PROMPT（大学生心理陪伴伙伴）
 * - 对话记忆：使用 Redis 持久化 ChatMemory，支持跨会话上下文
 * - 业务工具：注册 MentalHealthTools 中的学生端查询工具
 * - MCP 工具：通过 spring-ai-starter-mcp-client 自动发现的外部 MCP 工具
 * - 多模态支持：使用 qwen-vl-max 模型，支持图片理解
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

    private static final Logger log = LoggerFactory.getLogger(StudentChatClientConfig.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ChatMemory chatMemory;

    @Value("${spring.ai.model.name}")
    private String multiModalModelName;

    /**
     * 学生端专用 ChatClient 实例（MCP 启用时）
     * <p>
     * 注册 MentalHealthTools 业务工具 + MCP 外部工具。
     */
    @Bean(name = "studentChatClient")
    @ConditionalOnProperty(name = "spring.ai.mcp.client.enabled", havingValue = "true", matchIfMissing = false)
    public ChatClient studentChatClientWithMcp(ChatClient.Builder builder,
            List<ToolCallbackProvider> toolCallbackProviders) {
        Object mentalHealthTools = applicationContext.getBean("mentalHealthTools");

        log.info("MCP 已启用，检测到 {} 个工具回调提供者", toolCallbackProviders.size());
        for (ToolCallbackProvider provider : toolCallbackProviders) {
            var callbacks = provider.getToolCallbacks();
            if (callbacks != null && callbacks.length > 0) {
                for (var cb : callbacks) {
                    log.info("  工具: {}", cb.getToolDefinition().name());
                }
            }
        }

        log.info("学生端ChatClient配置: 多模态模型={}, 已启用withMultiModel=true", multiModalModelName);

        // 构建 ChatClient：
        // 1. 使用 student 系统提示词
        // 2. 启用 Redis 对话记忆
        // 3. 注册学生端业务工具（MentalHealthTools）
        // 4. 注册 MCP 外部工具（由 Boot Starter 自动发现的 ToolCallbackProvider）
        // 5. 启用多模态配置
        return builder.clone()
                .defaultOptions(DashScopeChatOptions.builder()
                        .withModel(multiModalModelName)
                        .withMultiModel(true)
                        .build())
                .defaultSystem(AiPrompts.STUDENT_WELL_BEING_PROMPT)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .defaultTools(mentalHealthTools)
                .defaultToolCallbacks(toolCallbackProviders.toArray(new ToolCallbackProvider[0]))
                .build();
    }

    /**
     * 学生端专用 ChatClient 实例（MCP 禁用时）
     * <p>
     * 仅注册 MentalHealthTools 业务工具，不连接外部 MCP 服务。
     */
    @Bean(name = "studentChatClient")
    @ConditionalOnProperty(name = "spring.ai.mcp.client.enabled", havingValue = "false", matchIfMissing = true)
    public ChatClient studentChatClientWithoutMcp(ChatClient.Builder builder) {
        Object mentalHealthTools = applicationContext.getBean("mentalHealthTools");
        log.info("MCP 已禁用，仅注册 MentalHealthTools 业务工具");
        log.info("学生端ChatClient配置: 多模态模型={}, 已启用withMultiModel=true", multiModalModelName);

        return builder.clone()
                .defaultOptions(DashScopeChatOptions.builder()
                        .withModel(multiModalModelName)
                        .withMultiModel(true)
                        .build())
                .defaultSystem(AiPrompts.STUDENT_WELL_BEING_PROMPT)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .defaultTools(mentalHealthTools)
                .build();
    }
}
