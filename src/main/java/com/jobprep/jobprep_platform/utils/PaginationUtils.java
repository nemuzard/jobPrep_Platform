package com.jobprep.jobprep_platform.utils;



public class PaginationUtils {
    

    /**
     *  calculate offset based on page number and page size 
     * @param page current page number ---- start from 1
     * @param pageSize 
     * @return
     */
    public static int calculateOffset(int page, int pageSize){
        if (page<1){
            throw new IllegalArgumentException("Invalid page number.");
        }
        if (pageSize<1){
            throw new IllegalArgumentException("Invalid page size ");
        }
        return (page-1)*pageSize;
    }
}
