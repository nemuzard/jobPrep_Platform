package com.jobprep.jobprep_platform.service.serviceImpl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.jobprep.jobprep_platform.annotation.NeedLogin;
import com.jobprep.jobprep_platform.mapper.NoteMapper;
import com.jobprep.jobprep_platform.mapper.QuestionMapper;
import com.jobprep.jobprep_platform.model.base.ApiResponse;
import com.jobprep.jobprep_platform.model.base.EmptyVO;
import com.jobprep.jobprep_platform.model.base.Pagination;
import com.jobprep.jobprep_platform.model.dto.note.CreateNoteRequest;
import com.jobprep.jobprep_platform.model.dto.note.NoteQueryParams;
import com.jobprep.jobprep_platform.model.dto.note.UpdateNoteRequest;
import com.jobprep.jobprep_platform.model.entity.Note;
import com.jobprep.jobprep_platform.model.entity.Question;
import com.jobprep.jobprep_platform.model.entity.User;
import com.jobprep.jobprep_platform.model.vo.category.CategoryVO;
import com.jobprep.jobprep_platform.model.vo.note.CreateNoteVO;
import com.jobprep.jobprep_platform.model.vo.note.DownloadNoteVO;
import com.jobprep.jobprep_platform.model.vo.note.NoteHeatMapItem;
import com.jobprep.jobprep_platform.model.vo.note.NoteRankListItem;
import com.jobprep.jobprep_platform.model.vo.note.NoteVO;
import com.jobprep.jobprep_platform.model.vo.note.Top3Count;
import com.jobprep.jobprep_platform.scope.RequestScopeData;
import com.jobprep.jobprep_platform.service.CategoryService;
import com.jobprep.jobprep_platform.service.CollectionNoteService;
import com.jobprep.jobprep_platform.service.NoteLikeService;
import com.jobprep.jobprep_platform.service.NoteService;
import com.jobprep.jobprep_platform.service.QuestionService;
import com.jobprep.jobprep_platform.service.UserService;
import com.jobprep.jobprep_platform.utils.ApiResponseUtil;
import com.jobprep.jobprep_platform.utils.MarkdownUtil;
import com.jobprep.jobprep_platform.utils.PaginationUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {

    private final NoteMapper noteMapper;

    private final UserService userService;

    private final QuestionService questionService;
    
    private final NoteLikeService noteLikeService;

    private final CollectionNoteService collectionNoteService;
    private final RequestScopeData requestScopeData;

    private final CategoryService categoryService;

    private final QuestionMapper questionMapper;

    @Override
    public ApiResponse<List<NoteVO>> getNotes(NoteQueryParams params){
        int offset = PaginationUtils.calculateOffset(params.getPage(), params.getPageSize());
        int total = noteMapper.countNotes(params);
        Pagination pagination = new Pagination(params.getPage(), params.getPageSize(), total);

        List<Note> notes = noteMapper.findByQueryParams(params, offset, params.getPageSize());
        // get questionIds and AuthorIds and move duplicates
        List<Integer> questionIds = notes.stream().map(Note::getQuestionId).distinct().toList();
        List<Long> authorIds = notes.stream().map(Note::getAuthorId).distinct().toList();
        List<Integer> noteIds = notes.stream().map(Note::getNoteId).toList();

        Map<Long,User> userMapByIds = userService.getUserMapByIds(authorIds);
        Map<Integer, Question> questionMapByIds = questionService.getQuestionMapByIds(questionIds);

        // current user liked notes and collection list
        Set<Integer> userLikedNoteIds;
        Set<Integer> userCollectedNoteIds;

        // if login, check if the user liked this note or collected.
        if(requestScopeData.isLogin() && requestScopeData.getUserId()!=null){
            Long currentUserId = requestScopeData.getUserId();
            userLikedNoteIds = noteLikeService.findUserLikedNoteIds(currentUserId, noteIds);
            userCollectedNoteIds = collectionNoteService.findUserCollectedNoteIds(currentUserId, noteIds);
        }else{// not login - empty
            userCollectedNoteIds = Collections.emptySet();
            userLikedNoteIds = Collections.emptySet();
        }

        // liked and collected notes
        try{
            List<NoteVO> noteVOs = notes.stream().map(note->{
                NoteVO noteVO = new NoteVO();
                BeanUtils.copyProperties(note, noteVO);

                User author = userMapByIds.get(note.getAuthorId());
                if(author!=null){
                    NoteVO.SimpleAuthorVO authorVO = new NoteVO.SimpleAuthorVO();
                    BeanUtils.copyProperties(author, authorVO);
                    noteVO.setAuthor(authorVO);
                }
                Question question = questionMapByIds.get(note.getQuestionId());
                if(question!=null){
                    NoteVO.SimpleQuestionVO questionVO = new NoteVO.SimpleQuestionVO();
                    BeanUtils.copyProperties(question, questionVO);
                    noteVO.setQuestion(questionVO);
                }

                NoteVO.UserActionsVO userActionsVO = new NoteVO.UserActionsVO();
                if(userLikedNoteIds!=null && userLikedNoteIds.contains(note.getNoteId())){
                    userActionsVO.setIsLiked(true);
                }
                if (userCollectedNoteIds != null && userCollectedNoteIds.contains(note.getNoteId())) {
                    userActionsVO.setIsCollected(true);
                }

                if(MarkdownUtil.needCollapsed(note.getContent())){
                    noteVO.setNeedCollapsed(true);
                    noteVO.setDisplayContent(MarkdownUtil.extractIntroduction(note.getContent()));
                }else{
                    noteVO.setNeedCollapsed(false);
                }
                noteVO.setUserActions(userActionsVO);
                return noteVO;
            }).toList();
            return ApiResponseUtil.success("success",noteVOs,pagination);
        }catch(Exception e){
            log.error("failed to map notes", e);
            return ApiResponseUtil.error("failed");
        }

    }

    @Override
    @NeedLogin
    public ApiResponse<CreateNoteVO> createNote(CreateNoteRequest request){
        Long userId = requestScopeData.getUserId();
        Integer questionId = request.getQuestionId();
        Question question = questionService.findById(questionId);
        if(question==null){
            return ApiResponseUtil.error("does not exist");
        }
        Note note = new Note();
        BeanUtils.copyProperties(request, note);
        note.setAuthorId(userId);

        try{
            noteMapper.insert(note);
            CreateNoteVO createNoteVO = new CreateNoteVO();
            createNoteVO.setNoteId(note.getNoteId());
            return ApiResponseUtil.success("success",createNoteVO);

        }catch(Exception e){
            return ApiResponseUtil.error("error");
        }
    }
    @Override
    @NeedLogin
    public ApiResponse<EmptyVO> updateNote(Integer noteId, UpdateNoteRequest request){
        Long userId = requestScopeData.getUserId();
        Note note = noteMapper.findById(noteId);
        if(note == null){
            return ApiResponseUtil.error("error");
        }
        // note not belong to
        if(!Objects.equals(userId, note.getAuthorId())){
            return ApiResponseUtil.error("Failed");
        }
        try{
            note.setContent(request.getContent());
            noteMapper.update(note);
            return ApiResponseUtil.success("success");
        }catch(Exception e){
            return ApiResponseUtil.error("failed");
        }
    }

    @Override
    @NeedLogin
    public ApiResponse<EmptyVO> deleteNote(Integer nodeId){
        Long userId = requestScopeData.getUserId();
        Note note = noteMapper.findById(nodeId);
        if (note == null){
            return ApiResponseUtil.error("failed");
        }
        if(!Objects.equals(userId,note.getAuthorId())){
            return ApiResponseUtil.error("failed");
        }
        try{
            noteMapper.deleteById(nodeId);
            return ApiResponseUtil.success("success");
        }catch(Exception e){
            log.error(e);
            return ApiResponseUtil.error("error");
        }
    }

    @Override
    @NeedLogin
    public ApiResponse<DownloadNoteVO> downloadNote(){
        Long userId = requestScopeData.getUserId();
        List<Note> userNotes = noteMapper.findByAuthorId(userId);
        Map<Integer,Note> questionNoteMap = userNotes.stream().collect(Collectors.toMap(Note::getQuestionId,note->note));
        if(userNotes.isEmpty()){
            return ApiResponseUtil.error("does not exist");
        }
        List<CategoryVO> catgeoryTree = categoryService.buildCategoryTree();
        var  markdownContent = new StringBuilder();

        List<Integer> questionIds = userNotes.stream().map(Note::getQuestionId).toList();
        List<Question> questions = questionMapper.findByIdBatch(questionIds);

        for (CategoryVO categoryVO: catgeoryTree){
            boolean hasTopLevelToc = false;
            if(categoryVO.getChildren().isEmpty()){
                continue;
            }

            for(CategoryVO.ChildrenCategoryVO childrenCategoryVO: categoryVO.getChildren()){
                boolean hasSubLevelToc = false;
                Integer categoryId = childrenCategoryVO.getCategoryId();
                List<Question> categoryQuestionList = questions.stream().filter(question->question.getCategoryId().equals(categoryId)).toList();

                if(categoryQuestionList.isEmpty()){
                    continue;
                }

                for(Question question:categoryQuestionList){
                    if(!hasTopLevelToc){
                        markdownContent.append("#").append(categoryVO.getName()).append("\n");
                        hasTopLevelToc=true;
                    }
                    if(!hasSubLevelToc){
                        markdownContent.append("## ").append(childrenCategoryVO.getName()).append("\n");
                        hasSubLevelToc = true;
                    }

                    markdownContent.append("### [")
                                .append(question.getTitle())
                                .append("]")
                                .append("(https://jobprep.jobprep_platform.com/questions/")
                                .append(question.getQuestionId())
                                .append(")\n");
                    Note note = questionNoteMap.get(question.getQuestionId());
                    markdownContent.append(note.getContent()).append("\n");
                }


            }

        }
        DownloadNoteVO downloadNoteVO = new DownloadNoteVO();
        downloadNoteVO.setMarkdown(markdownContent.toString());
        return ApiResponseUtil.success("success", downloadNoteVO);

    }

    @Override
    public ApiResponse<List<NoteRankListItem>> submitNoteRank(){
        return ApiResponseUtil.success("get rank success",noteMapper.submitNoteRank());
    }

    @Override
    public ApiResponse<List<NoteHeatMapItem>> submitNoteHeatMap(){
        Long userId = requestScopeData.getUserId();
        return ApiResponseUtil.success("success",noteMapper.submitNoteHeatMap(userId));
    }

    @Override
    public ApiResponse<Top3Count> submitNoteTop3Count() {

        Long userId = requestScopeData.getUserId();

        Top3Count top3Count = noteMapper.submitNoteTop3Count(userId);
        return ApiResponseUtil.success("success", top3Count);
    }
}
