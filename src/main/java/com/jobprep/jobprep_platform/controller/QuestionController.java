package com.jobprep.jobprep_platform.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jobprep.jobprep_platform.model.base.ApiResponse;
import com.jobprep.jobprep_platform.model.base.EmptyVO;
import com.jobprep.jobprep_platform.model.dto.question.CreateQuestionBatchBody;
import com.jobprep.jobprep_platform.model.dto.question.CreateQuestionBody;
import com.jobprep.jobprep_platform.model.dto.question.QuestionQueryParam;
import com.jobprep.jobprep_platform.model.dto.question.SearchQuestionBody;
import com.jobprep.jobprep_platform.model.dto.question.UpdateQuestionBody;
import com.jobprep.jobprep_platform.model.vo.question.CreateQuestionVO;
import com.jobprep.jobprep_platform.model.vo.question.QuestionNoteVO;
import com.jobprep.jobprep_platform.model.vo.question.QuestionUserVO;
import com.jobprep.jobprep_platform.model.vo.question.QuestionVO;
import com.jobprep.jobprep_platform.service.QuestionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class QuestionController {
    private final QuestionService questionService;

    @GetMapping("/admin/questions")
    public ApiResponse<List<QuestionVO>> adminGetQuestions(@Valid QuestionQueryParam params) {
        return questionService.getQuestions(params);
    }

    @PostMapping("/admin/questions")
    public ApiResponse<CreateQuestionVO> createQuestion(@Valid @RequestBody CreateQuestionBody body) {
        return questionService.createQuestion(body);
    }

    @PostMapping("/admin/questions/batch")
    public ApiResponse<EmptyVO> createQuestionBatch(@RequestBody CreateQuestionBatchBody body) {
        return questionService.createQuestionBatch(body);
    }

    @PatchMapping("/admin/questions/{questionId}")
    public ApiResponse<EmptyVO> updateQuestion(
            @PathVariable Integer questionId,
            @Valid @RequestBody UpdateQuestionBody body) {
        return questionService.updateQuestion(questionId, body);
    }

    @DeleteMapping("/admin/questions/{questionId}")
    public ApiResponse<EmptyVO> deleteQuestion(@PathVariable Integer questionId) {
        return questionService.deleteQuestion(questionId);
    }

    @GetMapping("/questions")
    public ApiResponse<List<QuestionUserVO>> userGetQuestions(@Valid QuestionQueryParam params) {
        return questionService.userGetQuestions(params);
    }

    @GetMapping("/questions/{questionId}")
    public ApiResponse<QuestionNoteVO> userGetQuestion(@PathVariable Integer questionId) {
        return questionService.userGetQuestion(questionId);
    }

    @PostMapping("/questions/search")
    public ApiResponse<List<QuestionVO>> searchQuestions(@Valid @RequestBody SearchQuestionBody body) {
        return questionService.searchQuestions(body);
    }
}
