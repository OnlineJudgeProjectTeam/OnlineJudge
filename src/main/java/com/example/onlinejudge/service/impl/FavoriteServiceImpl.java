package com.example.onlinejudge.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.onlinejudge.entity.Favorite;
import com.example.onlinejudge.mapper.FavoriteMapper;
import com.example.onlinejudge.service.IFavoriteService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements IFavoriteService {

    @Override
    public void addFavorite(Integer userId, Integer problemId){
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setProblemId(problemId);
        this.save(favorite);
    }

    @Override
    public PageInfo<Favorite> getFavoriteList(Integer userId, Integer pageNum, Integer pageSize,Integer navSize){
        PageHelper.startPage(pageNum,pageSize);
        LambdaQueryWrapper<Favorite> favoriteLambdaQueryWrapper = new LambdaQueryWrapper<>();
        favoriteLambdaQueryWrapper.eq(Favorite::getUserId,userId);
        return new PageInfo<>(this.list(favoriteLambdaQueryWrapper),navSize);
    }
}
