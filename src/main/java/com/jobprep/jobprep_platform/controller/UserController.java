package com.jobprep.jobprep_platform.controller;
import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.jobprep.jobprep_platform.model.base.ApiResponse;
import com.jobprep.jobprep_platform.model.dto.user.LoginRequest;
import com.jobprep.jobprep_platform.model.dto.user.RegisterRequest;
import com.jobprep.jobprep_platform.model.dto.user.UpdateUserRequest;
import com.jobprep.jobprep_platform.model.dto.user.UserQueryParam;
import com.jobprep.jobprep_platform.model.entity.User;
import com.jobprep.jobprep_platform.model.vo.user.AvatarVO;
import com.jobprep.jobprep_platform.model.vo.user.LoginUserVO;
import com.jobprep.jobprep_platform.model.vo.user.RegisterVO;
import com.jobprep.jobprep_platform.model.vo.user.UserVO;
import com.jobprep.jobprep_platform.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api")
public class UserController {
    // autowired userService related opeartaion
    @Autowired
    private UserService userService;

    /**
     * User registration interface
     * Processes user registration requests, 
     * verifies the request body, and calls userService to complete the registration.
     * @param request
     * @return registration result including user info 
     */
    @PostMapping("/users")
    public ApiResponse<RegisterVO> register(
        @Valid
        @RequestBody
        RegisterRequest request){
            return userService.register(request);
    }


    /**
     * User login interface
     * process login request, verfiy request and call UserService to login
     * @param request
     * @return login result
     */
    @PostMapping("/users/login")
    public ApiResponse<LoginUserVO> login(
        @Valid 
        @RequestBody
        LoginRequest request
    ){
        return userService.login(request);
    }

    /**
     * auto login
     * @return
     */
    @PostMapping("/whoami")
    public ApiResponse<LoginUserVO> whoami(){
        return userService.whoami();
    }

    /**
     * query user information based on userID
     * verfiy ID and call userService to return user info
     * @param userId
     * @return
     */
    @GetMapping("/users/{userId}")
    public ApiResponse<UserVO> getUserInfo(
        @PathVariable
        @Pattern(regexp = "\\d+", message = "Wrong ID format")
        Long userId
    ){
        return userService.getUserInfo(userId);
    }

    /**
     * update user info interface
     * verify user request and call specific service to complete the request 
     * @param request
     * @return updated user info
     */
    @PatchMapping("/users/me")
    public ApiResponse<LoginUserVO> updateUserInfo(
        @Valid
        @RequestBody
        UpdateUserRequest request
    ){
        return userService.updateUserInfo(request);
    }
    /**
     * upload user avatar
     * @param file
     * @return result, including avatar url...
     */
    @PostMapping("/users/avatar")
    public ApiResponse<AvatarVO> uploadAvatar(
        @RequestParam("file") MultipartFile file
    ){
        return userService.uploadAvatar(file);
    }

    /**
     * admin query user list
     * @param queryParam
     * @return
     */
    @GetMapping("/admin/users")
    public ApiResponse<List<User>> adminGetUser(
        @Valid 
        UserQueryParam queryParam
    ){
        return userService.getUserList(queryParam);
    }





}
