package com.example.onlinejudge.service;

import com.example.onlinejudge.service.impl.SolutionServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

/**
 * @author admin
 * @version 1.0.0
 * @ClassName ISolutionServiceTest.java
 * @Description TODO
 * @createTime 2023年06月13日 23:57:00
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ISolutionServiceTest {

    @Autowired
    private ISolutionService solutionService;

    @Test
    public void creatSolution() throws IOException {
        String code = "#include<stdio.h>\n" +
                "int* twoSum(int* nums, int numsSize, int target, int* returnSize){\n" +
                "    int i,j,q=0;\n" +
                "    int *ret;\n" +
                "    ret=(int*)malloc(2*sizeof(int));\n" +
                "    for(i=0;i<numsSize;i++){\n" +
                "        for(j=i+1;j<numsSize;j++){\n" +
                "            if(nums[i]+nums[j]==target){\n" +
                "                ret[0]=i;\n" +
                "                ret[1]=j;\n" +
                "                *returnSize=2;\n" +
                "                q=1;\n" +
                "                break;\n" +
                "            }\n" +
                "        }\n" +
                "        if(q==1){\n" +
                "            return ret;\n" +
                "        }\n" +
                "    }\n" +
                "    *returnSize=0;\n" +
                "    return NULL;\n" +
                "    test1\n"+
                "}";
        Integer userId = 1;
        Integer problemId = 1;
        Integer language = 0;
        solutionService.createSolution(code,problemId,userId,language);
    }
}
