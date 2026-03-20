package com.mc.questionnaire.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

/**
 * 仅用于 Spring AI {@code .entity()} 反序列化模型输出。
 * {@code options} 在模型输出中常为 JSON 对象，故用 {@link JsonNode} 接收。
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AiGenerateQuestionEntity {

    private String content;

    private String type;

    private JsonNode options;
}
