package com.jobprep.jobprep_platform.service.serviceImpl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jobprep.jobprep_platform.mapper.CollectionNoteMapper;
import com.jobprep.jobprep_platform.service.CollectionNoteService;

@Service
public class CollectionNoteServiceImpl implements CollectionNoteService{
    @Autowired
    private CollectionNoteMapper collectionNoteMapper;

    @Override
    public Set<Integer> findUserCollectedNoteIds(Long userId, List<Integer> noteIds){
        List<Integer> userCollectedNoteIds = collectionNoteMapper.findUserCollectedNoteIds(userId, noteIds);
        return new HashSet<>(userCollectedNoteIds);

    }
}
