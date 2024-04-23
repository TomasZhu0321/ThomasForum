package com.thomasForum.controller;

import com.google.code.kaptcha.Producer;
import com.thomasForum.entity.Page;
import com.thomasForum.entity.User;
import com.thomasForum.service.UserService;
import com.thomasForum.util.ThomasForumConstant;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

@Controller
public class RegisterController implements ThomasForumConstant {
    private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);
    @Autowired
    private UserService userService;
    @Autowired
    private Producer kaptchaProducer;

    @RequestMapping(path="/login",method = RequestMethod.GET)
    public String getLoginPage(){
        return "/site/login";
    }
    @RequestMapping(path="/register",method = RequestMethod.GET)
    public String getRegisterPage(){
        return "/site/register";
    }

    @RequestMapping(path="/register",method = RequestMethod.POST)
    public String register(Model model, User user){
        Map<String, Object> map = userService.register(user);
        if(map == null || map.isEmpty()){
            model.addAttribute("msg","We've sent you the activation link in your email, please activate your account!");
            model.addAttribute("target","/index");
            return "/site/operate-result";
        }
        else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));
            return "/site/register";
        }
    }
    @RequestMapping(path="/activation/{userId}/{code}",method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("userId") int userId, @PathVariable("code") String code){
        int result = userService.activation(userId,code);
        if(result == ACTIVATION_SUCCESS){
            model.addAttribute("msg","Your account has been successfully activated! Now you can login the thomas forum :)");
            model.addAttribute("target","/login");
        }
        else if(result == ACTIVATION_REPEAT){
            model.addAttribute("msg","Sorry, this account has been activated already, you can login use this account!");
            model.addAttribute("target","/index");
        }
        else{
            model.addAttribute("msg","Sorry, there may be some issues about your activation code");
            model.addAttribute("target","/index");
        }
        return "/site/operate-result";
    }
    // get verification code
    @RequestMapping(path = "/kaptcha", method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response, HttpSession session){
        //create kaptcha
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);
        // store Kaptcha in session
        session.setAttribute("kaptcha",text);
        // Output stream write image as HTTP response
        response.setContentType("image/png");
        try {
            OutputStream os = response.getOutputStream();
            ImageIO.write(image,"png",os);
        } catch (IOException e) {
            logger.error("Generating Kaptcha failed : " + e.getMessage());
        }
    }

    /**
     * 1. submit login info, verify matching
     * 2. get kaptcha, and check whether matching or not in session
     * 3. process error from map
     * 4. create ticket as token
     * @return
     */
    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String login(String username, String password, String code, boolean rememberme, Model model, HttpSession session, HttpServletResponse response){
        String kaptcha = (String) session.getAttribute("kaptcha");
        if(StringUtils.isBlank(code) || StringUtils.isBlank(kaptcha) || !code.equals(kaptcha)){
            model.addAttribute("codeMsg", "Verification Code is wrong!");
            return "/site/login";
        }
        int expiredSeconds = rememberme == true ? REMEMBER_EXPIRED_SECONDS: DEFAULT_EXPIRED_SECONDS;
        Map<String,Object > map = userService.login(username,password,expiredSeconds);
        //do have ticket
        if(map.containsKey("ticket")){
            Cookie cookie = new Cookie("ticket",map.get("ticket").toString());
            cookie.setPath("/");
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);
            return "redirect:/index";
        }else {
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            return "/site/login";
        }
    }
    @RequestMapping(path = "/logout",method = RequestMethod.GET)
    public String logout(@CookieValue("ticket") String ticket){
        userService.logout(ticket);
        return "redirect:/login";

    }
}
