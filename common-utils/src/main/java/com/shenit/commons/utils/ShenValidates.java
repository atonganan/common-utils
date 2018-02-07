package com.shenit.commons.utils;

import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Set;

/**
 * 校验用的工具类.
 * Created by jiangnan on 19/07/2017.
 */
public final class ShenValidates {
    private static final String SHOULD_NOT_BE_NULL_HINTS = " should not be null";

    /**
     * 判断两个值是否相等
     * @param a
     * @param b
     * @return
     */
    public static boolean ne(Object a, Object b) {
        return !eq(a,b);
    }

    /**
     * 判断两个值是否相等
     * @param a
     * @param b
     * @return
     */
    public static boolean eq(Object a, Object b){
        if(a instanceof Number && b instanceof Number) {
            return a == b || (a != null && ((Number) a).doubleValue() == ((Number) b).doubleValue());
        }
        return a  == b || (a != null && a.equals(b));
    }

    /**
     * 判断一个值是否在一个数组中出现
     * @param source 源值
     * @param collections 值的集合
     * @param <T>
     * @return
     */
    public static <T> boolean in(T source, T... collections){
        if(ArrayUtils.isEmpty(collections)) return false;
        for(T item : collections){
            if(eq(source,item)) return true;
        }
        return false;
    }


    /**
     * 检查一个数组中的值中是否有任何一个后续的条件值
     * @param vals
     * @param conditions
     * @return
     */
    public static <T> boolean containsAny(T[] vals, T... conditions) {
        for(T val : vals){
            if(ArrayUtils.contains(conditions,val)) return true;
        }
        return false;
    }

    /**
     * 检查一个数组中的值中是否有任何一个后续的条件值
     * @param vals
     * @param conditions
     * @return
     */
    public static <T> boolean containsAny(Collection<T> vals, T... conditions) {
        if(CollectionUtils.isEmpty(vals)) return false;
        Set<T> copy = Sets.newHashSet(vals);
        copy.retainAll(Sets.newHashSet(conditions));
        return CollectionUtils.isNotEmpty(copy);
    }


    /**
     * 判断一个对象是否指定类的子类
     * @param obj
     * @param types
     * @return
     */
    public static boolean instanceOf(Object obj, Class<?>... types) {
        for(Class<?> type : types){
            if(type.isInstance(obj)) return true;
        }
        return false;
    }

    /**
     * 判断数值是否大于指定整形
     * @param base
     * @param toCompare
     * @return
     */
    public static boolean gt(Number base, Number toCompare) {
        if(base == null ) return false;
        if(toCompare == null) return true;
        return base.doubleValue() > toCompare.doubleValue();
    }

    /**
     * 大于或等于判断
     * @param base
     * @param toCompare
     * @return
     */
    public static boolean gte(Number base, Number toCompare) {
        if(base == null ) return false;
        if(toCompare == null) return true;
        return base.doubleValue() >= toCompare.doubleValue();
    }

    /**
     * 检查所有的元素是否被包含在指定的集合中
     * @param matchedLevels
     * @param items
     * @param <T>
     * @return
     */
    public static <T> boolean containsAll(Collection<T> matchedLevels, T... items) {
        if(CollectionUtils.isEmpty(matchedLevels)) return false;
        Set<T> itemSet = Sets.newHashSet(ShenArrays.asList(items));
        itemSet.removeAll(matchedLevels);
        return CollectionUtils.isEmpty(itemSet);
    }


    /**
     * 检查是否其中一组参数的值为空.
     * @param pairs
     */
    public static void notNullOrEmpty(Object... pairs) {
        for(int i=0;i<pairs.length;i+=2){
            Object val = ShenArrays.get(pairs,i);
            String hints = ShenArrays.getString(pairs,i+1);
            if(val instanceof String && StringUtils.isEmpty((CharSequence) val))
                throw new IllegalArgumentException(hints);
            else if(val == null)
                throw new IllegalArgumentException(hints);
        }
    }

    /**
     * 获取首个不为空的值
     * @param vals
     * @param <T>
     * @return
     */
    public static <T> T anyOf(T... vals){
        if(ArrayUtils.isEmpty(vals)) return null;
        for(T val : vals){
            if(val != null) return val;
        }
        return null;
    }

    /**
     * 获取首个不为空的值
     * @param vals
     * @return
     */
    public static String anyNotBlank(String... vals){
        if(ArrayUtils.isEmpty(vals)) return null;
        for(String val : vals){
            if(StringUtils.isNotBlank(val)) return val;
        }
        return null;
    }

    /**
     * 获取首个不为空的值
     * @param vals
     * @return
     */
    public static String anyNotEmpty(String... vals){
        if(ArrayUtils.isEmpty(vals)) return null;
        for(String val : vals){
            if(StringUtils.isNotEmpty(val)) return val;
        }
        return null;
    }
}
