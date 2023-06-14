package com.example.onlinejudge.controller;


import com.example.onlinejudge.common.UserHolder;
import com.example.onlinejudge.dto.UserDto;
import com.example.onlinejudge.service.IFavoriteService;
import com.example.onlinejudge.service.IUserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author diandianjun
 * @since 2023-06-13
 */
@RestController
@RequestMapping("/favorite")
@ApiOperation("收藏管理")
public class FavoriteController {
    @Autowired
    private IFavoriteService favoriteService;

    @GetMapping("/addFavorite")
    @ApiOperation("添加/取消收藏")
    public void addFavorite(@ApiParam("题目id") Integer problemId){
        UserDto user = UserHolder.getUser();
        favoriteService.addFavorite(user.getId(), problemId);
    }

    @GetMapping("/getFavoriteList")
    @ApiOperation("获取收藏列表")
    public void getFavoriteList(@ApiParam("第几页") Integer pageNum,@ApiParam("每页几条数据") Integer pageSize,@ApiParam("导航页个数") Integer navSize){
        UserDto user = UserHolder.getUser();
        favoriteService.getFavoriteList(user.getId(), pageNum, pageSize, navSize);
    }
}
