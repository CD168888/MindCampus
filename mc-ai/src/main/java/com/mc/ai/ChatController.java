package com.mc.ai;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class ChatController {

    private final ChatClient chatClient;

    public ChatController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    /**
     * 同步聊天接口：返回 AI 生成的文本结果（String）
     *
     * @param input 用户输入的查询内容
     * @return AI 生成的完整文本回复
     */
    @GetMapping("/chatTest")
    public String chat(String input) {
        return chatClient.prompt()
                .user(input)
                .call()
                .content();
    }

    /**
     * 流式聊天接口：通过 Server-Sent Events (SSE) 返回 AI 生成的文本流
     *
     * @param input 用户输入的查询内容
     * @param response HttpServletResponse 对象，用于设置响应头信息
     * @return 实时文本流，以 SSE 格式返回，前端可通过 EventSource 接收
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/chat/SSEStream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatStreamContent(String input, HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8"); // 显式设置 UTF-8
        response.setContentType("text/event-stream;charset=UTF-8");

        return Flux.defer(() -> chatClient.prompt()
                .user(input)
                .stream()
                .content()
                .map(content -> "data: " + content + "\n\n") // SSE 格式
        );
    }
    
    /**
     * 流式聊天接口：返回 AI 生成的纯文本流
     *
     * @param input 用户输入的查询内容
     * @return 实时文本流，以纯文本格式返回
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/chat/stream", produces = "text/plain;charset=UTF-8")
    public Flux<String> chatStreamContent(String input) {
        return chatClient.prompt()
                .user(input)
                .stream()
                .content();
    }
}