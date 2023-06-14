package com.example.onlinejudge.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.onlinejudge.common.R;
import com.example.onlinejudge.common.Type;
import com.example.onlinejudge.common.UserHolder;
import com.example.onlinejudge.dto.SolutionDto;
import com.example.onlinejudge.dto.UserDto;
import com.example.onlinejudge.entity.Problem;
import com.example.onlinejudge.entity.Solution;
import com.example.onlinejudge.entity.User;
import com.example.onlinejudge.mapper.ProblemMapper;
import com.example.onlinejudge.mapper.SolutionMapper;
import com.example.onlinejudge.service.FileService;
import com.example.onlinejudge.service.ISolutionService;
import com.example.onlinejudge.service.IUserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.onlinejudge.utils.RedisConstants.SOLUTION_LIKED_KEY;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author diandianjun
 * @since 2023-06-13
 */
@Service
public class SolutionServiceImpl extends ServiceImpl<SolutionMapper, Solution> implements ISolutionService {

    @Autowired
    private ProblemServiceImpl problemService;
    @Autowired
    private ProblemMapper problemMapper;

    @Value("${path.user}")
    private String userFilePath;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    private IUserService userService;
    @Autowired
    private FileService fileService;

    @Override
    public R<String> createSolution(String code, Integer problemId, Integer userId, Integer language) throws IOException {
        // 根据用户ID和题目ID查询用户和题目信息
        Solution solution = new Solution();
        solution.setUserId(userId);
        solution.setProblemId(problemId);
        solution.setLanguage(language);
        String filepath = getFilePath(solution);
        File Folder = new File(filepath);
        if (!Folder.exists()) {
            Folder.mkdirs();
        }
        File file = new File(Folder + "/" + "solution.md");
        if (!file.exists()) {
            file.createNewFile();
            fileService.writeFile(file.getPath(), code);
            solution.setCreatedTime(LocalDateTime.now());
            // 保存题解对象到数据库
            save(solution);
            return R.success("提交成功");
        } else throw new IllegalArgumentException("题解已存在");
    }

    @Override
    public R<String> deleteSolution(Integer solutionId) throws IOException {
        // 根据题解ID查询题解信息
        Solution solution = getById(solutionId);
        if (solution == null) {
            throw new IllegalArgumentException("错误的题解id");
        }
        // 删除题解文件
        String filePath = getFilePath(solution) + "/solution.md";
        File file = new File(filePath);
        file.delete();
        // 删除题解记录
        removeById(solutionId);
        return R.success("删除成功");
    }

    @Override
    public R<String> updateSolution(Integer solutionId, String code) {
       Solution solution = getById(solutionId);
        if (solution == null) {
            throw new IllegalArgumentException("错误的题解id");
        }
        String filePath = getFilePath(solution) + "/solution.md";
        fileService.writeFile(filePath, code);
        return null;
    }

    @Override
    public R<PageInfo<SolutionDto>> getSolutionList(Integer pageNum, Integer pageSize, Integer navSize, Integer problemId,Integer language) {
        PageHelper.startPage(pageNum, pageSize);
        LambdaQueryWrapper<Solution> solutionLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (problemId != null) {
            solutionLambdaQueryWrapper.eq(Solution::getProblemId, problemId);
        }
        solutionLambdaQueryWrapper.orderByDesc(Solution::getCreatedTime);
        ArrayList<SolutionDto> solutionDtos = new ArrayList<>();
        HashMap<Integer, Problem> problemMap = problemMapper.getProblemMap();
        List<Solution> list = list(solutionLambdaQueryWrapper);
        for(Solution solution:list){
            Integer userId = solution.getUserId();
            User user = userService.QueryById(userId);
            SolutionDto solutionDto = new SolutionDto(solution);
            Problem problem = problemMap.get(solution.getProblemId());
            solutionDto.setProblemName(problem.getName());
            String path = userFilePath + "/" + user.getUsername() + "/" + problem.getName()+"/"+getLanguageFolder(language)+ "/solution.md";
            String content = fileService.readFile(path);
            isSolutionLiked(solution);
            solutionDto.setIsLike(solution.getIsLike());
            solutionDto.setContent(content);
            solutionDtos.add(solutionDto);
        }
        PageInfo<SolutionDto> solutionDtoPageInfo = new PageInfo<>(solutionDtos, navSize);
        return R.success(solutionDtoPageInfo);
    }

    @Override
    public R<String> likeSolution(Integer solutionId) {
       Integer userId = UserHolder.getUser().getId();
        // 判断当前登录用户是否已经点赞
        String key = SOLUTION_LIKED_KEY + solutionId;
        Double score = stringRedisTemplate.opsForZSet().score(key, userId.toString());
        if (score == null) {
            // 如果未点赞，可以点赞,数据库点赞数 + 1
            boolean isSuccess = update().setSql("likes = likes + 1").eq("id", solutionId).update();
            // 保存用户到Redis的set集合  zadd key value score
            if (isSuccess) {
                stringRedisTemplate.opsForZSet().add(key, userId.toString(), System.currentTimeMillis());
            }
        } else {
            // 如果已点赞，取消点赞
            boolean isSuccess = update().setSql("likes = likes - 1").eq("id", solutionId).update();
            if (isSuccess) {
                stringRedisTemplate.opsForZSet().remove(key, userId.toString());
            }
        }
        return R.success("成功");
    }

    private void isSolutionLiked(Solution solution){
        // 1.获取登录用户
        User user = UserHolder.getUser();
        if (user == null) {
            // 用户未登录，无需查询是否点赞
            return;
        }
        Integer userId = user.getId();
        // 2.判断当前登录用户是否已经点赞
        String key = SOLUTION_LIKED_KEY + solution.getId();
        Double score = stringRedisTemplate.opsForZSet().score(key, userId.toString());
        solution.setIsLike(score != null);
    }


    // 根据题解信息获取文件路径
    private String getFilePath(Solution solution) {
    // 构建文件路径逻辑，根据题解的语言和其他属性来确定文件路径
        User user = userService.getById(solution.getUserId());
        Problem problem = problemService.getById(solution.getProblemId());
        if (problem == null) {
            // 用户或题目不存在，处理相应的错误逻辑
            throw new IllegalArgumentException("题目不存在");
        }
        String Language = getLanguageFolder(solution.getLanguage());
        String filePath = userFilePath + "/" + user.getUsername() + "/" + problem.getName() + "/" + Language ;
        return filePath;
    }


    private String getLanguageFolder(int language) {
        if (language == Type.java) {
            return "java";
        } else if (language == Type.c) {
            return "c";
        } else if (language == Type.javascript) {
            return "javascript";
        } else {
            throw new IllegalArgumentException("不支持的语言");
        }
    }

}

