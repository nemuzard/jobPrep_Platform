package com.jobprep.jobprep_platform.service.resumematch;

import com.jobprep.jobprep_platform.model.vo.resumematch.ResumeMatchProgressVO;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface ResumeMatchEventService {
    SseEmitter subscribe(Long jobId);

    void publish(ResumeMatchProgressVO progress);
}
