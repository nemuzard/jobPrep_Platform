package com.jobprep.jobprep_platform.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.jobprep.jobprep_platform.model.entity.Category;

@Mapper
public interface CategoryMapper {

    /**
     * Insert a new category into the database.
     * @param category
     * @return
     */
    int insert(Category category);

    /**
     * Batch insert categories into the database.
     * @param categories List of categories to be inserted.
     * @return
     */
    int insertBatch(@Param("categories") List<Category> categories);
    
    /**
     * Retrieve a list of all categories from the database.
     * @return
     */
    List<Category> categoryList();

    /**
     * Find a category by its ID.
     * @param categoryId
     * @return
     */
    Category findById(@Param("categoryId") Integer categoryId);

    List<Category> findByIdBatch(@Param("categoryIds") List<Integer> categoryIds);

    /**
     * find categories whose id or parent id is categoryId
     * @param categoryId the id used for filtering
     * @return a list of matching categories 
     */
    List<Category> findByIdOrParentId(@Param("categoryId") Integer categoryId);

    // delete
    int deleteById(@Param("categoryId") Integer categoryId);
    int deleteByIdBatch(@Param("categoryIds") List<Integer> categoryIds);


    // update 
    int update(Category category);
    // find by name
    Category findByName(@Param("categoryName") String categoryName);
    
}
