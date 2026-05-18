package com.jobprep.jobprep_platform.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@MapperScan("com.jobprep.jobprep_platform.mapper")
@EnableTransactionManagement
public class MyBatisConfig {
    
}
