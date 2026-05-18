package com.jobprep.jobprep_platform.mapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import com.jobprep.jobprep_platform.model.entity.NoteCollect;

@Mapper
public interface NoteCollectMapper {

    int insert(NoteCollect noteCollect);
    int delete(@Param("noteId") Integer noteId,@Param("userId")Long userId);
    // find collection 
    NoteCollect findByNoteIdAndUserId(@Param("noteId") Integer noteId,@Param("userId") Long userId);
    // find user collection note id list 
    List<Integer> findNoteIdsByUserId(@Param("userId") Long userId);

    
}
