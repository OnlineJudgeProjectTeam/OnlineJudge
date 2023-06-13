package com.example.onlinejudge.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.onlinejudge.common.R;
import com.example.onlinejudge.dto.UserDto;
import com.example.onlinejudge.entity.User;
import com.example.onlinejudge.mapper.UserMapper;
import com.example.onlinejudge.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.onlinejudge.service.MailService;
import com.example.onlinejudge.utils.PasswordEncoder;
import com.example.onlinejudge.utils.RegexUtils;
import com.example.onlinejudge.utils.ValidateImageCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.example.onlinejudge.utils.RedisConstants.*;

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
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getUsername,username);
        User user = getOne(userLambdaQueryWrapper);
        if(user==null){
            return R.error("用户不存在");
        }
        if(PasswordEncoder.matches(user.getPassword(),password)){
            return R.error("密码错误");
        }
        String token = UUID.randomUUID().toString();
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getEmail());
        userDto.setAvatar(user.getAvatar());
        userDto.setName(user.getName());
        userDto.setSex(user.getSex());
        userDto.setSchool(user.getSchool());
        userDto.setCreatedTime(user.getCreatedTime());
        userDto.setCompany(user.getCompany());
        userDto.setDescription(user.getDescription());
        userDto.setToken(token);
        Map<String, Object> userMap = BeanUtil.beanToMap(userDto, new HashMap<>(),
                CopyOptions.create()
                        .setIgnoreNullValue(true)
                        //在setFieldValueEditor中也需要判空
                        .setFieldValueEditor((fieldName,fieldValue) -> {
                            if (fieldValue == null){
                                fieldValue = "0";
                            }else {
                                fieldValue = fieldValue + "";
                            }
                            return fieldValue;
                        }));
        //存储
        stringRedisTemplate.opsForHash().putAll(LOGIN_USER_KEY+token,userMap);
        //设置有效期
        stringRedisTemplate.expire(LOGIN_USER_KEY+token,LOGIN_USER_TTL, TimeUnit.MINUTES);
        return R.success(userDto);
    }

    @Override
    public R<UserDto> loginByCode(String email, String code) {
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getEmail,email);
        User user = getOne(userLambdaQueryWrapper);
        if(user==null){
            return R.error("用户不存在");
        }
        String redisCode = stringRedisTemplate.opsForValue().get(LOGIN_CODE_KEY+email);
        if(redisCode==null){
            return R.error("验证码已过期");
        }
        if(!redisCode.equals(code)){
            return R.error("验证码错误");
        }
        String token = UUID.randomUUID().toString();
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getEmail());
        userDto.setAvatar(user.getAvatar());
        userDto.setName(user.getName());
        userDto.setSex(user.getSex());
        userDto.setSchool(user.getSchool());
        userDto.setCreatedTime(user.getCreatedTime());
        userDto.setCompany(user.getCompany());
        userDto.setDescription(user.getDescription());
        userDto.setToken(token);
        Map<String, Object> userMap = BeanUtil.beanToMap(userDto, new HashMap<>(),
                CopyOptions.create()
                        .setIgnoreNullValue(true)
                        //在setFieldValueEditor中也需要判空
                        .setFieldValueEditor((fieldName,fieldValue) -> {
                            if (fieldValue == null){
                                fieldValue = "0";
                            }else {
                                fieldValue = fieldValue + "";
                            }
                            return fieldValue;
                        }));
        //存储
        stringRedisTemplate.opsForHash().putAll(LOGIN_USER_KEY+token,userMap);
        //设置有效期
        stringRedisTemplate.expire(LOGIN_USER_KEY+token,LOGIN_USER_TTL, TimeUnit.MINUTES);
        return R.success(userDto);
    }
}
