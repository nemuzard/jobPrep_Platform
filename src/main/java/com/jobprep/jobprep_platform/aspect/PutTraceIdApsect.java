package com.jobprep.jobprep_platform.aspect;

import java.util.UUID;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.jboss.logging.MDC;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PutTraceIdApsect {
    private static final String TRACE_ID_KEY = "traceId";

    @Before("execution(* com.jobprep..*(..))")
    public void addTraceIdToLog(){
        if(MDC.get(TRACE_ID_KEY) == null){
            String traceId = UUID.randomUUID().toString();
            MDC.put(TRACE_ID_KEY, traceId);
        }
    }
}
