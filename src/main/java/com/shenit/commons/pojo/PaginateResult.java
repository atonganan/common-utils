package com.shenit.commons.pojo;

import com.shenit.commons.utils.Payload;

import java.util.List;

/**
 * 分页结果.
 * Created by jiangnan on 20/07/2017.
 */
public class PaginateResult<R,P> {
    public List<R> list;
    public Payload extras;
    public P paginate;

    public PaginateResult(List<R> list, P paginate){
        this.list = list;
        this.paginate = paginate;
    }
}
