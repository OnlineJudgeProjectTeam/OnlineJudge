package com.example.onlinejudge.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.onlinejudge.common.R;
import com.example.onlinejudge.common.Type;
import com.example.onlinejudge.common.UserHolder;
import com.example.onlinejudge.dto.SolutionDto;
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
    public Solution createSolution(String code, Integer problemId, Integer userId, Integer language) throws IOException {
        // 根据用户ID和题目ID查询用户和题目信息
        Solution solution = new Solution();
        solution.setUserId(userId);
        solution.setProblemId(problemId);
        solution.setLanguage(language);
        Problem problem = problemService.QueryById(problemId);
        User user = userService.QueryById(userId);
        // 生成题解文件路径
        String filepath = userFilePath + "/" + user.getUsername() + "/" + problem.getName()+"/solution";
        File folder = new File(filepath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        String folderName = UUID.randomUUID().toString();
        solution.setFolderName(folderName);
        new File(folder.getPath()+"/"+folderName).mkdirs();
        File file = new File(folder.getPath()+"/"+folderName + "/" + "solution.md");
        if (!file.exists()) {
            file.createNewFile();
            fileService.writeFile(file.getPath(), code);
            solution.setCreatedTime(LocalDateTime.now());
            solution.setUpdatedTime(LocalDateTime.now());
            solution.setLikes(0);
            solution.setIsLike(false);
            // 保存题解对象到数据库
            save(solution);
            problemService.update().setSql("solutions = solutions + 1").eq("id", problemId).update();
            return solution;
        } else throw new IllegalArgumentException("题解已存在");
    }

    @Override
    public R<String> deleteSolution(Integer solutionId) throws IOException {
        // 根据题解ID查询题解信息
        LambdaQueryWrapper<Solution> queryWrapper = new LambdaQueryWrapper<>();
        User user =  UserHolder.getUser();
        queryWrapper.eq(Solution::getUserId,user.getId()).eq(Solution::getId,solutionId);
        Solution solution = getOne(queryWrapper);
        if (solution == null) {
            throw new IllegalArgumentException("错误的题解id");
        }
        Problem problem = problemService.getById(solution.getProblemId());
        // 删除题解文件
        String filePath = userFilePath+"/"+user.getUsername()+"/"+problem.getName()+"/solution/"+solution.getFolderName();
        fileService.deleteFile(filePath);
        // 删除题解记录
        removeById(solutionId);
        problemService.update().setSql("solutions = solutions - 1").eq("id", solution.getProblemId()).update();
        //删除redis记录
        stringRedisTemplate.delete(SOLUTION_LIKED_KEY + solutionId);
        return R.success("删除成功");
    }

    @Override
    public Solution updateSolution(Integer solutionId, String code) {
        LambdaQueryWrapper<Solution> queryWrapper = new LambdaQueryWrapper<>();
        User user =  UserHolder.getUser();
        queryWrapper.eq(Solution::getUserId,user.getId()).eq(Solution::getId,solutionId);
        Solution solution = getOne(queryWrapper);
        if (solution == null) {
            throw new IllegalArgumentException("错误的题解id");
        }
        Problem problem = problemService.getById(solution.getProblemId());
        String filePath = userFilePath+"/"+user.getUsername()+"/"+problem.getName()+"/solution/"+solution.getFolderName()+"/solution.md";
        fileService.writeFile(filePath, code);
        solution.setUpdatedTime(LocalDateTime.now());
        isSolutionLiked(solution);
        updateById(solution);
        return solution;
    }

    @Override
    public R<PageInfo<SolutionDto>> getSolutionList(Integer pageNum, Integer pageSize, Integer navSize, Integer problemId,Integer id) {
        LambdaQueryWrapper<Solution> solutionLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (problemId != null) {
            solutionLambdaQueryWrapper.eq(Solution::getProblemId, problemId);
        }
        if(id != null) {
            solutionLambdaQueryWrapper.eq(Solution::getUserId, id);
        }
        solutionLambdaQueryWrapper.orderByDesc(Solution::getCreatedTime);
        ArrayList<SolutionDto> solutionDtos = new ArrayList<>();
        HashMap<Integer, Problem> problemMap = problemMapper.getProblemMap();
        PageHelper.startPage(pageNum, pageSize);
        List<Solution> list = list(solutionLambdaQueryWrapper);
        for(Solution solution:list){
            Integer userId = solution.getUserId();
            User user = userService.QueryById(userId);
            SolutionDto solutionDto = new SolutionDto(solution);
            Problem problem = problemMap.get(solution.getProblemId());
            solutionDto.setProblemName(problem.getName());
            String path = userFilePath + "/" + user.getUsername() + "/" + problem.getName()+"/solution/"+solution.getFolderName()+ "/solution.md";
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
        LambdaQueryWrapper<Solution> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Solution::getId,solutionId);
        Solution solution = getOne(queryWrapper);
        if (solution == null) {
            throw new IllegalArgumentException("错误的题解id");
        }
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

}

