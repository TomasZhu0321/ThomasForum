package com.thomasForum;

import com.thomasForum.util.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = ThomasForumApplication.class)
public class SensitiveTest {
    @Autowired
    SensitiveFilter sensitiveFilter;
    @Test
    public void sensitiveTest(){
        String text = "$fu$c$k$$UMotherfucker";
        String res = sensitiveFilter.filter(text);
        System.out.println(res);
    }
}
