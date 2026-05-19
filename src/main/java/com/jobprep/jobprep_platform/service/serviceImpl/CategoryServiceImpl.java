package com.jobprep.jobprep_platform.service.serviceImpl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jobprep.jobprep_platform.service.CategoryService;
import com.jobprep.jobprep_platform.utils.ApiResponseUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.jobprep.jobprep_platform.mapper.CategoryMapper;
import com.jobprep.jobprep_platform.mapper.QuestionMapper;
import com.jobprep.jobprep_platform.model.base.ApiResponse;
import com.jobprep.jobprep_platform.model.base.EmptyVO;
import com.jobprep.jobprep_platform.model.dto.category.CreateCategoryBody;
import com.jobprep.jobprep_platform.model.dto.category.UpdateCategoryBody;
import com.jobprep.jobprep_platform.model.dto.question.CreateQuestionBatchBody;
import com.jobprep.jobprep_platform.model.entity.Category;
import com.jobprep.jobprep_platform.model.vo.category.CategoryVO;
import com.jobprep.jobprep_platform.model.vo.category.CreateCategoryVO;

@Service
public class CategoryServiceImpl implements CategoryService {
   
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired 
    private QuestionMapper questionMapper;
    
    public List<CategoryVO> buildCategoryTree(){
        // get all categories 
        List<Category> categories = categoryMapper.categoryList();

        // construct map for parent categories
        // used for quick search a certain category
        Map<Integer,CategoryVO> categoryMap = new HashMap();

        categories.forEach(category ->{
            if(category.getParentCategoryId()==0){
                // parent 
                CategoryVO categoryVO = new CategoryVO();
                BeanUtils.copyProperties(category, categoryVO);
                categoryVO.setChildren(new ArrayList<>());
                categoryMap.put(category.getCategoryId(),categoryVO);    
            }else{
                // children
                CategoryVO.ChildrenCategoryVO childrenCategoryVO = new CategoryVO.ChildrenCategoryVO();
                BeanUtils.copyProperties(category, childrenCategoryVO);
                //put children category to corrsponding parent category
                CategoryVO parentCategory = categoryMap.get(category.getParentCategoryId());
                if (parentCategory!=null){
                    parentCategory.getChildren().add(childrenCategoryVO);
                }
            }
        });
        return new ArrayList<>(categoryMap.values());
    }
    @Override
    public ApiResponse<List<CategoryVO>> categoryList(){
        return ApiResponseUtil.success("get category list - success!",buildCategoryTree());
    }
    @Override
    @Transactional
    public ApiResponse<EmptyVO> deleteCategory(Integer categoryId) throws RuntimeException{
        // find category id or parent id 
        List<Category> categories = categoryMapper.findByIdOrParentId(categoryId);
        if(categories.isEmpty()){
            return ApiResponseUtil.error("illegal category id");
        }
        // get ids
        List<Integer> categoryIds = categories.stream().map(Category::getCategoryId).toList();
        // delete all 
        try{
            int deleteCount = categoryMapper.deleteByIdBatch(categoryIds);
            if(deleteCount!=categoryIds.size()){
                throw new RuntimeException("delete failed");
            }
            /**
             * Todo - need to consider if delete question, 
             * should the note also be deleted?
             */
            questionMapper.deleteByCategoryIdBatch(categoryIds);
            return ApiResponseUtil.success("success");

        } catch (Exception e ){
            throw new RuntimeException("deletion failed.");
        }
    }

    @Override
    public ApiResponse<CreateCategoryVO> createCategory(CreateCategoryBody categoryBody){
        if(categoryBody.getParentCategoryId()!=0){
            Category parent = categoryMapper.findById(categoryBody.getParentCategoryId());
            if(parent == null){
                return ApiResponseUtil.error("Parent category id does not exist.");
            }
        }
        Category category = new Category();
        BeanUtils.copyProperties(categoryBody, category);

        // insert this category
        try{
            categoryMapper.insert(category);
            CreateCategoryVO createCategoryVO = new CreateCategoryVO();
            createCategoryVO.setCategoryId(category.getCategoryId());
            return ApiResponseUtil.success("success",createCategoryVO);
        } catch(Exception e){
            return ApiResponseUtil.error("failed");
        }
    }
    @Override
    public ApiResponse<EmptyVO> updateCategory(Integer categoryId, UpdateCategoryBody categoryBody){
        Category category = categoryMapper.findById(categoryId);
        if(category==null){
            return ApiResponseUtil.error("category id does not exist.");
        }
        category.setName(categoryBody.getName());
        try{
            categoryMapper.update(category);
            return ApiResponseUtil.success("update success");

        }catch (Exception e){
            return ApiResponseUtil.error("update failed");
        }
    }
    // if not found, create one
    @Override
    public Category findOrCreateCategory(String categoryName){
        Category category = categoryMapper.findByName(categoryName);
        if(category!=null){
            return category;
        }
        try{
            Category category2 = new Category();
            category2.setName(categoryName.trim());
            category2.setParentCategoryId(0);
            categoryMapper.insert(category2);
            return category2;
        } catch (Exception e) {
            throw new RuntimeException("failed to create new categort.");
        }
    }

    @Override
    public Category findOrCreateCategory(String categoryName, Integer parentCategoryId){
        Category category = categoryMapper.findByName(categoryName);
        if(category!=null){
            return category;
        }
        try{
            Category category2 = new Category();
            category2.setName(categoryName.trim());
            category2.setParentCategoryId(parentCategoryId);
            categoryMapper.insert(category2);
            return category2;
        } catch (Exception e) {
            throw new RuntimeException("failed to create new categort.");
        }
    }

}
