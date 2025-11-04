-- =========================
    create_by VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME COMMENT '创建时间',
    update_by VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME COMMENT '更新时间',
    remark VARCHAR(200) DEFAULT NULL COMMENT '备注'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='心理测评结果表';

CREATE TABLE questionnaire_answer (
    answer_id           BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '答题记录ID',
    result_id           BIGINT NOT NULL COMMENT '关联心理测评结果ID',
    questionnaire_id    BIGINT NOT NULL COMMENT '问卷ID',
    question_id         BIGINT NOT NULL COMMENT '题目ID',
    
    -- 冗余字段（用于历史追溯和快速统计）
    type                VARCHAR(20) NOT NULL COMMENT '题目类型（choice选择题/short_answer简答题）',
    content             VARCHAR(500) NOT NULL COMMENT '题干内容',
    options             JSON COMMENT '选择题选项（A/B/C/D…），简答题为空',
    standard_answer     VARCHAR(200) COMMENT '标准答案（仅选择题有效）',
    score               INT DEFAULT 0 COMMENT '分值',

    -- 用户作答部分
    user_answer         VARCHAR(500) COMMENT '用户作答内容',
    is_correct          TINYINT(1) DEFAULT NULL COMMENT '是否答对（1正确/0错误/空表示简答题）',
    obtain_score        INT DEFAULT 0 COMMENT '用户获得分数',

    create_by VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME COMMENT '创建时间',
    update_by VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME COMMENT '更新时间',
    remark VARCHAR(200) DEFAULT NULL COMMENT '备注
) COMMENT='心理测评答题记录表（含冗余题目信息）';