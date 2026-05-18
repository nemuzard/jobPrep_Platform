package com.jobprep.jobprep_platform.service;

import org.springframework.transaction.annotation.Transactional;

import com.jobprep.jobprep_platform.model.base.ApiResponse;
import com.jobprep.jobprep_platform.model.base.EmptyVO;
import com.jobprep.jobprep_platform.model.dto.collection.CollectionQueryParams;

import com.jobprep.jobprep_platform.model.dto.collection.UpdateCollectionBody;
import com.jobprep.jobprep_platform.model.vo.collection.CollectionVO;
import com.jobprep.jobprep_platform.model.vo.collection.CreateCollectionVO;
import com.jobprep.jobprep_platform.model.dto.collection.CreateCollectionBody;
import java.util.List;

@Transactional
public interface CollectionService {

    /**
     *  get collections 
     * @param queryParams
     * @return
     */
    ApiResponse<List<CollectionVO>> getCollections(CollectionQueryParams queryParams);

    /**
     * create collection
     * @param requestBody 
     * @return
     */
    ApiResponse<CreateCollectionVO> createCollection(CreateCollectionBody requestBody);

    ApiResponse<EmptyVO> deleteCollection(Integer collectionId);

    ApiResponse<EmptyVO> batchModifyCollection(UpdateCollectionBody requestBody);
    
}
