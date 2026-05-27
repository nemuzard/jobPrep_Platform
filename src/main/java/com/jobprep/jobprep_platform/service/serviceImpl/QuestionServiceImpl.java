package com.jobprep.jobprep_platform.service.serviceImpl;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jobprep.jobprep_platform.mapper.CategoryMapper;
import com.jobprep.jobprep_platform.mapper.NoteMapper;
import com.jobprep.jobprep_platform.mapper.QuestionMapper;
import com.jobprep.jobprep_platform.model.base.ApiResponse;
import com.jobprep.jobprep_platform.model.base.EmptyVO;
import com.jobprep.jobprep_platform.model.base.Pagination;
import com.jobprep.jobprep_platform.model.dto.question.CreateQuestionBatchBody;
import com.jobprep.jobprep_platform.model.dto.question.CreateQuestionBody;
import com.jobprep.jobprep_platform.model.dto.question.QuestionQueryParam;
import com.jobprep.jobprep_platform.model.dto.question.SearchQuestionBody;
import com.jobprep.jobprep_platform.model.dto.question.UpdateQuestionBody;
import com.jobprep.jobprep_platform.model.entity.Category;
import com.jobprep.jobprep_platform.model.entity.Note;
import com.jobprep.jobprep_platform.model.entity.Question;
import com.jobprep.jobprep_platform.model.vo.question.CreateQuestionVO;
import com.jobprep.jobprep_platform.model.vo.question.QuestionNoteVO;
import com.jobprep.jobprep_platform.model.vo.question.QuestionUserVO;
import com.jobprep.jobprep_platform.model.vo.question.QuestionVO;
import com.jobprep.jobprep_platform.scope.RequestScopeData;
import com.jobprep.jobprep_platform.service.CategoryService;
import com.jobprep.jobprep_platform.service.QuestionService;
import com.jobprep.jobprep_platform.utils.ApiResponseUtil;
import com.jobprep.jobprep_platform.utils.MarkdownAST;
import com.jobprep.jobprep_platform.utils.PaginationUtils;
import com.vladsch.flexmark.ast.BulletList;
import com.vladsch.flexmark.ast.Heading;
import com.vladsch.flexmark.ast.ListItem;
import com.vladsch.flexmark.ast.OrderedList;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.Node;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService{
    private final QuestionMapper questionMapper;
    private final CategoryMapper categoryMapper;
    private final RequestScopeData requestScopeData;
    private final NoteMapper noteMapper;
    private final CategoryService categoryService;

    // Match:
    //   (考点: XXX)
    //   (考点：XXX）
    //   (Point: XXX)
    //   (Knowledge Point: XXX)
    private static final Pattern POINT_PATTERN =
            Pattern.compile("[（(]\\s*(?:考点|Exam Point|Point|Knowledge\\s*Point)\\s*[：:]\\s*(.*?)\\s*[)）]",
                    Pattern.CASE_INSENSITIVE);

    // Match:
    //   【简单】、【中等】、【困难】
    //   【Easy】、【Medium】、【Hard】
    private static final Pattern LEVEL_PATTERN =
            Pattern.compile("【\\s*(简单|中等|困难|Easy|Medium|Hard)\\s*】",
                    Pattern.CASE_INSENSITIVE);
    @Override
    public Question findById(Integer questionId) {
        return questionMapper.findById(questionId);
    }

    @Override
    public Map<Integer, Question> getQuestionMapByIds(List<Integer> questionIds) {

        if (questionIds.isEmpty()) return Collections.emptyMap();

        List<Question> questions = questionMapper.findByIdBatch(questionIds);
        return questions.stream().collect(Collectors.toMap(Question::getQuestionId, question -> question));
    }

    @Override
    public ApiResponse<List<QuestionVO>> getQuestions(QuestionQueryParam queryParams) {

        int offset = PaginationUtils.calculateOffset(queryParams.getPage(), queryParams.getPageSize());
        int total = questionMapper.countByQueryParam(queryParams);

        Pagination pagination = new Pagination(queryParams.getPage(), queryParams.getPageSize(), total);
        List<Question> questions = questionMapper.findByQueryParam(queryParams, offset, queryParams.getPageSize());

        List<QuestionVO> questionVOs = questions.stream().map(question -> {
            QuestionVO questionVO = new QuestionVO();
            BeanUtils.copyProperties(question, questionVO);
            return questionVO;
        }).toList();

        return ApiResponseUtil.success("Success", questionVOs, pagination);
    }

    @Override
    public ApiResponse<CreateQuestionVO> createQuestion(CreateQuestionBody createQuestionBody) {

       
        Category category = categoryMapper.findById(createQuestionBody.getCategoryId());
        if (category == null) {
            return ApiResponseUtil.error("illegal id");
        }

        Question question = new Question();
        BeanUtils.copyProperties(createQuestionBody, question);

        try {
            questionMapper.insert(question);
            CreateQuestionVO createQuestionVO = new CreateQuestionVO();
            createQuestionVO.setQuestionId(question.getQuestionId());
            return ApiResponseUtil.success("success", createQuestionVO);
        } catch (Exception e) {
            return ApiResponseUtil.error("failed");
        }
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<EmptyVO> createQuestionBatch(CreateQuestionBatchBody createQuestionBatchBody){
        // get markdown file
        String markdown = createQuestionBatchBody.getMarkdown();

        MarkdownAST markdownAST = new MarkdownAST(markdown);
        Document document = markdownAST.getMarkdownAST();

    
        for (Node child = document.getFirstChild(); child != null; child = child.getNext()) {
            if (child instanceof Heading parentHeading) {  // exist heading 
                if (parentHeading.getLevel() == 1) {  // if top level 
                    // 
                    String parentCategoryName = markdownAST.getHeadingText(parentHeading);
                    Category parentCategory = categoryService.findOrCreateCategory(parentCategoryName);

                    Node childCategory = parentHeading.getNext();

                    for (; childCategory != null; childCategory = childCategory.getNext()) {
                        //next parent category (Heading level=1)，break current iteration
                        if (childCategory instanceof Heading nextParent) {
                            if (nextParent.getLevel() == 1) {
                                break;
                            }
                        }

                        // secondary heading,  Heading level = 
                        if (childCategory instanceof Heading subHeading) {
                            if (subHeading.getLevel() == 2) {
                                String subCategoryName = markdownAST.getHeadingText(subHeading);

                                Category subCategory = categoryService.findOrCreateCategory(
                                        subCategoryName,
                                        parentCategory.getCategoryId()
                                );

                                Node listBlockNode = subHeading.getNext();
                                if (!(listBlockNode instanceof BulletList) &&
                                        !(listBlockNode instanceof OrderedList)) {
                                    // no list found
                                    continue;
                                }

                                for (Node listItem = listBlockNode.getFirstChild();
                                     listItem != null;
                                     listItem = listItem.getNext()) {

                                    if (listItem instanceof ListItem listItem2) {
                                        String listItemText = markdownAST.getListItemText(listItem2);

                                        // parse exam point
                                        String examPoint = "";
                                        Matcher matchPoint = POINT_PATTERN.matcher(listItemText);
                                        if (!matchPoint.find()) {
                                            throw new RuntimeException("failed");
                                        }
                                        examPoint = matchPoint.group(1);

                                        // difficulty
                                        String difficultyStr = "";
                                        Matcher matchLevel = LEVEL_PATTERN.matcher(listItemText);
                                        if (!matchLevel.find()) {
                                            throw new RuntimeException("failed");
                                        }
                                        difficultyStr = matchLevel.group(1);

                                        // title
                                        String title = listItemText
                                                .replaceAll(POINT_PATTERN.pattern(), "")
                                                .replaceAll(LEVEL_PATTERN.pattern(), "")
                                                .trim();

                                        // check if exist
                                        Question question = questionMapper.findByTitle(title);

                                        if (question != null) {
                                            throw new RuntimeException("already exist");
                                        }

                                        
                                        Map<String, Integer> difficultyMap = new HashMap<>();
                                        difficultyMap.put("简单", 1);
                                        difficultyMap.put("中等", 2);
                                        difficultyMap.put("困难", 3);
                                        difficultyMap.put("Easy", 1);
                                        difficultyMap.put("Medium", 2);
                                        difficultyMap.put("Hard", 3);

                                        Integer difficultyVal = difficultyMap.get(difficultyStr);
                                        if (difficultyVal == null) {
                                            throw new RuntimeException("failed");
                                        }

                                        //create question
                                        Question addQuestion = new Question();

                                        addQuestion.setTitle(title);
                                        addQuestion.setCategoryId(subCategory.getCategoryId());
                                        addQuestion.setExamPoint(examPoint);
                                        addQuestion.setDifficulty(difficultyVal);

                                        try {
                                            questionMapper.insert(addQuestion);
                                        } catch (Exception e) {
                                            throw new RuntimeException("failed: " + e.getMessage());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return ApiResponseUtil.success("success");
    }
    @Override
    public ApiResponse<EmptyVO> updateQuestion(Integer questionId, UpdateQuestionBody updateQuestionBody) {
        Question question = new Question();
        BeanUtils.copyProperties(updateQuestionBody, question);
        question.setQuestionId(questionId);
        // update
        try {
            questionMapper.update(question);
            return ApiResponseUtil.success("success");
        } catch (Exception e) {
            return ApiResponseUtil.error("failed");
        }
    }

    @Override
    public ApiResponse<EmptyVO> deleteQuestion(Integer questionId) {
        if (questionMapper.deleteById(questionId) > 0) {
            return ApiResponseUtil.success("success");
        } else {
            return ApiResponseUtil.error("failed");
        }
    }

    // additional info on if the question is finished
    @Override
    public ApiResponse<List<QuestionUserVO>> userGetQuestions(QuestionQueryParam queryParams) {


        int offset = PaginationUtils.calculateOffset(queryParams.getPage(), queryParams.getPageSize());
        int total = questionMapper.countByQueryParam(queryParams);
        Pagination pagination = new Pagination(queryParams.getPage(), queryParams.getPageSize(), total);

    
        List<Question> questions = questionMapper.findByQueryParam(queryParams, offset, queryParams.getPageSize());

        // get questionId
        List<Integer> questionIds = questions.stream().map(Question::getQuestionId).toList();

       
        Set<Integer> userFinishedQuestionIds;

        // if login , check finished questions 
        if (requestScopeData.isLogin() && requestScopeData.getUserId() != null) {
            userFinishedQuestionIds = noteMapper.filterFinishedQuestionIdsByUser(requestScopeData.getUserId(), questionIds);
        } else {
            userFinishedQuestionIds = Collections.emptySet();
        }

        List<QuestionUserVO> questionUserVOs = questions.stream().map(question -> {
            QuestionUserVO questionUserVO = new QuestionUserVO();
            QuestionUserVO.UserQuestionStatus userQuestionStatus = new QuestionUserVO.UserQuestionStatus();

            // check if question is finished
            if (userFinishedQuestionIds != null && userFinishedQuestionIds.contains(question.getQuestionId())) {
                userQuestionStatus.setFinished(true);  // finished
            }

            BeanUtils.copyProperties(question, questionUserVO);

            // 
            questionUserVO.setUserQuestionStatus(userQuestionStatus);
            return questionUserVO;
        }).toList();

        return ApiResponseUtil.success("success", questionUserVOs, pagination);
    }

    @Override
    public ApiResponse<QuestionNoteVO> userGetQuestion(Integer questionId) {

        // check if question exist
        Question question = questionMapper.findById(questionId);
        if (question == null) {
            return ApiResponseUtil.error("Illegal questionId");
        }

        QuestionNoteVO questionNoteVO = new QuestionNoteVO();
        QuestionNoteVO.UserNote userNote = new QuestionNoteVO.UserNote();

        // if login, find user's note
        if (requestScopeData.isLogin() && requestScopeData.getUserId() != null) {
            Note note = noteMapper.findByAuthorAndQuestionId(requestScopeData.getUserId(), questionId);
            if (note != null) {
                userNote.setFinished(true);
                BeanUtils.copyProperties(note, userNote);
            }
        }

        BeanUtils.copyProperties(question, questionNoteVO);
        questionNoteVO.setUserNote(userNote);

       
        // TODO: 
        questionMapper.incrementViewCount(questionId);

        return ApiResponseUtil.success("success", questionNoteVO);
    }

    @Override
    public ApiResponse<List<QuestionVO>> searchQuestions(SearchQuestionBody body) {
        String keyword = body.getKeyword();

        // TODO: 
        List<Question> questionList = questionMapper.findByKeyword(keyword);

        List<QuestionVO> questionVOList = questionList.stream().map(question -> {
            QuestionVO questionVO = new QuestionVO();
            BeanUtils.copyProperties(question, questionVO);
            return questionVO;
        }).toList();

        return ApiResponseUtil.success("Success", questionVOList);
    }




}
