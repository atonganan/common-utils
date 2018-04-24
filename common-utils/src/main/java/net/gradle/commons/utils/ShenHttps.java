package net.gradle.commons.utils;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.Map;

/**
 * Created by jiangnan on 30/06/2017.
 */
public final class ShenHttps {
    private static final Logger LOG = LoggerFactory.getLogger(ShenHttps.class);
    private static final String X_FORWARDED_FOR = "X-Forwarded-For";
    private static final String X_REAL_IP = "X-Real-IP";

    /**
     * 尝试获取用户IP
     * @param request
     * @return
     */
    public static String getClientIp(HttpServletRequest request){
        return ShenChoose.first(request.getRemoteAddr(),
                request.getHeader(X_FORWARDED_FOR),
                request.getHeader(X_REAL_IP));
    }

    /**
     * 获取HeaderMap
     * @param request
     * @return
     */
    public static Map<String,String> getHeaderMap(HttpServletRequest request) {
        Map<String,String> headers = Maps.newHashMap();
        for(Enumeration<String> names = request.getHeaderNames(); names.hasMoreElements();){
            String header = names.nextElement();
            headers.put(header, request.getHeader(header));
        }
        return headers;
    }

    public static String dumpRequest(HttpServletRequest request) {
        StringBuilder builder = new StringBuilder();
        builder.append(request.getMethod()).append(StringUtils.SPACE).append(request.getRequestURI());
        if(StringUtils.isNotBlank(request.getQueryString())) builder.append(ShenStrings.QUESTION_MARK);
        builder.append('\n');
        String header;
        for(Enumeration<String> headers = request.getHeaderNames();headers.hasMoreElements();){
            header = headers.nextElement();
            builder.append(header).append(ShenStrings.COLON).append(StringUtils.SPACE).append(request.getHeader(header));
        }
        return builder.toString();
    }

}
