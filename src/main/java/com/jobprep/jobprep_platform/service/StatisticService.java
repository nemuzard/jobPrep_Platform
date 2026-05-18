package com.jobprep.jobprep_platform.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.jobprep.jobprep_platform.model.base.ApiResponse;
import com.jobprep.jobprep_platform.model.dto.statistic.StatisticQueryParam;
import com.jobprep.jobprep_platform.model.entity.Statistic;

@Transactional
public interface StatisticService {
    ApiResponse<List<Statistic>> getStatistic(StatisticQueryParam queryParam);
}
