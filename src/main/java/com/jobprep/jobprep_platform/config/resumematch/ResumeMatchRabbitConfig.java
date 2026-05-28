package com.jobprep.jobprep_platform.config.resumematch;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class ResumeMatchRabbitConfig {
    private final ResumeMatchProperties properties;

    @Bean
    public DirectExchange resumeMatchExchange() {
        return new DirectExchange(properties.getQueue().getExchange(), true, false);
    }

    @Bean
    public Queue resumeMatchQueue() {
        return new Queue(properties.getQueue().getQueueName(), true);
    }

    @Bean
    public Binding resumeMatchBinding(Queue resumeMatchQueue, DirectExchange resumeMatchExchange) {
        return BindingBuilder.bind(resumeMatchQueue)
                .to(resumeMatchExchange)
                .with(properties.getQueue().getRoutingKey());
    }

    @Bean
    public MessageConverter resumeMatchMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
