package com.shenit.commons.pojo;

/**
 * Created by jiangnan on 20/07/2017.
 */
public class Pagination {
    //开始获取的记录行数
    public long fromIndex;
    //获取到的记录行数
    public long toIndex;
    //总记录条数
    public long totalCount;
    //获取记录数
    public int limit;
    //页数
    public int page;
    //总页数
    public int totalPage;

    public Pagination(int page, int limit, long total){
        this(page,limit);
        totalCount(total);
    }

    public Pagination(int page, int limit){
        paginate(page,limit);
    }

    public Pagination paginate(int page, int limit){
        this.page = page;
        this.limit = limit;
        this.fromIndex = this.page * this.limit;
        toIndex();
        return this;
    }

    public Pagination toIndex(){
        this.toIndex = Math.min(fromIndex + limit,totalCount);
        return this;
    }

    public Pagination totalCount(long total){
        this.totalCount = total;
        this.totalPage = (int) ((total / limit) + 1);
        toIndex();
        return this;
    }
}
