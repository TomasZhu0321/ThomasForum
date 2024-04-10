package com.thomasForum.controller;

import ch.qos.logback.core.model.Model;
import com.thomasForum.util.ThomasforumUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/alpha")
public class AlphaController {

    @RequestMapping("/hello")
    @ResponseBody
    public String sayHello(Model model){
        return "Hello Spring Boot!!";
    }
    @RequestMapping(value = "/cookie/set",method = RequestMethod.GET)
    @ResponseBody
    public String setCookies(HttpServletResponse response){
        Cookie cookie = new Cookie("code", ThomasforumUtil.generateUUID());
        cookie.setMaxAge(60*60);
        cookie.setPath("/alpha");
        response.addCookie(cookie);
        return "set cookie";
    }
    @RequestMapping(value = "/cookie/get",method = RequestMethod.GET)
    @ResponseBody
    public String getCookies(@CookieValue("code") String code){
        System.out.println(code);
        return "get cookie";
    }
}
