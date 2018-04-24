package net.gradle.commons.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.function.IntFunction;


public final class ShenArrays {

    public static void shuffle(int[] array) {
        Random rnd = ThreadLocalRandom.current();
        for (int i = array.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            int a = array[index];
            array[index] = array[i];
            array[i] = a;
        }
    }

    /**
     * 转换为长整型的数组
     * @param array 原数组
     * @param <T> 源类型
     * @return
     */
    public static <T> String[] strs(T[] array){
        return transfer(array,String::valueOf, String[]::new);
    }


    /**
     * 转换为整形的数组
     * @param array 原数组
     * @param <T> 源类型
     * @return
     */
    public static <T> Integer[] ints(T[] array){
        return transfer(array,
                v -> {
                    if(v == null) return 0;
                    else if(v instanceof Number) return ((Number)v).intValue();
                    else return NumberUtils.toInt(ShenStrings.str(v),0);
                }, Integer[]::new);
    }

    /**
     * 转换为字符串的数组
     * @param array 原数组
     * @param <T> 源类型
     * @return
     */
    public static <T> Long[] longs(T[] array){
        return transfer(array,
                v -> {
                    if(v == null) return 0l;
                    else if(v instanceof Number) return ((Number)v).longValue();
                    else return NumberUtils.toLong(ShenStrings.str(v),0l);
                }, Long[]::new);
    }

    /**
     * 转换为另一个类型的数组
     * @param array 原数组
     * @param func 转换函数
     * @param generator 数组生成函数
     * @param <T> 源类型
     * @param <V> 目标类型
     * @return
     */
    public static <T,V> V[] transfer(T[] array, Function<T,V> func, IntFunction<V[]> generator){
        return Arrays.stream(array).map(func).toArray(generator);
    }

    public static void shuffle(byte[] array) {
        Random rnd = ThreadLocalRandom.current();
        for (int i = array.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            byte a = array[index];
            array[index] = array[i];
            array[i] = a;
        }
    }
    /**
     * Shuffle an array directly.
     * @param array
     * @param <T>
     */
    public static <T> void shuffle(T[] array){
        Random rnd = ThreadLocalRandom.current();
        for (int i = array.length - 1; i > 0; i--)
        {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            T a = array[index];
            array[index] = array[i];
            array[i] = a;
        }
    }

    /**
     * Filter not empty array items.
     * @param array
     * @param <T>
     * @return
     */
    public static <T> T[] notEmpty(T[] array){
        if(ArrayUtils.isEmpty(array)) return array;
        List<T> items = new ArrayList<>();
        for(int i=0;i<array.length; i++){
            if(array[i] != null) items.add(array[i]);
        }
        return items.toArray((T[])Array.newInstance(
                array.getClass().getComponentType(),
                items.size()));
    }



    /**
     * Get data from index.
     * @param data
     * @param index
     * @return
     */
    public static byte get(byte[] data, int index) {
        return data == null || data.length <= index ? -1 :  data[index];
    }

    /**
     * 获取数组中第i个元素.如果超出范围返回默认值，如果为负数尝试进行一次修正.
     * @param fields
     * @param i
     * @param <T>
     * @return
     */
    public static <T> T get(T[] fields, int i) {
        return get(fields, i, null);
    }

    /**
     * 获取数组中第i个元素.如果超出范围返回默认值，如果为负数尝试进行一次修正.
     * @param fields
     * @param i
     * @param defaultVal
     * @param <T>
     * @return
     */
    public static <T> T get(T[] fields, int i, T defaultVal) {
        if(ArrayUtils.isEmpty(fields)) return defaultVal;
        //处理i的范围
        if(i < 0) i = Math.abs(fields.length + i);    //如果继续是负数就让他去吧。。。。
        return i >= fields.length ? defaultVal : fields[i];
    }

    /**
     * 检查数组的所有内容是否都为空
     * @param vals
     * @return
     */
    public static boolean isEmpty(Object[] vals) {
        if(ArrayUtils.isEmpty(vals)) return true;
        boolean result = true;
        for(Object val : vals){
            if(val instanceof Collection) result &= CollectionUtils.isEmpty((Collection<?>) val);
            else result &= val == null;
        }
        return result;
    }

    /**
     * 获取Long值
     * @param result
     * @param i 索引
     * @param l 默认值
     */
    public static Long getLong(Object[] result, int i, Long l) {
        return NumberUtils.toLong(ShenStrings.str(get(result,i)),l);
    }

    /**
     * 获取字符串
     * @param result
     * @param i
     * @return
     */
    public static String getString(Object[] result, int i) {
        return ShenStrings.str(get(result,i));
    }

    /**
     * 把数组里面的内容分解成hash
     * @param kvs key1, val1, key2, val2... 这样的数组
     * @return 返回一个hash
     */
    public static <K,V> Map<K,V> extract(Object... kvs) {
        Map<K,V> result = Maps.newHashMap();
        for(int i=0;i<kvs.length;i++){
            result.put(
                    (K) kvs[i],
                    i+1 >= kvs.length ? null : (V) kvs[i+1]);
        }
        return result;
    }

    /**
     * 变成列表
     * @param objs
     * @param <T>
     * @return
     */
    public static <T> List<T> asList(T[] objs) {
        List<T> list = Lists.newArrayList();
        for(T obj : objs) list.add(obj);
        return list;
    }

    /**
     * 把一个二维数组扁平化成一维数组
     * @param vals
     * @return
     */
    public static long[] flatten(long[][] vals) {
        int length =0;
        for(int i=0;i<vals.length;i++){
            length += vals[i].length;
        }
        long[] result = new long[length];
        int index = 0;
        for(long[] vs : vals){
            System.arraycopy(vs,0,result,index,vs.length);
            index += vs.length;
        }
        return result;
    }

    /**
     * 生成数组
     * @param from From number inclusive
     * @param to To number inclusive
     * @return
     */
    public static int[] generate(int from, int to) {
        int[] result = new int[to - from + 1];
        for(int i=from; i <= to;i++){
            result[i - from] = i;
        }
        return result;
    }

    /**
     * 生成数组
     * @param from From number inclusive
     * @param to To number inclusive
     * @return
     */
    public static long[] generate(long from, long to) {
        long[] result = new long[(int) (to - from + 1)];
        for(long i = from; i <= to;i++){
            result[(int) (i - from)] = i;
        }
        return result;
    }

    /**
     * 划分成n行
     * @param vals
     * @param columns
     * @param rows
     * @return
     */
    public static long[][] split(long[] vals,int columns,int rows) {
        long[][] result = new long[rows][columns];
        int index = 0;
        for(int i=0;i<rows;i++){
            int rowLength = Math.min(vals.length,index+columns);
            result[i] = new long[columns];
            System.arraycopy(result[i],0,
                    ArrayUtils.subarray(vals,index, rowLength),0,
                    rowLength - index);
            index +=  columns;
        }
        return result;
    }

    /**
     * 展开数组里的array或者collection
     * @param vals
     * @return
     */
    public static Object[] expand(Object[] vals) {
        if(vals == null) return  vals;
        int length = vals.length;
        for(Object val : vals){
            if(val == null) continue;
            else if(val instanceof Collection) length += CollectionUtils.size(val) - 1;
            else if(val.getClass().isArray()) length += ArrayUtils.getLength(val) - 1;
        }
        Object[] newVals = new Object[length];
        int index = 0;
        for(int i =0;i<vals.length;i++){
            Object val = vals[i];
            if(val instanceof Collection) {
                Object[] tmp = ((Collection<?>) val).toArray();
                for(int j=0;j<tmp.length;j++) newVals[index++] = tmp[j];
            }
            else if(val.getClass().isArray()) {
                Object[] tmp = (Object[]) val;
                for(int j=0;j<tmp.length;j++) newVals[index++] = tmp[j];
            }else newVals[index++] = val;
        }
        return newVals;
    }

    public static <T> Set<T> asSet(T[] objs) {
        Set<T> set = Sets.newHashSet();
        for(T obj : objs) set.add(obj);
        return set;
    }
}