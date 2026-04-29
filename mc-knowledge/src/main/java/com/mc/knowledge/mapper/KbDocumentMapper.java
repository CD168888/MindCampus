package com.mc.knowledge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mc.knowledge.domain.entity.KbDocument;
import org.apache.ibatis.annotations.Mapper;

/**
 * 知识库文档Mapper接口
 *
 * @author MindCampus
 */
@Mapper
public interface KbDocumentMapper extends BaseMapper<KbDocument> {
}
