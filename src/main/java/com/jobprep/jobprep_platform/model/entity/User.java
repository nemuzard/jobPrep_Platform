package com.jobprep.jobprep_platform.model.entity;
import java.time.LocalDateTime;
import lombok.*;
import java.time.LocalDate;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class User {

    /**
     * User id, cannot be changed
     * system generated
     */
    private Long userId;

    /**
     * account, unique, cannot be changed, 0-9,aA-zZ,_ allowed
     */
    private String account;

    /**
     * username
     * english letters, 0-1, _
     */
    private String username;

    /**
     * hashed password
     */

    private String password;

    /**
     *  gender 1 = Female, 2 = Male, 3 = Other 
     */
    private Integer gender;
    /**
     * DOB
     */
    private LocalDate birthday;

    /**
     * avatar url
     */
    private String avatarUrl;
    /**
     * user email
     */
    private String email;

    /**
     * school
     */
    private String school;

    /**
     * user bio
     */
    private String signature;

    /**
     * account status 0 = ok, 1 = banned
     */
    private Integer isBanned;

    /**
     * 0-user or 1-admin
     */
    private Integer isAdmin;

    /**
     * Last login time
     */
    private LocalDateTime lastLoginAt;

    /**
     * create time
     */
    private LocalDateTime createdAt;
    
    /**
     * update time 
     */
    private LocalDateTime updatedAt;

}
