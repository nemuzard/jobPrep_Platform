package com.jobprep.jobprep_platform.service.serviceImpl;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.jobprep.jobprep_platform.mapper.NoteMapper;
import com.jobprep.jobprep_platform.mapper.UserMapper;
import com.jobprep.jobprep_platform.model.base.ApiResponse;
import com.jobprep.jobprep_platform.model.entity.Note;
import com.jobprep.jobprep_platform.model.entity.User;
import com.jobprep.jobprep_platform.service.SearchService;
import com.jobprep.jobprep_platform.utils.ApiResponseUtil;
import com.jobprep.jobprep_platform.utils.SearchUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {
    private final NoteMapper noteMapper;
    private final UserMapper userMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String NOTE_SEARCH_CACHE_KEY = "search:note:%s:%d:%d";
    private static final String USER_SEARCH_CACHE_KEY = "search:user:%s:%d:%d";
    private static final String NOTE_TAG_SEARCH_CACHE_KEY = "search:note:tag:%s:%s:%d:%d";
    private static final long CACHE_EXPIRE_TIME = 30;//min

    @Override
    public ApiResponse<List<Note>> searchNotes(String keyword, int page, int pageSize){
        try{
            String cacheKey = String.format(NOTE_SEARCH_CACHE_KEY, keyword, page, pageSize);
            // 1 - try to get from cache
            List<Note> cachedResult = (List<Note>) redisTemplate.opsForValue().get(cacheKey);
            if (cachedResult != null) {
                return ApiResponseUtil.success("Success", cachedResult);
            }

            // 2 - process keyword
            keyword = SearchUtils.preprocessKeyword(keyword);
            int offset = (page - 1) * pageSize;
            
            // search 
            List<Note> notes = noteMapper.searchNotes(keyword, pageSize, offset);
            
            // save to cache 
            redisTemplate.opsForValue().set(cacheKey, notes, CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
            
            return ApiResponseUtil.success("Success", notes);
        } catch (Exception e) {
            log.error("failed", e);
            return ApiResponseUtil.error("failed");
        }
        
    }
    @Override
    public ApiResponse<List<User>> searchUsers(String keyword, int page, int pageSize){
        try{
            String cacheKey = String.format(USER_SEARCH_CACHE_KEY, keyword, page, pageSize);
            List<User> cachedResult = (List<User>) redisTemplate.opsForValue().get(cacheKey);
            if(cachedResult!=null){
                return ApiResponseUtil.success("success", cachedResult);
            }
            int offset = (page - 1) * pageSize;
            List<User> users = userMapper.searchUsers(keyword, pageSize, offset);
            redisTemplate.opsForValue().set(cacheKey, users, CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
            
            return ApiResponseUtil.success("success", users);
        }catch(Exception e){
            log.error("failed",e);
            return ApiResponseUtil.error("failed");
        }
    }

    @Override
    public ApiResponse<List<Note>> searchNotesByTag(String keyword,String tag,int page,int pageSize){
        try {
            String cacheKey = String.format(NOTE_TAG_SEARCH_CACHE_KEY, keyword, tag, page, pageSize);
        
            List<Note> cachedResult = (List<Note>) redisTemplate.opsForValue().get(cacheKey);
            if (cachedResult != null) {
                return ApiResponseUtil.success("success", cachedResult);
            }
            keyword = SearchUtils.preprocessKeyword(keyword);
            int offset = (page - 1) * pageSize;
            List<Note> notes = noteMapper.searchNotesByTag(keyword, tag, pageSize, offset);
            redisTemplate.opsForValue().set(cacheKey, notes, CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
            
            return ApiResponseUtil.success("success", notes);
        } catch (Exception e) {
            log.error("failed", e);
            return ApiResponseUtil.error("failed");
        }
    }
}
