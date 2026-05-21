package com.jobprep.jobprep_platform.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.jobprep.jobprep_platform.model.dto.message.MessageQueryParams;
import com.jobprep.jobprep_platform.model.entity.Message;
import com.jobprep.jobprep_platform.model.vo.message.UnreadCountByType;

import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

@Mapper
public interface MessageMapper {
    int insert(Message message);

    List<Message> selectByUserId(Long userid);

    /**
     * select message based on userid/search params/offset
     */
    List<Message> selectByParams(@Param("userId") Long userId, 
                                @Param("params") MessageQueryParams params,
                                @Param("offset") int offset);
    
    /**
     * count number of messages that satisifies centain conditions 
     * @param userId
     * @param params
     * @return
     */
    int countByParams(@Param("userId") Long userId, @Param("params") MessageQueryParams params);

    int markAsRead(@Param("messageId")Integer messageId, 
                    @Param("userId") Long userId);

    int markAsReadBatch(@Param("messageIds") List<Integer> messageIds,@Param("userId") Long userId);
    int markAllAsRead(@Param("userId") Long userId);
    int deleteMessage(@Param("messageId") Integer messageId,@Param("userId")Long userId);

    int countUnread(@Param("userId")Long userId);
    List<UnreadCountByType> countUnreadByType(@Param("userId") Long userId);


}
