package com.example.onlinejudge.service.impl;

import com.example.onlinejudge.common.R;
import com.example.onlinejudge.common.Type;
import com.example.onlinejudge.common.UserHolder;
import com.example.onlinejudge.entity.Problem;
import com.example.onlinejudge.entity.Solution;
import com.example.onlinejudge.entity.User;
import com.example.onlinejudge.mapper.SolutionMapper;
import com.example.onlinejudge.service.FileService;
import com.example.onlinejudge.service.ISolutionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.onlinejudge.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author diandianjun
 * @since 2023-06-13
 */
@Service
public class SolutionServiceImpl extends ServiceImpl<SolutionMapper, Solution> implements ISolutionService {

    @Autowired
    private ProblemServiceImpl problemService;

    @Value("${path.user}")
    private String userFilePath;

    @Autowired
    private IUserService userService;
    @Autowired
    private FileService fileService;

    @Override
    public R<String> createSolution(String code, Integer problemId, Integer userId,Integer language) throws IOException {
        // 根据用户ID和题目ID查询用户和题目信息
        User user = userService.getById(userId);
        String username = user.getUsername();
        Problem problem = problemService.getById(problemId);
        if (problem == null) {
            // 用户或题目不存在，处理相应的错误逻辑
            throw new IllegalArgumentException("题目不存在");
        }
     String Language = getLanguageFolder(language);
            File Folder = new File(userFilePath + "/" + username + "/" + problem.getName() + "/"+Language);
            if (!Folder.exists()) {
                Folder.mkdirs();
            }
            File file = new File(Folder+"/" + "solution.md");
            if(!file.exists()){
                file.createNewFile();
            }
            fileService.writeFile(file.getPath(), code);
            // 创建一个新的题解对象
            Solution solution = new Solution();
            solution.setUserId(userId);
            solution.setProblemId(problemId);
            solution.setLanguage(language);
            solution.setCreatedTime(LocalDateTime.now());
            // 保存题解对象到数据库
            save(solution);
            return R.success("提交成功");
        }

        private String getLanguageFolder(int language) {
            if (language == Type.java) {
                return "java";
            } else if (language == Type.c) {
                return "c";
            }
                else if(language == Type.javascript){
                    return "javascript";
            }
            else {
                throw new IllegalArgumentException("不支持的语言");
            }
        }

    }

