package com.jobprep.jobprep_platform.service;

import com.jobprep.jobprep_platform.model.base.ApiResponse;
import java.util.List;
import com.jobprep.jobprep_platform.model.entity.User;
import com.jobprep.jobprep_platform.model.entity.Note;

public interface SearchService {
    ApiResponse<List<Note>> searchNotes(String keyword,int page,int pageSize);
    ApiResponse<List<User>> searchUsers(String keyword,int page,int pageSize);
    ApiResponse<List<Note>> searchNotesByTag(String keyword, String tag, int page, int pageSize);
}

