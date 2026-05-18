package com.jobprep.jobprep_platform.scope;

import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;


/**
 * Used to store global data within the lifecycle of the current request.
 */
@Component
@RequestScope
@Data
public class RequestScopeData {
    private String token;
    private Long userId;
    private boolean isLogin;
    
}
