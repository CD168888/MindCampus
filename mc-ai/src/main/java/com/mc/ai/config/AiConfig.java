package com.mc.ai.config;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.memory.redis.RedisChatMemoryRepository;
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
 * Spring AI 核心配置类
 * <p>
 * 负责配置 ChatClient 和对话记忆功能，是 AI 能力的核心配置入口。
 * <p>
 * 工具注册机制：通过 ApplicationContext 动态扫描运行时获取所有 Supplier/Function 类型的 Spring Bean，
 * 并将其注册给 ChatClient。此设计使得：
 * - mc-ai 模块在编译期不感知任何业务工具的存在（无循环依赖）
 * - mc-project 中的业务工具 Bean（如 ToolConfig 中的 @Bean）被扫描并自动注册
 * - 新增业务工具只需在 mc-project 中添加 @Bean 方法即可，无需修改本类
 *
 * @author caidu
 */
@Configuration
public class AiConfig {

    @Value("${spring.ai.memory.redis.host}")
    private String redisHost;

    @Value("${spring.ai.memory.redis.port}")
    private int redisPort;

    // 若没有设置密码则注释该项
    // @Value("${spring.ai.memory.redis.password}")
    // private String redisPassword;

    @Value("${spring.ai.memory.redis.timeout}")
    private int redisTimeout;

    @Value("${spring.ai.dashscope.chat.options.model}")
    private String modelName;

    @Value("${spring.ai.model.name}")
    private String multiModalModelName;

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 配置基础文本模型 ChatClient 实例
     * <p>
     * 该 Client 用于通用对话场景，具备以下特性：
     * - 使用 qwen-plus 模型（由 application.yml 配置）
     * - 配备对话记忆功能（ChatMemory，底层 Redis 存储）
     * - 自动注册运行时扫描到的所有业务工具 Bean
     * - 默认系统提示词由调用方（mc-project）通过 .system() 方法动态传入，
     *   不在本类中硬编码，保证提示词的灵活性
     */
    @Bean
    @Primary
    public ChatClient chatClient(ChatClient.Builder builder, ChatMemory chatMemory) {
        // 运行时扫描 ApplicationContext 中所有 Supplier 和 Function Bean 作为工具
        String[] toolNames = getToolNames();

        return builder.clone()
                // 使用 DashScopeChatOptions 配置模型参数
                .defaultOptions(DashScopeChatOptions.builder()
                        .withModel(modelName)
                        .build())
                // 添加会话记忆顾问，底层使用 Redis 持久化对话历史
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                // 自动注册运行时扫描到的业务工具（Supplier/Function Bean）
                .defaultToolNames(toolNames)
                .build();
    }

    /**
     * 配置多模态模型 ChatClient 实例
     * <p>
     * 该 Client 用于处理包含图片附件的对话场景，具备以下特性：
     * - 使用 qwen-vl-plus 模型（视觉理解能力，由 application.yml 配置）
     * - 必须开启 MultiModel 选项，否则流式和多模态功能失效
     * - 配备对话记忆功能（与主 ChatClient 共享 ChatMemory）
     * - 自动注册运行时扫描到的所有业务工具 Bean
     */
    @Bean
    public ChatClient multiModalChatClient(ChatClient.Builder builder, ChatMemory chatMemory) {
        String[] toolNames = getToolNames();

        return builder.clone()
                // 必须使用 DashScopeChatOptions 并开启 MultiModel
                .defaultOptions(DashScopeChatOptions.builder()
                        .withModel(multiModalModelName)
                        .withMultiModel(true) // 灵魂参数：不加这个流式和多模态都会失效
                        .build())
                // 添加会话记忆顾问
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                // 注意：部分视觉模型可能对 Tool 调用支持不好，如果报错可以把下面这行注释掉
                .defaultToolNames(toolNames)
                .build();
    }

    /**
     * 获取所有工具名称
     * <p>
     * 公共配置方法，自动发现并注册 Spring 容器中所有的 Supplier 和 Function 类型的 Bean 作为 AI 工具。
     * 这样做的好处是无需手动维护工具列表，新增的工具 Bean 会自动被注册到 ChatClient 中。
     * <p>
     * 关键设计：此方法在运行时通过 ApplicationContext.getBeansOfType() 扫描，
     * 因此 mc-ai 模块在编译期不依赖 mc-project 中的任何具体类，
     * 完美避免循环依赖问题。
     */
    private String[] getToolNames() {
        Map<String, Supplier> supplierBeans = applicationContext.getBeansOfType(Supplier.class);
        Map<String, Function> functionBeans = applicationContext.getBeansOfType(Function.class);
        Set<String> allToolNames = new HashSet<>(supplierBeans.keySet());
        allToolNames.addAll(functionBeans.keySet());
        return allToolNames.toArray(new String[0]);
    }

    /**
     * 配置 Redis 对话记忆仓库
     * <p>
     * 用于持久化对话历史，支持跨会话恢复和历史记录查询。
     */
    @Bean
    public RedisChatMemoryRepository chatMemoryRepository() {
        return RedisChatMemoryRepository.builder()
                .host(redisHost)
                .port(redisPort)
                // 若没有设置密码则注释该项
                // .password(redisPassword)
                .timeout(redisTimeout)
                .build();
    }

    /**
     * 配置 ChatMemory Bean，供 ChatClient 注入使用
     * <p>
     * ChatMemory 是 Spring AI 的对话记忆接口，
     * MessageWindowChatMemory 实现类使用滑动窗口策略管理历史消息。
     */
    @Bean
    public ChatMemory chatMemory(RedisChatMemoryRepository chatMemoryRepository) {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(chatMemoryRepository)
                .build();
    }
}
