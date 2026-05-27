package com.jobprep.jobprep_platform.model.base;

public class PaginationApiResponse<T> extends ApiResponse<T> {
    
    private final Pagination pagination;

    /**
     * contructor
     * @param code
     * @param msg
     * @param data
     * @param pagination
     */
    public PaginationApiResponse(int code, String msg, T data, Pagination pagination){
        super(code, msg, data);// ApiResponse constructor
        this.pagination = pagination;
    }

    /**
     * 
     * @return
     */
    public Pagination getPagination(){
        return pagination;
    }

    /**
     * @deprecated use {@link #getPagination()}.
     */
    @Deprecated
    public Pagination gePagination(){
        return pagination;
    }

}
