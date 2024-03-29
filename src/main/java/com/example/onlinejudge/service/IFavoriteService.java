package com.example.onlinejudge.service;

import com.example.onlinejudge.entity.Favorite;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.onlinejudge.entity.FavoriteDto;
import com.example.onlinejudge.entity.Problem;
import com.github.pagehelper.PageInfo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author diandianjun
 * @since 2023-06-13
 */
public interface IFavoriteService extends IService<Favorite> {
    public Boolean addFavorite(Integer userId, Integer problemId);

    PageInfo<Problem> getFavoriteList(Integer userId, Integer pageNum, Integer pageSize, Integer navSize);
}
