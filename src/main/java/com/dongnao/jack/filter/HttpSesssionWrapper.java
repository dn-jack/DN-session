package com.dongnao.jack.filter;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import com.dongnao.jack.redis.RedisApi;
import com.dongnao.jack.util.SpringContextUtil;

public class HttpSesssionWrapper implements HttpSession {
    
    private HttpServletRequest request;
    
    private HttpServletResponse response;
    
    //    Map<String, Object> sessionAttr = new HashMap<String, Object>();
    
    private String id = UUID.randomUUID().toString();
    
    public HttpSesssionWrapper(HttpServletRequest request,
            HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }
    
    public long getCreationTime() {
        // TODO Auto-generated method stub
        return 0;
    }
    
    public String getId() {
        // TODO Auto-generated method stub
        return null;
    }
    
    public long getLastAccessedTime() {
        // TODO Auto-generated method stub
        return 0;
    }
    
    public ServletContext getServletContext() {
        // TODO Auto-generated method stub
        return null;
    }
    
    public void setMaxInactiveInterval(int interval) {
        // TODO Auto-generated method stub
        
    }
    
    public int getMaxInactiveInterval() {
        // TODO Auto-generated method stub
        return 0;
    }
    
    public HttpSessionContext getSessionContext() {
        // TODO Auto-generated method stub
        return null;
    }
    
    public Object getAttribute(String name) {
        // TODO Auto-generated method stub
        return getValueFromRedis(name);
    }
    
    public Object getValue(String name) {
        // TODO Auto-generated method stub
        return null;
    }
    
    public Enumeration getAttributeNames() {
        // TODO Auto-generated method stub
        return null;
    }
    
    public String[] getValueNames() {
        // TODO Auto-generated method stub
        return null;
    }
    
    public void setAttribute(String name, Object value) {
        String sessionId = getSessionIdFromCookie();
        if (sessionId == null) {
            //            sessionId = UUID.randomUUID().toString();
            Map<String, Object> sessionAttr = new HashMap<String, Object>();
            sessionAttr.put(name, value);
            SessionFilterDelegate sessionFilterDelegate = (SessionFilterDelegate)SpringContextUtil.getApplicationContext()
                    .getBean("sessionFilterDelegate");
            Map<String, Map<String, Object>> sessionAttrs = sessionFilterDelegate.getSessionAttr();
            
            if (sessionAttrs.containsKey(id)) {
                Map<String, Object> map = sessionAttrs.get(id);
                map.put(name, value);
                //                RedisApi.hmset(id, map);
            }
            else {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put(name, value);
                sessionAttrs.put(id, map);
                //                RedisApi.hmset(id, map);
            }
            writeCookie(id);
            RedisApi.hmset(id, sessionAttrs.get(id));
        }
        else {
            SessionFilterDelegate sessionFilterDelegate = (SessionFilterDelegate)SpringContextUtil.getApplicationContext()
                    .getBean("sessionFilterDelegate");
            Map<String, Map<String, Object>> sessionAttrs = sessionFilterDelegate.getSessionAttr();
            Map<String, Object> map = sessionAttrs.get(sessionId);
            map.put(name, value);
            RedisApi.hmset(id, sessionAttrs.get(id));
        }
        
    }
    
    public void putValue(String name, Object value) {
        // TODO Auto-generated method stub
        
    }
    
    public void removeAttribute(String name) {
        // TODO Auto-generated method stub
        
    }
    
    public void removeValue(String name) {
        // TODO Auto-generated method stub
        
    }
    
    public void invalidate() {
        // TODO Auto-generated method stub
        
    }
    
    public boolean isNew() {
        // TODO Auto-generated method stub
        return false;
    }
    
    private String getSessionIdFromCookie() {
        
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if ("sessionId".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        
        return null;
    }
    
    private String getValueFromRedis(String name) {
        return RedisApi.hmget(getSessionIdFromCookie(), name).get(0);
    }
    
    private void writeCookie(String id) {
        Cookie cookie = new Cookie("sessionId", id);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
    
}
