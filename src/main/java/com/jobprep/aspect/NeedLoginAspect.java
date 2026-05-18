package com.jobprep.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jobprep.jobprep_platform.annotation.NeedLogin;
import com.jobprep.jobprep_platform.scope.RequestScopeData;
import com.jobprep.jobprep_platform.utils.ApiResponseUtil;

@Aspect
@Component
public class NeedLoginAspect {
    @Autowired
    private RequestScopeData requestScopeData;

    @Around("@annotation(needLogin)")
    public Object around(ProceedingJoinPoint jointPoint,NeedLogin needLogin) throws Throwable {
        if(!requestScopeData.isLogin()){
            return ApiResponseUtil.error("Not login");
        }
        if (requestScopeData.getUserId()==null){
            return ApiResponseUtil.error("user id is null");
        }
        return jointPoint.proceed();
    } 
}
