package com.example.onlinejudge.service.impl;

import com.example.onlinejudge.entity.Favorite;
import com.example.onlinejudge.mapper.FavoriteMapper;
import com.example.onlinejudge.service.IFavoriteService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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

}
