package com.mc.questionnaire.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

/**
 * AI 生成整套问卷的实体类（用于 Spring AI .entity() 反序列化）
 * AI 返回格式可能是:
 *   1. 数组: [{"content":"...","type":"choice","options":{...}}, ...]
 *   2. 对象: {"questions": [{"content":"...","type":"choice","options":{...}}, ...]}
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AiGenerateQuestionnaireEntity {

    private java.util.List<AiGenerateQuestionnaireItemEntity> questions;

    private AiGenerateQuestionnaireItemEntity single;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AiGenerateQuestionnaireItemEntity {
        private String content;
        private String type;
        private JsonNode options;
    }
}
