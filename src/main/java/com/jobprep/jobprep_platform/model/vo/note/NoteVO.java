package com.jobprep.jobprep_platform.model.vo.note;

import java.time.LocalDateTime;

import lombok.Data;

// view object for note details, used in note details page
@Data
public class NoteVO {
    private Integer noteId;
    private String content;
    private Boolean needCollapsed = false;
    private String displayContent;
    private Integer likeCount;
    private Integer commentCount;
    private Integer collectCount;
    private LocalDateTime createdAt;
    private SimpleAuthorVO author;
    private UserActionsVO userActions;
    private SimpleQuestionVO question;

    @Data
    public static class SimpleAuthorVO {
        private Long userId;
        private String username;
        private String avatarUrl;
    }

    @Data
    public static class UserActionsVO {
        private Boolean isLiked = false;
        private Boolean isCollected = false;
    }

    @Data
    public static class SimpleQuestionVO {
        private Integer questionId;
        private String title;
    }
}
