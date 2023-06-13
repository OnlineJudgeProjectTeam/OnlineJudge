package com.example.onlinejudge.service;

import com.example.onlinejudge.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class IUserServiceTest {

    @Autowired
    private IUserService userService;

    @Test
    public void sentCodeTest() {
        String email="1730523754@qq.com";
        userService.sendCode(email);
    }
    @Test
    public void sentCodeTest1() {
        String email="1767390619@qq.com";
        userService.sendCode(email);
    }

    @Test
    public void register(){
        String email = "1767390619@qq.com";
        userService.register(email,"aahang","12345","yk3e","aa");
    }

    @Test
    public void QueryByIdTest() {
        Integer id=1;
        User user = userService.QueryById(id);
        System.out.println(user);
    }
}
