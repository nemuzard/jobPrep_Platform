package com.jobprep.jobprep_platform.service;
import com.jobprep.jobprep_platform.model.entity.Question;
import com.jobprep.jobprep_platform.model.dto.question.*;
import com.jobprep.jobprep_platform.model.base.EmptyVO;
import com.jobprep.jobprep_platform.model.vo.question.CreateQuestionVO;
import com.jobprep.jobprep_platform.model.vo.question.QuestionVO;
import com.jobprep.jobprep_platform.model.vo.question.QuestionNoteVO;
import com.jobprep.jobprep_platform.model.vo.question.QuestionUserVO;
import org.springframework.transaction.annotation.Transactional;
import com.jobprep.jobprep_platform.model.base.ApiResponse;

import java.util.Map;
import java.util.List;

@Transactional
public interface QuestionService {
    // find specific question by its id  
    Question findById(Integer questionId);

    // find questions in batch by ids 
    Map<Integer,Question> getQuestionMapByIds(List<Integer> questionIds);

    /**
     *  return question list 
     * @param queryParams
     * @return
     */
    ApiResponse<List<QuestionVO>> getQuestions(QuestionQueryParam queryParams);
    
    /**
     * 
     * @param createQuestionBody
     * @return
     */
    ApiResponse<CreateQuestionVO> createQuestion(CreateQuestionBody createQuestionBody);


    ApiResponse<EmptyVO> createQuestionBatch(CreateQuestionBatchBody createQuestionBatchBody);

    ApiResponse<EmptyVO> updateQuestion(Integer question, UpdateQuestionBody updateQuestionBody);

    ApiResponse<EmptyVO> deleteQuestion(Integer questionId);

    /**
     * user get question list 
     * @param queryParams
     * @return
     */
    ApiResponse<List<QuestionUserVO>> userGetQuestions(QuestionQueryParam queryParams);

    // get single question
    ApiResponse<QuestionNoteVO> userGetQuestion(Integer questionId);

    ApiResponse<List<QuestionVO>> searchQuestions(SearchQuestionBody body);

}
