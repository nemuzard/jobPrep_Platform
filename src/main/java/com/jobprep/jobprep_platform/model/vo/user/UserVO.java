package com.jobprep.jobprep_platform.model.vo.user;
import java.time.LocalDateTime;
import lombok.Data;
@Data
public class UserVO {
    private String username;
    private Integer gender;
    private String avatarUrl;
    private String email;
    private String school;
    private String signature;
    private LocalDateTime lastLoginAt;
}
