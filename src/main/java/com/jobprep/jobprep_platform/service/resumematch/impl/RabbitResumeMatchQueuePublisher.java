package com.jobprep.jobprep_platform.service.resumematch.impl;

import com.jobprep.jobprep_platform.config.resumematch.ResumeMatchProperties;
import com.jobprep.jobprep_platform.model.message.resumematch.ResumeMatchTaskMessage;
import com.jobprep.jobprep_platform.service.resumematch.ResumeMatchQueuePublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RabbitResumeMatchQueuePublisher implements ResumeMatchQueuePublisher {
    private final RabbitTemplate rabbitTemplate;
    private final ResumeMatchProperties properties;

    @Override
    public void publish(ResumeMatchTaskMessage message) {
        rabbitTemplate.convertAndSend(
                properties.getQueue().getExchange(),
                properties.getQueue().getRoutingKey(),
                message
        );
    }
}
