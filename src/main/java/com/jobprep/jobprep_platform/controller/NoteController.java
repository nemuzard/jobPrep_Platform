package com.jobprep.jobprep_platform.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jobprep.jobprep_platform.model.base.ApiResponse;
import com.jobprep.jobprep_platform.model.base.EmptyVO;
import com.jobprep.jobprep_platform.model.dto.note.CreateNoteRequest;
import com.jobprep.jobprep_platform.model.dto.note.NoteQueryParams;
import com.jobprep.jobprep_platform.model.dto.note.UpdateNoteRequest;
import com.jobprep.jobprep_platform.model.vo.note.CreateNoteVO;
import com.jobprep.jobprep_platform.model.vo.note.DownloadNoteVO;
import com.jobprep.jobprep_platform.model.vo.note.NoteHeatMapItem;
import com.jobprep.jobprep_platform.model.vo.note.NoteRankListItem;
import com.jobprep.jobprep_platform.model.vo.note.NoteVO;
import com.jobprep.jobprep_platform.model.vo.note.Top3Count;
import com.jobprep.jobprep_platform.service.NoteService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
public class NoteController {
    private final NoteService noteService;

    @GetMapping
    public ApiResponse<List<NoteVO>> getNotes(@Valid NoteQueryParams params) {
        return noteService.getNotes(params);
    }

    @PostMapping
    public ApiResponse<CreateNoteVO> createNote(@Valid @RequestBody CreateNoteRequest request) {
        return noteService.createNote(request);
    }

    @PatchMapping("/{noteId}")
    public ApiResponse<EmptyVO> updateNote(
            @PathVariable Integer noteId,
            @Valid @RequestBody UpdateNoteRequest request) {
        return noteService.updateNote(noteId, request);
    }

    @DeleteMapping("/{noteId}")
    public ApiResponse<EmptyVO> deleteNote(@PathVariable Integer noteId) {
        return noteService.deleteNote(noteId);
    }

    @GetMapping("/ranklist")
    public ApiResponse<List<NoteRankListItem>> getNoteRankList() {
        return noteService.submitNoteRank();
    }

    @GetMapping("/heatmap")
    public ApiResponse<List<NoteHeatMapItem>> getNoteHeatMap() {
        return noteService.submitNoteHeatMap();
    }

    @GetMapping("/top3count")
    public ApiResponse<Top3Count> getTop3Count() {
        return noteService.submitNoteTop3Count();
    }

    @GetMapping("/download")
    public ApiResponse<DownloadNoteVO> downloadNote() {
        return noteService.downloadNote();
    }
}
