package com.jobprep.jobprep_platform.service;

import org.springframework.transaction.annotation.Transactional;

import com.jobprep.jobprep_platform.model.base.ApiResponse;
import java.util.List;
import java.util.Set;
@Transactional
public interface NoteLikeService {
    
    Set<Integer> findUserLikedNoteIds(Integer userId, List<Integer> noteIds);
    ApiResponse<Void> likeNote(Integer noteId);
    ApiResponse<Void> unlikeNote(Integer noteId);
}
