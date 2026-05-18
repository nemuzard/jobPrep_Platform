package com.jobprep.jobprep_platform.model.entity;

import lombok.Data;
import java.util.Date;

@Data
public class Category {
    
    private Integer categoryId;
    private String name;
    // parentCategoryId = 0 means this category is a top-level category
    private Integer parentCategoryId;
    private Date createdAt;
    private Date updatedAt;

}
