package com.example.onlinejudge.controller;


import com.example.onlinejudge.common.R;
import com.example.onlinejudge.dto.ACData;
import com.example.onlinejudge.dto.SubmissionDto;
import com.example.onlinejudge.service.ISubmissionService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDateTime;

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

    @GetMapping("/get-ac-rate")
    @ApiOperation("获取通过率")
    public R<ACData> getAcRate(@ApiParam("用户id") Integer userId,@ApiParam("题目难度") String difficulty){
        if(difficulty==null){
            difficulty = "";
        }
        ACData acData = submissionService.getAcData(userId, difficulty);
        return R.success(acData);
    }

    @GetMapping("/get-submission-list")
    @ApiOperation("获取做题记录列表")
    public R getSubmissionList(@ApiParam("用户id") Integer userId,@ApiParam("第几页") Integer pageNum,@ApiParam("一页几条数据") Integer pageSize,@ApiParam("导航页个数") Integer navSize,@ApiParam("编程语言") Integer language,@ApiParam("难度") String difficulty,@ApiParam("是否通过") Integer pass,@ApiParam("起始时间") String startTime,@ApiParam("结束时间") String endTime){
        LocalDateTime startLocalDateTime = null;
        LocalDateTime endLocalDateTime = null;
        if(startTime!=null&&!startTime.equals("")){
            DateTime start = DateTime.parse(startTime);
            startLocalDateTime = Instant.ofEpochMilli(start.getMillis()).atZone(start.getZone().toTimeZone().toZoneId()).toLocalDateTime();
        }
        if(endTime!=null&&!endTime.equals("")){
            DateTime end = DateTime.parse(endTime);
            endLocalDateTime = Instant.ofEpochMilli(end.getMillis()).atZone(end.getZone().toTimeZone().toZoneId()).toLocalDateTime();
        }
        PageInfo<SubmissionDto> submissionList = submissionService.getSubmissionList(userId, pageNum, pageSize, navSize, language, difficulty, pass, startLocalDateTime, endLocalDateTime);
        return R.success(submissionList);
    }
}
