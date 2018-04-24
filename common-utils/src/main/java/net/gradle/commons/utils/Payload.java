package net.gradle.commons.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jgnan on 12/01/2017.
 */
public class Payload extends HashMap<String,Object> {
    public Payload(Object... kvs){
        values(kvs);
    }

    public Payload(){super();}

    public Payload values(Object... kvs){
        int length = kvs == null ? 0 : kvs.length;
        Object val;
        for(int i=0; i< length ;i+=2){
            if(kvs[i] == null) continue;
            else if(kvs[i] instanceof Map) {
                putAll((Map<? extends String, ?>) kvs[i]);
                continue;
            }

            put(kvs[i].toString(), kvs.length == i + 1 ? null : kvs[i+1]);
        }
        return this;
    }

    public static Payload wrap(Object... kvs) {
        return new Payload(kvs);
    }

    /**
     * 转换为QUERYString
     * @return
     */
    public String toQuery(){
        StringBuilder builder = new StringBuilder();
        for(String key : keySet()){
            builder.append(key).append(ShenStrings.EQ).append(get(key));
            builder.append(ShenStrings.AMP);
        }
        if(builder.length() > 0) builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }


    public String toString(){
        return GsonUtils.format(this);
    }

    public byte[] getBytes(){
        return toString().getBytes();
    }
}