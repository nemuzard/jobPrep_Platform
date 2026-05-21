package com.jobprep.jobprep_platform.mapper;

import org.apache.ibatis.annotations.Mapper;

import io.lettuce.core.dynamic.annotation.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;
import java.util.Set;
import com.jobprep.jobprep_platform.model.entity.CommentLike;
@Mapper
public interface CommentLikeMapper {
    
    void insert(CommentLike CommentLike);
    void delete(@Param("commentId") Integer commentId, @Param("userId") Long userId);

    /**
     *  find the commentIds that the user has liked from the given list of commentIds
     * @param userId
     * @param commentIds
     * @return
     */
    Set<Integer> findUserLikedCommentIds(@Param("userId") Long userId,
            @Param("commentIds") List<Integer> commentIds);
    
    @Select("SELECT COUNT(*)>0 FROM comment_like"+
            "WHERE user_id = #{userId} AND comment_id = #{commentId}")
    boolean checkIsliked(@Param("userId") Long userId, @Param("commentId") Integer commentId);

}
