package com.jobprep.jobprep_platform.utils;
import com.huaban.analysis.jieba.JiebaSegmenter;

import java.util.List;

import org.springframework.util.StringUtils;


public class SearchUtils {
    private static final JiebaSegmenter segmenter = new JiebaSegmenter();
    
    public static String preprocessKeyword(String keyword){
        if (!StringUtils.hasText(keyword)) {
            return "";
        }
        // 1. Convert punctuation/symbols to spaces
        keyword = keyword.replaceAll("[\\p{P}\\p{S}]", " ");

        // 2. Normalize multiple spaces
        keyword = keyword.replaceAll("\\s+", " ").trim();

        // 3. Segment Chinese and preserve English tokens
        List<String> words = segmenter.sentenceProcess(keyword);
        
        // 4. Join tokens
        return String.join(" ", words).trim();
    }

    public static int calculateOffset(int page, int pageSize){
        return Math.max(0,(page-1)*pageSize);
    }
}
