package com.thomasForum.controller;



import com.thomasForum.entity.DiscussPost;
import com.thomasForum.entity.Page;
import com.thomasForum.entity.User;
import com.thomasForum.service.DiscussPostService;
import com.thomasForum.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private UserService userService;

    @RequestMapping(path = "/index", method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page){
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index");
        List<DiscussPost> discussList = discussPostService.findDiscussPosts(0,page.getOffset(),page.getLimit());
        List<Map<String,Object>> postAndUser = new ArrayList<>();
        if(discussList!=null){
            for(DiscussPost dp : discussList){
                User userInfo = userService.findUserById(dp.getUserId());
                Map<String,Object> map = new HashMap<>();
                map.put("user",userInfo);
                map.put("discussPost", dp);
                postAndUser.add(map);
            }
        }
        model.addAttribute("discussPosts",postAndUser);
        return "/index";
    }
}
