package com.jobprep.jobprep_platform.service.resumematch.impl;

import com.jobprep.jobprep_platform.model.vo.resumematch.ResumeMatchProgressVO;
import com.jobprep.jobprep_platform.service.resumematch.ResumeMatchEventService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Log4j2
@Service
public class SseResumeMatchEventService implements ResumeMatchEventService {
    private static final long EMITTER_TIMEOUT_MS = 10 * 60 * 1000L;
    private final ConcurrentHashMap<Long, CopyOnWriteArrayList<SseEmitter>> emittersByJob = new ConcurrentHashMap<>();

    @Override
    public SseEmitter subscribe(Long jobId) {
        SseEmitter emitter = new SseEmitter(EMITTER_TIMEOUT_MS);
        emittersByJob.computeIfAbsent(jobId, ignored -> new CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> remove(jobId, emitter));
        emitter.onTimeout(() -> remove(jobId, emitter));
        emitter.onError(error -> remove(jobId, emitter));
        return emitter;
    }

    @Override
    public void publish(ResumeMatchProgressVO progress) {
        List<SseEmitter> emitters = emittersByJob.get(progress.getJobId());
        if (emitters == null || emitters.isEmpty()) {
            return;
        }
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("resume-match-progress")
                        .data(progress));
            } catch (IOException | IllegalStateException e) {
                log.warn("failed to send resume match SSE event for job {}", progress.getJobId(), e);
                remove(progress.getJobId(), emitter);
            }
        }
    }

    private void remove(Long jobId, SseEmitter emitter) {
        CopyOnWriteArrayList<SseEmitter> emitters = emittersByJob.get(jobId);
        if (emitters == null) {
            return;
        }
        emitters.remove(emitter);
        if (emitters.isEmpty()) {
            emittersByJob.remove(jobId);
        }
    }
}
