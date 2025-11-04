//package com.mc.ai.service;
//
//import org.springframework.stereotype.Service;
//import com.mc.system.service.ISysConfigService;
//import jakarta.annotation.Resource;
//
//@Service
//public class AiPromptService {
//
//    @Resource
//    private ISysConfigService configService; // 读取若依配置表 sys_config
//
//    private static final String DEFAULT_PROMPT = """
//            你是一名专业的心理测评智能分析师，请根据提供的问卷题目信息与学生答题数据，
//            完成测评分析并返回标准 JSON 结果，确保输出符合以下结构：
//
//            {
//              "student_id": "{{student_id}}",
//              "questionnaire_id": "{{questionnaire_id}}",
//              "total_score": 0,
//              "risk_level": "低|中|高",
//              "radar_data": {
//                "情绪稳定性": 0,
//                "人际关系": 0,
//                "压力管理": 0,
//                "自我认知": 0,
//                "社会适应": 0
//              },
//              "analysis_text": "string",
//              "ai_status": "1"
//            }
//
//            问卷题目数据：
//            {{question_list}}
//
//            学生答题数据：
//            {{student_answers}}
//            """;
//
//    public String buildPrompt(Long studentId, Long questionnaireId, String questionListJson, String studentAnswerJson) {
//        // 支持从配置中心加载模板
//        String basePrompt = configService.selectConfigByKey("ai.analysis.prompt");
//        if (basePrompt == null || basePrompt.isEmpty()) {
//            basePrompt = DEFAULT_PROMPT;
//        }
//
//        return basePrompt
//                .replace("{{student_id}}", String.valueOf(studentId))
//                .replace("{{questionnaire_id}}", String.valueOf(questionnaireId))
//                .replace("{{question_list}}", questionListJson)
//                .replace("{{student_answers}}", studentAnswerJson);
//    }
//}
