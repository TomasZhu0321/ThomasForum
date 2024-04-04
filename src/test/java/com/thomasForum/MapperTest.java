package com.thomasForum;

import com.thomasForum.dao.AlphaDao;
import com.thomasForum.dao.DiscussPostMapper;
import com.thomasForum.dao.UserMapper;
import com.thomasForum.entity.DiscussPost;
import com.thomasForum.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;


@SpringBootTest
@ContextConfiguration(classes = ThomasForumApplication.class)
public class MapperTest {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Test
    public void testSelectUser(){
        User user;
        user = userMapper.selectById(112);
        System.out.println(user);
    }
    @Test
    public void testSelectDiscussPost(){
        int discussPost = discussPostMapper.selectDiscussPostRows(111);
        List<DiscussPost> page = discussPostMapper.selectDiscussPosts(111,0,10);
        System.out.println(discussPost);
        System.out.println("**************");
        for(DiscussPost dp : page){
            System.out.println(dp);
        }
    }
}
