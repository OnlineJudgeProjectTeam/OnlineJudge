package com.example.onlinejudge.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;


@SpringBootTest
@RunWith(SpringRunner.class)
public class ISubmissionServiceTest {
    @Autowired
    private ISubmissionService submissionService;

    @Test
    public void getSubmissionListTest(){
        int userId = 1;
        int pageNum = 1;
        int pageSize = 1;
        int navSize = 1;
        Integer language = 0;
        String difficulty = null;
        Integer pass = null;
        LocalDateTime startTime = LocalDateTime.of(2020, 1, 1, 0, 0, 0);
        LocalDateTime endTime = LocalDateTime.now();
        System.out.println(submissionService.getSubmissionList(userId, pageNum, pageSize, navSize, language, difficulty, pass, startTime, endTime));
    }

    @Test
    public void getAcDataTest(){
        int userId = 1;
        int problemId = 1;
        String difficulty = "";
        System.out.println(submissionService.getAcData(userId, problemId, difficulty));
    }
}
