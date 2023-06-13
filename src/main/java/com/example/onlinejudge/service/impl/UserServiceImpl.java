package com.example.onlinejudge.service.impl;

import com.example.onlinejudge.common.R;
import com.example.onlinejudge.dto.UserDto;
import com.example.onlinejudge.entity.User;
import com.example.onlinejudge.mapper.UserMapper;
import com.example.onlinejudge.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.onlinejudge.service.MailService;
import com.example.onlinejudge.utils.RegexUtils;
import com.example.onlinejudge.utils.ValidateImageCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.util.concurrent.TimeUnit;

import static com.example.onlinejudge.utils.RedisConstants.LOGIN_CODE_KEY;
import static com.example.onlinejudge.utils.RedisConstants.LOGIN_CODE_TTL;

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
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    MailService mailService;


    @Override
    public R<String> sendCode(String email) {
        //校验邮箱
        if(!RegexUtils.isEmailInvalid(email)){
            return R.error("邮箱不正确");
        }
        //生成验证码
        String securityCode = ValidateImageCodeUtils.getSecurityCode();
        //将验证码保存到redis中
        stringRedisTemplate.opsForValue().set(LOGIN_CODE_KEY+email,securityCode,LOGIN_CODE_TTL, TimeUnit.MINUTES);
        //发送验证码
        String title="oj邮箱验证码";
        String content="您的邮箱验证码为："+securityCode+"，请在15分钟内使用，切勿告诉他人";
        mailService.sendVertifyCode(email,title,content);
        //返回状态
        return R.success("验证码发送成功");
    }

    @Override
    public R<User> register(String email, String username, String password, String code, String name, MultipartFile avatar) {
        return null;
    }

    @Override
    public R<UserDto> login(String username, String password) {
        return null;
    }

    @Override
    public R<UserDto> loginByCode(String email, String code) {
        return null;
    }
}
