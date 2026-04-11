package com.mc.ai.mcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

/**
 * MCP Server 配置（预留）
 * <p>
 * 注意：当前仅使用了 MCP Client 端（连接外部 MCP 服务器）。
 * 如果后续需要将本项目工具暴露为 MCP Server（供外部AI助手调用），
 * 需要添加 spring-ai-starter-mcp-server-webmvc 依赖并在此配置。
 * <p>
 * 当前接入的外部 MCP 服务：
 * - Tavily：提供 tavily-search、tavily-extract 工具
 *
 * @author MindCampus MCP Team
 */
@Configuration
public class McpServerConfig {

    private static final Logger log = LoggerFactory.getLogger(McpServerConfig.class);

    public McpServerConfig() {
        log.info("MindCampus MCP 模块已加载");
        log.info("MCP Client 配置: classpath:mcp-servers.json");
    }
}
