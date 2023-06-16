package com.example.onlinejudge.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.onlinejudge.common.R;
import com.example.onlinejudge.common.UserHolder;
import com.example.onlinejudge.dto.UserCodeDto;
import com.example.onlinejudge.dto.UserDto;
import com.example.onlinejudge.entity.User;
import com.example.onlinejudge.mapper.UserMapper;
import com.example.onlinejudge.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.onlinejudge.service.MailService;
import com.example.onlinejudge.utils.PasswordEncoder;
import com.example.onlinejudge.utils.RegexUtils;
import com.example.onlinejudge.utils.ValidateImageCodeUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.HashMap;
import java.util.List;
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

    @Value("${path.user}")
    private String userFilePath;


    @Override
    public R<String> sendCode(String email) {
        //校验邮箱
        if(!RegexUtils.isEmailInvalid(email)){
            return R.error("邮箱不正确");
        }
        if(Boolean.TRUE.equals(stringRedisTemplate.hasKey(LOGIN_CODE_KEY + email))){
            Long expire = stringRedisTemplate.getExpire(LOGIN_CODE_KEY + email, TimeUnit.SECONDS);
            if(expire>840){
                return R.error("验证码已发送，请勿重复发送，"+(expire-840)+"秒后可重新发送");
            }
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
    public R<String> register(String email, String username, String password, String code,String name) {
        String cacheCode = stringRedisTemplate.opsForValue().get(LOGIN_CODE_KEY + email);
        if (cacheCode == null || !cacheCode.equals(code)) {
            // 不一致，报错
            return R.error("验证码不一致");
        }
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        User tbUser = getOne(queryWrapper);
        if (tbUser!=null) {
            return R.error("用户名已存在");
        }
        if (!RegexUtils.isPasswordInvalid(password)) {
            return R.error("密码格式不正确");
        }
        password = PasswordEncoder.encode(password);
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setName(name);
        // 创建用户代码文件夹
        String userCodeFolderPath = userFilePath +"/" + username;
        File userCodeFolder = new File(userCodeFolderPath);
        if (!userCodeFolder.exists()) {
            userCodeFolder.mkdirs();
        }
        save(user);
        return R.success("注册成功");
    }
    @Override
    public R<UserDto> login(String username, String password) {
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getUsername,username);
        User user = getOne(userLambdaQueryWrapper);
        if(user==null){
            return R.error("用户不存在");
        }
        if(!PasswordEncoder.matches(user.getPassword(),password)){
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

    @Override
    public R<String> update(User user) {
        user.setId(UserHolder.getUser().getId());
        updateById(user);
        return R.success("保存成功");
    }

    @Override
    public User QueryById(Integer id) {
        String key = CACHE_USER_KEY+id;
        Map<Object, Object> userMap = stringRedisTemplate.opsForHash().entries(key);
        if(userMap.isEmpty()){
            User user = getById(id);
            if(user==null){
                user = new User();
            }
            Map<String, Object> userMap1 = BeanUtil.beanToMap(user, new HashMap<>(),
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
            stringRedisTemplate.opsForHash().putAll(CACHE_USER_KEY+id,userMap1);
            //设置有效期
            stringRedisTemplate.expire(CACHE_USER_KEY+id,CACHE_USER_TTL, TimeUnit.MINUTES);
            return user;
        }
        if(!userMap.containsKey("id")||userMap.get("id")==""||userMap.get("id")=="0"){
            return null;
        }
        User user = new User();
        BeanUtil.copyProperties(userMap,user);
        return user;
    }

    @Override
    public R<String> updatePassword(UserCodeDto userCodeDto) {
        String cacheCode = stringRedisTemplate.opsForValue().get(LOGIN_CODE_KEY + userCodeDto.getEmail());
        if (cacheCode == null || !cacheCode.equals(userCodeDto.getCode())) {
            // 不一致，报错
            return R.error("验证码不一致");
        }
        // 输入密码的逻辑
        String username = userCodeDto.getUsername();
        String password = userCodeDto.getPassword();
        if (!RegexUtils.isPasswordInvalid(password)) {
            return R.error("密码格式不正确");
        }
        //将密码进行md5加密，
        String finalPassword = PasswordEncoder.encode(password);
        User user = query().eq("email",userCodeDto.getEmail()).one();
        user.setPassword(finalPassword);
        updateById(user);
        return R.success("修改成功");
    }

    @Override
    public R<String> logout() {
        UserDto userDto =UserHolder.getUser();
        String token = userDto.getToken();
        stringRedisTemplate.delete(LOGIN_USER_KEY +token);
        return R.success("退出成功");
    }

    @Override
    public PageInfo<User> getRank(Integer pageNum, Integer pageSize, Integer navSize) {
        PageHelper.startPage(pageNum,pageSize);
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.orderByDesc(User::getAcRate);
        List<User> list = list(userLambdaQueryWrapper);
        for(User user:list){
            user.setPassword(null);
        }
        return new PageInfo<>(list, navSize);
    }
}


