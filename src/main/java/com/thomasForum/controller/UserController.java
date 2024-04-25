package com.thomasForum.controller;

import com.thomasForum.annotation.LoginRequired;
import com.thomasForum.entity.User;
import com.thomasForum.service.UserService;
import com.thomasForum.util.HostHolder;
import com.thomasForum.util.ThomasforumUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.http.HttpResponse;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);

    @Value("${thomasforum.path.domain}")
    private String domain;

    @Value("${thomasforum.path.upload}")
    private String uploadPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;
    @LoginRequired
    @RequestMapping(path="/setting",method = RequestMethod.GET)
    public String getSettingPage(){
        return "/site/setting";
    }

    @LoginRequired
    @RequestMapping(path="/upload",method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model){
        if(headerImage == null){
            model.addAttribute("error", "You haven't chosen an image yet!");
            return "/site/setting";
        }
        String fileName = headerImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if(StringUtils.isBlank(suffix)){
            model.addAttribute("error", "File format is wrong!");
            return "/site/setting";
        }
        //generate random fileName
        fileName = ThomasforumUtil.generateUUID() + suffix;
        //file path
        File dest = new File(uploadPath + "/" + fileName);
        try {
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("Upload file fail!" + e.getMessage());
            throw new RuntimeException(e);
        }
        // update current user Image path (web)
        User user = hostHolder.getUser();
        String headerUrl = domain + "/user/header/" + fileName;
        userService.updateHeader(user.getId(),headerUrl);

        return "redirect:/index";
    }

    @LoginRequired
    @RequestMapping(path="/password",method = RequestMethod.POST)
    public String changePassword(String oldPassword,String newPassword, String confirmPassword, Model model){
        User user = hostHolder.getUser();
        Map<String,Object > map = userService.updatePassword(user.getId(),oldPassword,newPassword,confirmPassword);
        model.addAttribute("passwordMsg",map.get("passwordMsg"));
        if(map.isEmpty()){
            return "redirect:/index";
        }
        return "/site/setting";
    }

    @RequestMapping(path = "header/{fileName}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response){
        fileName = uploadPath + "/" + fileName;
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        response.setContentType("image/"+suffix);
        try(
            InputStream fileInputStream = new FileInputStream(fileName);
            OutputStream out = response.getOutputStream();
        ){
            byte[] buffer = new byte[1024];
            int bytesRead;
            while((bytesRead = fileInputStream.read(buffer))!= -1){
                out.write(buffer,0,bytesRead);
            }
        }catch (IOException e){
            logger.error("Can't get Header Image" + e.getMessage());
        }
    }
}
