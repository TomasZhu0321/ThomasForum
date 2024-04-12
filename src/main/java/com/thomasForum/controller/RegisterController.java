package com.thomasForum.controller;

import com.google.code.kaptcha.Producer;
import com.thomasForum.entity.Page;
import com.thomasForum.entity.User;
import com.thomasForum.service.UserService;
import com.thomasForum.util.ThomasForumConstant;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
            model.addAttribute("msg","Your account has been successfully activated! Now you can explore the thomas forum :)");
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
}
