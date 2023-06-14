package com.example.onlinejudge.service;

import com.example.onlinejudge.common.R;
import com.example.onlinejudge.dto.UserDto;
import com.example.onlinejudge.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author diandianjun
 * @since 2023-06-13
 */
public interface IUserService extends IService<User> {
    R<String> sendCode(String phone);

    R<String> register(String email, String username, String password, String code, String name);

    R<UserDto> login(String username, String password);

    R<UserDto> loginByCode(String email, String code);

    R<String>  update(User user);

    User QueryById(Integer id);

    R<String> logout(Integer id);
}
