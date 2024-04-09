package com.thomasForum;

import com.thomasForum.util.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@SpringBootTest
@ContextConfiguration(classes = ThomasForumApplication.class)
public class MailTest {
     @Autowired
     private MailClient mailClient;

     @Autowired
     private TemplateEngine templateEngine;

     @Test
    public void testMail(){
         mailClient.sendMail("tomaszhu0321@gmail.com", "Hi","xx");
     }

    @Test
    public void testMailHTML(){
        Context context = new Context();
        context.setVariable("username","Felipe");
        String content = templateEngine.process("/mail/mailDemo",context);
        mailClient.sendMail("tomaszhu0321@gmail.com", "Hi",content);
    }
}
