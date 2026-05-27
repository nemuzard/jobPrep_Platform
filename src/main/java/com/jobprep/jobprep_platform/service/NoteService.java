package com.jobprep.jobprep_platform.service;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import com.jobprep.jobprep_platform.model.base.ApiResponse;
import com.jobprep.jobprep_platform.model.dto.note.CreateNoteRequest;
import com.jobprep.jobprep_platform.model.dto.note.NoteQueryParams;
import com.jobprep.jobprep_platform.model.dto.note.UpdateNoteRequest;
import com.jobprep.jobprep_platform.model.vo.note.*;
import com.jobprep.jobprep_platform.model.base.EmptyVO;

@Transactional
public interface NoteService {

    /**
     * fetch note list 
     * @param params query params for fetching notes, including pagination and filtering options
     * @return response containing a list of note view objects 
     * that match the query condition 
     */
    ApiResponse<List<NoteVO>> getNotes(NoteQueryParams params);
    
    /**
     * create a new note
     * @param request request object containing the details of the note to be created, such as title, content
     * @return response containing the view object of the created note
     */
    ApiResponse<CreateNoteVO> createNote(CreateNoteRequest request);

    /**
     * update an existing note
     * @param noteId id of the note to be updated
     * @param request
     * @return
     */
    ApiResponse<EmptyVO> updateNote(Integer noteId, UpdateNoteRequest request);

    /**
     * delete a note
     * @param noteId
     * @return
     */
    ApiResponse<EmptyVO> deleteNote(Integer noteId);

    /**
     * download note content in markdown format
     * @param noteId
     * @return
     */
    ApiResponse<DownloadNoteVO> downloadNote();

    /**
     * submit note rank data for the current user, which will be used to generate the note heat map and note rank list
     * @return
     */
    ApiResponse<List<NoteRankListItem>> submitNoteRank();
    
    /**
     * submit note heat map data 
     * @return
     */
    ApiResponse<List<NoteHeatMapItem>> submitNoteHeatMap();

    ApiResponse<Top3Count> submitNoteTop3Count();
}
