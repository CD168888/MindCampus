package com.mc.knowledge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mc.knowledge.domain.entity.KbDocumentChunk;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文档切分块Mapper接口
 *
 * @author MindCampus
 */
@Mapper
public interface KbDocumentChunkMapper extends BaseMapper<KbDocumentChunk> {
}
