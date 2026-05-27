package com.jobprep.jobprep_platform.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.jobprep.jobprep_platform.model.entity.QuestionListItem;
import com.jobprep.jobprep_platform.model.vo.questionListItem.QuestionListItemVO;

@Mapper
public interface QuestionListItemMapper {
    
    // insert an item into the list
    int insert(QuestionListItem questionListItem);

    List<QuestionListItemVO> findByQuestionListId(@Param("questionListId") Integer questionListId);

    int countByQuestionListId(@Param("questionListId") Integer questionListId);

    List<QuestionListItemVO> findByQuestionListIdPage(@Param("questionListId") Integer questionListId,
                                                @Param("limit") Integer limit,
                                                @Param("offset") Integer offset);

    int deleteByQuestionListId(@Param("questionListId") Integer questionListId);

    int deleteByQuestionListIdAndQuestionId(@Param("questionListId") Integer questionListId,@Param("questionId")Integer questionId);

    int nextRank(@Param("questionListId") Integer questionListId);
    
    int updateQuestionRank(QuestionListItem questionListItem);
}
