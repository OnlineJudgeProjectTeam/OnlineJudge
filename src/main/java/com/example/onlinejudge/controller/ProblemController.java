package com.example.onlinejudge.controller;


import com.example.onlinejudge.common.R;
import com.example.onlinejudge.dto.ProblemDto;
import com.example.onlinejudge.dto.RunDto;
import com.example.onlinejudge.entity.Problem;
import com.example.onlinejudge.service.IProblemService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author diandianjun
 * @since 2023-06-13
 */
@RestController
@RequestMapping("/problem")
@Api("题目管理")
public class ProblemController {
    @Autowired
    private IProblemService problemService;

    @GetMapping("/getProblemList")
    @ApiOperation("获取分页题目列表，按照难度、标签、题目名称筛选")
    public R<PageInfo<Problem>> getProblemList(@ApiParam("第几页") Integer pageNum, @ApiParam("一页几条数据") Integer pageSize, @ApiParam("导航页显示个数") Integer navSize, @ApiParam("题目名称") String name, @ApiParam("标签") String tags, @ApiParam("难度") String difficulty){
        return R.success(problemService.getProblemList(pageNum, pageSize, navSize, name, tags, difficulty));
    }

    @GetMapping("/getProblemContent")
    @ApiOperation("获取题目内容")
    public R<ProblemDto> getProblemContent(@ApiParam("题目id") Integer problemId,@ApiParam("语言，0为java，1为c") Integer language){
        Problem problem = problemService.QueryById(problemId);
        ProblemDto problemDto = new ProblemDto(problem);
        String problemDescription = problemService.getProblemDescription(problemId);
        problemDto.setDescription(problemDescription);
        String problemTemplate = problemService.getProblemTemplate(problemId, language);
        problemDto.setTemplate(problemTemplate);
        return R.success(problemDto);
    }

    @GetMapping("submit")
    @ApiOperation("提交代码")
    public R<RunDto> submit(@ApiParam("代码") String code,@ApiParam("用户id") Integer userId,@ApiParam("题目id") Integer problemId,@ApiParam("语言，0为java，1为c") Integer language){
        RunDto result = null;
        if(language == 0){
            result = problemService.JavaJudge(code, userId, problemId, 0);
        }else if(language == 1){
            result = problemService.CJudge(code, userId, problemId, 0);
        }
        return R.success(result);
    }

    @GetMapping("getAnswer")
    @ApiOperation("获取题目答案")
    public R<String> getAnswer(@ApiParam("题目id") Integer problemId,@ApiParam("语言，0为java，1为c") Integer language){
        String result = problemService.getAnswer(problemId, language);
        return R.success(result);
    }

    @GetMapping("getRandomProblem")
    @ApiOperation("获取随机题目")
    public R<Problem> getRandomProblem(){
        Problem problem = problemService.getRandomProblem();
        return R.success(problem);
    }

    @GetMapping("getEveryProblem")
    @ApiOperation("获取每日一题")
    public R<Problem> getEveryProblem(){
        Problem problem = problemService.getRandomProblem();
        return R.success(problem);
    }
}
