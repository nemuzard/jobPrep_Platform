package com.jobprep.jobprep_platform.service.serviceImpl;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.jobprep.jobprep_platform.mapper.QuestionListItemMapper;
import com.jobprep.jobprep_platform.mapper.QuestionListMapper;
import com.jobprep.jobprep_platform.model.base.ApiResponse;
import com.jobprep.jobprep_platform.model.base.EmptyVO;
import com.jobprep.jobprep_platform.model.dto.questionList.CreateQuestionListBody;
import com.jobprep.jobprep_platform.model.dto.questionList.UpdateQuestionListBody;
import com.jobprep.jobprep_platform.model.entity.QuestionList;
import com.jobprep.jobprep_platform.model.vo.questionList.CreateQuestionListVO;
import com.jobprep.jobprep_platform.service.QuestionListService;
import com.jobprep.jobprep_platform.utils.ApiResponseUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuestionListServiceImpl implements QuestionListService{
    private final QuestionListMapper questionListMapper;
    private final QuestionListItemMapper questionListItemMapper;

    @Override
    public ApiResponse<QuestionList> getQuestionList(Integer questionListId) {
        return ApiResponseUtil.success("SUCCESS", questionListMapper.findById(questionListId));
    }

    @Override
    public ApiResponse<List<QuestionList>> getQuestionLists() {
        return ApiResponseUtil.success("SUCCESS", questionListMapper.findAll());
    }

    @Override
    public ApiResponse<CreateQuestionListVO> createQuestionList(CreateQuestionListBody body) {

        QuestionList questionList = new QuestionList();
        BeanUtils.copyProperties(body, questionList);

        // create
        try {
            questionListMapper.insert(questionList);
            CreateQuestionListVO questionListVO = new CreateQuestionListVO();
            questionListVO.setQuestionListId(questionList.getQuestionListId());
            return ApiResponseUtil.success("SUCCESS", questionListVO);
        } catch (Exception e) {
            return ApiResponseUtil.error("FAILED");
        }
    }

    @Override
    public ApiResponse<EmptyVO> deleteQuestionList(Integer questionListId) {
        // delete list and all questions 
        QuestionList questionList = questionListMapper.findById(questionListId);

        if (questionList == null) {
            return ApiResponseUtil.error("not exist");
        }

        try {
            questionListMapper.deleteById(questionListId);
    
            questionListItemMapper.deleteByQuestionListId(questionListId);
            return ApiResponseUtil.success("SUCCESS");
        } catch (Exception e) {
            return ApiResponseUtil.error("FAILED");
        }
    }

    @Override
    public ApiResponse<EmptyVO> updateQuestionList(Integer questionListId, UpdateQuestionListBody body) {

        QuestionList questionList = new QuestionList();
        BeanUtils.copyProperties(body, questionList);
        questionList.setQuestionListId(questionListId);

        System.out.println(questionList);

        try {
            questionListMapper.update(questionList);
            return ApiResponseUtil.success("SUCCESS");
        } catch (Exception e) {
            return ApiResponseUtil.error("FAILED");
        }
    }
}
