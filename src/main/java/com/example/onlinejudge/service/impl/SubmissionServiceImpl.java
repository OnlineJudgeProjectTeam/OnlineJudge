package com.example.onlinejudge.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.onlinejudge.dto.ACData;
import com.example.onlinejudge.dto.SubmissionDto;
import com.example.onlinejudge.entity.Problem;
import com.example.onlinejudge.entity.Submission;
import com.example.onlinejudge.entity.User;
import com.example.onlinejudge.mapper.ProblemMapper;
import com.example.onlinejudge.mapper.SubmissionMapper;
import com.example.onlinejudge.service.FileService;
import com.example.onlinejudge.service.ISubmissionService;
import com.example.onlinejudge.service.IUserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author diandianjun
 * @since 2023-06-13
 */
@Service
public class SubmissionServiceImpl extends ServiceImpl<SubmissionMapper, Submission> implements ISubmissionService {

    @Autowired
    private ProblemMapper problemMapper;
    @Autowired
    private IUserService userService;
    @Value("${path.user}")
    private String userPath;
    @Autowired
    private FileService fileService;

    @Override
    public ACData getAcData(Integer userId, String difficulty) {
        ACData ACData = new ACData();
        {
            LambdaQueryWrapper<Submission> submissionLambdaQueryWrapper = new LambdaQueryWrapper<>();
            submissionLambdaQueryWrapper.eq(Submission::getUserId, userId);
            submissionLambdaQueryWrapper.eq(Submission::getPass, 1);
            if (!difficulty.equals("")) {
                submissionLambdaQueryWrapper.eq(Submission::getDifficulty, difficulty);
            }
            List<Submission> list = this.list(submissionLambdaQueryWrapper);
            ACData.setAcNum(list.size());
        }
        {
            LambdaQueryWrapper<Submission> submissionLambdaQueryWrapper = new LambdaQueryWrapper<>();
            submissionLambdaQueryWrapper.eq(Submission::getUserId, userId);
            if (!difficulty.equals("")) {
                submissionLambdaQueryWrapper.eq(Submission::getDifficulty, difficulty);
            }
            List<Submission> list = this.list(submissionLambdaQueryWrapper);
            ACData.setSubmitNum(list.size());
        }
        if (ACData.getSubmitNum() == 0) {
            ACData.setAcRate(new BigDecimal(0));
            return ACData;
        }
        BigDecimal divide = new BigDecimal(ACData.getAcNum()).divide(new BigDecimal(ACData.getSubmitNum()), 2, BigDecimal.ROUND_HALF_UP);
        BigDecimal multiply = divide.multiply(new BigDecimal(100));
        ACData.setAcRate(multiply);
        return ACData;
    }

    @Override
    public PageInfo<SubmissionDto> getSubmissionList(Integer userId, Integer pageNum, Integer pageSize, Integer navSize, Integer language, String difficulty, Integer pass, LocalDateTime startTime, LocalDateTime endTime, Integer problemId) {
        LambdaQueryWrapper<Submission> submissionLambdaQueryWrapper = new LambdaQueryWrapper<>();
        submissionLambdaQueryWrapper.eq(Submission::getUserId, userId);
        User user = userService.getById(userId);
        if (language != null) {
            submissionLambdaQueryWrapper.eq(Submission::getLanguage, language);
        }
        if (difficulty != null && !difficulty.equals("")) {
            submissionLambdaQueryWrapper.eq(Submission::getDifficulty, difficulty);
        }
        if (pass != null) {
            submissionLambdaQueryWrapper.eq(Submission::getPass, pass);
        }
        if (startTime != null) {
            submissionLambdaQueryWrapper.ge(Submission::getExecutionTime, startTime);
        }
        if (endTime != null) {
            submissionLambdaQueryWrapper.le(Submission::getExecutionTime, endTime);
        }
        if (problemId != null) {
            submissionLambdaQueryWrapper.eq(Submission::getProblemId, problemId);
        }
        ArrayList<SubmissionDto> submissionDtos = new ArrayList<>();
        HashMap<Integer, Problem> problemTitleMap = problemMapper.getProblemMap();
        PageHelper.startPage(pageNum, pageSize);
        for (Submission submission : this.list(submissionLambdaQueryWrapper)) {
            SubmissionDto submissionDto = new SubmissionDto(submission);
            Problem problem = problemTitleMap.get(submission.getProblemId());
            submissionDto.setProblemName(problem.getName());
            String userCodePath = userPath + "/" + user.getUsername() + "/" + problem.getName() + "/submission/" + submission.getFolderName();
            String code = fileService.readFile(userCodePath + "/code.txt");
            submissionDto.setCode(code);
            String output;
            if (submission.getPass() == 1) {
                output = fileService.readFile(userCodePath + "/stdout.txt");
            } else {
                output = fileService.readFile(userCodePath + "/stderr.txt");
            }
            submissionDto.setOutput(output);
            submissionDtos.add(submissionDto);
        }
        PageInfo<SubmissionDto> submissionDtoPageInfo = new PageInfo<>(submissionDtos, navSize);
        long count = count(submissionLambdaQueryWrapper);
        submissionDtoPageInfo.setTotal(count);
        return submissionDtoPageInfo;
    }

    @Override
    public BigDecimal getTimeBeat(Integer problemId, Integer language, Long timeCost) {
        LambdaQueryWrapper<Submission> submissionLambdaQueryWrapper = new LambdaQueryWrapper<>();
        submissionLambdaQueryWrapper.eq(Submission::getProblemId, problemId);
        submissionLambdaQueryWrapper.eq(Submission::getLanguage, language);
        submissionLambdaQueryWrapper.eq(Submission::getPass, 1);
        long total = count(submissionLambdaQueryWrapper);
        if (total == 0)
            return new BigDecimal(0);
        submissionLambdaQueryWrapper.ge(Submission::getTimeCost, timeCost);
        long better = count(submissionLambdaQueryWrapper);
        return new BigDecimal(better).divide(new BigDecimal(total), 2, BigDecimal.ROUND_HALF_UP);
    }

    @Override
    public BigDecimal getMemoryBeat(Integer problemId, Integer language, Long memoryCost) {
        LambdaQueryWrapper<Submission> submissionLambdaQueryWrapper = new LambdaQueryWrapper<>();
        submissionLambdaQueryWrapper.eq(Submission::getProblemId, problemId);
        submissionLambdaQueryWrapper.eq(Submission::getLanguage, language);
        submissionLambdaQueryWrapper.eq(Submission::getPass, 1);
        long total = count(submissionLambdaQueryWrapper);
        if (total == 0)
            return new BigDecimal(0);
        submissionLambdaQueryWrapper.ge(Submission::getMemoryCost, memoryCost);
        long better = count(submissionLambdaQueryWrapper);
        return new BigDecimal(better).divide(new BigDecimal(total), 2, BigDecimal.ROUND_HALF_UP);
    }
}
