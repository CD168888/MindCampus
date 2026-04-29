package com.mc.knowledge.kg.service.impl;

import com.mc.knowledge.domain.vo.RagResultDTO;
import com.mc.knowledge.domain.vo.StudentProfileNode;
import com.mc.knowledge.kg.graph.Neo4jClient;
import com.mc.knowledge.kg.service.IEmbeddingService;
import com.mc.knowledge.kg.service.IStudentProfileKGService;
import com.mc.knowledge.kg.vector.MilvusVectorClient;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 学生画像知识图谱服务实现
 *
 * @author MindCampus
 */
@Service
@Slf4j
public class StudentProfileKGServiceImpl implements IStudentProfileKGService {

    @Resource
    private Neo4jClient neo4jClient;

    @Resource
    private MilvusVectorClient milvusVectorClient;

    @Resource
    private IEmbeddingService embeddingService;

    @Value("${spring.knowledge.rag.top-k:5}")
    private int ragTopK;

    @Value("${spring.knowledge.rag.min-score:0.5}")
    private double ragMinScore;

    @Override
    public StudentProfileNode queryStudentProfile(Long userId) {
        return neo4jClient.queryProfile(userId);
    }

    @Override
    public String queryEmotionHistory(Long userId, int limit) {
        return neo4jClient.queryEmotionHistory(userId, limit);
    }

    @Override
    public String queryRiskTrend(Long userId) {
        return neo4jClient.queryRiskTrend(userId);
    }

    @Override
    public void initStudentNode(Long userId, Long studentId, String studentNo, String name, String grade, String major, String className) {
        if (!neo4jClient.studentExists(userId)) {
            neo4jClient.upsertStudent(userId, studentId, studentNo, name, grade, major, className);
            log.info("[KG] 初始化学生节点 - userId: {}", userId);
        }
    }

    @Override
    public void syncAssessment(Long userId, Long resultId, String questionnaireTitle, Integer totalScore, String riskLevel) {
        neo4jClient.addAssessment(userId, resultId, questionnaireTitle, totalScore, riskLevel);
        log.info("[KG] 同步评估记录 - userId: {}, resultId: {}", userId, resultId);
    }

    @Override
    public void syncEmotionState(Long userId, String emotion, Integer intensity) {
        neo4jClient.addEmotionState(userId, emotion, intensity);
        log.info("[KG] 同步情绪状态 - userId: {}, emotion: {}", userId, emotion);
    }

    @Override
    public void syncInteraction(Long userId, Long sessionId, Integer messageCount, Double avgScore) {
        neo4jClient.upsertInteraction(userId, sessionId, messageCount, avgScore);
        log.info("[KG] 同步对话交互 - userId: {}, sessionId: {}", userId, sessionId);
    }

    @Override
    public void updateProfile(Long userId, String personality, String communicationStyle,
                              List<String> interests, List<String> concerns) {
        neo4jClient.upsertProfile(userId, personality, communicationStyle, interests, concerns);
        log.info("[KG] 更新性格画像 - userId: {}", userId);
    }

    @Override
    public String buildKgContext(Long userId) {
        StudentProfileNode profile = queryStudentProfile(userId);
        if (profile == null) {
            return "【用户画像】暂无该用户的画像数据。";
        }

        StringBuilder ctx = new StringBuilder();
        ctx.append("【用户画像】\n");

        ctx.append("- 基本信息：");
        if (profile.getName() != null) ctx.append("姓名：").append(profile.getName());
        if (profile.getGrade() != null) ctx.append("，年级：").append(profile.getGrade());
        if (profile.getMajor() != null) ctx.append("，专业：").append(profile.getMajor());
        if (profile.getClassName() != null) ctx.append("，班级：").append(profile.getClassName());
        ctx.append("\n");

        if (profile.getLatestScore() != null) {
            ctx.append("- 最近评估：")
               .append("分数 ").append(profile.getLatestScore())
               .append("，风险等级：").append(profile.getRiskLevel() != null ? profile.getRiskLevel() : "未知")
               .append("\n");
        }

        if (profile.getLatestEmotion() != null) {
            ctx.append("- 当前情绪状态：").append(profile.getLatestEmotion());
            if (profile.getEmotionIntensity() != null) {
                ctx.append("（强度 ").append(profile.getEmotionIntensity()).append("/10）");
            }
            ctx.append("\n");
        }

        if (profile.getPersonality() != null && !profile.getPersonality().isEmpty()) {
            ctx.append("- 性格特点：").append(profile.getPersonality()).append("\n");
        }
        if (profile.getCommunicationStyle() != null && !profile.getCommunicationStyle().isEmpty()) {
            ctx.append("- 沟通风格：").append(profile.getCommunicationStyle()).append("\n");
        }
        if (profile.getInterests() != null && !profile.getInterests().isEmpty()) {
            ctx.append("- 兴趣爱好：").append(String.join("、", profile.getInterests())).append("\n");
        }
        if (profile.getConcerns() != null && !profile.getConcerns().isEmpty()) {
            ctx.append("- 关注事项：").append(String.join("、", profile.getConcerns())).append("\n");
        }

        List<StudentProfileNode.AssessmentRecord> history = profile.getAssessmentHistory();
        if (history != null && !history.isEmpty()) {
            ctx.append("- 历史评估记录（共 ").append(history.size()).append(" 次）：\n");
            int count = 0;
            for (StudentProfileNode.AssessmentRecord r : history) {
                if (count >= 5) break;
                if (r.getQuestionnaireTitle() != null) {
                    ctx.append("  ").append(++count).append(". ").append(r.getQuestionnaireTitle())
                       .append("，分数：").append(r.getTotalScore())
                       .append("，风险：").append(r.getRiskLevel() != null ? r.getRiskLevel() : "未知")
                       .append("\n");
                }
            }
        }

        return ctx.toString();
    }

    @Override
    public List<RagResultDTO> ragRetrieve(String query, Long kbId, int topK) {
        try {
            float[] queryVector = embeddingService.embed(query);
            return milvusVectorClient.search(queryVector, kbId, topK, ragMinScore);
        } catch (Exception e) {
            log.error("[RAG] 检索失败 - query: {}, kbId: {}", query, kbId, e);
            return new ArrayList<>();
        }
    }
}
