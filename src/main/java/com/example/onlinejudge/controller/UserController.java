package com.example.onlinejudge.controller;


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
public class UserController {
    @Autowired
    AliOSSUtils aliOSSUtils;
    @PostMapping("/upload")
    public R<String> upload(MultipartFile image) throws Exception {
        String url = aliOSSUtils.upload(image);
        return R.success(url);
    }
}