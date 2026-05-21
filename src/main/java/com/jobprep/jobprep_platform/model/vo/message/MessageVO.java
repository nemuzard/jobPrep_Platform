package com.jobprep.jobprep_platform.model.vo.message;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MessageVO {
    private Integer messageId;
    private Sender sender;
    private Integer type;
    private Target target;
    private String content;
    private Boolean isRead;
    private LocalDateTime createdAt;

    @Data
    public static class Sender{
        private Long userId;
        private String username;
        private String avatarUrl;
    }

    @Data
    public static class Target{
        private Integer targetId;
        private Integer targetType;
        private  QuestionSummary questionSummary;
    }
    @Data
    public static class QuestionSummary{
        private Integer questionId;
        private String title;
    }

}
