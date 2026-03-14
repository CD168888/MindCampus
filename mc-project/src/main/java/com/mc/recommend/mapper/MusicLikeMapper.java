package com.mc.recommend.mapper;

import com.mc.recommend.domain.MusicLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 音乐点赞 Mapper接口
 *
 * @author caidu
 * @date 2025-11-08
 */
@Mapper
public interface MusicLikeMapper {

    /**
     * 检查用户是否已点赞
     */
    int checkLike(@Param("musicId") Long musicId, @Param("userId") Long userId);

    /**
     * 新增点赞
     */
    int insertLike(MusicLike musicLike);

    /**
     * 取消点赞
     */
    int deleteLike(@Param("musicId") Long musicId, @Param("userId") Long userId);

    /**
     * 获取音乐点赞数
     */
    int selectLikeCount(@Param("musicId") Long musicId);
}
