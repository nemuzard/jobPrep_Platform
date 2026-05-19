package com.jobprep.jobprep_platform.service.serviceImpl;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jobprep.jobprep_platform.annotation.NeedLogin;
import com.jobprep.jobprep_platform.mapper.CollectionMapper;
import com.jobprep.jobprep_platform.mapper.CollectionNoteMapper;
import com.jobprep.jobprep_platform.mapper.NoteMapper;
import com.jobprep.jobprep_platform.model.base.ApiResponse;
import com.jobprep.jobprep_platform.model.dto.collection.CollectionQueryParams;
import com.jobprep.jobprep_platform.model.dto.collection.CreateCollectionBody;
import com.jobprep.jobprep_platform.model.entity.Collection;
import com.jobprep.jobprep_platform.model.vo.collection.CollectionVO;
import com.jobprep.jobprep_platform.model.vo.collection.CreateCollectionVO;
import com.jobprep.jobprep_platform.scope.RequestScopeData;
import com.jobprep.jobprep_platform.service.CollectionService;
import com.jobprep.jobprep_platform.utils.ApiResponseUtil;

@Service
public class CollectionServiceImpl implements CollectionService {
    @Autowired
    private RequestScopeData requestScopeData;
    @Autowired
    private CollectionMapper collectionMapper;

    @Autowired
    private CollectionNoteMapper collectionNoteMapper;
    @Autowired
    private NoteMapper noteMapper;

    @Override
    public ApiResponse<List<CollectionVO>> getCollections(CollectionQueryParams queryParams){
        // collection list 
        List<Collection> collections = collectionMapper.findByCreatorId(queryParams.getCreatorId());
        List<Integer> collectionIds = collections.stream().map(Collection::getCollectionId).toList();
        final Set<Integer> collectedNoteIdCollectionIds;

        // Check whether noteId was passed in
        if (queryParams.getNoteId()!=null){
            collectedNoteIdCollectionIds = collectionNoteMapper.filterCollectionIdsByNoteId(queryParams.getNoteId(),collectionIds);

        }else{
            collectedNoteIdCollectionIds=Collections.emptySet();
        }
        // Map collections to a CollectionVO list
        List<CollectionVO> collectionVOList = collections.stream().map(collection ->{
            CollectionVO collectionVO = new CollectionVO();
            BeanUtils.copyProperties(collection, collectionVO);

            // check if noteId was passed in, and current collection includes this note
            if(queryParams.getNoteId() == null){
                return collectionVO;
            }
            // set note status in the collection list 
            CollectionVO.NoteStatus noteStatus = new CollectionVO.NoteStatus();

            noteStatus.setIsCollected(collectedNoteIdCollectionIds.contains(collection.getCollectionId()));
            noteStatus.setNoteId(queryParams.getNoteId());
            collectionVO.setNoteStatus(noteStatus);
            return collectionVO;
        }).toList();
        return ApiResponseUtil.success("Collection list returned successfully",collectionVOList);
    }

    @Override
    @NeedLogin
    public ApiResponse<CreateCollectionVO> createCollection(CreateCollectionBody requestBody){
        Long creatorId = requestScopeData.getUserId();
        Collection collection = new Collection();
        
    }
  

}
