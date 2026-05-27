package com.jobprep.jobprep_platform.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jobprep.jobprep_platform.model.base.ApiResponse;
import com.jobprep.jobprep_platform.model.dto.statistic.StatisticQueryParam;
import com.jobprep.jobprep_platform.model.entity.Statistic;
import com.jobprep.jobprep_platform.service.StatisticService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/statistic")
@RequiredArgsConstructor
public class StatisticController {
    private final StatisticService statisticService;

    @GetMapping
    public ApiResponse<List<Statistic>> getStatistic(@Valid StatisticQueryParam params) {
        return statisticService.getStatistic(params);
    }
}
