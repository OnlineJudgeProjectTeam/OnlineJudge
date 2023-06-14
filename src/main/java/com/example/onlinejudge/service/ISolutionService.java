package com.example.onlinejudge.service;

import com.example.onlinejudge.common.R;
import com.example.onlinejudge.entity.Solution;
import com.baomidou.mybatisplus.extension.service.IService;
import io.swagger.models.auth.In;

import java.io.IOException;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author diandianjun
 * @since 2023-06-13
 */
public interface ISolutionService extends IService<Solution> {
    R<String > createSolution(String code, Integer problemId, Integer userId,Integer language) throws IOException;
}
