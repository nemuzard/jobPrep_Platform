package com.jobprep.jobprep_platform.mapper;

import com.jobprep.jobprep_platform.model.entity.Statistic;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StatisticMapper {
    int insert(Statistic statistic);
    int countStatistic();
    //
    List<Statistic> findByPage(@Param("limit")Integer limit,@Param("offset") Integer offset);
}
