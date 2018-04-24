package net.gradle.commons.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Collection utils.
 * Created by jgnan on 05/11/2016.
 */
public final class ShenCollections {


    /**
     * 过滤空的内容
     * @param coll
     * @param supplier
     * @param <C>
     * @param <T>
     * @return
     */
    public static <C extends Collection<T>,T> C excludeNull(Collection<T> coll, Supplier<C> supplier){
        return filter(coll,item -> item != null,supplier);
    }

    /**
     * 过滤空的内容
     * @param coll
     * @param collector
     * @param <C>
     * @param <T>
     * @return
     */
    public static <C extends Collection<T>,T> C excludeNull(Collection<T> coll, Collector<T,?,C> collector){
        return filter(coll,item -> item != null,collector);
    }

    /**
     * 过滤空的内容
     * @param coll
     * @param supplier
     * @param <C>
     * @param <T>
     * @return
     */
    public static <C extends Collection<T>,T> C filter(Collection<T> coll, Predicate<T> predicate, Supplier<C> supplier){
        if(CollectionUtils.isEmpty(coll)) return (C) coll;
        return coll.stream().filter(predicate).collect(Collectors.toCollection(supplier));
    }

    /**
     * 过滤空的内容
     * @param coll
     * @param collector
     * @param <C>
     * @param <T>
     * @return
     */
    public static <C extends Collection<T>,T> C filter(Collection<T> coll, Predicate<T> predicate, Collector<T,?,C> collector){
        if(CollectionUtils.isEmpty(coll)) return (C) coll;
        return coll.stream().filter(predicate).collect(collector);
    }

    /**
     * 转换为其他值的列表.
     * @param coll
     * @param func
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T,R> Set<R> collectSet(Collection<T> coll, Function<T,R> func){
        if(CollectionUtils.isEmpty(coll)) return null;
        return coll.parallelStream().map(func).collect(Collectors.toSet());
    }

    /**
     * 按照分类项进行汇总
     * @param coll 集合
     * @param defaultValue 汇总时的默认值
     * @param keyProvider 键提供者
     * @param reduceProc 汇总过程
     * @param <T> 集合类
     * @param <K> 键类
     * @param <V> 汇总值类
     * @return
     */
    public static <T,K,V> Map<K,V> reduce(Collection<T> coll, V defaultValue, Function<T,K> keyProvider,BiFunction<V,T,V> reduceProc){
        if(CollectionUtils.isEmpty(coll)) return null;
        Map<K,V> maps = Maps.newLinkedHashMap();
        for(T t : coll){
            K key = keyProvider.apply(t);
            V val = reduceProc.apply(maps.getOrDefault(key,defaultValue),t);
            maps.put(key,val);
        }
        return maps;
    }


    /**
     * 转换为其他值的列表.
     * @param coll
     * @param func
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T,R> List<R> collect(List<T> coll, Function<T,R> func){
        if(CollectionUtils.isEmpty(coll)) return Lists.newArrayList();
        return coll.stream().map(func).collect(Collectors.toList());
    }

    /**
     * 转换为其他值的列表.
     * @param coll
     * @param func
     * @param filter 移除空对象
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T,R> List<R> collect(List<T> coll, Function<T,R> func,Predicate<R> filter){
        if(CollectionUtils.isEmpty(coll)) return Lists.newArrayList();
        Stream<R> stream = coll.stream().map(func);
        if(filter != null) stream = stream.filter(filter);
        return stream.collect(Collectors.toList());
    }

    /**
     * Get item of specific index.
     * @param coll List of objects
     * @param index Index to get
     * @param <T>
     * @return If index is out of range, it'll be fixed within the range
     */
    public static <T> T get(Collection<T> coll, int index){
        if(CollectionUtils.isEmpty(coll)) return null;
        int size = CollectionUtils.size(coll);
        if(index < 0) index += size;
        if(index >= size) return null;  //超出索引大小
        if(!ShenNumbers.between(index,0,size)) index = ShenNumbers.fixed(index,size);
        if(!ShenNumbers.between(index,0,size)) return null; //only fixed once
        return IterableUtils.get(coll,index);
    }

    /**
     * 转换为所索引类型.
     * @param coll
     * @param keyFunc
     * @param itemFunc
     * @param <K>
     * @param <V>
     * @return
     */
    public static <T,K,V> Map<K,V> map(Collection<T> coll, Function<T,K> keyFunc, Function<T,V> itemFunc){
        return coll == null ? null : coll.parallelStream().collect(Collectors.toMap(keyFunc, itemFunc,(a,b) -> b));
    }

    /**
     * 转换为所索引类型.
     * @param coll
     * @param keyFunc
     * @param itemFunc
     * @param mergeFunc  当有冲突的时候使用的函数
     * @param <K>
     * @param <V>
     * @return
     */
    public static <T,K,V> Map<K,V> map(Collection<T> coll, Function<T,K> keyFunc, Function<T,V> itemFunc, BinaryOperator<V> mergeFunc){
        return coll == null ? null : coll.parallelStream().collect(Collectors.toMap(keyFunc, itemFunc,mergeFunc));
    }

    /**
     * 转换为所索引类型.
     * @param coll
     * @param func
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K,V> Map<K,V> map(Collection<V> coll, Function<V,K> func){
        return map(coll,func, (item) -> item);
    }


    /**
     * Get item of specific index.
     * @param list List of objects
     * @param index Index to get
     * @param <T>
     * @return If index is out of range, it'll return null
     */
    public static final <T> T getWithin(Collection<T> list, int index){
        if(CollectionUtils.isEmpty(list)) return null;
        int size = CollectionUtils.size(list);
        if(!ShenNumbers.between(index,0,size)) return null;
        return IterableUtils.get(list,index);
    }

    /**
     * 判断是否整个集合中的所有东西都是空的
     * @param iters
     * @return
     */
    public static boolean allEmpty(Iterable<?> iters) {
        if(iters == null) return true;

        for(Object obj : iters){
            if(obj != null) return false;
        }
        return true;
    }

    /**
     * 根据索引键进行索引统计.
     * @param coll 集合
     * @param keyFunc 获取索引键的值
     * @param accumulateFunc 统计逻辑
     * @param initVal 统计的初始值
     * @param <K>
     * @param <V>
     * @param <T>
     * @return
     */
    public static <K,V,T> Map<K,V> accumulate(Collection<T> coll, Map<K,V> total, Function<T,K> keyFunc, BiFunction<T,V,V> accumulateFunc, V initVal){
        Map<K,V> results = total == null ? Maps.newHashMap() : total;
        K hint;
        V result;
        for(T record : coll){
            hint = keyFunc.apply(record);
            result = results.getOrDefault(hint,initVal);
            results.put(hint,accumulateFunc.apply(record,result));
        }
        return results;
    }

    public static <T> Set<T> fillTo(Set<T> coll, Set<T> another, int size) {
        if (coll.size() < size) {
            for (Iterator iter = another.iterator(); iter.hasNext(); ) {
                coll.add((T) iter.next());
                if (coll.size() == size) break;
            }
        }
        return coll;
    }

    /**
     * 随机获取一个
     * @param coll
     * @param <T>
     * @return
     */
    public static <T> T random(Collection<T> coll) {
        return get(random(coll,1),0);
    }

    /**
     * 随机获取一个实例
     * @param coll 集合
     * @param num  获取数量 随机抽取第几个
     */
    public static <T> Collection<T> random(Collection<T> coll, Integer num) {
        if(coll.isEmpty()) return null;
        Random rand = new Random(System.currentTimeMillis());
        int size = CollectionUtils.size(coll);
        if(num == null) num =1;
        //随机获取多个
        List<T> toSort = Lists.newArrayList(coll);
        Collections.shuffle(toSort);
        return sub(toSort,0,num);
    }

    /**
     * 获取子列表
     * @param list
     * @param offset
     * @param <T>
     * @return
     */
    public static <T> List<T> sub(List<T> list, int offset){
        return sub(list,offset,null);
    }

    /**
     * 获取一个子集
     * @param list 列表
     * @param offset 开始位置
     * @param fetchSize 获取个数
     * @param <T>
     * @return 如果offset为负数，会尝试转成正数. 如果offset > list的大小，则返回空;
     * 如果fetchSize大于可获取个数，获取所有的元素， 否则获取指定个元素
     */
    public static <T> List<T> sub(List<T> list, int offset, Integer fetchSize){
        List<T> sub = Lists.newArrayList();
        int size = CollectionUtils.size(list);
        if(size < 1) return sub;
        if(offset < 0) offset += size;
        offset = ShenNumbers.fixed(offset,0,size - 1);
        int toIndex= fetchSize == null ? size - 1 : Math.min(offset+fetchSize,size);
        return list.subList(offset,toIndex);
    }

    /**
     * 抽取第一个元素
     * @param col
     * @param <T>
     * @return
     */
    public static <T> T peek(Collection<T> col) {
        return col.stream().findFirst().orElse(null);
    }


    /**
     * 把一个2围的数组扁平化
     * @param items
     * @param <V>
     * @return
     */
    public static <V> List<V> flatten(List<Collection<V>> items) {
        List<V> result = Lists.newArrayList();
        for(Collection<V> is : items){
            result.addAll(is);
        }
        return result;
    }

    /**
     * 重复加载指定的集合元素 N 次到指定的集合中
     * @param coll 集合
     * @param n 加载次数
     * @param items 加载元素
     * @param <T>
     * @return
     */
    public static <C extends Collection<T>, T> C fill(C coll, int n, T... items) {
        for(int i=0; i < n; i++){
            for(T item : items) coll.add(item);
        }
        return coll;
    }
}
