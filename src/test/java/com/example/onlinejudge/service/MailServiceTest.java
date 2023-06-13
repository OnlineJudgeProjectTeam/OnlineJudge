package com.example.onlinejudge.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class MailServiceTest {
    @Autowired
    private MailService mailService;

    @Test
    public void sendVertifyCodeTest() {
        String to = "1730523754@qq.com";
        String title = "验证码";
        String content = "123456";
        mailService.sendVertifyCode(to, title, content);
    }
}
