package com.jobprep.jobprep_platform.service.resumematch;

import com.jobprep.jobprep_platform.model.message.resumematch.ResumeMatchTaskMessage;

public interface ResumeMatchQueuePublisher {
    void publish(ResumeMatchTaskMessage message);
}
