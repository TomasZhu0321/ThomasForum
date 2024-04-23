package com.thomasForum.service;

import com.thomasForum.dao.LoginTicketMapper;
import com.thomasForum.dao.UserMapper;
import com.thomasForum.entity.LoginTicket;
import com.thomasForum.entity.User;
import com.thomasForum.util.MailClient;
import com.thomasForum.util.ThomasForumConstant;
import com.thomasForum.util.ThomasforumUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService implements ThomasForumConstant {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private MailClient mailClient;

    @Value("${thomasforum.path.domain}")
    private String domain;
    @Autowired
    private TemplateEngine templateEngine;
    public User findUserById(int id){
        return userMapper.selectById(id);
    }

    public LoginTicket findLoginTicket(String ticket){
        return loginTicketMapper.selectLoginTicket(ticket);
    }
    public Map<String, Object> register(User user){
        Map <String, Object > map = new HashMap<>();
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null!");
        }
        if(StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg", "Username cannot be null!");
        }
        if(StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg", "Email address cannot be null!");
        }
        if(StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg", "Password cannot be null!");
        }

        if(userMapper.selectByName(user.getUsername())!= null){
            map.put("usernameMsg","This user has been register!");
            return map;
        }
        if(userMapper.selectByEmail(user.getEmail())!= null){
            map.put("emailMsg","This email has been register!");
            return map;
        }
        //register user
        user.setSalt(ThomasforumUtil.generateUUID().substring(0,5));
        user.setPassword(ThomasforumUtil.md5(user.getPassword()) +user.getSalt());
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(ThomasforumUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        System.out.println(user);
        userMapper.insertUser(user);

        Context context = new Context();
        context.setVariable("email",user.getEmail());
        context.setVariable("url", domain + "/activation/" + user.getId() + "/" + user.getActivationCode());
        String content = templateEngine.process("/mail/activation",context);
        mailClient.sendMail(user.getEmail(),"Account Activation", content);
        return map;
    }

    public Map<String,Object> login(String username, String password, int expiredSeconds){
        Map<String, Object> map = new HashMap<>();
        if(StringUtils.isBlank(username)){
            map.put("usernameMsg", "Username cannot be null!");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("passwordMsg", "Password cannot be null!");
            return map;
        }
        User user = userMapper.selectByName(username);
        if(user == null){
            map.put("usernameMsg","This user is not existing!");
            return map;
        }
        if(user.getStatus() == 0){
            map.put("usernameMsg","This account has not been activated yet!");
            return map;
        }
        if(!user.getPassword().equals(ThomasforumUtil.md5(password) + user.getSalt())){
            map.put("passwordMsg","Password is incorrect!");
            return map;
        }
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(ThomasforumUtil.generateUUID());
        loginTicketMapper.insertLoginTicket(loginTicket);
        map.put("ticket",loginTicket.getTicket());
        return map;
    }

    public int activation(int userId, String code){
        User user = userMapper.selectById(userId);
        if(user.getStatus() == 1){
            return ACTIVATION_REPEAT;
        }
        else if (user.getActivationCode().equals(code)){
            userMapper.updateStatus(userId,1);
            return ACTIVATION_SUCCESS;
        }
        else {
            return ACTIVATION_FAILURE;
        }
    }

    public void logout(String ticket){
        loginTicketMapper.updateLoginTicket(ticket,1);
    }

}
