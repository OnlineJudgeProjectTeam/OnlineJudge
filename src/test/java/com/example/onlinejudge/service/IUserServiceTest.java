package com.example.onlinejudge.service;

import com.example.onlinejudge.common.R;
import com.example.onlinejudge.entity.User;
import com.example.onlinejudge.utils.PasswordEncoder;
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
        R<String> stringR = userService.sendCode(email);
        System.out.println(stringR);
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
    public void loginTest(){
        String username = "admin";
        String password = "123456";
        System.out.println(userService.login(username,password));
    }

    @Test
    public void loginByCodeTest(){
        String email = "1730523754@qq.com";
        String code = "2c5e";
        System.out.println(userService.loginByCode(email,code));
    }

    @Test
    public void QueryByIdTest() {
        Integer id=1;
        User user = userService.QueryById(id);
        System.out.println(user);
    }

    @Test
    public void PasswordTest(){
        System.out.println(PasswordEncoder.encode("1234"));
        System.out.println(PasswordEncoder.matches("3152ltt4wbrrs5sxvosv@12df906b6db13909ddb2e009849ffe48","1234"));
    }

}
