package com.jobprep.jobprep_platform.service.serviceImpl;
import com.jobprep.jobprep_platform.annotation.NeedLogin;
import com.jobprep.jobprep_platform.model.base.ApiResponse;
import com.jobprep.jobprep_platform.model.base.Pagination;
import com.jobprep.jobprep_platform.model.dto.user.LoginRequest;
import com.jobprep.jobprep_platform.model.dto.user.RegisterRequest;
import com.jobprep.jobprep_platform.model.dto.user.UpdateUserRequest;
import com.jobprep.jobprep_platform.model.dto.user.UserQueryParam;

import com.jobprep.jobprep_platform.model.entity.User;
import com.jobprep.jobprep_platform.mapper.UserMapper;
import com.jobprep.jobprep_platform.model.vo.user.AvatarVO;
import com.jobprep.jobprep_platform.model.vo.user.RegisterVO;
import com.jobprep.jobprep_platform.model.vo.user.LoginUserVO;
import com.jobprep.jobprep_platform.model.vo.user.UserVO;

import com.jobprep.jobprep_platform.scope.RequestScopeData;
import com.jobprep.jobprep_platform.service.EmailService;
import com.jobprep.jobprep_platform.service.FileService;
import com.jobprep.jobprep_platform.service.UserService;

import com.jobprep.jobprep_platform.utils.ApiResponseUtil;
import com.jobprep.jobprep_platform.utils.JwtUtil;
import com.jobprep.jobprep_platform.utils.PaginationUtils;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;;

@Log4j2
@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private FileService fileService;

    @Autowired
    private RequestScopeData requestScopeData;

    @Autowired
    private EmailService emailService;



    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<RegisterVO> register(RegisterRequest request){
        
        User existingUser = userMapper.findByAccount(request.getAccount());
        if(existingUser!=null){
            return ApiResponseUtil.error("Duplicate registration is not allowed");
        
        }

        if (request.getEmail() != null && !request.getEmail().isEmpty()){
            existingUser = userMapper.findByEmail(request.getEmail());
            if (existingUser!=null){
                return ApiResponseUtil.error("email already registered");
            }

            if (request.getVerifyCode()==null || request.getVerifyCode().isEmpty()){
                return ApiResponseUtil.error("please provide verification code");
            }

            if(!emailService.checkVerificationCode(request.getEmail(), request.getVerifyCode())){
                return ApiResponseUtil.error("Invalid verfication code");
            }


        }

        User user  = new User();
        BeanUtils.copyProperties(request, user);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        try{

            userMapper.insert(user);
            String token = jwtUtil.generateToken(user.getUserId());
            RegisterVO registerVO = new RegisterVO();
            BeanUtils.copyProperties(user, registerVO);
            userMapper.updateLastLoginAt(user.getUserId());
            return ApiResponseUtil.success("Success!",registerVO,token);
        } catch (Exception e){
            log.error("Registration failed.",e);
            return ApiResponseUtil.error("Registration failed, please try again later.");
        }
    }


    @Override
    public ApiResponse<LoginUserVO> login(LoginRequest request){
        
        User user  = null;

        // find user by email/account
        if (request.getAccount()!=null&&!request.getAccount().isEmpty()){
            user = userMapper.findByAccount(request.getAccount());
        }else if(request.getEmail()!=null && !request.getEmail().isEmpty()){
            user = userMapper.findByEmail(request.getEmail());
        }else{
            return ApiResponseUtil.error("Please provide valid account/email");
        }

        // verify password
        if (user == null){
            return ApiResponseUtil.error("User does not exist.");
        }
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            return ApiResponseUtil.error("Wrong password");

        }
        // Generate JWT 
        String token = jwtUtil.generateToken(user.getUserId());
        LoginUserVO userVO = new LoginUserVO();
        BeanUtils.copyProperties(user, userVO);
        userMapper.updateLastLoginAt(user.getUserId());

        return ApiResponseUtil.success("success!",userVO,token);
    }

    @Override
    public ApiResponse<LoginUserVO> whoami(){
        Long userId = requestScopeData.getUserId();
        if(userId == null){
            return ApiResponseUtil.error("Incorrect user id");
        }
        try{

            User user = userMapper.findById(userId);
            if (user == null){
                return ApiResponseUtil.error("User does not exist.");
            }
            // generate new token 
            String newToken = jwtUtil.generateToken(userId);
            if (newToken==null){
                return ApiResponseUtil.error("System error.");
            }
            
            // user info -> VO
            LoginUserVO userVO = new LoginUserVO();
            BeanUtils.copyProperties(user, userVO);

            //update login time and return 
            userMapper.updateLastLoginAt(userId);
            return ApiResponseUtil.success("Auto login success!",userVO,newToken);


        } catch (Exception e){
            return ApiResponseUtil.error("System error.");
        }
        
    }

    @Override
    public ApiResponse<UserVO> getUserInfo(Long userId){
        User user = userMapper.findById(userId);
        if(user == null){
            return ApiResponseUtil.error("User does not exist.");
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);

        return ApiResponseUtil.success("Fetch user info success!",userVO);
    }

    @Override
    @Transactional
    @NeedLogin
    public ApiResponse<LoginUserVO> updateUserInfo(UpdateUserRequest request){
        Long userId = requestScopeData.getUserId();
        User user = new User();
        BeanUtils.copyProperties(request, user);
        user.setUserId(userId);
        System.out.println(user);
        try{
            userMapper.update(user);
            return ApiResponseUtil.success("Update user info success");
        }catch (Exception e){
            return ApiResponseUtil.error("Update failed: "+e.getMessage() );
        }
    }

    @Override
    public Map<Long,User> getUserMapByIds(List<Long> authorIds){
        if(authorIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<User> users = userMapper.findByIdBatch(authorIds);
        return users.stream()
                    .collect(Collectors.toMap(User::getUserId,user->user));
    }

    @Override
    public ApiResponse<List<User>> getUserList(UserQueryParam userQueryParam){
        //pagination
        int total = userMapper.countByQueryParam(userQueryParam);
        int offset = PaginationUtils.calculateOffset(userQueryParam.getPage(),userQueryParam.getPageSize());
        Pagination pagination = new Pagination(userQueryParam.getPage(),userQueryParam.getPageSize(), total);

        try {
            List<User> users = userMapper.findByQueryParam(userQueryParam, userQueryParam.getPageSize(), offset);
            return ApiResponseUtil.success("Fetch user list success!",users,pagination);
        }catch (Exception e){
            return ApiResponseUtil.error(e.getMessage());
        }

    }

    @Override
    public ApiResponse<AvatarVO> uploadAvatar(MultipartFile file){
        try{
            String url = fileService.uploadImage(file);
            AvatarVO avatarVO = new AvatarVO();
            avatarVO.setUrl(url);
            return ApiResponseUtil.success("Upload success.");
        }catch (Exception e){
            return ApiResponseUtil.error(e.getMessage());
        }
    }




}
