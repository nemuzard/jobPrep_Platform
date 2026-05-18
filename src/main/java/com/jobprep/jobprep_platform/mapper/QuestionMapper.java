package com.jobprep.jobprep_platform.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.jobprep.jobprep_platform.model.dto.question.QuestionQueryParam;
import com.jobprep.jobprep_platform.model.entity.Question;
import java.util.List;

@Mapper
public interface QuestionMapper {
    
    // insert question to db
    int insert(Question question);

    // find based on question id 
    Question findById(@Param("questionId") Integer questionId);

    // find question list by ids
    List<Question> findByIdBatch(@Param("questionIds") List<Integer> questionIds);

    //find question list based on some parameters, with offset and limit/page 
    List<Question> findByQueryParam(@Param("queryParam") QuestionQueryParam queryParam,
                                    @Param("offset") int offset,
                                    @Param("limit") int limit);
    
    // find by title
    Question findByTitle(@Param("title") String title);

    List<Question> findByKeyword(@Param("keyword") String keyword);
    
    // update problem, 1-success,2-failed
    int update(@Param("question") Question question);
    int incrementViewCount(@Param("questionId") Integer questionId);
    // count how many questions match some query conditions
    int countByQueryParam(@Param("queryParam") QuestionQueryParam queryParam);
    
    int deleteById(Integer questionId);
    int deleteByCategoryId(Integer categoryId);

    int deleteByCategoryIdBatch(@Param("categoryIds") List<Integer> categoryIds);

}
