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
import com.jobprep.jobprep_platform.model.dto.questionList.CreateQuestionListBody;
import com.jobprep.jobprep_platform.model.dto.questionList.UpdateQuestionListBody;
import com.jobprep.jobprep_platform.model.dto.questionListItem.CreateQuestionListItemBody;
import com.jobprep.jobprep_platform.model.dto.questionListItem.QuestionListItemQueryParams;
import com.jobprep.jobprep_platform.model.dto.questionListItem.SortQuestionListItemBody;
import com.jobprep.jobprep_platform.model.entity.QuestionList;
import com.jobprep.jobprep_platform.model.vo.questionList.CreateQuestionListVO;
import com.jobprep.jobprep_platform.model.vo.questionListItem.CreateQuestionListItemVO;
import com.jobprep.jobprep_platform.model.vo.questionListItem.QuestionListItemUserVO;
import com.jobprep.jobprep_platform.model.vo.questionListItem.QuestionListItemVO;
import com.jobprep.jobprep_platform.service.QuestionListItemService;
import com.jobprep.jobprep_platform.service.QuestionListService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class QuestionListController {
    private final QuestionListService questionListService;
    private final QuestionListItemService questionListItemService;

    @GetMapping("/admin/questionlists/{questionListId}")
    public ApiResponse<QuestionList> getQuestionList(@PathVariable Integer questionListId) {
        return questionListService.getQuestionList(questionListId);
    }

    @GetMapping("/admin/questionlists")
    public ApiResponse<List<QuestionList>> getQuestionLists() {
        return questionListService.getQuestionLists();
    }

    @PostMapping("/admin/questionlists")
    public ApiResponse<CreateQuestionListVO> createQuestionList(@Valid @RequestBody CreateQuestionListBody body) {
        return questionListService.createQuestionList(body);
    }

    @DeleteMapping("/admin/questionlists/{questionListId}")
    public ApiResponse<EmptyVO> deleteQuestionList(@PathVariable Integer questionListId) {
        return questionListService.deleteQuestionList(questionListId);
    }

    @PatchMapping("/admin/questionlists/{questionListId}")
    public ApiResponse<EmptyVO> updateQuestionList(
            @PathVariable Integer questionListId,
            @Valid @RequestBody UpdateQuestionListBody body) {
        return questionListService.updateQuestionList(questionListId, body);
    }

    @GetMapping("/admin/questionlist-items/{questionListId}")
    public ApiResponse<List<QuestionListItemVO>> getQuestionListItems(@PathVariable Integer questionListId) {
        return questionListItemService.getQuestionListItems(questionListId);
    }

    @PostMapping("/admin/questionlist-items")
    public ApiResponse<CreateQuestionListItemVO> createQuestionListItem(
            @Valid @RequestBody CreateQuestionListItemBody body) {
        return questionListItemService.createQuestionListItem(body);
    }

    @DeleteMapping("/admin/questionlist-items/{questionListId}/{questionId}")
    public ApiResponse<EmptyVO> deleteQuestionListItem(
            @PathVariable Integer questionListId,
            @PathVariable Integer questionId) {
        return questionListItemService.deleteQuestionListItem(questionListId, questionId);
    }

    @PatchMapping("/admin/questionlist-items/sort")
    public ApiResponse<EmptyVO> sortQuestionListItems(@Valid @RequestBody SortQuestionListItemBody body) {
        return questionListItemService.sortQuestionListItem(body);
    }

    @GetMapping("/questionlist-items")
    public ApiResponse<List<QuestionListItemUserVO>> userGetQuestionListItems(
            @Valid QuestionListItemQueryParams params) {
        return questionListItemService.userGetQuestionListItems(params);
    }
}
