package com.dongnao.jack.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class SessionHttpServletRequest extends HttpServletRequestWrapper {
    
    private HttpServletRequest request;
    
    private HttpServletResponse response;
    
    public SessionHttpServletRequest(HttpServletRequest request,
            HttpServletResponse response) {
        super(request);
        this.request = request;
        this.response = response;
    }
    
    public HttpSession getSession(boolean create) {
        return super.getSession(create);
    }
    
    public HttpSession getSession() {
        return new HttpSesssionWrapper(request, response);
    }
    
}
