package com.mc.questionnaire.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mc.questionnaire.domain.dto.AiGenerateQuestionDTO;
import com.mc.questionnaire.domain.dto.AiGenerateQuestionEntity;
import com.mc.questionnaire.domain.dto.AiGenerateQuestionnaireDTO;
import com.mc.questionnaire.domain.dto.AiGenerateQuestionnaireEntity;
import com.mc.questionnaire.domain.vo.AiGenerateQuestionVO;
import com.mc.questionnaire.domain.vo.AiGenerateQuestionnaireVO;
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

    @Override
    public AiGenerateQuestionnaireVO generateQuestionnaire(AiGenerateQuestionnaireDTO dto) {
        log.info("AI 生成整套问卷请求 - 主题: {}, 选择题: {} 道, 简答题: {} 道",
                dto.getDescription(), dto.getChoiceCount(), dto.getShortAnswerCount());

        AiGenerateQuestionnaireVO vo = new AiGenerateQuestionnaireVO();
        List<AiGenerateQuestionVO> questions = new java.util.ArrayList<>();

        int total = dto.getChoiceCount() + dto.getShortAnswerCount();
        String userPrompt = String.format(
                "请生成一套大学生心理健康测评问卷，包含 %d 道选择题（评估学业压力、情绪管理、人际关系、自我认知等维度）和 %d 道简答题（引导深度自我反思）。\n" +
                "主题要求：%s\n" +
                "要求：\n" +
                "1. 选择题要有区分度，能有效评估心理健康各维度\n" +
                "2. 简答题放在问卷末尾，要有深度，引导学生自我反思，无标准答案\n" +
                "3. 返回标准 JSON 对象，格式为 { \"questions\": [ ... ] }，其中每道题目包含 content（题干）、type（choice/short_answer）、options（选择题时为对象如 {\"A\":\"...\",\"B\":\"...\",\"C\":\"...\",\"D\":\"...\"}，简答题时为 null）\n" +
                "4. questions 数组中选择题在前，简答题在后，共 %d 道题目",
                dto.getChoiceCount(), dto.getShortAnswerCount(), dto.getDescription(), total);

        try {
            AiGenerateQuestionnaireEntity entity = questionGenerationChatClient
                    .prompt()
                    .user(userPrompt)
                    .call()
                    .entity(AiGenerateQuestionnaireEntity.class);

            if (entity != null && entity.getQuestions() != null) {
                for (AiGenerateQuestionnaireEntity.AiGenerateQuestionnaireItemEntity item : entity.getQuestions()) {
                    String entityType = item.getType();
                    if (!"choice".equals(entityType) && !"short_answer".equals(entityType)) {
                        entityType = "choice"; // 兜底
                    }
                    questions.add(toVoFromItem(item, entityType));
                }
            }

            // 如果题目数量不足，补充生成
            while (questions.size() < dto.getChoiceCount()) {
                AiGenerateQuestionDTO singleDto = new AiGenerateQuestionDTO();
                singleDto.setType("choice");
                singleDto.setDescription(dto.getDescription());
                questions.add(0, generateQuestion(singleDto));
            }
            while (questions.size() < total) {
                AiGenerateQuestionDTO singleDto = new AiGenerateQuestionDTO();
                singleDto.setType("short_answer");
                singleDto.setDescription(dto.getDescription());
                questions.add(generateQuestion(singleDto));
            }

            vo.setQuestions(questions);
            log.info("AI 整套问卷生成成功，共 {} 道题目", questions.size());
        } catch (Exception e) {
            log.error("AI 生成整套问卷失败: {}", e.getMessage(), e);
            vo.setQuestions(questions);
        }

        return vo;
    }

    private AiGenerateQuestionVO toVoFromItem(AiGenerateQuestionnaireEntity.AiGenerateQuestionnaireItemEntity item, String requestedType) {
        AiGenerateQuestionVO vo = new AiGenerateQuestionVO();
        vo.setContent(item != null ? item.getContent() : null);
        vo.setType(requestedType);

        if (!"choice".equals(requestedType)) {
            vo.setOptions(null);
            vo.setOptionList(List.of());
            return vo;
        }

        String optionsJson = normalizeOptionsJson(item != null ? item.getOptions() : null);
        vo.setOptions(optionsJson);
        vo.setOptionList(buildOptionList(optionsJson));
        return vo;
    }
}
