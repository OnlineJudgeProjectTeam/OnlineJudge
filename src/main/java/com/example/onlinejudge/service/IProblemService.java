package com.example.onlinejudge.service;

import com.example.onlinejudge.entity.Problem;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author diandianjun
 * @since 2023-06-13
 */
public interface IProblemService extends IService<Problem> {
    public String JavaJudge(String code,Integer userId,Integer problemId,Integer number);
    Problem QueryById(Integer id);
    Boolean JavaCompile(String workingDirectory,Integer number);
    Boolean JavaRun(String workingDirectory,Integer number);
    public String CJudge(String code,Integer userId,Integer problemId,Integer number);
    Boolean CCompile(String workingDirectory,Integer number);
    Boolean CRun(String workingDirectory,Integer number);
}
