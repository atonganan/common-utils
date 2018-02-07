package com.shenit.commons.exp;

import com.shenit.commons.pojo.Response;
import com.shenit.commons.utils.GsonUtils;
import com.shenit.commons.utils.ShenStrings;

public class BusinessException extends RuntimeException{
    public Response resp = new Response();
    public BusinessException(){ }
    public BusinessException(int code){
        super(ShenStrings.str(code));
        resp.code =code;
    }

    public BusinessException(String msg){
        super(msg);
        resp.msg =msg;
    }
    public BusinessException(int code, Object message){
        super(GsonUtils.format(message));
        resp.code = code;
        if(message instanceof String) resp.msg = (String)message;
        else resp.data = message;
    }

}
