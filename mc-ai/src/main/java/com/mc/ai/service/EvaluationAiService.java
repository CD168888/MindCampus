//package com.mc.ai.service;
//
//import com.mc.ai.domain.EvaluationAIResult;
//import jakarta.annotation.Resource;
//import org.springframework.ai.chat.client.ChatClient;
//import org.springframework.stereotype.Service;
//
//@Service
//public class EvaluationAiService {
//
//    @Resource
//    private ChatClient chatClient;
//
//    @Resource
//    private AiPromptService aiPromptService;
//
//    public EvaluationAIResult analyzeEvaluation(Long studentId, Long questionnaireId, String questionListJson, String studentAnswerJson) {
//
//        String prompt = aiPromptService.buildPrompt(studentId, questionnaireId, questionListJson, studentAnswerJson);
//
//        // ✅ 使用阿里 ChatClient 直接解析为对象
//        return chatClient.prompt(prompt)
//                .call()
//                .entity(EvaluationAIResult.class);
//    }
//}
