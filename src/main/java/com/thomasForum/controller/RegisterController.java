package com.thomasForum.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class RegisterController {
    @RequestMapping(path="/site/register",method = RequestMethod.GET)
    public String getRegisterPage(){
        return "/site/register";
    }
}
