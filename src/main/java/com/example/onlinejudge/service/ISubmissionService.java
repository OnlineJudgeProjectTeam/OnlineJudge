package com.example.onlinejudge.service;

import com.example.onlinejudge.dto.ACData;
import com.example.onlinejudge.dto.SubmissionDto;
import com.example.onlinejudge.entity.Submission;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author diandianjun
 * @since 2023-06-13
 */
public interface ISubmissionService extends IService<Submission> {
    public ACData getAcData(Integer userId, String difficulty);
    PageInfo<SubmissionDto> getSubmissionList(Integer userId, Integer pageNum, Integer pageSize, Integer navSize, Integer language, String difficulty, Integer pass, LocalDateTime startTime, LocalDateTime endTime,Integer problemId);
    public BigDecimal getTimeBeat(Integer problemId, Integer language, Long timeCost);
    public BigDecimal getMemoryBeat(Integer problemId, Integer language, Long memoryCost);
}
