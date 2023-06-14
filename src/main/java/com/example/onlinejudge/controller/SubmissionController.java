package com.example.onlinejudge.controller;


import com.example.onlinejudge.common.R;
import com.example.onlinejudge.dto.ACData;
import com.example.onlinejudge.service.ISubmissionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
@RequestMapping("/submission")
@Api("做题记录管理")
public class SubmissionController {
    @Autowired
    private ISubmissionService submissionService;

//    @GetMapping("/getAcRate")
//    @ApiOperation("获取通过率")
//    public R<ACData> getAcRate(){
//
//    }
}
