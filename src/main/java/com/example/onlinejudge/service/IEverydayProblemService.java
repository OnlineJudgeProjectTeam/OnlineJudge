package com.example.onlinejudge.service;

import com.example.onlinejudge.entity.EverydayProblem;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.onlinejudge.entity.Problem;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author diandianjun
 * @since 2023-06-14
 */
public interface IEverydayProblemService extends IService<EverydayProblem> {
    public Problem getEverydayProblem();
}
