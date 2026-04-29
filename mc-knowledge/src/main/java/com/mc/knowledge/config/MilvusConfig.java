package com.mc.knowledge.config;

import io.milvus.v2.client.ConnectConfig;
import io.milvus.v2.client.MilvusClientV2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Zilliz Cloud Milvus 向量数据库配置（v2 API）
 *
 * @author MindCampus
 */
@Configuration
public class MilvusConfig {

    @Value("${spring.milvus.uri}")
    private String uri;

    @Bean
    public MilvusClientV2 milvusClientV2() {
        ConnectConfig.ConnectConfigBuilder builder = ConnectConfig.builder()
            .uri(uri);

        String token = System.getenv("ZILLIZ_API_KEY");
        if (token != null && !token.isEmpty()) {
            builder.token(token);
        }

        return new MilvusClientV2(builder.build());
    }
}
