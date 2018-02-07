package com.shenit.commons.utils;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * Number utilities for Meme project
 * Created by jgnan on 05/11/2016.
 */
public final class GosuNumbers {
    /**
     * 判斷是否數字類型
     * @param type
     * @return
     */
    public static boolean isNumberType(Class<?> type){
        return Number.class.isAssignableFrom(type) ||
                int.class.isAssignableFrom(type) ||
                short.class.isAssignableFrom(type) ||
                long.class.isAssignableFrom(type) ||
                byte.class.isAssignableFrom(type) ||
                float.class.isAssignableFrom(type) ||
                double.class.isAssignableFrom(type);
    }

    /**
     * 判斷是否數字類型
     * @param type
     * @return
     */
    public static boolean isIntegerType(Class<?> type){
        return Number.class.isAssignableFrom(type) ||
                int.class.isAssignableFrom(type) ||
                short.class.isAssignableFrom(type) ||
                long.class.isAssignableFrom(type) ||
                byte.class.isAssignableFrom(type);
    }

    /**
     * 判断是否浮点类型
     * @param type
     * @return
     */
    public static boolean isDoubleType(Class<?> type){
        return Number.class.isAssignableFrom(type) ||
                float.class.isAssignableFrom(type) ||
                double.class.isAssignableFrom(type);
    }

    /**
     * Check whether value within specific range
     * @param val Value to check
     * @param ranges Ranges, if only one given, check whether within size.
     * @return
     */
    public static boolean between(int val, int... ranges){
        if(ranges == null || ranges.length == 0) return true;
        if(ranges.length == 1) return val <= ranges[0] && val >= 0;
        int max = Arrays.stream(ranges).max().getAsInt();
        int min = Arrays.stream(ranges).min().getAsInt();
        return min <= val && val <= max;
    }

    /**
     * Ensure value within [min(ranges),max(ranges))
     * @param val
     * @param ranges
     * @return
     */
    public static int fixed(int val, int... ranges) {
        int min = 0;
        int max = 0;
        if(ranges.length == 1) return Math.min(val,ranges[0]);
        max = Arrays.stream(ranges).max().getAsInt();
        min = Arrays.stream(ranges).min().getAsInt();
        if(val < min) val= min;
        else if(val > max) val = max;
        return val;
    }

    /**
     * Ensure value within [min(ranges),max(ranges))
     * @param val
     * @param ranges
     * @return
     */
    public static double fixed(double val, Double... ranges) {
        Double min = null;
        Double max = null;
        if(ranges.length ==0) return val;
        if(ranges.length == 1) return Math.min(val,ranges[0] == null ? 0d : ranges[0]);
        for(Double item : ranges){
            if(item == null) continue;
            if(max == null) max = item;
            if(min == null) min = item;
            if(max != null) max = Math.max(max, item);
            if(min != null) min = Math.min(min, item);
        }
        if(max != null) val = Math.min(max, val);
        if(min != null) val = Math.max(min, val);
        return val;
    }

    /**
     * 变成2位小数的值.
     * @param val
     *
     * @return
     */
    public static String currencyFormat(Number val){
        return scale(val, 2, BigDecimal.ROUND_FLOOR).toPlainString();
    }

    /**
     * 变成金额格式.
     * @param val
     *
     * @return
     */
    public static BigDecimal toCurrency(Number val){
        return scale(val, 4, BigDecimal.ROUND_FLOOR)
                .setScale(2,BigDecimal.ROUND_CEILING);
    }

    /**
     * 变成任意位小数的值.
     * @param val 数值
     * @param scale 小数点后位数
     * @param roundingMode 四舍五入模式
     * @return
     */
    public static BigDecimal scale(Number val, int scale, int roundingMode){
        if(val == null) return BigDecimal.ZERO;
        return (val instanceof BigDecimal ? (BigDecimal) val : new BigDecimal(val.doubleValue())).setScale(scale,roundingMode);
    }

    /**
     * 转换为整型的值
     * @param finalScore
     * @param defaultVal
     * @return
     */
    public static int intValue(Number finalScore, int defaultVal) {
        return finalScore == null ? defaultVal : finalScore.intValue();
    }

    /**
     * 转换为双精度浮点型的值
     * @param finalScore
     * @param defaultVal
     * @return
     */
    public static double doubleValue(Number finalScore, double defaultVal) {
        return finalScore == null ? defaultVal : finalScore.doubleValue();
    }

    /**
     * 转换为长整型型的值
     * @param finalScore
     * @param defaultVal
     * @return
     */
    public static long longValue(Number finalScore, long defaultVal) {
        return finalScore == null ? defaultVal : finalScore.longValue();
    }

    /**
     * 转换为单精度浮点型的值
     * @param finalScore
     * @param defaultVal
     * @return
     */
    public static float floatValue(Number finalScore, float defaultVal) {
        return finalScore == null ? defaultVal : finalScore.floatValue();
    }

    /**
     * 批量转Long
     * @param defaultVal
     * @param vals
     * @return
     */
    public static long[] longValues(long defaultVal,String... vals) {
        return Arrays.stream(vals)
                .mapToLong(val -> NumberUtils.toLong(val,defaultVal))
                .toArray();
    }

    public static Long toLong(Object val) {
        return toLong(val,null);
    }

    public static Long toLong(Object val, Long l) {
        if(val == null) return l;
        if(val instanceof Number) return ((Number) val).longValue();
        BigDecimal decimal = new BigDecimal(GosuStrings.str(val));
        return decimal == null ? l : decimal.longValue();
    }

    public static Integer toInteger(Object val) {
        return toInteger(val,null);
    }

    public static Integer toInteger(Object val, Integer dval) {
        if(val == null) return dval;
        return val instanceof Number ?
                ((Number) val).intValue() :
                new BigDecimal(GosuStrings.str(val)).intValue();
    }

    public static Long[] toLongs(Iterable<?> vals) {
        List<Long> ls = Lists.newArrayList();
        for(Object val : vals) {
            Long l = toLong(val,0l);
            if(l == null) continue;
            ls.add(l);
        }
        return ls.toArray(new Long[0]);
    }

    public static Integer[] toIntegers(Iterable<?> vals) {
        List<Integer> nums = Lists.newArrayList();
        for(Object val : vals) {
            Integer i = toInteger(val,0);
            if(i == null) continue;
            nums.add(i);
        }
        return nums.toArray(new Integer[0]);
    }

    public static Integer[] toIntegers(Object[] vals) {
        List<Integer> nums = Lists.newArrayList();
        Object val;
        for(int i=0;i < vals.length;i++){
            val = toInteger(vals[i],0);
            if(val == null) continue;
            nums.add((Integer) val);
        }
        return nums.toArray(new Integer[0]);
    }

    /**
     * 尝试把对象转换为长整型
     * @param vals
     * @return
     */
    public static Long[] toLongs(Object[] vals) {
        List<Long> longs = Lists.newArrayList();
        Object val;
        for(int i=0;i < vals.length;i++){
            val = toLong(vals[i],0l);
            if(val == null) continue;
            longs.add((Long) val);
        }
        return longs.toArray(new Long[0]);
    }

    /**
     * 转成double值. 如果为空就会提供默认值
     * @param val
     * @param defaultVal
     * @return
     */
    public static Double toDouble(Object val, double defaultVal) {
        Double result = defaultVal;
        if(val == null) return result;
        if(val instanceof Number){
            result = ((Number) val).doubleValue();
        }else if(val instanceof String){
            result = NumberUtils.toDouble((String) val,defaultVal);
        }else{
            result = NumberUtils.toDouble(val.toString(),defaultVal);
        }
        return result;
    }

    /**
     * 统计结果
     * @param vals
     * @return
     */
    public static int sum(Integer... vals){
        int result = 0;
        for(Integer val : vals){
            result += toInteger(val,0);
        }
        return result;
    }

    public static int multi(Integer... vals) {
        int result = 1;
        for(Integer val : vals){
            result *= toInteger(val,1);
        }
        return result;
    }

    /**
     * 把BigDecimal转换为特定格式的类型
     * @param val
     * @param clazz
     * @param <V>
     * @return
     */
    public static <V extends Number> V convert(BigDecimal val, Class<V> clazz){
        if(clazz.equals(Integer.class) || clazz.equals(int.class)) return (V) Integer.valueOf(val.intValue());
        else if(clazz.equals(Long.class) || clazz.equals(long.class)) return (V) Long.valueOf(val.longValue());
        else if(clazz.equals(Double.class) || clazz.equals(double.class)) return (V) Double.valueOf(val.doubleValue());
        else if(clazz.equals(Float.class) || clazz.equals(float.class)) return (V) Float.valueOf(val.floatValue());
        else if(clazz.equals(Short.class) || clazz.equals(short.class)) return (V) Short.valueOf(val.shortValue());
        return null;
    }

    /**
     * 统计
     * @param type
     * @param vals
     * @param <V>
     * @return
     */
    public static <V extends Number> V add(Class<V> type,V... vals) {
        BigDecimal sum = BigDecimal.ZERO;
        for(V val : vals) {
            if(val == null) continue;
            sum = sum.add(new BigDecimal(val.doubleValue()));
        }
        return convert(sum,type);
    }

    /**
     * 设置标志位
     * @param val
     * @param flags
     * @return
     */
    public static boolean hasFlags(int val, int... flags){
        for(int flag : flags){
            if((val & flag) != flag) return false;
        }
        return true;
    }

    /**
     * 设置标志位
     * @param val
     * @param flags
     * @return
     */
    public static boolean anyFlags(int val, int... flags){
        for(int flag : flags){
            if((val & flag) == flag) return true;
        }
        return false;
    }

    /**
     * 设置多个标志位
     * @param val
     * @param flags
     * @return
     */
    public static int setFlags(int val, int... flags){
        for(int flag: flags){
            val = val | flag;
        }
        return val;
    }

    /**
     * 移除多个标志位
     * @param val
     * @param flags
     * @return
     */
    public static int removeFlags(int val, int... flags){
        for(int flag: flags){
            val = (val & ~flag);
        }
        return val;
    }

    /**
     * 生成一个随机整数, 支持负数
     * @param fromInclusive 开始数（包含），可为负数
     * @param toInclusive 结束数（包含），可为负数
     * @return
     */
    public static int nextInt(int fromInclusive, int toInclusive) {
        int result = 0;
        if(fromInclusive < 0){
            //两个都是负数，反转后取反
            if(toInclusive < 0) {
                result =  - RandomUtils.nextInt(Math.abs(toInclusive),Math.abs(fromInclusive) + 1);
            } else {
                result = fromInclusive + RandomUtils.nextInt(0, toInclusive + Math.abs(fromInclusive) + 1);
            }
        }else {
            result = RandomUtils.nextInt(fromInclusive, toInclusive + 1);
        }
        return result;

    }
}