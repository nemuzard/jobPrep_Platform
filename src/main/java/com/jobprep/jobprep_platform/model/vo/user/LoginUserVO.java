package com.jobprep.jobprep_platform.model.vo.user;

import java.time.LocalDate;
import lombok.Data;
/**
 * LoginUserVO - currently logged-in user's information.
 * UserVO - contains information about other users retrieved by the currently logged-in user.
 */
@Data
public class LoginUserVO {
    private Long userId;
    private String account;
    private String username;
    private Integer gender;
    private LocalDate birthday;
    private String avatarUrl;
    private String email;
    private String school;
    private String signature;
    private Integer isAdmin;
}
