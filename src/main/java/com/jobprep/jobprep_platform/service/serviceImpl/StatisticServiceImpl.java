package com.jobprep.jobprep_platform.service.serviceImpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jobprep.jobprep_platform.mapper.StatisticMapper;
import com.jobprep.jobprep_platform.model.base.ApiResponse;
import com.jobprep.jobprep_platform.model.base.Pagination;
import com.jobprep.jobprep_platform.model.dto.statistic.StatisticQueryParam;
import com.jobprep.jobprep_platform.model.entity.Statistic;
import com.jobprep.jobprep_platform.service.StatisticService;
import com.jobprep.jobprep_platform.utils.ApiResponseUtil;
import com.jobprep.jobprep_platform.utils.PaginationUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService{
    private final StatisticMapper statisticMapper;
    @Override
    public ApiResponse<List<Statistic>> getStatistic(StatisticQueryParam queryParam){
        Integer page = queryParam.getPage();
        Integer pageSize = queryParam.getPageSize();
        int offset = PaginationUtils.calculateOffset(page, pageSize);
        int total = statisticMapper.countStatistic();
        Pagination pagination = new Pagination(page, pageSize, total);
        try{
            List<Statistic> statistics = statisticMapper.findByPage(pageSize, offset);
            return ApiResponseUtil.success("success", statistics, pagination);
        }catch (Exception e ){
            return ApiResponseUtil.error(e.getMessage());
        }
    }

}
