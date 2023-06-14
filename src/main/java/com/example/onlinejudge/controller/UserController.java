package com.example.onlinejudge.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.onlinejudge.common.R;
import com.example.onlinejudge.dto.UserCodeDto;
import com.example.onlinejudge.dto.UserDto;
import com.example.onlinejudge.entity.User;
import com.example.onlinejudge.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.onlinejudge.common.R;
import com.example.onlinejudge.utils.AliOSSUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author diandianjun
 * @since 2023-06-13
 */
@RestController
@RequestMapping("/user")
@Api("用户管理")
public class UserController {
    @Autowired
    private IUserService userService;

    @GetMapping("/RegisterSend")
    @ApiOperation("发送注册验证码")
    public R<String> RegisterSend(@ApiParam("注册邮箱") String email){
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getEmail,email);
        User user = userService.getOne(userLambdaQueryWrapper);
        if(user!=null){
            return R.error("该邮箱已被注册");
        }
        userService.sendCode(email);
        return R.success("验证码已发送");
    }

    @GetMapping("/LoginSend")
    @ApiOperation("发送登录验证码")
    public R<String> LoginSend(@ApiParam("登录邮箱") String email){
        userService.sendCode(email);
        return R.success("验证码已发送");
    }

    @PostMapping("/Register")
    @ApiOperation("注册")
    public R<String> Register(@RequestBody @ApiParam("注册数据") UserCodeDto userCodeDto){
        return userService.register(userCodeDto.getEmail(), userCodeDto.getUsername(), userCodeDto.getPassword(), userCodeDto.getCode(), userCodeDto.getName());
    }

    @PostMapping("/Login")
    @ApiOperation("登录")
    public R<UserDto> Login(@RequestBody @ApiParam("登录数据") User user){
        return userService.login(user.getUsername(), user.getPassword());
    }

    @PostMapping("/LoginByCode")
    @ApiOperation("验证码登录")
    public R<UserDto> LoginByCode(@RequestBody @ApiParam("登录数据") UserCodeDto user){
        return userService.loginByCode(user.getEmail(), user.getCode());
    }

    @GetMapping("/Logout")
    @ApiOperation("登出")
    public R<String> Logout(@ApiParam("用户id") Integer id){
        return userService.logout(id);
    }
    @Autowired
    AliOSSUtils aliOSSUtils;
    @PostMapping("/upload")
    public R<String> upload(MultipartFile image) throws Exception {
        String url = aliOSSUtils.upload(image);
        return R.success(url);
    }
}
