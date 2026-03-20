package com.mc.questionnaire.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mc.questionnaire.domain.dto.AiGenerateQuestionDTO;
import com.mc.questionnaire.domain.dto.AiGenerateQuestionEntity;
import com.mc.questionnaire.domain.vo.AiGenerateQuestionVO;
import com.mc.questionnaire.service.IQuestionBankAiService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 题库 AI 题目生成服务实现：使用 .entity() 固定输出结构，再转为前端 VO（含 options 字符串与 optionList）
 */
@Service
@Slf4j
public class QuestionBankAiServiceImpl implements IQuestionBankAiService {

    @Resource(name = "questionGenerationChatClient")
    private ChatClient questionGenerationChatClient;

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public AiGenerateQuestionVO generateQuestion(AiGenerateQuestionDTO dto) {
        log.info("AI 生成题目请求 - 类型: {}, 描述: {}", dto.getType(), dto.getDescription());

        String typeText = "choice".equals(dto.getType()) ? "选择题（含A/B/C/D四个选项）" : "简答题";
        String userPrompt = String.format("请生成一道%s，主题要求：%s", typeText, dto.getDescription());

        AiGenerateQuestionEntity entity = questionGenerationChatClient
                .prompt()
                .user(userPrompt)
                .call()
                .entity(AiGenerateQuestionEntity.class);

        AiGenerateQuestionVO vo = toVo(entity, dto.getType());
        log.info("AI 题目生成成功 - 内容: {}, options 已规范化: {}", vo.getContent(), vo.getOptions() != null);
        return vo;
    }

    private AiGenerateQuestionVO toVo(AiGenerateQuestionEntity entity, String requestedType) {
        AiGenerateQuestionVO vo = new AiGenerateQuestionVO();
        vo.setContent(entity != null ? entity.getContent() : null);
        // 以管理端选择的类型为准，避免模型 type 字段不一致
        vo.setType(requestedType);

        if (!"choice".equals(requestedType)) {
            vo.setOptions(null);
            vo.setOptionList(List.of());
            return vo;
        }

        String optionsJson = normalizeOptionsJson(entity != null ? entity.getOptions() : null);
        vo.setOptions(optionsJson);
        vo.setOptionList(buildOptionList(optionsJson));
        return vo;
    }

    /**
     * 将模型输出的 options（对象、字符串或 null）规范为与题库表一致的 JSON 字符串
     */
    private String normalizeOptionsJson(JsonNode optionsNode) {
        if (optionsNode == null || optionsNode.isNull()) {
            return null;
        }
        try {
            if (optionsNode.isTextual()) {
                String text = optionsNode.asText().trim();
                if (text.isEmpty() || "null".equalsIgnoreCase(text)) {
                    return null;
                }
                // 校验是否为合法 JSON 对象
                JsonNode parsed = objectMapper.readTree(text);
                if (parsed.isObject()) {
                    return objectMapper.writeValueAsString(parsed);
                }
                return null;
            }
            if (optionsNode.isObject()) {
                return objectMapper.writeValueAsString(optionsNode);
            }
        } catch (Exception e) {
            log.warn("规范化 options 失败: {}", e.getMessage());
        }
        return null;
    }

    private List<AiGenerateQuestionVO.OptionItem> buildOptionList(String optionsJson) {
        if (optionsJson == null || optionsJson.isBlank()) {
            return List.of();
        }
        try {
            Map<String, String> map = objectMapper.readValue(optionsJson, new TypeReference<LinkedHashMap<String, String>>() {});
            List<AiGenerateQuestionVO.OptionItem> list = new ArrayList<>();
            for (Map.Entry<String, String> e : map.entrySet()) {
                if (e.getKey() == null || e.getValue() == null) {
                    continue;
                }
                AiGenerateQuestionVO.OptionItem item = new AiGenerateQuestionVO.OptionItem();
                item.setLabel(e.getKey().trim());
                item.setContent(e.getValue().trim());
                list.add(item);
            }
            list.sort(Comparator.comparing(AiGenerateQuestionVO.OptionItem::getLabel));
            return list;
        } catch (Exception e) {
            log.warn("解析 options 为列表失败: {}", e.getMessage());
            return List.of();
        }
    }
}
