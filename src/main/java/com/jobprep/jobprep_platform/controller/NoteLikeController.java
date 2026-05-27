package com.jobprep.jobprep_platform.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jobprep.jobprep_platform.model.base.ApiResponse;
import com.jobprep.jobprep_platform.model.base.EmptyVO;
import com.jobprep.jobprep_platform.service.NoteLikeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/like/note")
@RequiredArgsConstructor
public class NoteLikeController {
    private final NoteLikeService noteLikeService;

    @PostMapping("/{noteId}")
    public ApiResponse<EmptyVO> likeNote(@PathVariable Integer noteId) {
        return noteLikeService.likeNote(noteId);
    }

    @DeleteMapping("/{noteId}")
    public ApiResponse<EmptyVO> unlikeNote(@PathVariable Integer noteId) {
        return noteLikeService.unlikeNote(noteId);
    }
}
