package com.example.onlinejudge.service;

import com.example.onlinejudge.common.R;
import com.example.onlinejudge.dto.SolutionDto;
import com.example.onlinejudge.entity.Solution;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import io.swagger.models.auth.In;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author diandianjun
 * @since 2023-06-13
 */
public interface ISolutionService extends IService<Solution> {
    Solution  createSolution(String content, Integer problemId, Integer userId,Integer language) throws IOException;

    R<String>  deleteSolution(Integer solutionId) throws IOException;

    Solution updateSolution(Integer solutionId,String content);

    R<PageInfo<SolutionDto>> getSolutionList(Integer pageNum, Integer pageSize, Integer navSize, Integer problemId,Integer userId);

    R<String> likeSolution(Integer solutionId);
}
