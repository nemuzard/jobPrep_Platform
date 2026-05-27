package com.jobprep.jobprep_platform.service.serviceImpl;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.jobprep.jobprep_platform.mapper.NoteMapper;
import com.jobprep.jobprep_platform.mapper.QuestionListItemMapper;
import com.jobprep.jobprep_platform.mapper.QuestionListMapper;
import com.jobprep.jobprep_platform.mapper.UserMapper;
import com.jobprep.jobprep_platform.model.base.ApiResponse;
import com.jobprep.jobprep_platform.model.base.EmptyVO;
import com.jobprep.jobprep_platform.model.base.Pagination;
import com.jobprep.jobprep_platform.model.dto.questionListItem.CreateQuestionListItemBody;
import com.jobprep.jobprep_platform.model.dto.questionListItem.QuestionListItemQueryParams;
import com.jobprep.jobprep_platform.model.dto.questionListItem.SortQuestionListItemBody;
import com.jobprep.jobprep_platform.model.entity.QuestionList;
import com.jobprep.jobprep_platform.model.entity.QuestionListItem;
import com.jobprep.jobprep_platform.model.vo.questionListItem.CreateQuestionListItemVO;
import com.jobprep.jobprep_platform.model.vo.questionListItem.QuestionListItemUserVO;
import com.jobprep.jobprep_platform.model.vo.questionListItem.QuestionListItemVO;
import com.jobprep.jobprep_platform.scope.RequestScopeData;
import com.jobprep.jobprep_platform.service.QuestionListItemService;
import com.jobprep.jobprep_platform.utils.ApiResponseUtil;
import com.jobprep.jobprep_platform.utils.PaginationUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
public class QuestionListItemServiceImpl implements QuestionListItemService {
    private final QuestionListItemMapper questionListItemMapper;
    private final QuestionListMapper questionListMapper;
    private final RequestScopeData requestScopeData;
    private final NoteMapper noteMapper;    
    private final UserMapper userMapper;

    @Override
    public ApiResponse<List<QuestionListItemUserVO>> userGetQuestionListItems(QuestionListItemQueryParams queryParams){
        int offset = PaginationUtils.calculateOffset(queryParams.getPage(), queryParams.getPageSize());

        int total = questionListItemMapper.countByQuestionListId(queryParams.getQuestionListId());

        Pagination pagination = new Pagination(queryParams.getPage(), queryParams.getPageSize(), total);
        Integer questionListId = queryParams.getQuestionListId();

        QuestionList questionList = questionListMapper.findById(questionListId);
        List<QuestionListItemVO> questionListItems =
                questionListItemMapper.findByQuestionListIdPage(
                        queryParams.getQuestionListId(),
                        queryParams.getPageSize(),
                        offset
                );
        List<Integer> questionIds = questionListItems.stream()
                .map(questionListItemVO -> questionListItemVO.getQuestion().getQuestionId())
                .toList();

        final Set<Integer> userFinishedQuestionIds;
        if (requestScopeData.isLogin()) {
            userFinishedQuestionIds =
                    noteMapper.filterFinishedQuestionIdsByUser(requestScopeData.getUserId(), questionIds);
        } else {
            userFinishedQuestionIds = Collections.emptySet();
        }
        List<QuestionListItemUserVO> list = questionListItems.stream().map(questionListItemVO -> {

            QuestionListItemUserVO questionListItemUserVO = new QuestionListItemUserVO();
            BeanUtils.copyProperties(questionListItemVO, questionListItemUserVO);

            QuestionListItemUserVO.UserQuestionStatus userQuestionStatus =
                    new QuestionListItemUserVO.UserQuestionStatus();

            if (requestScopeData.isLogin()) {  // login status
                userQuestionStatus.setFinished(userFinishedQuestionIds.contains(questionListItemVO.getQuestion().getQuestionId()));
            } else {
                userQuestionStatus.setFinished(false);
            }

            questionListItemUserVO.setUserQuestionStatus(userQuestionStatus);

            return questionListItemUserVO;
        }).toList();
        return ApiResponseUtil.success("success",list, pagination);
        
    }
    @Override
    public ApiResponse<List<QuestionListItemVO>> getQuestionListItems(Integer questionListId) {

        List<QuestionListItemVO> byQuestionListId = questionListItemMapper.findByQuestionListId(questionListId);

        return ApiResponseUtil.success("success", byQuestionListId);
    }

    @Override
    public ApiResponse<CreateQuestionListItemVO> createQuestionListItem(CreateQuestionListItemBody body) {

        QuestionListItem questionListItem = new QuestionListItem();
        BeanUtils.copyProperties(body, questionListItem);

        try {
            // create rank
            int rank = questionListItemMapper.nextRank(body.getQuestionListId());
            questionListItem.setRank(rank);

            questionListItemMapper.insert(questionListItem);
            CreateQuestionListItemVO createQuestionListItemVO = new CreateQuestionListItemVO();
            createQuestionListItemVO.setRank(questionListItem.getRank());
            return ApiResponseUtil.success("success", createQuestionListItemVO);
        } catch (Exception e) {
            return ApiResponseUtil.error("failed");
        }
    }

    @Override
    public ApiResponse<EmptyVO> deleteQuestionListItem(Integer questionListId, Integer questionId) {
        try {
            questionListItemMapper.deleteByQuestionListIdAndQuestionId(questionListId, questionId);
            return ApiResponseUtil.success("success");
        } catch (Exception e) {
            return ApiResponseUtil.error("failed");
        }
    }

    @Override
    public ApiResponse<EmptyVO> sortQuestionListItem(SortQuestionListItemBody body) {
        // TODO:
        List<Integer> questionIds = body.getQuestionIds();
        Integer questionListId = body.getQuestionListId();

        try {
            for (int i = 0; i < questionIds.size(); i++) {
                QuestionListItem questionListItem = new QuestionListItem();
                questionListItem.setQuestionId(questionIds.get(i));
                questionListItem.setQuestionListId(questionListId);
                questionListItem.setRank(i + 1);
                questionListItemMapper.updateQuestionRank(questionListItem);
            }
            return ApiResponseUtil.success("success");
        } catch (Exception e) {
            return ApiResponseUtil.error("failed");
        }
    }

}
