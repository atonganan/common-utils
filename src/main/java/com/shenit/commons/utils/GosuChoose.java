package com.shenit.commons.utils;

import org.apache.commons.lang3.ArrayUtils;

/**
 * Created by jiangnan on 30/06/2017.
 */
public final class GosuChoose {
    /**
     * 获取第一个非空的值
     * @param vals
     * @param <T>
     * @return
     */
    public static <T> T first(T... vals){
        if(ArrayUtils.isEmpty(vals)) return null;
        for(T val : vals){
            if(val != null) return val;
        }
        return null;
    }
}
