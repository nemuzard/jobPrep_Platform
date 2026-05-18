package com.jobprep.jobprep_platform.service.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jobprep.jobprep_platform.service.CategoryService;
import java.util.List;
import com.jobprep.jobprep_platform.mapper.CategoryMapper;
import com.jobprep.jobprep_platform.mapper.QuestionMapper;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired QuestionMapper questionMapper;
    
    public List<CategoryVO> buildCategoryTree(){

    }
    
}
