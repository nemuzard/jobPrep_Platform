package com.jobprep.jobprep_platform.mapper;

import com.jobprep.jobprep_platform.model.entity.Collection;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CollectionMapper {
    // Find a collection by its ID
    Collection findById(@Param("collectionId") Integer collectionId);

    // Find all collections for a specific user
    List<Collection> findByCreatorId(@Param("creatorId") Long creatorId);

    /**
     * Find a collection by its ID and the creator's ID 
     * to ensure that the collection belongs to the user.
     * @param collectionId
     * @param creatorId
     * @return
     */
    Collection findByIdAndCreatorId(@Param("collectionId") Integer collectionId, @Param("creatorId") Long creatorId);

    /**
     * Count the number of collections 
     * @param creatorId
     * @param noteId
     * @return
     */
    int countByCreatorIdAndNoteId(
        @Param("creatorId") Long creatorId,
        @Param("noteId") Integer noteId
    );

    int insert(Collection collection);
    int update(Collection collection);
    /**
     * Delete a collection by its ID.
     * @param collectionId
     * @return the number of rows affected
     */
    int deleteById(@Param("collectionId") Integer collectionId);

}
