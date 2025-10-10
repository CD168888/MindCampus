package com.mc.ai.config;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 通义千问 DashScope 全局 ChatClient 配置类
 *
 * 统一在这里构造 ChatClient Bean，让其他模块通过 @Autowired 直接使用
 */
@Configuration
public class DashScopeAiConfig {

    private static final String DEFAULT_PROMPT = "你是一个博学的智能助手，请根据用户提问回答问题。";

    @Bean

    /**
     * 构建 DashScope 平台的 ChatClient 实例
     *
     * 该客户端配置了默认系统提示词、日志跟踪功能以及特定于 DashScope 的聊天选项。
     * 其他模块可以通过自动注入的方式直接使用此 Bean 进行 AI 对话交互。
     *
     * @param chatClientBuilder Spring AI 提供的 ChatClient 构建器
     * @return 配置完成的 ChatClient 实例
     */
    public ChatClient dashScopeChatClient(ChatClient.Builder chatClientBuilder) {
        return chatClientBuilder
                .defaultSystem(DEFAULT_PROMPT)
                .defaultAdvisors(new SimpleLoggerAdvisor()) // 启用日志跟踪，便于调试和监控
                .defaultOptions(
                        DashScopeChatOptions.builder()
                                .withTopP(0.7) // 设置 Top-P 采样参数，控制生成文本的多样性
                                .build()
                )
                .build();
    }
}
