package com.example.onlinejudge.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class IUserSeviceTest {

    @Autowired
    private IUserService userService;

    @Test
    public void sentCodeTest() {
        String email="1730523754@qq.com";
        userService.sendCode(email);
    }
}
