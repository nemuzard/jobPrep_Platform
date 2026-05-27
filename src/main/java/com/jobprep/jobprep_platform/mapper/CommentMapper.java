package com.jobprep.jobprep_platform.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.jobprep.jobprep_platform.model.dto.comment.CommentQueryParams;
import com.jobprep.jobprep_platform.model.entity.Comment;
import java.util.List;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CommentMapper {
    
    void insert(Comment comment);
    void update(Comment comment);
    void deleteById(@Param("commentId") Integer commentId);
    
    /**
     * find comment based on id 
     * @param commentId
     * @return comment instance
     */
    Comment findById(@Param("commentId") Integer commentId);

    /**
     * find comment list based on note id 
     * @param nodeId
     * @return
     */
    List<Comment> findByNoteId(@Param("noteId") Integer nodeId);

    /**
     * find comment list
     * @param params
     * @param pageSize
     * @param offset
     * @return comment list 
     */
    List<Comment> findByQueryParam(
            @Param("params") CommentQueryParams params,
            @Param("pageSize") Integer pageSize,
            @Param("offset") Integer offset

    );

    /**
     * count comments 
     * @param params
     * @return
     */
    int countByQueryParam(@Param("params") CommentQueryParams params);

    
    void incrementLikeCount(@Param("commentId") Integer commentId);
    void decrementLikeCount(@Param("commentId") Integer commentId);
    void incrementReplyCount(@Param("commentId") Integer commentId);
    void decrementReplyCount(@Param("commentId") Integer commentId);


}
