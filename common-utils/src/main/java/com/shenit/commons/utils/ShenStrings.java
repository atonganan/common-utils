package com.shenit.commons.utils;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by jgnan on 28/12/2016.
 */
public final class ShenStrings {
    private static final Logger LOG = LoggerFactory.getLogger(ShenStrings.class);
    public static final String HYPHEN = "-";
    public static final String UNDER_SCORE = "_";
    public static final String SLASH = "/";
    public static final String BACKSLASH = "\\";
    public static final String COMMA = ",";
    public static final String SEMICOLON = ";";
    public static final String EQ = "=";
    public static final String COLON = ":";
    public static final String AMP = "&";
    public static final String ASTERISK= "*";
    public static final String LBRACE  = "(";
    public static final String RBRACE  = ")";
    public static final String NULL = "null";
    public static final String QUESTION_MARK = "?";
    public static final String FALSE = "false";
    public static final String EMPTY_JSON = "{}";
    public static final String POUND = "#";
    public static final String QUOTE = "'";
    public static final String QUOTE_UNICODE = "\\u0027";
    public static final String DOUBLE_QUOTE = "\"";
    public static final String DOT = ".";

    /**
     * 转成字符串
     * @param obj
     * @return
     */
    public static String str(Object obj){
        return str(obj,null);
    }

    /**
     * 转成字符串
     * @param obj
     * @param defaultVal
     * @return
     */
    public static String str(Object obj,String defaultVal){
        if(obj == null) return defaultVal;
        String result = null;
        if(obj instanceof String) result = (String)obj;
        else if(obj instanceof Number) result = obj.toString();
        else if(obj instanceof Date) result = ShenDates.format((Date) obj);
        else if(obj.getClass().isEnum()) result = obj.toString();
        else result = GsonUtils.format(obj);
        return result;
    }

    /**
     * Join with seperators.
     * @param objs
     * @return
     */
    public static String joinWith(Object...objs){
        if(ArrayUtils.isEmpty(objs)) return StringUtils.EMPTY;
        if(objs.length == 1) return str(objs[0]);
        String seperator = str(objs[objs.length-1]);
        objs = ArrayUtils.remove(objs, objs.length-1);
        List<String> items = Arrays.stream(objs)
                .filter(item -> item != null)
                .map(item ->  {
                    if(item.getClass().isArray()){
                        Object[] newObjs = Arrays.copyOf((Object[])item,ArrayUtils.getLength(item) + 1);
                        newObjs[newObjs.length-1] = seperator;
                        return joinWith(newObjs);
                    }else {
                        return str(item);
                    }

                })
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.toList());
        return join(items,seperator);
    }

    /**
     * 把Map中的KV连城字符串
     * @param map
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K,V> String join(Map<K,V> map){
        return join(map,StringUtils.EMPTY);
    }

    /**
     * 把Map中的KV连城字符串
     * @param map
     * @param kvSeparator
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K,V> String join(Map<K,V> map, String kvSeparator){
        return join(map,kvSeparator,StringUtils.EMPTY);
    }


    /**
     * 把Map中的KV连城字符串
     * @param map
     * @param kvSeparator
     * @param pairSeparator
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K,V> String join(Map<K,V> map, String kvSeparator, String pairSeparator){
        return join(map,kvSeparator,pairSeparator,true);
    }

    /**
     * 把Map的KV连城字符串
     * @param map
     * @param kvSeparator
     * @param pairSeparator
     * @param includeEmpty
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K,V> String join(Map<K,V> map, String kvSeparator, String pairSeparator,boolean includeEmpty){
        StringBuilder builder = new StringBuilder();
        for(K key : map.keySet()){
            String val = str(map.get(key));
            if(!includeEmpty && StringUtils.isEmpty(val) ) continue;
            builder.append(key).append(kvSeparator).append(val).append(pairSeparator);
        }
        return builder.length() > 0 ? builder.substring(0,builder.lastIndexOf(pairSeparator)) : null;
    }

    /**
     * join with character separator
     * @param list
     * @param separator
     * @return
     */
    public static String join(List<?> list, char separator) {
        return join(list,String.valueOf(separator));
    }

    /**
     * Joins list of string items to a single string, where items are separated
     * with a defined separator.
     *
     * @param list      to join into string
     * @param separator to be used between elements
     * @return items joined into a single string
     */
    public static String join(List<?> list, String separator) {

        if (separator == null) {
            throw new IllegalArgumentException("Missing separator!");
        }

        String output = "";

        if (list != null && list.size() > 0) {
            for (int i = 1; i <= list.size(); i++) {
                output = output + list.get(i - 1);
                if (i < list.size()) {
                    output = output + separator;
                }
            }
        }

        return output;
    }

    /**
     * Joins list of string items to a single string, where items are separated
     * with a defined separator.
     *
     * @param list      to join into string
     * @param separator to be used between elements
     * @return items joined into a single string
     */
    public static String join(Set<?> list, String separator) {

        if (separator == null || separator.toString().equalsIgnoreCase("")) {
            throw new IllegalArgumentException("Missing separator!");
        }

        String output = "";

        if (list != null && list.size() > 0) {
            boolean joined = false;
            for (Object item : list) {
                output = output + ShenStrings.str(item);
                output = output + separator;
                joined = true;
            }
            if(joined) output = output.substring(0,output.length() -1);
        }

        return output;
    }

    /**
     * To utf8 bytes
     * @param method
     * @return
     */
    public static byte[] utf8(String method) {
        try {
            return method == null ? null : method.getBytes(StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            LOG.warn("[utf8] Could not parse to utf8 due to exception",e);
        }
        return null;
    }

    /**
     * To utf8 bytes
     * @param method
     * @return
     */
    public static byte[] utf16(String method) {
        try {
            return method == null ? null : method.getBytes(StandardCharsets.UTF_16.name());
        } catch (UnsupportedEncodingException e) {
            LOG.warn("[utf16] Could not parse to utf8 due to exception",e);
        }
        return null;
    }

    public static String utf8(byte[] data){
        return new String(data, StandardCharsets.UTF_8).replaceAll(QUOTE_UNICODE,QUOTE);
    }

    public static String utf16(byte[] data){
        return new String(data, StandardCharsets.UTF_16);
    }

    /**
     * Trim all string.
     * @param strs
     * @return
     */
    public static String[] trim(String[] strs) {
        if(ArrayUtils.isEmpty(strs)) return strs;
        return Arrays.stream(strs).map(item -> StringUtils.trim(item)).toArray(String[]::new);
    }

    public static String[] split(String str, String separator) {
        return StringUtils.isNotEmpty(str) ? str.split(separator) : null;
    }

    /**
     * 驼峰变蛇形
     * @param str
     * @return
     */
    public static String snake(String str) {
        return snake(str,false);
    }

    /**
     * 驼峰变蛇形
     * @param str
     * @param upper 是否变成纯大写
     * @return
     */
    public static String snake(String str,boolean upper) {
        return CaseFormat.UPPER_CAMEL.to(upper ? CaseFormat.UPPER_UNDERSCORE : CaseFormat.LOWER_UNDERSCORE, str);
    }

    /**
     * 蛇形变驼峰
     * @param str
     * @return
     */
    public static String camel(String str) {
        return camel(str,false);
    }

    /**
     * 蛇形变驼峰
     * @param str
     * @param upperCapital
     * @return
     */
    public static String camel(String str,boolean upperCapital) {
        return CaseFormat.LOWER_UNDERSCORE.to(upperCapital ? CaseFormat.UPPER_CAMEL : CaseFormat.LOWER_CAMEL, str);
    }

    /**
     * 转换为字符串.
     * @param vals
     * @return
     */
    public static String[] strs(Iterable<?> vals) {
        List<String> strs = Lists.newArrayList();
        for(Object val : vals) {
            String s = str(val,null);
            if(s == null) continue;
            strs.add(s);
        }
        return strs.toArray(new String[0]);
    }

    /**
     * 转换为字符串.
     * @param vals
     * @return
     */
    public static String[] strs(Object[] vals) {
        List<String> strs = Lists.newArrayList();
        Object val;
        for(int i=0;i<vals.length;i++){
            if(vals[i] == null) continue;
            val = str(vals[i],null);
            strs.add((String) val);
        }
        return strs.toArray(new String[0]);
    }

    /**
     * Wrap all items in collections
     * @param strs
     * @param wrap
     * @return
     */
    public static List<String> wrapAll(Collection<String> strs, String wrap) {
        if(CollectionUtils.isEmpty(strs) || StringUtils.isEmpty(wrap)) return strs == null ? null : Lists.newArrayList(strs);
        return strs.stream().map(str -> StringUtils.wrap(str,wrap)).collect(Collectors.toList());
    }

    /**
     * Wrap all items in collections
     * @param strs
     * @param wrap
     * @return
     */
    public static List<String> wrapAll(Collection<String> strs, char wrap) {
        return wrapAll(strs,String.valueOf(wrap));
    }

    public static boolean isEmptyJson(String json) {
        return StringUtils.isEmpty(json) || EMPTY_JSON.equals(json);
    }


    /**
     * Get bytes using default charset
     * @param key
     * @return
     */
    public static byte[] bytes(Object key) {
        String str = str(key);
        return StringUtils.isBlank(str) ? null : str.getBytes();
    }


    /**
     * Get bytes for serials of keys.
     * @param keys
     * @param charset
     * @return
     */
    public static byte[][] byteses(Charset charset,Object... keys) {
        if(ArrayUtils.isEmpty(keys)) return null;
        byte[][] bytes = new byte[keys.length][];
        for(int i=0;i<keys.length;i++) {
            bytes[i] = bytes(keys[i],charset);
        }
        return bytes;
    }

    /**
     * Get bytes.
     * @param key
     * @param charset
     * @return
     */
    public static byte[] bytes(Object key,String charset) {
        if(key == null) return null;
        try {
            String str = ShenStrings.str(key);
            return StringUtils.isEmpty(charset) ? str.getBytes() : str.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            LOG.warn("[bytes] Unsupported charset -> {}",charset);
        }
        return null;
    }

    /**
     * Get bytes.
     * @param key
     * @param charset
     * @return
     */
    public static byte[] bytes(Object key,Charset charset) {
        if(key == null) return null;
        String str = ShenStrings.str(key);
        return charset == null ? str.getBytes() : str.getBytes(charset);
    }

    /**
     * 使用空字串连接字符集
     * @param strings
     * @return
     */
    public static String join(List<String> strings) {
        return join(strings,StringUtils.EMPTY);
    }
}
