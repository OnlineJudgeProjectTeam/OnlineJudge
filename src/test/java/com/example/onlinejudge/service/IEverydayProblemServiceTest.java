package com.example.onlinejudge.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class IEverydayProblemServiceTest {
    @Autowired
    private IEverydayProblemService everydayProblemService;

    @Test
    public void getEverydayProblemTest(){
        System.out.println(everydayProblemService.getEverydayProblem());
    }
}
