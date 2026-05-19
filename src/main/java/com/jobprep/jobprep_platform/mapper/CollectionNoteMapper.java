package com.jobprep.jobprep_platform.mapper;

import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import java.util.Set;
import org.apache.ibatis.annotations.Param;
import com.jobprep.jobprep_platform.model.entity.CollectionNote;

@Mapper
public interface CollectionNoteMapper {
    /**
     * find the note ids that the user has collected from the given note ids
     * @param userId
     * @param noteIds
     * @return
     */
    List<Integer> findUserCollectedNoteIds(
        @Param("userId") Long userId,
        @Param("noteIds") List<Integer> noteIds
    );

    /**
     * find the collection ids that the note belongs to from the given collection ids
     * @param noteId
     * @param collectionIds
     * @return
     */
    Set<Integer> filterCollectionIdsByNoteId(
        @Param("noteId") Integer noteId,
        @Param("collectionIds") List<Integer> collectionIds
    );

    int insert(CollectionNote collectionNote);
    int deleteByCollectionId(@Param("collectionId") Integer collectionId);
    int deleteByCollectionIdAndNoteId(
        @Param("collectionId") Integer collectionId,
        @Param("noteId") Integer noteId
    );


}
