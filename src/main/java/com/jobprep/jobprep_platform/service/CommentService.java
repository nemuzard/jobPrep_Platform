package com.jobprep.jobprep_platform.service;

import org.springframework.transaction.annotation.Transactional;
import com.jobprep.jobprep_platform.model.base.ApiResponse;
import com.jobprep.jobprep_platform.model.dto.comment.CreateCommentRequest;
import com.jobprep.jobprep_platform.model.dto.comment.UpdateCommentRequest;
import com.jobprep.jobprep_platform.model.dto.comment.CommentQueryParams;
import com.jobprep.jobprep_platform.model.vo.comment.CommentVO;
import com.jobprep.jobprep_platform.model.base.EmptyVO;
import java.util.List;

@Transactional
public interface CommentService {
    /**
     * Create a new comment
     * @param request
     * @return Comment id or error message
     */
    ApiResponse<Integer> createComment(CreateCommentRequest request);
    /// Update an existing comment
    // why update comment need commend id? because we need to know which comment to update
    // but why in dto, we dont have comment id? because we can get comment id from path variable, and in request body, we only need content to update
    ApiResponse<EmptyVO> updateComment(Integer commentId, UpdateCommentRequest request);

    /**
     * Delete a comment
     * @param commentId
     * @return
     */
    ApiResponse<EmptyVO> deleteComment(Integer commentId);

    /**
     * Get comments for a note
     * @param params 
     * @return
     */
    ApiResponse<List<CommentVO>> getComments(CommentQueryParams params);

    // why dont need user id?
    //  because we can get user id from token, and in request body,
    //  we only need comment id to like or unlike
    ApiResponse<EmptyVO> likeComment(Integer commentId);

    ApiResponse<EmptyVO> unlikeComment(Integer commentId);



}
