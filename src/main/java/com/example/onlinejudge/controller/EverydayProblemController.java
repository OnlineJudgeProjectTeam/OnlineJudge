package com.example.onlinejudge.controller;


import com.example.onlinejudge.common.R;
import com.example.onlinejudge.entity.Problem;
import com.example.onlinejudge.service.IEverydayProblemService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author diandianjun
 * @since 2023-06-14
 */
@RestController
@RequestMapping("/everyday-problem")
@CrossOrigin
public class EverydayProblemController {

    @Autowired
    private IEverydayProblemService everydayProblemService;

    @GetMapping("get-everyday-problem")
    @ApiOperation("获取每日一题")
    public R<Problem> getEveryProblem(){
        Problem problem = everydayProblemService.getEverydayProblem();
        return R.success(problem);
    }
}
