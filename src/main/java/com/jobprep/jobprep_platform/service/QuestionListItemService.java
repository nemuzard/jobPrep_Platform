package com.jobprep.jobprep_platform.service;

import org.springframework.transaction.annotation.Transactional;
import com.jobprep.jobprep_platform.model.base.ApiResponse;
import com.jobprep.jobprep_platform.model.base.EmptyVO;
import com.jobprep.jobprep_platform.model.dto.questionListItem.CreateQuestionListItemBody;
import com.jobprep.jobprep_platform.model.dto.questionListItem.QuestionListItemQueryParams;
import com.jobprep.jobprep_platform.model.dto.questionListItem.SortQuestionListItemBody;
//import com.jobprep.jobprep_platform.model.entity.QuestionListItem;
import com.jobprep.jobprep_platform.model.vo.questionListItem.CreateQuestionListItemVO;
import com.jobprep.jobprep_platform.model.vo.questionListItem.QuestionListItemVO;
import com.jobprep.jobprep_platform.model.vo.questionListItem.QuestionListItemUserVO;
import java.util.List;

/**
 * QuestionListItemService 
 * business layer for adding, deleting, querying, 
 * and sorting questions inside a question list.
 */
@Transactional
public interface QuestionListItemService {

    /**
     *  client side - get question list
     * @param queryParams
     * @return
     */
    ApiResponse<List<QuestionListItemUserVO>> userGetQuestionListItems(QuestionListItemQueryParams queryParams);

    /**
     * admin - get question set 
     * @param questionListId
     * @return
     */
    ApiResponse<List<QuestionListItemVO>> getQuestionListItems(Integer questionListId);

    ApiResponse<CreateQuestionListItemVO> createQuestionListItem(CreateQuestionListItemBody body);

    ApiResponse<EmptyVO> deleteQuestionListItem(Integer questionListId, Integer questionId);

    ApiResponse<EmptyVO> sortQuestionListItem(SortQuestionListItemBody body);

}
