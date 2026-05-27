package com.jobprep.jobprep_platform.service.serviceImpl;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jobprep.jobprep_platform.annotation.NeedLogin;
import com.jobprep.jobprep_platform.mapper.NoteLikeMapper;
import com.jobprep.jobprep_platform.mapper.NoteMapper;
import com.jobprep.jobprep_platform.model.base.ApiResponse;
import com.jobprep.jobprep_platform.model.base.EmptyVO;
import com.jobprep.jobprep_platform.model.dto.message.MessageDTO;
import com.jobprep.jobprep_platform.model.entity.Note;
import com.jobprep.jobprep_platform.model.entity.NoteLike;
import com.jobprep.jobprep_platform.model.enums.message.MessageTargetType;
import com.jobprep.jobprep_platform.model.enums.message.MessageType;
import com.jobprep.jobprep_platform.scope.RequestScopeData;
import com.jobprep.jobprep_platform.service.MessageService;
import com.jobprep.jobprep_platform.service.NoteLikeService;
import com.jobprep.jobprep_platform.utils.ApiResponseUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoteLikeServiceImpl implements NoteLikeService {
    private final NoteLikeMapper noteLikeMapper;
    private final NoteMapper noteMapper;
    private final RequestScopeData requestScopeData;
    private final MessageService messageService;

    @Override
    @NeedLogin
    @Transactional
    public ApiResponse<EmptyVO> likeNote(Integer noteId){
        Long userId = requestScopeData.getUserId();
        Note note = noteMapper.findById(noteId);
        if(note == null){
            return ApiResponseUtil.error("does not exist");
        }
        try{
            NoteLike noteLike = new NoteLike();
            noteLike.setNoteId(noteId);
            noteLike.setUserId(userId);
            noteLike.setCreatedAt(new Date());
            noteLikeMapper.insert(noteLike);
            noteMapper.likeNote(noteId);
            
            MessageDTO messageDTO = new MessageDTO();
            messageDTO.setType(MessageType.LIKE);
            messageDTO.setReceiverId(note.getAuthorId());
            messageDTO.setSenderId(userId);

            messageDTO.setTargetType(MessageTargetType.NOTE);
            messageDTO.setTargetId(noteId);
            messageDTO.setIsRead(false);

            System.out.println(messageDTO);

            messageService.createMessage(messageDTO);
            return ApiResponseUtil.success("liked!");
        }catch(Exception e){
            return ApiResponseUtil.error("failed");
        }
    }
    @Override
    @NeedLogin
    @Transactional
    public ApiResponse<EmptyVO> unlikeNote(Integer noteId){
        Long userId = requestScopeData.getUserId();
        Note note = noteMapper.findById(noteId);
        if(note == null ){
            return ApiResponseUtil.error("not exist");
        }
        try{
            NoteLike noteLike = noteLikeMapper.findByUserIdAndNoteId(userId, noteId);
            if (noteLike != null) {
                noteLikeMapper.delete(noteLike);
                noteMapper.unlikeNote(noteId);
            }
            return ApiResponseUtil.success("unliked");
        }catch(Exception e){
            return ApiResponseUtil.error("failed");
        }
    }
    @Override
    public Set<Integer> findUserLikedNoteIds(Long userId, List<Integer> noteIds) {
        List<Integer> likedIds = noteLikeMapper.findUserLikedNoteIds(userId, noteIds);
        return new HashSet<>(likedIds);
    }

}
