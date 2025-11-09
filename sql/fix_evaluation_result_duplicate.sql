-- ====================================================================
-- 修复问卷发送重复数据问题
-- 日期: 2025-11-08
-- 说明: 为 evaluation_result 表添加唯一索引，防止同一学生同一问卷重复创建测评记录
-- ====================================================================

-- 1. 先删除可能存在的重复数据（保留最早的记录）
DELETE t1 FROM evaluation_result t1
INNER JOIN evaluation_result t2 
WHERE 
    t1.student_id = t2.student_id 
    AND t1.questionnaire_id = t2.questionnaire_id
    AND t1.result_id > t2.result_id;

-- 2. 添加唯一索引约束
ALTER TABLE evaluation_result 
ADD UNIQUE INDEX uk_student_questionnaire (student_id, questionnaire_id) 
COMMENT '学生问卷唯一索引：同一学生不能对同一问卷创建多条测评记录';

-- 3. 验证索引创建
SHOW INDEX FROM evaluation_result WHERE Key_name = 'uk_student_questionnaire';

