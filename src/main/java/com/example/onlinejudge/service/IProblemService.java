package com.example.onlinejudge.service;

import com.example.onlinejudge.dto.RunDto;
import com.example.onlinejudge.entity.Problem;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author diandianjun
 * @since 2023-06-13
 */
public interface IProblemService extends IService<Problem> {
    public RunDto JavaJudge(String code, Integer userId, Integer problemId, Integer number) throws IOException;
    Problem QueryById(Integer id);
    Boolean JavaCompile(String workingDirectory,Integer number);
    Boolean JavaRun(String workingDirectory,Integer number);
    public RunDto CJudge(String code,Integer userId,Integer problemId,Integer number) throws IOException;
    Boolean CCompile(String workingDirectory,Integer number);
    Boolean CRun(String workingDirectory,Integer number);
    public Problem getRandomProblem();
    public PageInfo<Problem> getProblemList(Integer pageNum, Integer pageSize, Integer navSize,String name, String tags, String difficulty);
    public String getAnswer(Integer problemId,Integer language);
    public String getProblemDescription(Integer problemId);
    public String getProblemTemplate(Integer problemId,Integer language);
    void isProblemFavorite(Problem problem);
}
