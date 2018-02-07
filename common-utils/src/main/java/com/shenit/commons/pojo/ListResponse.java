package com.shenit.commons.pojo;

import com.google.common.collect.Lists;

import java.util.Collection;

/**
 * 集合对象
 * Created by jiangnan on 12/06/2017.
 */
public class ListResponse<T> {
    public Collection<?> list;
    public ListResponse(){this.list= Lists.newArrayList();}
    public ListResponse(Collection<?> l){this.list= l;}
}
