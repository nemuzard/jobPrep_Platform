package com.jobprep.jobprep_platform.service;

import java.util.List;
import java.util.Map;

import com.jobprep.jobprep_platform.model.base.ApiResponse;
import com.jobprep.jobprep_platform.model.dto.user.*;
import com.jobprep.jobprep_platform.model.entity.User;
import com.jobprep.jobprep_platform.model.vo.user.AvatarVO;
import com.jobprep.jobprep_platform.model.vo.user.RegisterVO;
import com.jobprep.jobprep_platform.model.vo.user.UserVO;
import com.jobprep.jobprep_platform.model.vo.user.LoginUserVO;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Transactional
public interface UserService {
    /**
     * user register 
     * @param request
     * @return
     */
    ApiResponse<RegisterVO> register(RegisterRequest request);
    
    ApiResponse<LoginUserVO> login(LoginRequest request);

    /**
     * auto login 
     * @return Information of the currently logged-in user (automatic login based on token verification)
     */
    ApiResponse<LoginUserVO> whoami();

    ApiResponse<UserVO> getUserInfo(Long userId);

    ApiResponse<LoginUserVO> updateUserInfo(UpdateUserRequest request);

    /**
     * 
     * @param authorIds List containing multiple user IDs
     * @return A Map where the key is userId and the value is the corresponding User object.
     */
    Map<Long,User> getUserMapByIds(List<Long> authorIds);

    /**
     * get user list 
     * @param userQueryParam User query parameters, including conditions for querying a list of users.
     * @return
     */
    ApiResponse<List<User>> getUserList(UserQueryParam userQueryParam);

    ApiResponse<AvatarVO> uploadAvatar(MultipartFile file);
}
