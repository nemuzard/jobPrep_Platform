package com.jobprep.jobprep_platform.task.statistic;

import java.time.LocalDate;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.jobprep.jobprep_platform.mapper.NoteMapper;
import com.jobprep.jobprep_platform.mapper.StatisticMapper;
import com.jobprep.jobprep_platform.mapper.UserMapper;
import com.jobprep.jobprep_platform.model.entity.Statistic;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.scheduling.enabled", havingValue = "true", matchIfMissing = true)
public class DailyStatistics {
    private final UserMapper userMapper;
    private final NoteMapper noteMapper;
    private final StatisticMapper statisticMapper;
    
    @Scheduled(cron = "0 59 23 * * ?")
    public void dailyStatistics(){
        Statistic statistic = new Statistic();
        // user data
        int todayLoginCount = userMapper.getTodayLoginCount();
        int todayRegisterCount = userMapper.getTodayRegisterCount();
        int totalRegisterCount = userMapper.getTotalRegisterCount();
        // note data 
        int todayNoteCount = noteMapper.getTodayNoteCount();
        int todaySubmitNoteUserCount = noteMapper.getTodaySubmitNoteUserCount();
        int totalNoteCount = noteMapper.getTotalNoteCount();

        
        statistic.setLoginCount(todayLoginCount);
        statistic.setRegisterCount(todayRegisterCount);
        statistic.setTotalRegisterCount(totalRegisterCount);

        statistic.setNoteCount(todayNoteCount);
        statistic.setSubmitNoteCount(todaySubmitNoteUserCount);
        statistic.setTotalNoteCount(totalNoteCount);

        statistic.setDate(LocalDate.now());
        try{
            statisticMapper.insert(statistic);
            log.info("[Scheduled Task] Daily statistics inserted successfully, statistic={}",statistic);
        }catch(Exception e){
            log.error("Task failed, statistic={},error={}",statistic,e.getMessage());
            e.printStackTrace();
        }


    }
}
