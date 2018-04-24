package net.gradle.commons.exp;

import net.gradle.commons.pojo.Response;
import net.gradle.commons.utils.GsonUtils;
import net.gradle.commons.utils.ShenStrings;

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
