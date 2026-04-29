package com.mc.knowledge.kg.service;

import com.mc.knowledge.domain.vo.RagResultDTO;
import com.mc.knowledge.domain.vo.StudentProfileNode;

import java.util.List;

/**
 * 学生画像知识图谱服务接口
 *
 * @author MindCampus
 */
public interface IStudentProfileKGService {

    /**
     * 查询学生的完整画像上下文（从 Neo4j 图谱检索）
     *
     * @param userId 用户ID
     * @return 学生画像节点上下文
     */
    StudentProfileNode queryStudentProfile(Long userId);

    /**
     * 查询学生情绪历史
     *
     * @param userId 用户ID
     * @param limit  返回记录数
     * @return 情绪历史描述字符串
     */
    String queryEmotionHistory(Long userId, int limit);

    /**
     * 查询学生风险趋势
     *
     * @param userId 用户ID
     * @return 风险趋势描述字符串
     */
    String queryRiskTrend(Long userId);

    /**
     * 初始化学生节点（首次登录时调用）
     *
     * @param userId    用户ID
     * @param studentId 学生ID
     * @param studentNo 学号
     * @param name      姓名
     * @param grade     年级
     * @param major     专业
     * @param className 班级
     */
    void initStudentNode(Long userId, Long studentId, String studentNo, String name, String grade, String major, String className);

    /**
     * 同步评估记录到图谱
     *
     * @param userId            用户ID
     * @param resultId          评估结果ID
     * @param questionnaireTitle 问卷标题
     * @param totalScore        总分
     * @param riskLevel         风险等级
     */
    void syncAssessment(Long userId, Long resultId, String questionnaireTitle, Integer totalScore, String riskLevel);

    /**
     * 同步情绪状态到图谱
     *
     * @param userId     用户ID
     * @param emotion    情绪描述
     * @param intensity  情绪强度（1-10）
     */
    void syncEmotionState(Long userId, String emotion, Integer intensity);

    /**
     * 同步 AI 对话交互统计到图谱
     *
     * @param userId        用户ID
     * @param sessionId     会话ID
     * @param messageCount  消息数量
     * @param avgScore      平均情绪分数
     */
    void syncInteraction(Long userId, Long sessionId, Integer messageCount, Double avgScore);

    /**
     * 更新学生性格画像（管理员手动设置或 AI 分析）
     *
     * @param userId              用户ID
     * @param personality         性格描述
     * @param communicationStyle  沟通风格
     * @param interests          兴趣爱好
     * @param concerns            关注事项
     */
    void updateProfile(Long userId, String personality, String communicationStyle,
                       List<String> interests, List<String> concerns);

    /**
     * 构建图谱上下文字符串（用于 Prompt 增强）
     *
     * @param userId 用户ID
     * @return 格式化的图谱上下文字符串
     */
    String buildKgContext(Long userId);

    /**
     * RAG 相似度检索
     *
     * @param query  查询文本
     * @param kbId  知识库ID（null 表示搜索所有）
     * @param topK  返回前 K 条
     * @return RAG 检索结果列表
     */
    List<RagResultDTO> ragRetrieve(String query, Long kbId, int topK);
}
