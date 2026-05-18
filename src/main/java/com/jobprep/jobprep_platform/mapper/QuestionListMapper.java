package com.jobprep.jobprep_platform.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.jobprep.jobprep_platform.model.entity.QuestionList;
import java.util.List;

@Mapper
public interface QuestionListMapper {
    int insert(QuestionList questionList);
    QuestionList findById(@Param("questionListId") Integer questionListId);
    List<QuestionList> findAll();
    
    int update(QuestionList questionList);
    int deleteById(@Param("questionListId") Integer questionListId);
}
