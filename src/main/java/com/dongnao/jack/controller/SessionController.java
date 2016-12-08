package com.dongnao.jack.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/session")
public class SessionController {
    
    @RequestMapping("/login")
    public @ResponseBody String login(HttpServletRequest request,
            HttpServletResponse response, @RequestBody String param) {
        HttpSession session = request.getSession();
        session.setAttribute("name", "jack");
        session.setAttribute("sex", 18);
        return "登陆成功！";
    }
}
