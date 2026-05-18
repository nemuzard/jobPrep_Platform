package com.jobprep.jobprep_platform.service;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Set;

@Transactional
public interface CollectionNoteService {
    Set<Integer> findUserCollectedNoteIds(Integer userId, List<Integer> noteIds);
}
