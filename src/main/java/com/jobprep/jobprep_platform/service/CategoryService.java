package com.jobprep.jobprep_platform.service;

import com.jobprep.jobprep_platform.model.base.ApiResponse;
import com.jobprep.jobprep_platform.model.base.EmptyVO;
import com.jobprep.jobprep_platform.model.dto.category.CreateCategoryBody;
import com.jobprep.jobprep_platform.model.vo.category.CategoryVO;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import com.jobprep.jobprep_platform.model.entity.Category;
import com.jobprep.jobprep_platform.model.vo.category.CreateCategoryVO;
@Transactional
public interface CategoryService {
    /**
     * create a category tree, where each category contains its children categories
     * @return
     */
    List<CategoryVO> buildCategoryTree();

    /**
     * get all categories in a flat list,
     * @return
     */
    ApiResponse<List<CategoryVO>> categoryList();

    /**
     * delete a category by id, and all its children categories will also be deleted
     * @param categoryId
     * @return
     */
    ApiResponse<EmptyVO> deleteCategory(Integer categoryId);
    /**
     * create a category,
     * This method receives a Category object and saves it to the database
     * after performing the necessary validations.
     * If the operation succeeds, a CategoryVO object containing the newly
     * created category information will be returned.
     * @param createCategoryBody the request body containing the category information to be created(name,description,..)
     * @return ApiResponse object, HTTP status code, error messsage(if any), new created categoryVO object
     */
    ApiResponse<CreateCategoryVO> createCategory(CreateCategoryBody createCategoryBody);

    /**
     * update a category by id  
     * @param categoryId
     * @param updateCategoryBody
     * @return
     */
    ApiResponse<EmptyVO> updateCategory(Integer categoryId, CreateCategoryBody updateCategoryBody);

    /**
     * find a category by name, if not exist, create a new category with the given name and return it
     * @param categoryName
     * @return
     */
    Category findOrCreateCategory(String categoryName);

    /**
     * find a category by name and parent category id,
     *  if not exist, create a 
     * new category with the given name and parent category id, then return it
     * @param categoryName
     * @param parentCategoryId
     * @return
     */
    Category findOrCreateCategory(String categoryName, Integer parentCategoryId);

}
