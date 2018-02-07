package com.shenit.commons.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;

/**
 * Map utils
 * Created by jgnan on 27/12/2016.
 */
public final class GosuMaps {
    private static final Logger LOG = LoggerFactory.getLogger(GosuMaps.class);

    /**
     * Put all key - value pairs to map.
     * @param map Map to operate on
     * @param kvs Key value pairs
     * @return
     */
    public static <K extends Comparable, V> Map<K, V> putAll(Map<K, V> map, Object...kvs){
        if(map == null || kvs == null || kvs.length == 0) return map;
        for(int i=0;i<kvs.length;i+=2){
            map.put((K) kvs[i],i + 1 < kvs.length ? (V) kvs[i+1] : null);
        }
        return map;
    }

    /**
     * 合并两个Map
     * @param base
     * @param toMerge
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K,V> Map<K,V> merge(Map<K,V> base, Map<K,V> toMerge){
        if(MapUtils.isEmpty(base)) return toMerge;
        if(MapUtils.isEmpty(toMerge)) return base;

        //防止污染参数
        Map<K,V> newMap = Maps.newLinkedHashMap(base);
        newMap.putAll(toMerge);
        return newMap;
    }

    /**
     * 递增值
     * @param map
     * @param field
     * @param val
     * @param <K>
     * @return
     */
    public static <K> Map<K,Double> incr(Map<K,Double> map, K field, Double val){
        return doIncr(map,field,val,Double.class);
    }


    /**
     * 递增值
     * @param map
     * @param field
     * @param val
     * @param <K>
     * @return
     */
    public static <K> Map<K,Integer> incr(Map<K,Integer> map, K field, Integer val){
        return doIncr(map,field,val,Integer.class);
    }


    /**
     * 递增值
     * @param map
     * @param field
     * @param val
     * @param <K>
     * @return
     */
    public static <K> Map<K,Long> incr(Map<K,Long> map, K field, Long val){
        return doIncr(map,field,val,Long.class);
    }

    /**
     * 把两个数组压缩成一个hash对象
     * @param fields 键数组
     * @param vals 值数组
     * @return
     */
    public static <K,V> Map<K,V> zip(K[] fields, V[] vals) {
        Map<K,V> result = new LinkedHashMap<>();
        for(int i = 0 ;i< fields.length;i++){
            result.put(fields[i], GosuArrays.get(vals,i,null));
        }
        return result;
    }

    /**
     * 把两个数组压缩成一个hash对象
     * @param fields 键数组
     * @param vals 值数组
     * @return
     */
    public static <K,V> Map<K,V> zip(Iterable<K> fields, Iterable<V> vals) {
        Map<K,V> result = new LinkedHashMap<>();
        Iterator<K> kIter = fields.iterator();
        Iterator<V> vIter = vals.iterator();
        for(;kIter.hasNext();){
            K k = kIter.next();
            V v = vIter.hasNext() ? vIter.next() : null;
            result.put(k,v);
        }
        return result;
    }

    /**
     * 把两个数组压缩成一个hash对象
     * @param fields 键数组
     * @param vals 值数组
     * @return
     */
    public static <K,V> Map<K,V> zip(List<K> fields, List<V> vals) {
        HashMap<K,V> result = new HashMap<>();
        for(int i = 0 ;i< fields.size();i++){
            result.put(fields.get(i), IterableUtils.get(vals,i));
        }
        return result;
    }

    /**
     * 获取一个键的值，如果没有则使用默认值，并且把最终结果回填到map中
     * @param map
     * @param key
     * @param defaultValue Default value to use if key's value not exists
     * @return
     */
    public static <K,V> V getSet(Map<K, V> map, K key, V defaultValue) {
        if(map == null) return defaultValue;
        V val = map.getOrDefault(key,defaultValue);
        if(map.get(key) == null) map.put(key,val);  //回填内容
        return val;
    }

    public static <T> T get(Map<String,Object> map, T defaultVal, Object... paths){
        if(paths.length == 0) return defaultVal;

        Object firstPart = paths[0];
        paths = ArrayUtils.subarray(paths,1,paths.length);

        Object data = MapUtils.getObject(map, GosuStrings.str(firstPart));
        if(data == null) return defaultVal;

        for(Object path : paths){
            if (LOG.isTraceEnabled()) LOG.trace("[get([map, defaultVal, paths])] Trying to parse part -> {}",path);
            if(data instanceof Map) {
                data = MapUtils.getObject((Map<Object,Object>)data, GosuStrings.str(path));
            }else if(path instanceof Number){
                int num = ((Number)path).intValue();

                if(data.getClass().isArray()){
                    data = GosuArrays.get((Object[]) data,num);
                }else if(data instanceof Collection){
                    data = GosuCollections.get(((Collection)data),((Number)path).intValue());
                }
            }else{
                return defaultVal;
            }
            if(data == null) return defaultVal;
        }
        try {
            if (LOG.isTraceEnabled()) LOG.trace("[get([map, defaultVal, paths])] Final result -> {}", data);
            return data == null ? defaultVal : (T) data;
        }catch(ClassCastException exp){
            if (LOG.isDebugEnabled()) LOG.debug("[get([map, defaultVal, paths])] Could not parse to type T due to exception");
        }
        return defaultVal;
    }

    /**
     * 获取所有指定keys的值并返回集合
     * @param map
     * @param keys
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K,V> List<V> getAll(Map<K,V> map, Collection<K> keys){
        List<V> result = Lists.newArrayList();
        if(map == null) return result;
        for(K key : keys) {
            if(!map.containsKey(key)) continue;
            result.add(map.get(key));
        }
        return result;
    }

    /**
     * 根据Keys过滤对应的键值对
     * @param map
     * @param keys
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> Map<K,V> entries(Map<K, V> map, Collection<K> keys) {
        if(CollectionUtils.isEmpty(keys)) return map;
        Map<K,V> result = Maps.newHashMap();
        for(K k : keys) {
            if (map.containsKey(k))
                result.put(k, map.get(k));
        }
        return result;
    }


    /**
     * 实现累加功能
     * @param map
     * @param field
     * @param val
     * @param type
     * @param <K>
     * @param <V>
     * @return
     */
    private static <K,V extends Number> Map<K,V> doIncr(Map<K, V> map, K field, V val, Class<V> type) {
        if(map ==null) return map;
        map.put(field, GosuNumbers.add(type,map.getOrDefault(field, GosuNumbers.convert(BigDecimal.ZERO,type)),val));
        return map;
    }
}