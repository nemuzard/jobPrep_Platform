package com.jobprep.jobprep_platform.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Set;
import com.jobprep.jobprep_platform.model.vo.note.Top3Count;
import com.jobprep.jobprep_platform.model.vo.note.NoteRankListItem;
import com.jobprep.jobprep_platform.model.vo.note.NoteHeatMapItem;
import com.jobprep.jobprep_platform.model.entity.Note;
import com.jobprep.jobprep_platform.model.dto.note.NoteQueryParams;
@Mapper
public interface NoteMapper {
    
    int countNotes(@Param("params") NoteQueryParams params);
    Note findById(@Param("noteId")Integer noteId);
    List<Note> findByQueryParams(
            @Param("params") NoteQueryParams params,
            @Param("offset") int offset,
            @Param("limit") int limit
    );

    Note findByAuthorAndQuestionId(
            @Param("authorId") Long authorId,
            @Param("questionId") Integer questionId
    );
    
    List<Note> findByAuthorId(@Param("authorId") Long authorId);

    Set<Integer> filterFinishedQuestionIdsByUser(@Param("authorId")Long authorId,
                                                @Param("questionIds")List<Integer> questionIds);
    
    int insert(Note note);
    int update(Note note);
    
    int likeNote(@Param("noteId")Integer noteId);
    int unlikeNote(@Param("noteId") Integer noteId);

    int collectNote(@Param("noteId")Integer noteId);
    int unCollectNote(@Param("noteId") Integer noteId);

    int deleteById(@Param("noteId")Integer noteId);
    // note submission rank
    List<NoteRankListItem> submitNoteRank();
    // submission heat map
    List<NoteHeatMapItem> submitNoteHeatMap(@Param("authorId") Long authorId);
    Top3Count submitNoteTop3Count(@Param("authorId") Long authorId);
    
    int getTodayNoteCount();
    int getTodaySubmitNoteUserCount();
    int getTotalNoteCount();

    void incrementCommentCount(@Param("noteId") Integer noteId);
    void decrementCommentCount(@Param("noteId") Integer noteId);

    // search note based on keywords & pagination
    List<Note> searchNotes(
            @Param("keyword") String keyword,
            @Param("limit") int limit,
            @Param("offset") int offset
    );
    //search by tag
    List<Note> searchNotesByTag(
        @Param("keyword")String keyword,
        @Param("tag") String tag,
        @Param("limit") int limit,
        @Param("offset") int offset
    );
}
