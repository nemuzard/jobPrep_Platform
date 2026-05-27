package com.jobprep.jobprep_platform.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jobprep.jobprep_platform.model.base.ApiResponse;
import com.jobprep.jobprep_platform.model.base.EmptyVO;
import com.jobprep.jobprep_platform.model.dto.collection.CollectionQueryParams;
import com.jobprep.jobprep_platform.model.dto.collection.CreateCollectionBody;
import com.jobprep.jobprep_platform.model.dto.collection.UpdateCollectionBody;
import com.jobprep.jobprep_platform.model.vo.collection.CollectionVO;
import com.jobprep.jobprep_platform.model.vo.collection.CreateCollectionVO;
import com.jobprep.jobprep_platform.service.CollectionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/collections")
@RequiredArgsConstructor
public class CollectionController {
    private final CollectionService collectionService;

    @GetMapping
    public ApiResponse<List<CollectionVO>> getCollections(@Valid CollectionQueryParams params) {
        return collectionService.getCollections(params);
    }

    @PostMapping
    public ApiResponse<CreateCollectionVO> createCollection(@Valid @RequestBody CreateCollectionBody body) {
        return collectionService.createCollection(body);
    }

    @DeleteMapping("/{collectionId}")
    public ApiResponse<EmptyVO> deleteCollection(@PathVariable Integer collectionId) {
        return collectionService.deleteCollection(collectionId);
    }

    @PostMapping("/batch")
    public ApiResponse<EmptyVO> batchUpdateCollection(@Valid @RequestBody UpdateCollectionBody body) {
        return collectionService.batchModifyCollection(body);
    }
}
