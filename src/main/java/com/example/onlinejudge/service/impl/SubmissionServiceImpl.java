package com.example.onlinejudge.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.onlinejudge.dto.ACData;
import com.example.onlinejudge.dto.SubmissionDto;
import com.example.onlinejudge.entity.Problem;
import com.example.onlinejudge.entity.Submission;
import com.example.onlinejudge.mapper.ProblemMapper;
import com.example.onlinejudge.mapper.SubmissionMapper;
import com.example.onlinejudge.service.IProblemService;
import com.example.onlinejudge.service.ISubmissionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author diandianjun
 * @since 2023-06-13
 */
@Service
public class SubmissionServiceImpl extends ServiceImpl<SubmissionMapper, Submission> implements ISubmissionService {

    @Autowired
    private ProblemMapper problemMapper;

    @Override
    public ACData getAcData(Integer userId, String difficulty) {
        ACData ACData = new ACData();
        {
            LambdaQueryWrapper<Submission> submissionLambdaQueryWrapper = new LambdaQueryWrapper<>();
            submissionLambdaQueryWrapper.eq(Submission::getUserId, userId);
            submissionLambdaQueryWrapper.eq(Submission::getPass, 1);
            if(!difficulty.equals("")){
                submissionLambdaQueryWrapper.eq(Submission::getDifficulty,difficulty);
            }
            List<Submission> list = this.list(submissionLambdaQueryWrapper);
            ACData.setAcNum(list.size());
        }
        {
            LambdaQueryWrapper<Submission> submissionLambdaQueryWrapper = new LambdaQueryWrapper<>();
            submissionLambdaQueryWrapper.eq(Submission::getUserId, userId);
            List<Submission> list = this.list(submissionLambdaQueryWrapper);
            ACData.setSubmitNum(list.size());
        }
        ACData.setAcRate(new BigDecimal(ACData.getAcNum()).divide(new BigDecimal(ACData.getSubmitNum()), 2, BigDecimal.ROUND_HALF_UP));
        return ACData;
    }

    @Override
    public PageInfo<SubmissionDto> getSubmissionList(Integer userId, Integer pageNum, Integer pageSize, Integer navSize, Integer language, String difficulty, Integer pass, LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapper<Submission> submissionLambdaQueryWrapper = new LambdaQueryWrapper<>();
        submissionLambdaQueryWrapper.eq(Submission::getUserId,userId);
        if(language != null){
            submissionLambdaQueryWrapper.eq(Submission::getLanguage,language);
        }
        if(difficulty != null && difficulty !=""){
            submissionLambdaQueryWrapper.eq(Submission::getDifficulty,difficulty);
        }
        if(pass != null){
            submissionLambdaQueryWrapper.eq(Submission::getPass,pass);
        }
        if(startTime != null){
            submissionLambdaQueryWrapper.ge(Submission::getExecutionTime,startTime);
        }
        if(endTime != null){
            submissionLambdaQueryWrapper.le(Submission::getExecutionTime,endTime);
        }
        ArrayList<SubmissionDto> submissionDtos = new ArrayList<>();
        HashMap<Integer, Problem> problemTitleMap = problemMapper.getProblemMap();
        PageHelper.startPage(pageNum,pageSize);
        for (Submission submission : this.list(submissionLambdaQueryWrapper)) {
            SubmissionDto submissionDto = new SubmissionDto(submission);
            Problem problem = problemTitleMap.get(submission.getProblemId());
            submissionDto.setProblemName(problem.getName());
            submissionDtos.add(submissionDto);
        }
        return new PageInfo<>(submissionDtos,navSize);
    }
}
