package com.example.onlinejudge.controller;


import com.example.onlinejudge.common.R;
import com.example.onlinejudge.common.UserHolder;
import com.example.onlinejudge.dto.ACData;
import com.example.onlinejudge.dto.SubmissionDto;
import com.example.onlinejudge.dto.TotalAcData;
import com.example.onlinejudge.service.ISubmissionService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;

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
@CrossOrigin
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

    @GetMapping("/get-total-ac-rate")
    @ApiOperation("获取总通过率")
    public R<TotalAcData> getTotalAcRate(@ApiParam("用户id") Integer userId){
        if(userId==null){
            userId = UserHolder.getUser().getId();
        }
        TotalAcData totalAcData = new TotalAcData();
        ACData total = submissionService.getAcData(userId, "");
        ACData easy = submissionService.getAcData(userId, "简单");
        ACData medium = submissionService.getAcData(userId, "中等");
        ACData hard = submissionService.getAcData(userId, "困难");
        ACData[] acData = {total, easy, medium, hard};
        totalAcData.setAcData(acData);
        return R.success(totalAcData);
    }

    @GetMapping("/get-submission-list")
    @ApiOperation("获取做题记录列表")
    public R<PageInfo<SubmissionDto>> getSubmissionList(@ApiParam("第几页") Integer pageNum,@ApiParam("一页几条数据") Integer pageSize,@ApiParam("导航页个数") Integer navSize,@ApiParam("编程语言") Integer language,@ApiParam("难度") String difficulty,@ApiParam("是否通过") Integer pass,@ApiParam("起始时间") String startTime,@ApiParam("结束时间") String endTime,Integer problemId) throws ParseException {
        LocalDateTime startLocalDateTime = null;
        LocalDateTime endLocalDateTime = null;
        startLocalDateTime = getLocalDateTime(startTime, startLocalDateTime);
        endLocalDateTime = getLocalDateTime(endTime, endLocalDateTime);
        Integer userId = UserHolder.getUser().getId();
        PageInfo<SubmissionDto> submissionList = submissionService.getSubmissionList(userId, pageNum, pageSize, navSize, language, difficulty, pass, startLocalDateTime, endLocalDateTime,problemId);
        return R.success(submissionList);
    }

    private LocalDateTime getLocalDateTime(String time, LocalDateTime localDateTime) throws ParseException {
        if(time !=null&&!time.equals("")){
            time = time.substring(4, time.indexOf("GMT")-1);
            SimpleDateFormat sf = new SimpleDateFormat("MMM dd yyyy HH:mm:ss", Locale.ENGLISH);
            Date date = sf.parse(time);
            Instant instant = date.toInstant();
            ZoneId zoneId = ZoneId.systemDefault();
            localDateTime = instant.atZone(zoneId).toLocalDateTime();
        }
        return localDateTime;
    }
}
