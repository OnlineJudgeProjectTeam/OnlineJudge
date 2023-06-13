package com.example.onlinejudge.service.impl;

import com.example.onlinejudge.entity.Problem;
import com.example.onlinejudge.mapper.ProblemMapper;
import com.example.onlinejudge.service.IProblemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.onlinejudge.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author diandianjun
 * @since 2023-06-13
 */
@Service
public class ProblemServiceImpl extends ServiceImpl<ProblemMapper, Problem> implements IProblemService {
    @Value("${path.user}")
    private String userPath;
    @Value("${path.problem}")
    private String problemPath;
    @Autowired
    private IUserService userService;

    @Override
    public String JavaJudge(String code, Integer userId,Integer problemId){
        return null;
    }
}
