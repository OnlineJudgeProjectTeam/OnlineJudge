package com.example.onlinejudge.service;

import com.example.onlinejudge.entity.Favorite;
import com.example.onlinejudge.entity.FavoriteDto;
import com.example.onlinejudge.entity.Problem;
import com.github.pagehelper.PageInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class IFavoriteServiceTest {
    @Autowired
    private IFavoriteService favoriteService;

    @Test
    public void addFavoriteTest(){
        int userId = 1;
        int problemId = 1;
        Boolean aBoolean = favoriteService.addFavorite(userId, problemId);
        System.out.println(aBoolean);
    }

    @Test
    public void getFavoriteListTest(){
        int userId = 1;
        int pageNum = 1;
        int pageSize = 1;
        int navSize = 1;
        PageInfo<Problem> favoriteList = favoriteService.getFavoriteList(userId, pageNum, pageSize, navSize);
        System.out.println(favoriteList);
    }
}
