package com.jobprep.jobprep_platform.controller.resumematch;

import com.jobprep.jobprep_platform.model.base.ApiResponse;
import com.jobprep.jobprep_platform.model.base.EmptyVO;
import com.jobprep.jobprep_platform.model.dto.resumematch.CreateResumeMatchJobRequest;
import com.jobprep.jobprep_platform.model.vo.resumematch.CreateResumeMatchJobVO;
import com.jobprep.jobprep_platform.model.vo.resumematch.ResumeMatchJobVO;
import com.jobprep.jobprep_platform.model.vo.resumematch.ResumeMatchProgressVO;
import com.jobprep.jobprep_platform.service.resumematch.ResumeMatchEventService;
import com.jobprep.jobprep_platform.service.resumematch.ResumeMatchService;
import com.jobprep.jobprep_platform.utils.ApiResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RestController
@RequestMapping("/api/resume-match")
@RequiredArgsConstructor
public class ResumeMatchController {
    private final ResumeMatchService resumeMatchService;
    private final ResumeMatchEventService eventService;

    @PostMapping("/jobs/presigned-upload")
    public ApiResponse<CreateResumeMatchJobVO> createPresignedUpload(@Valid @RequestBody CreateResumeMatchJobRequest request) {
        return resumeMatchService.createUploadJob(request);
    }

    @PutMapping("/uploads/{jobId}")
    public ApiResponse<EmptyVO> uploadToLocalObjectStorage(
            @PathVariable Long jobId,
            @RequestParam String token,
            @RequestBody byte[] content
    ) {
        try {
            resumeMatchService.acceptLocalUpload(jobId, token, content);
            return ApiResponseUtil.success("success");
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "failed to upload resume");
        }
    }

    @GetMapping("/jobs/{jobId}")
    public ApiResponse<ResumeMatchJobVO> getJob(@PathVariable Long jobId) {
        return resumeMatchService.getJob(jobId);
    }

    @GetMapping(value = "/jobs/{jobId}/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable Long jobId) throws IOException {
        ApiResponse<ResumeMatchJobVO> jobResponse = resumeMatchService.getJob(jobId);
        if (jobResponse.getCode() != HttpStatus.OK.value() || jobResponse.getData() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "resume match job not found");
        }

        SseEmitter emitter = eventService.subscribe(jobId);
        ResumeMatchJobVO job = jobResponse.getData();
        emitter.send(SseEmitter.event()
                .name("resume-match-progress")
                .data(ResumeMatchProgressVO.builder()
                        .jobId(jobId)
                        .status(job.getStatus())
                        .progress(job.getProgress())
                        .message("Current resume match job status.")
                        .build()));
        return emitter;
    }
}
