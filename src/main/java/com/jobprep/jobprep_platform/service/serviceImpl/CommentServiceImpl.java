package com.jobprep.jobprep_platform.service.serviceImpl;

import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.*;

import org.springframework.util.CollectionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jobprep.jobprep_platform.annotation.NeedLogin;
import com.jobprep.jobprep_platform.mapper.CommentLikeMapper;
import com.jobprep.jobprep_platform.mapper.CommentMapper;
import com.jobprep.jobprep_platform.mapper.NoteMapper;
import com.jobprep.jobprep_platform.mapper.UserMapper;
import com.jobprep.jobprep_platform.model.base.ApiResponse;
import com.jobprep.jobprep_platform.model.base.EmptyVO;
import com.jobprep.jobprep_platform.model.base.Pagination;
import com.jobprep.jobprep_platform.model.dto.comment.CommentQueryParams;
import com.jobprep.jobprep_platform.model.dto.comment.CreateCommentRequest;
import com.jobprep.jobprep_platform.model.dto.comment.UpdateCommentRequest;
import com.jobprep.jobprep_platform.model.dto.message.MessageDTO;
import com.jobprep.jobprep_platform.model.entity.Comment;
import com.jobprep.jobprep_platform.model.entity.CommentLike;
import com.jobprep.jobprep_platform.model.entity.Note;
import com.jobprep.jobprep_platform.model.entity.User;
import com.jobprep.jobprep_platform.model.enums.message.MessageTargetType;
import com.jobprep.jobprep_platform.model.enums.message.MessageType;
import com.jobprep.jobprep_platform.model.vo.comment.CommentVO;
import com.jobprep.jobprep_platform.model.vo.user.UserActionVO;
import com.jobprep.jobprep_platform.scope.RequestScopeData;
import com.jobprep.jobprep_platform.service.CommentService;
import com.jobprep.jobprep_platform.service.MessageService;
import com.jobprep.jobprep_platform.utils.ApiResponseUtil;
import com.jobprep.jobprep_platform.utils.PaginationUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService{
    private final CommentMapper commentMapper;
    private final NoteMapper noteMapper;
    private final UserMapper userMapper;
    private final CommentLikeMapper commentLikeMapper;
    private final RequestScopeData requestScopeData;
    private final MessageService messageService;

    @Override
    @NeedLogin
    @Transactional
    public ApiResponse<Integer> createComment(CreateCommentRequest request){
        log.info("Log comment: request={}",request);
        try{
            Long userId = requestScopeData.getUserId();
            Note note = noteMapper.findById(request.getNoteId());
            if(note == null){
                log.error("Note does not exist. Note id = {}",request.getNoteId());
                return ApiResponse.error(HttpStatus.NOT_FOUND.value(), "note does not exist");

            }
            Comment comment = new Comment();
            comment.setNoteId(request.getNoteId());
            comment.setContent(request.getContent());
            comment.setAuthorId(userId);
            comment.setParentId(request.getParentId());
            comment.setLikeCount(0);
            comment.setReplyCount(0);
            comment.setCreatedAt(LocalDateTime.now());
            comment.setUpdatedAt(LocalDateTime.now());
            commentMapper.insert(comment);
            log.info("Result: commentId = {}",comment.getCommentId());
            
            //increment count
            noteMapper.incrementCommentCount(request.getNoteId());
            // reply not new comment
            if(request.getParentId()!=null){
                commentMapper.incrementReplyCount(request.getParentId());
            }
            // send notification
            MessageDTO messageDTO = new MessageDTO();
            messageDTO.setType(MessageType.COMMENT);
            messageDTO.setTargetType(MessageTargetType.NOTE);
            messageDTO.setTargetId(request.getNoteId());
            messageDTO.setReceiverId(note.getAuthorId());
            messageDTO.setSenderId(userId);
            messageDTO.setContent(request.getContent());
            messageDTO.setIsRead(false);
            
            messageService.createMessage(messageDTO);
            return ApiResponse.success(comment.getCommentId());
        }catch(Exception e){
            log.error("failed", e);
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
        }
    }

    @Override
    @NeedLogin
    @Transactional
    public ApiResponse<EmptyVO> updateComment(Integer commentId, UpdateCommentRequest request ){
        Long userId = requestScopeData.getUserId();
        Comment comment = commentMapper.findById(commentId);
        if(comment == null){
            return ApiResponse.error(HttpStatus.NOT_FOUND.value(), "Not found");
        }
        if(!comment.getAuthorId().equals(userId)){
            return ApiResponse.error(HttpStatus.FORBIDDEN.value(), "Failed");
        }
        try{
            comment.setContent(request.getContent());
            comment.setUpdatedAt(LocalDateTime.now());
            commentMapper.update(comment);
            return ApiResponse.success(new EmptyVO());
        } catch(Exception e){
            log.error("update failed", e);
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), null);

        }

    }

    @Override
    @NeedLogin
    @Transactional
    public ApiResponse<EmptyVO> deleteComment(Integer commentId){
        Long userId = requestScopeData.getUserId();
        Comment comment = commentMapper.findById(commentId);
        if(comment == null){
            return ApiResponse.error(HttpStatus.NOT_FOUND.value(), "Comment does not exist");

        }

        if(!comment.getAuthorId().equals(userId)){
            return ApiResponse.error(HttpStatus.FORBIDDEN.value(), "Cannot delete");
        }
        try{
            commentMapper.deleteById(commentId);
            return ApiResponse.success(new EmptyVO());
        }catch(Exception e){
            log.error("deletion failed",e);
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "failed");
        }
    }

    @Override
    public ApiResponse<List<CommentVO>> getComments(CommentQueryParams params){
        try{
            List<Comment> comments = commentMapper.findByNoteId(params.getNoteId());
            System.out.println(comments);

            if(CollectionUtils.isEmpty(comments)){
                return ApiResponse.success(Collections.emptyList());
            }
            // first level - parent id ==0 
            List<Comment> firstLevel = comments.stream()
                    .filter(c -> c.getParentId() == null || c.getParentId() == 0)
                    .sorted(Comparator.comparing(Comment::getCreatedAt))      // increasing based on time
                    .toList();
            int from = PaginationUtils.calculateOffset(params.getPage(), params.getPageSize());
            if (from >= firstLevel.size()) {
                return ApiResponse.success(Collections.emptyList());          // greater than max page 
            }
            //paginate top-level comments only.
            //Replies are attached under their parent comments.
            int to = Math.min(from + params.getPageSize(), firstLevel.size());
            List<Comment> pagedFirst = firstLevel.subList(from, to);

            // parentId  => children
            Map<Integer, List<Comment>> repliesMap = comments.stream()
                    .filter(c -> c.getParentId() != null)
                    .collect(Collectors.groupingBy(Comment::getParentId));
            // Batch query authors
            List<Long> authorIds = comments.stream()
                    .map(Comment::getAuthorId)
                    .collect(Collectors.toList());

            Map<Long, User> authorMap = userMapper.findByIdBatch(authorIds)
                    .stream()
                    .collect(Collectors.toMap(User::getUserId, u -> u));

            //Get current user liked comments
            Long currentUserId = requestScopeData.getUserId();

            Set<Integer> likedSet;
            if (currentUserId != null) {
                List<Integer> allCommentIds = comments.stream()
                        .map(Comment::getCommentId)
                        .toList();
                likedSet = new HashSet<>(commentLikeMapper.findUserLikedCommentIds(currentUserId, allCommentIds));
            } else {
                likedSet = Collections.emptySet();
            }
            //Convert first-level comments to CommentVO
            List<CommentVO> result = pagedFirst.stream()
                    .map(c -> toVO(c, repliesMap, authorMap, likedSet))
                    .toList();

            Pagination pagination = new Pagination(params.getPage(), params.getPageSize(), firstLevel.size());

            return ApiResponseUtil.success("", result, pagination);
        }catch (Exception e){
            log.error("failed");
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),"failed");
        }
        
    }
    private CommentVO toVO(Comment c, 
                            Map<Integer,List<Comment>> repliesMaps,
                            Map<Long, User> authorMap,
                            Set<Integer> likedSet){
        
        CommentVO vo = new CommentVO();
        vo.setCommentId(c.getCommentId());
        vo.setNoteId(c.getNoteId());
        vo.setContent(c.getContent());
        vo.setLikeCount(c.getLikeCount());
        vo.setReplyCount(c.getReplyCount());
        vo.setCreatedAt(c.getCreatedAt());
        vo.setUpdatedAt(c.getUpdatedAt());

        //author info 
        User author = authorMap.get(c.getAuthorId());
        if (author != null) {
            CommentVO.SimpleAuthorVO a = new CommentVO.SimpleAuthorVO();
            a.setUserId(author.getUserId());
            a.setUsername(author.getUsername());
            a.setAvatarUrl(author.getAvatarUrl());
            vo.setAuthor(a);
        }

        // current user action
        if (!likedSet.isEmpty()) {
            UserActionVO actions = new UserActionVO();
            actions.setIsLiked(likedSet.contains(c.getCommentId()));
            vo.setUserActions(actions);
        } else {
            vo.setUserActions(new UserActionVO());
            vo.getUserActions().setIsLiked(false);
        }
        return vo;

    }
    @Override
    @NeedLogin
    @Transactional
    public ApiResponse<EmptyVO> likeComment(Integer commentId) {
        Long userId = requestScopeData.getUserId();
        System.out.println(userId+" liked "+ commentId);

        Comment comment = commentMapper.findById(commentId);
        if (comment == null){
            return ApiResponse.error(HttpStatus.NOT_FOUND.value(), "Comment not found");
        }

        try{
            commentMapper.incrementLikeCount(commentId);
            CommentLike commentLike = new CommentLike();
            commentLike.setCommentId(commentId);
            commentLike.setUserId(userId);

            commentLikeMapper.insert(commentLike);
            MessageDTO messageDTO = new MessageDTO();
            messageDTO.setType(MessageType.LIKE);
            messageDTO.setReceiverId(comment.getAuthorId());
            messageDTO.setSenderId(userId);
            messageDTO.setTargetType(MessageTargetType.NOTE);
            messageDTO.setTargetId(comment.getNoteId());
            messageDTO.setIsRead(false);

            messageService.createMessage(messageDTO);
            return ApiResponse.success(new EmptyVO());
        }catch(Exception e){
            log.error("Like Comment: failed");
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),"failed");
        }
    }
    
    @Override
    @NeedLogin
    @Transactional
    public ApiResponse<EmptyVO> unlikeComment(Integer commentId) {
        Long userId = requestScopeData.getUserId();
        Comment comment = commentMapper.findById(commentId);
        if(comment == null){
            return ApiResponse.error(HttpStatus.NOT_FOUND.value(), "Not found");
        }
        try{
            commentMapper.decrementLikeCount(commentId);
            commentLikeMapper.delete(commentId, userId);
            return ApiResponse.success(new EmptyVO());
        }catch (Exception e){
            log.error("failed",e);
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),"failed");
        }
    }
    
}
