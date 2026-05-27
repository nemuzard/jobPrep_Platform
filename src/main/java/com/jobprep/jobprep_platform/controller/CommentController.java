package com.jobprep.jobprep_platform.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jobprep.jobprep_platform.model.base.ApiResponse;
import com.jobprep.jobprep_platform.model.base.EmptyVO;
import com.jobprep.jobprep_platform.model.dto.comment.CommentQueryParams;
import com.jobprep.jobprep_platform.model.dto.comment.CreateCommentRequest;
import com.jobprep.jobprep_platform.model.dto.comment.UpdateCommentRequest;
import com.jobprep.jobprep_platform.model.vo.comment.CommentVO;
import com.jobprep.jobprep_platform.service.CommentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping
    public ApiResponse<List<CommentVO>> getComments(@Valid CommentQueryParams params) {
        return commentService.getComments(params);
    }

    @PostMapping
    public ApiResponse<Integer> createComment(@Valid @RequestBody CreateCommentRequest request) {
        return commentService.createComment(request);
    }

    @PatchMapping("/{commentId}")
    public ApiResponse<EmptyVO> updateComment(
            @PathVariable Integer commentId,
            @Valid @RequestBody UpdateCommentRequest request) {
        return commentService.updateComment(commentId, request);
    }

    @DeleteMapping("/{commentId}")
    public ApiResponse<EmptyVO> deleteComment(@PathVariable Integer commentId) {
        return commentService.deleteComment(commentId);
    }

    @PostMapping("/{commentId}/like")
    public ApiResponse<EmptyVO> likeComment(@PathVariable Integer commentId) {
        return commentService.likeComment(commentId);
    }

    @DeleteMapping("/{commentId}/like")
    public ApiResponse<EmptyVO> unlikeComment(@PathVariable Integer commentId) {
        return commentService.unlikeComment(commentId);
    }
}
