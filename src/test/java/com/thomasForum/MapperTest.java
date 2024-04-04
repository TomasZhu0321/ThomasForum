package com.thomasForum;

import com.thomasForum.dao.AlphaDao;
import com.thomasForum.dao.UserMapper;
import com.thomasForum.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;


@SpringBootTest
@ContextConfiguration(classes = ThomasForumApplication.class)
public class MapperTest {
    @Autowired
    private UserMapper userMapper;

    @Test
    public void testSelectUser(){
        User user;
        user = userMapper.selectById(112);
        System.out.println(user);
    }
}
