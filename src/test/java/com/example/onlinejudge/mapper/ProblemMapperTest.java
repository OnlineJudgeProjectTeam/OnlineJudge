package com.example.onlinejudge.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ProblemMapperTest {
    @Autowired
    private ProblemMapper problemMapper;

    @Test
    public void getProblemMapTest(){
        System.out.println(problemMapper.getProblemMap());
    }
}
