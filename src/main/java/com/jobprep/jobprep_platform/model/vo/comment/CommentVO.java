package com.jobprep.jobprep_platform.model.vo.comment;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import com.jobprep.jobprep_platform.model.vo.user.UserActionVO;
@Data
public class CommentVO {
    private Integer commentId;
    private Integer noteId;
    private String content;
    private Integer likeCount;
    private Integer replyCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private SimpleAuthorVO author;
    private UserActionVO userActions;
    private List<CommentVO> replies;
    
    @Data
    public static class SimpleAuthorVO {
        private Long userId;
        private String username;
        private String avatarUrl;
    }
}
