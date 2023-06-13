package com.example.onlinejudge.service.impl;

import com.example.onlinejudge.entity.User;
import com.example.onlinejudge.mapper.UserMapper;
import com.example.onlinejudge.service.IUserService;
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
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
