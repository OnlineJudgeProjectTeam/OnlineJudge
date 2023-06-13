package com.example.onlinejudge.service;

import com.example.onlinejudge.entity.Favorite;
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
        favoriteService.addFavorite(userId, problemId);
    }

    @Test
    public void getFavoriteListTest(){
        int userId = 1;
        int pageNum = 1;
        int pageSize = 1;
        int navSize = 1;
        PageInfo<Favorite> favoriteList = favoriteService.getFavoriteList(userId, pageNum, pageSize, navSize);
        System.out.println(favoriteList);
    }
}
