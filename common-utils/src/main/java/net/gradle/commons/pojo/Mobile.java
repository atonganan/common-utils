package net.gradle.commons.pojo;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

/**
 * 用于检测国内电话号码类型的工具类.
 */
public final class Mobile {
    private final static Pattern MOBILE = Pattern.compile("^1(?:3[0-9]|4[57]|5[0-35-9]|7[0135678]|8[0-9])\\d{8}$");

    /**
     * 校验号码正确性
     * @param phone
     * @return
     */
    public static boolean validate(String phone){
        if(phone == null) return false;

        phone = phone.replaceAll("[ -]", StringUtils.EMPTY);
        if(phone.length() != 11) return false;

        return MOBILE.matcher(phone).find();
    }

    /**
     * 查询电话号码的供应商
     * @param phone
     * @return
     */
    public static Provider provider(String phone){
        for(Provider p : Provider.values()){
            if(p.pattern.matcher(phone).find()) return p;
        }
        return null;
    }

    public enum Provider{
        //中国移动
        ChinaMobile(Pattern.compile("^1(?:3[4-9]|4[7]|5[0-27-9]|7[08]|8[2-478])\\d{8}$")),
        //中国联通
        ChinaUnicom(Pattern.compile("^1(?:3[0-2]|4[5]|5[56]|7[0156]|8[56])\\d{8}$")),
        //中国电信
        ChinaTelecom(Pattern.compile("^1(?:3[3]|4[9]|53|7[037]|8[019])\\d{8}$"));
        private Pattern pattern;
        Provider(Pattern p) {this.pattern = p;}
    }
}