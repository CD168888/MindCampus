package com.mc.ai.config;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * 全局 RestTemplate 超时配置
 * <p>
 * Spring AI Alibaba 底层使用 RestTemplate 发送 DashScope 请求。
 * 通过 @Primary 确保所有 RestTemplate 注入点默认使用此配置。
 * <p>
 * 配置说明：
 * - connectTimeout=30s：建立 TCP 连接的最大等待时间
 * - readTimeout=120s：等待服务器响应的最大时间（题目生成等耗时操作需要较长超时）
 * - maxTotal=100：连接池最大连接数
 * - defaultMaxPerRoute=20：每个路由的默认最大连接数
 *
 * @author caidu
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    @Primary
    public RestTemplate restTemplate() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(100);
        connectionManager.setDefaultMaxPerRoute(20);

        CloseableHttpClient closeableClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .build();
        HttpClient httpClient = closeableClient;

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setHttpClient(httpClient);
        factory.setConnectTimeout(Duration.ofSeconds(30));
        factory.setReadTimeout(Duration.ofSeconds(120));

        return new RestTemplate(factory);
    }
}
