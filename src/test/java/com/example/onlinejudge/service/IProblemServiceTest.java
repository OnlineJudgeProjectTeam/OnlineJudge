package com.example.onlinejudge.service;

import com.example.onlinejudge.dto.RunDto;
import com.example.onlinejudge.entity.Problem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.UnsupportedEncodingException;

@SpringBootTest
@RunWith(SpringRunner.class)
public class IProblemServiceTest {
    @Autowired
    private IProblemService problemService;

    @Test
    public void QueryByIdTest() {
        Integer id=2;
        Problem problem = problemService.QueryById(id);
        System.out.println(problem);
    }

    @Test
    public void JavaJudgeTest() throws UnsupportedEncodingException {
        String code="class Solution {\n" +
                "    public int[] twoSum(int[] nums, int target) {\n" +
                "        int n = nums.length;\n" +
                "        for (int i = 0; i < n; ++i) {\n" +
                "            for (int j = i + 1; j < n; ++j) {\n" +
                "                if (nums[i] + nums[j] == target) {\n" +
                "                    return new int[]{i, j};\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "        return new int[0];\n" +
                "    }\n" +
                "}";
        Integer userId = 1;
        Integer problemId = 1;
        RunDto result = problemService.JavaJudge(code, userId, problemId,1);
        System.out.println(result);
    }

    @Test
    public void CJudgeTest(){
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
                "}";
        Integer userId = 1;
        Integer problemId = 1;
        RunDto result = problemService.CJudge(code, userId, problemId,1);
        System.out.println(result);
    }

    @Test
    public void CJudgeTest2(){
        String code="import java.util.HashMap;\n" +
                "class Solution {\n" +
                "    public int[] twoSum(int[] nums, int target) {\n" +
                "        int[] res;\n" +
                "        HashMap<Integer, Integer> map = new HashMap<>();\n" +
                "        for (int i = 0; i < nums.length; i++) {\n" +
                "            if(map.containsKey(target-nums[i])){\n" +
                "                res=new int[]{map.get(target-nums[i]),i};\n" +
                "                return res;\n" +
                "            }\n" +
                "            map.put(nums[i],i);\n" +
                "        }\n" +
                "        return null;\n" +
                "    }\n" +
                "}";
        Integer userId = 1;
        Integer problemId = 1;
        RunDto result = problemService.CJudge(code, userId, problemId,1);
        System.out.println(result);
    }

    @Test
    public void getRandomProblemTest(){
        System.out.println(problemService.getRandomProblem());
    }

    @Test
    public void getProblemListTest(){
        int pageNum = 1;
        int pageSize = 1;
        int navSize = 1;
        String name = "两数之和";
        String tags = "数组";
        String difficulty = "困难";
        System.out.println(problemService.getProblemList(pageNum, pageSize, navSize, name,tags, difficulty));
    }

    @Test
    public void getAnswerTest(){
        Integer problemId = 1;
        Integer language = 0;
        System.out.println(problemService.getAnswer(problemId, language));
    }

    @Test
    public void getProblemDescriptionTest(){
        Integer problemId = 1;
        System.out.println(problemService.getProblemDescription(problemId));
    }

    @Test
    public void getProblemTemplateTest(){
        Integer problemId = 1;
        Integer language = 0;
        System.out.println(problemService.getProblemTemplate(problemId, language));
    }
}
