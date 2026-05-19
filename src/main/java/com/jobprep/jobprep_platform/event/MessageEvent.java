package com.jobprep.jobprep_platform.event;

import org.springframework.context.ApplicationEvent;
import com.jobprep.jobprep_platform.model.vo.message.MessageVO;

import lombok.Getter;

/**
 * message event 
 * used to describe that a message/notification should happen
 */
@Getter
public class MessageEvent extends ApplicationEvent {
    private final MessageVO message;
    private final Long receiverId;
    private final String eventType;

    public MessageEvent(Object source, MessageVO message, Long receiverId, String eventType){
        super(source);
        this.message=message;
        this.receiverId=receiverId;
        this.eventType=eventType;
    }
    public static MessageEvent createCommentEvent(Object source, MessageVO message, Long receiverId) {
        return new MessageEvent(source, message, receiverId, "COMMENT");
    }

    public static MessageEvent createLikeEvent(Object source, MessageVO message, Long receiverId) {
        return new MessageEvent(source, message, receiverId, "LIKE");
    }

    public static MessageEvent createSystemEvent(Object source, MessageVO message, Long receiverId) {
        return new MessageEvent(source, message, receiverId, "SYSTEM");
    }
}
