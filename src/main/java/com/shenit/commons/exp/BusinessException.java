package com.shenit.commons.exp;

import com.shenit.commons.pojo.Response;
import com.shenit.commons.utils.GosuStrings;
import com.shenit.commons.utils.GsonUtils;

public class BusinessException extends RuntimeException{
    public Response resp = new Response();
    public BusinessException(){ }
    public BusinessException(int code){
        super(GosuStrings.str(code));
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
