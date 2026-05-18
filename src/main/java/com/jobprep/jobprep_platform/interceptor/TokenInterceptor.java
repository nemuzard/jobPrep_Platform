package com.jobprep.jobprep_platform.interceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


import com.jobprep.jobprep_platform.scope.RequestScopeData;
import com.jobprep.jobprep_platform.utils.JwtUtil;

@Component
public class TokenInterceptor implements HandlerInterceptor{
    @Autowired
    private RequestScopeData requestScopeData;

    @Autowired
    private JwtUtil jwtUtil;

    
    @Override
    public boolean preHandle(HttpServletRequest request,HttpServletResponse response, Object handler) throws Exception{
        
        // intercept each request and retrieve the token from the request header.
        // process the token and store the information carried by the token in requestScopeData, which exists globally throughout the request lifecycle.
        String token = request.getHeader("Authorization");
        if (token == null || token.isBlank()){
            requestScopeData.setLogin(false);
            requestScopeData.setToken(null);
            requestScopeData.setUserId(null);
            return true;
        }
        // Typical format: "Bearer <jwt>"
        token = token.replaceFirst("(?i)^Bearer\\s+", "").trim();
        if(jwtUtil.validateToken(token)){
            Long userId = jwtUtil.getUserIdFromToken(token);
            requestScopeData.setUserId(userId);
            requestScopeData.setToken(token);
            requestScopeData.setLogin(true);
        }else{
            requestScopeData.setLogin(false);
        }
        return HandlerInterceptor.super.preHandle(request,response,handler);
    }
    
}
