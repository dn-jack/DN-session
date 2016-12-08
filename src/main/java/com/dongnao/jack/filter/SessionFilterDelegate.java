package com.dongnao.jack.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.dongnao.jack.redis.RedisApi;

@Service
public class SessionFilterDelegate {
    
    private Map<String, Map<String, Object>> sessionAttr = new HashMap<String, Map<String, Object>>();
    
    public Map<String, Map<String, Object>> getSessionAttr() {
        return sessionAttr;
    }
    
    public void setSessionAttr(Map<String, Map<String, Object>> sessionAttr) {
        this.sessionAttr = sessionAttr;
    }
    
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        try {
            SessionHttpServletRequest sessionRequest = new SessionHttpServletRequest(
                    (HttpServletRequest)request, (HttpServletResponse)response);
            chain.doFilter(sessionRequest, response);
            
            if (sessionAttr.size() > 0) {
                String id = getSessionIdFromCookie((HttpServletRequest)request);
                if (id == null) {
                    for (Map.Entry<String, Map<String, Object>> entry : sessionAttr.entrySet()) {
                        RedisApi.hmset(entry.getKey(), entry.getValue());
                    }
                }
                else {
                    RedisApi.hmset(id, sessionAttr.get(id));
                }
            }
        }
        catch (Exception e) {
            e.fillInStackTrace();
        }
    }
    
    private String getSessionIdFromCookie(HttpServletRequest request) {
        
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
}
