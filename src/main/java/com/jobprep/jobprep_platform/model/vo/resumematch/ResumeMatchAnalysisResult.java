package com.jobprep.jobprep_platform.model.vo.resumematch;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ResumeMatchAnalysisResult {
    private int score;
    private String summary;
    private List<String> strengths = new ArrayList<>();
    private List<String> gaps = new ArrayList<>();
    private List<String> recommendations = new ArrayList<>();
}
