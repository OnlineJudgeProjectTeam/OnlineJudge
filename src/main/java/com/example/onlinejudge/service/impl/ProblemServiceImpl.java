package com.example.onlinejudge.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.example.onlinejudge.entity.Problem;
import com.example.onlinejudge.entity.User;
import com.example.onlinejudge.mapper.ProblemMapper;
import com.example.onlinejudge.service.FileService;
import com.example.onlinejudge.service.IProblemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.onlinejudge.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.management.Query;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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
public class ProblemServiceImpl extends ServiceImpl<ProblemMapper, Problem> implements IProblemService {
    @Value("${path.user}")
    private String userPath;
    @Value("${path.problem}")
    private String problemPath;
    @Autowired
    private IUserService userService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private FileService fileService;
    private long timeCost;
    private long memoryCost;

    @Override
    public String JavaJudge(String code, Integer userId,Integer problemId,Integer number){
        User user = userService.QueryById(userId);
        Problem problem = QueryById(problemId);
        String username = user.getUsername();
        File file = new File(userPath + "/" + username + "/" + problem.getName());
        if(!file.exists()){
            file.mkdirs();
            new File(userPath + "/" + username + "/" + problem.getName() + "/java").mkdirs();
            new File(userPath + "/" + username + "/" + problem.getName() + "/c").mkdirs();
        }
        String userCodePath = userPath+"/"+username+"/"+problem.getName()+"/java";
        String problemCodePath = problemPath+"/"+problem.getName()+"/java";
        //将测试文件的依赖拷到用户目录下
        fileService.fileCopy(problemCodePath+"/dependency.txt",userCodePath+"/Test"+number+".java",false);
        //将用户代码拷到用户目录下
        fileService.appendFile(userCodePath+"/Test"+number+".java",code);
        //将测试文件拷到用户目录下
        fileService.fileCopy(problemCodePath+"/Test"+number+".java",userCodePath+"/Test"+number+".java",true);
        //编译
        Boolean compileResult = JavaCompile(userCodePath,number);
        if(!compileResult){
            return fileService.readFile(userCodePath + "/stderr.txt");
        }
        //运行
        Boolean runResult = JavaRun(userCodePath,number);
//        System.out.println("timeCost:"+timeCost+"ms");
//        System.out.println("memoryCost:"+memoryCost+"KB");
        if(!runResult){
            return fileService.readFile(userCodePath + "/stderr.txt");
        }else{
            if(timeCost>problem.getTimeLimit()){
                return "Time Limit Exceeded";
            }
            if(memoryCost>problem.getMemoryLimit()){
                return "Memory Limit Exceeded";
            }
            return fileService.readFile(userCodePath + "/stdout.txt");
        }
    }
    @Override
    public Problem QueryById(Integer id) {
        String key = CACHE_PROBLEM_KEY+id;
        Map<Object, Object> problemMap = stringRedisTemplate.opsForHash().entries(key);
        if(problemMap.isEmpty()){
            Problem problem = getById(id);
            if(problem==null){
                problem = new Problem();
            }
            Map<String, Object> problemMap1 = BeanUtil.beanToMap(problem, new HashMap<>(),
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
            stringRedisTemplate.opsForHash().putAll(CACHE_PROBLEM_KEY+id,problemMap1);
            //设置有效期
            stringRedisTemplate.expire(CACHE_PROBLEM_KEY+id,CACHE_PROBLEM_TTL, TimeUnit.MINUTES);
            return problem;
        }
        if(!problemMap.containsKey("id")||problemMap.get("id")==""||problemMap.get("id")=="0"){
            return null;
        }
        Problem problem = new Problem();
        BeanUtil.copyProperties(problemMap,problem);
        return problem;
    }

    @Override
    public Boolean JavaCompile(String workingDirectory,Integer number) {
        try {
            // 构建命令
            ProcessBuilder processBuilder = new ProcessBuilder("javac", "-encoding", "utf-8", "./Test"+number+".java");
            // 设置工作目录
            processBuilder.directory(new File(workingDirectory));
            // 设置输出文件
            File outputFile = new File(workingDirectory+"/stdout.txt");
            processBuilder.redirectOutput(outputFile);
            // 设置错误输出文件
            File errorFile = new File(workingDirectory+"/stderr.txt");
            processBuilder.redirectError(errorFile);
            // 执行命令
            Process process = processBuilder.start();
            // 等待命令执行完成
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                return false;
            }
        } catch (IOException | InterruptedException e) {
            return false;
        }
        return true;
    }

    @Override
    public Boolean JavaRun(String workingDirectory,Integer number) {
        try {
            // 构建命令
            ProcessBuilder processBuilder = new ProcessBuilder("java","Test"+number);
            // 设置工作目录
            processBuilder.directory(new File(workingDirectory));
            // 设置输出文件
            File outputFile = new File(workingDirectory+"/stdout.txt");
            processBuilder.redirectOutput(outputFile);
            File errorFile = new File(workingDirectory+"/stderr.txt");
            processBuilder.redirectError(errorFile);
            Runtime runtime = Runtime.getRuntime();
            runtime.gc();
            long startMemory = runtime.freeMemory();
            long startTime = System.currentTimeMillis();
            // 执行命令
            Process process = processBuilder.start();
            // 等待命令执行完成
            int exitCode = process.waitFor();
            long endTime = System.currentTimeMillis();
            long time = endTime - startTime;
            long endMemory = runtime.freeMemory();
            long memory = (startMemory - endMemory)/1024;
            timeCost=time;
            memoryCost=memory;
            if (exitCode != 0) {
                return false;
            }
        } catch (IOException | InterruptedException e) {
            return false;
        }
        return true;
    }

    @Override
    public String CJudge(String code, Integer userId, Integer problemId, Integer number) {
        User user = userService.QueryById(userId);
        Problem problem = QueryById(problemId);
        String username = user.getUsername();
        File file = new File(userPath + "/" + username + "/" + problem.getName());
        if(!file.exists()){
            file.mkdirs();
            new File(userPath + "/" + username + "/" + problem.getName() + "/java").mkdirs();
            new File(userPath + "/" + username + "/" + problem.getName() + "/c").mkdirs();
        }
        String userCodePath = userPath+"/"+username+"/"+problem.getName()+"/c";
        String problemCodePath = problemPath+"/"+problem.getName()+"/c";
        //将测试文件的依赖拷到用户目录下
        fileService.fileCopy(problemCodePath+"/dependency.txt",userCodePath+"/Test"+number+".c",false);
        //将用户代码拷到用户目录下
        fileService.appendFile(userCodePath+"/Test"+number+".c",code);
        //将测试文件拷到用户目录下
        fileService.fileCopy(problemCodePath+"/Test"+number+".c",userCodePath+"/Test"+number+".c",true);
        //编译
        Boolean compileResult = CCompile(userCodePath,number);
        if(!compileResult){
            return fileService.readFile(userCodePath + "/stderr.txt");
        }
        //运行
        Boolean runResult = CRun(userCodePath,number);
//        System.out.println("timeCost:"+timeCost+"ms");
//        System.out.println("memoryCost:"+memoryCost+"KB");
        if(!runResult){
            return fileService.readFile(userCodePath + "/stderr.txt");
        }else{
            if(timeCost>problem.getTimeLimit()){
                return "Time Limit Exceeded";
            }
            if(memoryCost>problem.getMemoryLimit()){
                return "Memory Limit Exceeded";
            }
            return fileService.readFile(userCodePath + "/stdout.txt");
        }
    }

    @Override
    public Boolean CCompile(String workingDirectory, Integer number) {
        try {
            // 构建命令
            ProcessBuilder processBuilder = new ProcessBuilder("gcc", "./Test"+number+".c","-o","Test"+number+".exe");
            // 设置工作目录
            processBuilder.directory(new File(workingDirectory));
            // 设置输出文件
            File outputFile = new File(workingDirectory+"/stdout.txt");
            processBuilder.redirectOutput(outputFile);
            // 设置错误输出文件
            File errorFile = new File(workingDirectory+"/stderr.txt");
            processBuilder.redirectError(errorFile);
            // 执行命令
            Process process = processBuilder.start();
            // 等待命令执行完成
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                return false;
            }
        } catch (IOException | InterruptedException e) {
            return false;
        }
        return true;
    }

    @Override
    public Boolean CRun(String workingDirectory, Integer number) {
        try {
            // 构建命令
            ProcessBuilder processBuilder = new ProcessBuilder(workingDirectory+"/"+"Test"+number+".exe");
            // 设置工作目录
            processBuilder.directory(new File(workingDirectory));
            // 设置输出文件
            File outputFile = new File(workingDirectory+"/stdout.txt");
            processBuilder.redirectOutput(outputFile);
            File errorFile = new File(workingDirectory+"/stderr.txt");
            processBuilder.redirectError(errorFile);
            Runtime runtime = Runtime.getRuntime();
            runtime.gc();
            long startMemory = runtime.freeMemory();
            long startTime = System.currentTimeMillis();
            // 执行命令
            Process process = processBuilder.start();
            // 等待命令执行完成
            int exitCode = process.waitFor();
            long endTime = System.currentTimeMillis();
            long endMemory = runtime.freeMemory();
            long time = endTime - startTime;
            long memory = (startMemory - endMemory)/1024;
            timeCost=time;
            memoryCost=memory;
            if (exitCode != 0) {
                return false;
            }
        } catch (IOException | InterruptedException e) {
            return false;
        }
        return true;
    }
}
