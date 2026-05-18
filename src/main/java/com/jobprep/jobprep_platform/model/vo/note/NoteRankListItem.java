package com.jobprep.jobprep_platform.model.vo.note;

import lombok.Data;

@Data
public class NoteRankListItem {
    private Long userId;
    private String username;
    private String avatarUrl;
    private Integer noteCount;
    private Integer rank;
}
