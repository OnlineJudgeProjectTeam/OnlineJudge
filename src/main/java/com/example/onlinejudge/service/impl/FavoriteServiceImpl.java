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
    public Boolean addFavorite(Integer userId, Integer problemId){
        LambdaQueryWrapper<Favorite> favoriteLambdaQueryWrapper = new LambdaQueryWrapper<>();
        favoriteLambdaQueryWrapper.eq(Favorite::getUserId,userId);
        favoriteLambdaQueryWrapper.eq(Favorite::getProblemId,problemId);
        if(this.getOne(favoriteLambdaQueryWrapper)!=null){
            return false;
        }
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setProblemId(problemId);
        this.save(favorite);
        return true;
    }

    @Override
    public PageInfo<Favorite> getFavoriteList(Integer userId, Integer pageNum, Integer pageSize,Integer navSize){
        PageHelper.startPage(pageNum,pageSize);
        LambdaQueryWrapper<Favorite> favoriteLambdaQueryWrapper = new LambdaQueryWrapper<>();
        favoriteLambdaQueryWrapper.eq(Favorite::getUserId,userId);
        return new PageInfo<>(this.list(favoriteLambdaQueryWrapper),navSize);
    }
}
