package com.shenit.commons.pojo;

import com.shenit.commons.utils.GsonUtils;
import com.shenit.commons.utils.ShenValidates;
import com.shenit.commons.utils.Payload;

/**
 * Created by jiangnan on 05/06/2017.
 */
public class Response<T> {
    public static final String OK_MSG = "ok";
    public static final int OK_CODE = 200;
    public static final Response OK = new Response();

    public int code;
    public String msg;
    public T data;

    public Response() {
        this.code = OK_CODE;
        this.msg = OK_MSG;
    }

    public Response(int code,String msg){
        this.code = code;
        this.msg = msg;
    }

    public Response(T data){
        this();
        this.data = data;
    }

    public static Response simple(int code, String msg) {
        return new Response(code, msg);
    }

    public Response status(int code, String msg){
        this.code = code;
        this.msg = msg;
        return this;
    }

    /**
     * 判断响应是否成功
     * @return
     */
    public boolean hasError(){
        return ShenValidates.ne(code,OK_CODE);
    }

    /**
     * 使用Payload作为data
     * @param data
     * @return
     */
    public static Response payload(Object... data){
        return data(Payload.wrap(data));
    }

    public static Response data(Object data){
        if(data == null) return new Response();
        return new Response(data);
    }

    @Override
    public String toString(){
        return GsonUtils.format(this);
    }
}
