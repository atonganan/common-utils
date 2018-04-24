package net.gradle.commons.pojo;

/**
 * 下拉刷新类的分页
 * Created by jiangnan on 20/07/2017.
 */
public class PullPagination {
    public Object fromId;
    //总记录条数
    public long totalCount;
    //获取记录数
    public int limit;
    public boolean hasMore;

    public PullPagination(Object fromId, int limit, long total,boolean hasMore){
        this.fromId = fromId;
        this.limit = limit;
        this.totalCount = total;
        this.hasMore = hasMore;
    }

}
