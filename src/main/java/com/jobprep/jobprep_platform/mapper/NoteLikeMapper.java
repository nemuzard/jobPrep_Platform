package com.jobprep.jobprep_platform.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import com.jobprep.jobprep_platform.model.entity.NoteLike;

@Mapper
public interface NoteLikeMapper {
    int insert(NoteLike noteLike);

    int delete(NoteLike noteLike);

    /**
     * find user liked note id list
     * @param userId
     * @param noteIds - all note ids 
     * @return
     */
    List<Integer> findUserLikedNoteIds(
        @Param("userId") Long userId,
        @Param("noteIds") List<Integer> noteIds
    );
    
    /**
     * find specific 'note-like-record' based on user id and note id
     * user to verify if the user liked certain note
     * @param userId
     * @param noteId
     * @return
     */
    NoteLike findByUserIdAndNoteId(
        @Param("userId") Long userId,
        @Param("noteId") Integer noteId
    );
    
}
