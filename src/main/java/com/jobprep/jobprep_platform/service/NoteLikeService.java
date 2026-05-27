package com.jobprep.jobprep_platform.service;

import org.springframework.transaction.annotation.Transactional;

import com.jobprep.jobprep_platform.model.base.ApiResponse;
import com.jobprep.jobprep_platform.model.base.EmptyVO;

import java.util.List;
import java.util.Set;
@Transactional
public interface NoteLikeService {
    
    Set<Integer> findUserLikedNoteIds(Long userId, List<Integer> noteIds);
    ApiResponse<EmptyVO> likeNote(Integer noteId);
    ApiResponse<EmptyVO> unlikeNote(Integer noteId);
}
