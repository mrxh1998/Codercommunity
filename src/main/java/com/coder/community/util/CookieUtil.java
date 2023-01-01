package com.coder.community.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class CookieUtil {
    public static String getValue(HttpServletRequest request,String name){
        if(request == null || name == null){
            throw new IllegalArgumentException("参数为空！");
        }
        Cookie[] cookies = request.getCookies();
        if(cookies == null){
            return null;
        }
        for(int i = 0; i < cookies.length;i++){
            if(cookies[i].getName().equals(name)){
                return cookies[i].getValue();
            }
        }
        return null;
    }
}
