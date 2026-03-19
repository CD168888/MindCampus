package com.mc.ai.config;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.memory.redis.RedisChatMemoryRepository;
import com.mc.ai.prompt.AiPrompts;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

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

    @Value("${spring.ai.dashscope.chat.options.model}")
    private String modelName;

    @Value("${spring.ai.model.name}")
    private String multiModalModelName;

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 配置 ChatClient 实例
     * 包含默认系统提示、对话记忆功能和工具
     */
//    @Bean
//    public ChatClient chatClient(ChatClient.Builder builder, RedisChatMemoryRepository chatMemoryRepository) {
//        return builder
//                // 设置默认系统提示
//                .defaultSystem(AiPrompts.GENERAL_ASSISTANT)
//                // 添加默认会话记忆顾问
//                .defaultAdvisors(
//                        MessageChatMemoryAdvisor.builder(
//                                MessageWindowChatMemory.builder()
//                                        .chatMemoryRepository(chatMemoryRepository)
//                                        .build())
//                                .build())
//                // 注册学生信息工具和心理评估工具
//                .defaultTools(studentInfoTool, studentPsychologicalTool)
//                .defaultToolNames("xxxxxx")
//                .build();
//    }


    // 基础文本模型
    @Bean
    @Primary
    public ChatClient chatClient(ChatClient.Builder builder, ChatMemory chatMemory) {
        // 获取工具 Bean 的逻辑保留
        String[] toolNames = getToolNames();

        return builder.clone()
                // 使用 DashScopeChatOptions
                .defaultOptions(DashScopeChatOptions.builder()
                        .withModel(modelName)
                        .build())
                .defaultSystem(AiPrompts.GENERAL_ASSISTANT)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .defaultToolNames(toolNames)
                .build();
    }

    // 多模态模型
    @Bean
    public ChatClient multiModalChatClient(ChatClient.Builder builder, ChatMemory chatMemory) {
        String[] toolNames = getToolNames();

        return builder.clone()
                // 必须使用 DashScopeChatOptions 并开启 MultiModel
                .defaultOptions(DashScopeChatOptions.builder()
                        .withModel(multiModalModelName) // 例如 qwen-vl-max
                        .withMultiModel(true)           // 灵魂参数：不加这个流式和多模态都会失效！
                        .build())
                .defaultSystem(AiPrompts.GENERAL_ASSISTANT)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                // 注意：部分视觉模型可能对 Tool 调用支持不好，如果报错可以把下面这行注释掉
                .defaultToolNames(toolNames)
                .build();
    }

    /**
     * 获取所有工具名称
     * 公共配置方法，自动发现并注册 Spring 容器中所有的 Supplier 和 Function 类型的 Bean 作为 AI 工具。
     * 这样做的好处是无需手动维护工具列表，新增的工具 Bean 会自动被注册到 ChatClient 中。
     */
    private String[] getToolNames() {
        Map<String, Supplier> supplierBeans = applicationContext.getBeansOfType(Supplier.class);
        Map<String, Function> functionBeans = applicationContext.getBeansOfType(Function.class);
        Set<String> allToolNames = new HashSet<>(supplierBeans.keySet());
        allToolNames.addAll(functionBeans.keySet());
        return allToolNames.toArray(new String[0]);
    }


//    /**
//     * 公共配置方法
//     * 该方法会自动发现并注册 Spring 容器中所有的 Supplier 和 Function 类型的 Bean 作为 AI 工具。
//     * 这样做的好处是无需手动维护工具列表，新增的工具 Bean 会自动被注册到 ChatClient 中。
//     *
//     * @param builder Spring AI 的 ChatClient 构建器，用于配置和创建 ChatClient 实例
//     * @param chatMemory Redis 聊天记忆仓库，用于持久化对话历史
//     * @return 配置完成的 ChatClient 实例，包含所有自动发现的工具和对话记忆功能
//     */
//    private ChatClient configureClient(ChatClient.Builder builder, ChatMemory chatMemory, String modelName){
//        // 获取 Spring 容器中所有 Supplier 和 Function Bean
//        Map<String, Supplier> supplierBeans = applicationContext.getBeansOfType(Supplier.class);
//        Map<String, Function> functionBeans = applicationContext.getBeansOfType(Function.class);
//
//        // 收集 Bean 名称
//        Set<String> allToolNames = new HashSet<>(supplierBeans.keySet());
//        allToolNames.addAll(functionBeans.keySet());
//
//        return builder
//                .defaultOptions(ChatOptions.builder().model(modelName).build())
//                .defaultSystem(AiPrompts.GENERAL_ASSISTANT)
//                .defaultAdvisors(
//                        MessageChatMemoryAdvisor.builder(chatMemory)
//                                .build())
//                .defaultToolNames(allToolNames.toArray(new String[0]))
//                .build();
//    }

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

    /**
     * 配置 ChatMemory Bean，供其他服务注入使用
     */
    @Bean
    public ChatMemory chatMemory(RedisChatMemoryRepository chatMemoryRepository) {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(chatMemoryRepository)
                .build();
    }

}
