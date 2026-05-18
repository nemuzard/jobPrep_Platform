package com.jobprep.jobprep_platform.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.jobprep.jobprep_platform.model.base.ApiResponse;
import com.jobprep.jobprep_platform.model.base.EmptyVO;
import com.jobprep.jobprep_platform.model.dto.questionList.CreateQuestionListBody;
import com.jobprep.jobprep_platform.model.dto.questionList.UpdateQuestionListBody;

import com.jobprep.jobprep_platform.model.entity.QuestionList;
import com.jobprep.jobprep_platform.model.vo.questionList.CreateQuestionListVO;

//used to manage the question list itself,
//  not the questions inside the list
@Transactional
public interface QuestionListService {
    /**
     *  get one specific question list. 
     * @param questionListId
     * @return
     */
    ApiResponse<QuestionList> getQuestionList(Integer questionListId);

    //get all question lists
    ApiResponse<List<QuestionList>> getQuestionLists();

    ApiResponse<CreateQuestionListVO> createQuestionList(CreateQuestionListBody body);
    ApiResponse<EmptyVO> deleteQuestionList(Integer questionListId);

    ApiResponse<EmptyVO> updateQuestionList(Integer questionListId, UpdateQuestionListBody body);


}
