package com.mc.ai.config;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DashScopeChatConfig {

    @Bean("qwenPlusChatClient")
    public ChatClient qwenPlusChatClient(ChatClient.Builder builder) {
        return builder
                .defaultSystem("你是一个知识渊博的智能问答助手。")
                .defaultOptions(DashScopeChatOptions.builder()
                        .withModel("qwen-plus")  // 指定模型
                        .withTemperature(0.7)
                        .build())
                .build();
    }

    @Bean("qwenVLChatClient")
    public ChatClient qwenVLChatClient(ChatClient.Builder builder) {
        return builder
                .defaultSystem("你是一个多模态视觉理解助手。")
                .defaultOptions(DashScopeChatOptions.builder()
                        .withModel("qwen-vl-max")  // 多模态大模型
                        .withTopP(0.8)
                        .build())
                .build();
    }
}
