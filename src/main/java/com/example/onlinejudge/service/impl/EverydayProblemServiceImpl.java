package com.example.onlinejudge.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.onlinejudge.entity.EverydayProblem;
import com.example.onlinejudge.entity.Problem;
import com.example.onlinejudge.mapper.EverydayProblemMapper;
import com.example.onlinejudge.service.IEverydayProblemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.onlinejudge.service.IProblemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.example.onlinejudge.utils.RedisConstants.*;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author diandianjun
 * @since 2023-06-14
 */
@Service
public class EverydayProblemServiceImpl extends ServiceImpl<EverydayProblemMapper, EverydayProblem> implements IEverydayProblemService {

    @Autowired
    private IProblemService problemService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Problem getEverydayProblem() {
        String key = CACHE_EVERYDAY_PROBLEM_KEY+"today";
        Map<Object, Object> everydayProblemMap = stringRedisTemplate.opsForHash().entries(key);
        if(everydayProblemMap.isEmpty()){
            LocalDate now = LocalDate.now();
            LambdaQueryWrapper<EverydayProblem> everydayProblemLambdaQueryWrapper = new LambdaQueryWrapper<>();
            everydayProblemLambdaQueryWrapper.eq(EverydayProblem::getDate,now);
            EverydayProblem everydayProblem = this.getOne(everydayProblemLambdaQueryWrapper);
            if(everydayProblem == null){
                Problem randomProblem = problemService.getRandomProblem();
                everydayProblem = new EverydayProblem();
                everydayProblem.setProblemId(randomProblem.getId());
                everydayProblem.setDate(now);
                this.save(everydayProblem);
                //存到redis中
                Map<String, Object> everydayProblemMap1 = BeanUtil.beanToMap(everydayProblem, new HashMap<>(),
                        CopyOptions.create()
                                .setIgnoreNullValue(true)
                                //在setFieldValueEditor中也需要判空
                                .setFieldValueEditor((fieldName,fieldValue) -> {
                                    if (fieldValue == null){
                                        fieldValue = "0";
                                    }else {
                                        fieldValue = fieldValue + "";
                                    }
                                    return fieldValue;
                                }));
                //存储
                stringRedisTemplate.opsForHash().putAll(CACHE_EVERYDAY_PROBLEM_KEY+"today",everydayProblemMap1);
                //设置有效期
                stringRedisTemplate.expire(CACHE_EVERYDAY_PROBLEM_KEY+"today",CACHE_EVERYDAY_PROBLEM_TTL, TimeUnit.MINUTES);
                return randomProblem;
            }else{
                //存到redis中
                Map<String, Object> everydayProblemMap1 = BeanUtil.beanToMap(everydayProblem, new HashMap<>(),
                        CopyOptions.create()
                                .setIgnoreNullValue(true)
                                //在setFieldValueEditor中也需要判空
                                .setFieldValueEditor((fieldName,fieldValue) -> {
                                    if (fieldValue == null){
                                        fieldValue = "0";
                                    }else {
                                        fieldValue = fieldValue + "";
                                    }
                                    return fieldValue;
                                }));
                //存储
                stringRedisTemplate.opsForHash().putAll(CACHE_EVERYDAY_PROBLEM_KEY+"today",everydayProblemMap1);
                //设置有效期
                stringRedisTemplate.expire(CACHE_EVERYDAY_PROBLEM_KEY+"today",CACHE_EVERYDAY_PROBLEM_TTL, TimeUnit.MINUTES);
                return problemService.QueryById(everydayProblem.getProblemId());
            }
        }else{
            Integer problemId = Integer.parseInt((String) everydayProblemMap.get("problemId"));
            return problemService.QueryById(problemId);
        }
    }
}
