package com.example.onlinejudge.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.onlinejudge.common.R;
import com.example.onlinejudge.entity.Favorite;
import com.example.onlinejudge.entity.FavoriteDto;
import com.example.onlinejudge.entity.Problem;
import com.example.onlinejudge.mapper.FavoriteMapper;
import com.example.onlinejudge.service.IFavoriteService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.onlinejudge.service.IProblemService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.example.onlinejudge.utils.RedisConstants.CACHE_PROBLEM_KEY;
import static com.example.onlinejudge.utils.RedisConstants.CACHE_PROBLEM_TTL;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author diandianjun
 * @since 2023-06-13
 */
@Service
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements IFavoriteService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private IProblemService problemService;

    @Override
    public Boolean addFavorite(Integer userId, Integer problemId){
        LambdaQueryWrapper<Favorite> favoriteLambdaQueryWrapper = new LambdaQueryWrapper<>();
        favoriteLambdaQueryWrapper.eq(Favorite::getUserId,userId);
        favoriteLambdaQueryWrapper.eq(Favorite::getProblemId,problemId);
        Favorite favorite = this.getOne(favoriteLambdaQueryWrapper);
        if(favorite!=null){
            this.removeById(favorite.getId());
            problemService.update().setSql("favorites = favorites - 1").eq("id", problemId).update();
            stringRedisTemplate.delete(CACHE_PROBLEM_KEY+problemId);
            return true;
        }
        favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setProblemId(problemId);
        this.save(favorite);
        problemService.update().setSql("favorites = favorites + 1").eq("id", problemId).update();
        stringRedisTemplate.delete(CACHE_PROBLEM_KEY+problemId);
        return true;
    }

    @Override
    public PageInfo<Problem> getFavoriteList(Integer userId, Integer pageNum, Integer pageSize, Integer navSize){
        PageHelper.startPage(pageNum,pageSize);
        LambdaQueryWrapper<Favorite> favoriteLambdaQueryWrapper = new LambdaQueryWrapper<>();
        favoriteLambdaQueryWrapper.eq(Favorite::getUserId,userId);
        List<Favorite> list = this.list(favoriteLambdaQueryWrapper);
        ArrayList<Problem> problems = new ArrayList<>();
        for (Favorite favorite : list) {
            Problem problem = problemService.getById(favorite.getProblemId());
            problems.add(problem);
        }
        PageInfo<Problem> problemPageInfo = new PageInfo<>(problems, navSize);
        long count = count(favoriteLambdaQueryWrapper);
        problemPageInfo.setTotal(count);
        return problemPageInfo;
    }
}
