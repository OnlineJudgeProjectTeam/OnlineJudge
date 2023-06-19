package com.example.onlinejudge.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.onlinejudge.common.Type;
import com.example.onlinejudge.common.UserHolder;
import com.example.onlinejudge.dto.RunDto;
import com.example.onlinejudge.dto.UserDto;
import com.example.onlinejudge.entity.Favorite;
import com.example.onlinejudge.entity.Problem;
import com.example.onlinejudge.entity.Submission;
import com.example.onlinejudge.entity.User;
import com.example.onlinejudge.mapper.ProblemMapper;
import com.example.onlinejudge.service.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.management.Query;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.example.onlinejudge.common.Type.RightAnserLenth;
import static com.example.onlinejudge.utils.RedisConstants.CACHE_PROBLEM_KEY;
import static com.example.onlinejudge.utils.RedisConstants.CACHE_PROBLEM_TTL;

/**
 * <p>
 * 服务实现类
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
    @Autowired
    ISubmissionService submissionService;
    @Autowired
    @Lazy
    IFavoriteService favoriteService;
    private long timeCost;
    private long memoryCost;

    @Override
    public RunDto JavaJudge(String code, Integer userId, Integer problemId, Integer number) throws UnsupportedEncodingException {
        User user = userService.QueryById(userId);
        Problem problem = QueryById(problemId);
        String username = user.getUsername();
        File file = new File(userPath + "/" + username + "/" + problem.getName());
        if (!file.exists()) {
            file.mkdirs();
            new File(userPath + "/" + username + "/" + problem.getName() + "/submission").mkdirs();
            new File(userPath + "/" + username + "/" + problem.getName() + "/solution").mkdirs();
        }
        String folderName = UUID.randomUUID().toString();
        new File(userPath + "/" + username + "/" + problem.getName() + "/submission/" + folderName).mkdirs();
        String userCodePath = userPath + "/" + username + "/" + problem.getName() + "/submission/" + folderName;
        String problemCodePath = problemPath + "/" + problem.getName() + "/java";
        //将测试文件的依赖拷到用户目录下
        fileService.fileCopy(problemCodePath + "/dependency.txt", userCodePath + "/Test" + number + ".java", false);
        //将用户代码拷到用户目录下
        fileService.appendFile(userCodePath + "/Test" + number + ".java", code);
        //将测试文件拷到用户目录下
        fileService.fileCopy(problemCodePath + "/Test" + number + ".java", userCodePath + "/Test" + number + ".java", true);
        //编译
        Submission submission = new Submission();
        submission.setProblemId(problemId);
        submission.setUserId(userId);
        submission.setLanguage(Type.java);
        submission.setDifficulty(problem.getDifficulty());
        submission.setExecutionTime(LocalDateTime.now());
        submission.setFolderName(folderName);
        Boolean compileResult = JavaCompile(userCodePath, number);
        Integer acNum = user.getAcNum();
        Integer submitNum = user.getSubmitNum();
        user.setSubmitNum(++submitNum);
        if(!compileResult){
            submission.setPass(Type.notPass);
            submission.setTimeCost(BigInteger.valueOf(-1));
            submission.setMemoryCost(BigInteger.valueOf(-1));
            submissionService.save(submission);
            String message = fileService.readFile(userCodePath + "/stderr.txt");
            user.setAcRate(new BigDecimal(acNum).divide(new BigDecimal(submitNum), 2, BigDecimal.ROUND_HALF_UP));
            userService.update(user);
            return new RunDto(message, -1L, -1L,new BigDecimal("0"),new BigDecimal("0"));
        }
        //运行
        Boolean runResult = JavaRun(userCodePath, number);
//        System.out.println("timeCost:"+timeCost+"ms");
//        System.out.println("memoryCost:"+memoryCost+"KB");
        if (!runResult) {//
            String message = fileService.readFile(userCodePath + "/stderr.txt");
            submission.setPass(0);
            submission.setTimeCost(BigInteger.valueOf(-1));
            submission.setMemoryCost(BigInteger.valueOf(-1));
            submissionService.save(submission);
            user.setAcRate(new BigDecimal(acNum).divide(new BigDecimal(submitNum), 2, BigDecimal.ROUND_HALF_UP));
            userService.update(user);
            return new RunDto(message, -1L, -1L,new BigDecimal("0"),new BigDecimal("0"));
        } else {
            submission.setTimeCost(BigInteger.valueOf(timeCost));
            submission.setMemoryCost(BigInteger.valueOf(memoryCost));
            submission.setPass(Type.notPass);
            if(timeCost>problem.getTimeLimit()){
                submissionService.save(submission);
                user.setAcRate(new BigDecimal(acNum).divide(new BigDecimal(submitNum), 2, BigDecimal.ROUND_HALF_UP));
                userService.update(user);
                return new RunDto("Time Limit Exceeded", timeCost, memoryCost,new BigDecimal("0"),new BigDecimal("0"));
            }
            if (memoryCost > problem.getMemoryLimit()) {
                submissionService.save(submission);
                user.setAcRate(new BigDecimal(acNum).divide(new BigDecimal(submitNum), 2, BigDecimal.ROUND_HALF_UP));
                userService.update(user);
                return new RunDto("Memory Limit Exceeded", timeCost, memoryCost,new BigDecimal("0"),new BigDecimal("0"));
            }
            String message = fileService.readFile(userCodePath + "/stdout.txt");
           if( message.length() == RightAnserLenth) {
               user.setAcNum(++acNum);
               submission.setPass(Type.pass);
           }
            submissionService.save(submission);
            user.setAcRate(new BigDecimal(acNum).divide(new BigDecimal(submitNum), 2, BigDecimal.ROUND_HALF_UP));
            userService.update(user);
            BigDecimal timeBeat = submissionService.getTimeBeat(problemId, Type.java, timeCost);
            BigDecimal memoryBeat = submissionService.getMemoryBeat(problemId, Type.java, memoryCost);
            return new RunDto(message, timeCost, memoryCost,timeBeat,memoryBeat);
        }
    }

    @Override
    public Problem QueryById(Integer id) {
        String key = CACHE_PROBLEM_KEY + id;
        Map<Object, Object> problemMap = stringRedisTemplate.opsForHash().entries(key);
        if (problemMap.isEmpty()) {
            Problem problem = getById(id);
            if (problem == null) {
                problem = new Problem();
            }
            Map<String, Object> problemMap1 = BeanUtil.beanToMap(problem, new HashMap<>(),
                    CopyOptions.create()
                            .setIgnoreNullValue(true)
                            //在setFieldValueEditor中也需要判空
                            .setFieldValueEditor((fieldName, fieldValue) -> {
                                if (fieldValue == null) {
                                    fieldValue = "0";
                                } else {
                                    fieldValue = fieldValue + "";
                                }
                                return fieldValue;
                            }));
            //存储
            stringRedisTemplate.opsForHash().putAll(CACHE_PROBLEM_KEY + id, problemMap1);
            //设置有效期
            stringRedisTemplate.expire(CACHE_PROBLEM_KEY + id, CACHE_PROBLEM_TTL, TimeUnit.MINUTES);
            return problem;
        }
        if (!problemMap.containsKey("id") || problemMap.get("id") == "" || problemMap.get("id") == "0") {
            return null;
        }
        Problem problem = new Problem();
        BeanUtil.copyProperties(problemMap, problem);
        return problem;
    }

    @Override
    public Boolean JavaCompile(String workingDirectory, Integer number) {
        try {
            // 构建命令
            ProcessBuilder processBuilder = new ProcessBuilder("javac","-J-Duser.language=en", "-encoding", "utf-8", "./Test" + number + ".java");
            // 设置工作目录
            processBuilder.directory(new File(workingDirectory));
            // 设置输出文件
            File outputFile = new File(workingDirectory + "/stdout.txt");
            processBuilder.redirectOutput(outputFile);
            // 设置错误输出文件
            File errorFile = new File(workingDirectory + "/stderr.txt");
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
    public Boolean JavaRun(String workingDirectory, Integer number) {
        try {
            // 构建命令
            ProcessBuilder processBuilder = new ProcessBuilder("java", "Test" + number);
            // 设置工作目录
            processBuilder.directory(new File(workingDirectory));
            // 设置输出文件
            File outputFile = new File(workingDirectory + "/stdout.txt");
            processBuilder.redirectOutput(outputFile);
            File errorFile = new File(workingDirectory + "/stderr.txt");
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
            long memory = (startMemory - endMemory) / 1024;
            timeCost = time;
            memoryCost = memory;
            if (exitCode != 0) {
                return false;
            }
        } catch (IOException | InterruptedException e) {
            return false;
        }
        return true;
    }

    @Override
    public RunDto CJudge(String code, Integer userId, Integer problemId, Integer number) {
        User user = userService.QueryById(userId);
        Problem problem = QueryById(problemId);
        String username = user.getUsername();
        File file = new File(userPath + "/" + username + "/" + problem.getName());
        if (!file.exists()) {
            file.mkdirs();
            new File(userPath + "/" + username + "/" + problem.getName() + "/submission").mkdirs();
            new File(userPath + "/" + username + "/" + problem.getName() + "/solution").mkdirs();
        }
        String folderName = UUID.randomUUID().toString();;
        new File(userPath + "/" + username + "/" + problem.getName() + "/submission/" + folderName).mkdirs();
        String userCodePath = userPath + "/" + username + "/" + problem.getName() + "/submission/" + folderName;
        String problemCodePath = problemPath + "/" + problem.getName() + "/c";
        //将测试文件的依赖拷到用户目录下
        fileService.fileCopy(problemCodePath + "/dependency.txt", userCodePath + "/Test" + number + ".c", false);
        //将用户代码拷到用户目录下
        fileService.appendFile(userCodePath + "/Test" + number + ".c", code);
        //将测试文件拷到用户目录下
        fileService.fileCopy(problemCodePath + "/Test" + number + ".c", userCodePath + "/Test" + number + ".c", true);
        //编译
        Boolean compileResult = CCompile(userCodePath,number);
        Submission submission = new Submission();
        submission.setProblemId(problemId);
        submission.setUserId(userId);
        submission.setLanguage(Type.c);
        submission.setDifficulty(problem.getDifficulty());
        submission.setExecutionTime(LocalDateTime.now());
        submission.setFolderName(folderName);
        Integer acNum = user.getAcNum();
        Integer submitNum = user.getSubmitNum();
        user.setSubmitNum(++submitNum);
        if(!compileResult){
            submission.setPass(Type.notPass);
            submission.setTimeCost(BigInteger.valueOf(-1));
            submission.setMemoryCost(BigInteger.valueOf(-1));
            submissionService.save(submission);
            user.setAcRate(new BigDecimal(acNum).divide(new BigDecimal(submitNum), 2, BigDecimal.ROUND_HALF_UP));
            userService.update(user);
            String message = fileService.readFile(userCodePath + "/stderr.txt");
            return new RunDto(message, -1L, -1L,new BigDecimal("0"),new BigDecimal("0"));
        }
        //运行
        Boolean runResult = CRun(userCodePath, number);
//        System.out.println("timeCost:"+timeCost+"ms");
//        System.out.println("memoryCost:"+memoryCost+"KB");

        if (!runResult) {
            String message = fileService.readFile(userCodePath + "/stderr.txt");
            submission.setPass(Type.notPass);
            submission.setTimeCost(BigInteger.valueOf(-1));
            submission.setMemoryCost(BigInteger.valueOf(-1));
            submissionService.save(submission);
            user.setAcRate(new BigDecimal(acNum).divide(new BigDecimal(submitNum), 2, BigDecimal.ROUND_HALF_UP));
            userService.update(user);
            return new RunDto(message, -1L, -1L,new BigDecimal("0"),new BigDecimal("0"));
        }else{
            submission.setPass(Type.notPass);
            submission.setTimeCost(BigInteger.valueOf(timeCost));
            submission.setMemoryCost(BigInteger.valueOf(memoryCost));
            if (timeCost > problem.getTimeLimit()) {
                submissionService.save(submission);
                user.setAcRate(new BigDecimal(acNum).divide(new BigDecimal(submitNum), 2, BigDecimal.ROUND_HALF_UP));
                userService.update(user);
                return new RunDto("Time Limit Exceeded", timeCost, memoryCost,new BigDecimal("0"),new BigDecimal("0"));
            }
            if (memoryCost > problem.getMemoryLimit()) {
                submissionService.save(submission);
                user.setAcRate(new BigDecimal(acNum).divide(new BigDecimal(submitNum), 2, BigDecimal.ROUND_HALF_UP));
                userService.update(user);
                return new RunDto("Memory Limit Exceeded", timeCost, memoryCost,new BigDecimal("0"),new BigDecimal("0"));
            }
            String message = fileService.readFile(userCodePath + "/stdout.txt");
            if (message.length() == RightAnserLenth) {
                user.setAcNum(++acNum);
                submission.setPass(Type.pass);
            }
            user.setAcRate(new BigDecimal(acNum).divide(new BigDecimal(submitNum), 2, BigDecimal.ROUND_HALF_UP));
            userService.update(user);
            submissionService.save(submission);
            BigDecimal timeBeat = submissionService.getTimeBeat(problemId, Type.c, timeCost);
            BigDecimal memoryBeat = submissionService.getMemoryBeat(problemId, Type.c, memoryCost);
            return new RunDto(message, timeCost, memoryCost,timeBeat,memoryBeat);
        }
    }

    @Override
    public Boolean CCompile(String workingDirectory, Integer number) {
        try {
            // 构建命令
            ProcessBuilder processBuilder = new ProcessBuilder("gcc", "./Test" + number + ".c", "-o", "Test" + number);
            // 设置工作目录
            processBuilder.directory(new File(workingDirectory));
            // 设置输出文件
            File outputFile = new File(workingDirectory + "/stdout.txt");
            processBuilder.redirectOutput(outputFile);
            // 设置错误输出文件
            File errorFile = new File(workingDirectory + "/stderr.txt");
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
            String os = System.getProperty("os.name");
            ProcessBuilder processBuilder;
            //Windows操作系统
            if (os != null && os.toLowerCase().startsWith("windows")) {
                processBuilder = new ProcessBuilder(workingDirectory + "/" + "Test" + number);
            } else{//Linux操作系统
                processBuilder = new ProcessBuilder("./" + "Test" + number);
            }
            // 构建命令
            // 设置工作目录
            processBuilder.directory(new File(workingDirectory));
            // 设置输出文件
            File outputFile = new File(workingDirectory + "/stdout.txt");
            processBuilder.redirectOutput(outputFile);
            File errorFile = new File(workingDirectory + "/stderr.txt");
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
            long memory = (startMemory - endMemory) / 1024;
            timeCost = time;
            memoryCost = memory;
            if (exitCode != 0) {
                return false;
            }
        } catch (IOException | InterruptedException e) {
            return false;
        }
        return true;
    }

    @Override
    public Problem getRandomProblem() {
        List<Problem> list = list();
        int size = list.size();
        Random random = new Random();
        int index = random.nextInt(size);
        Problem problem = list.get(index);
        UserDto user = UserHolder.getUser();
        LambdaQueryWrapper<Favorite> favoriteLambdaQueryWrapper = new LambdaQueryWrapper<>();
        favoriteLambdaQueryWrapper.eq(Favorite::getProblemId, problem.getId());
        favoriteLambdaQueryWrapper.eq(Favorite::getUserId, problem.getId());
        Favorite one = favoriteService.getOne(favoriteLambdaQueryWrapper);
        if (one != null) {
            problem.setIsFavorite(1);
        } else {
            problem.setIsFavorite(0);
        }
        return problem;
    }

    @Override
    public PageInfo<Problem> getProblemList(Integer pageNum, Integer pageSize, Integer navSize, String name, String tags, String difficulty) {
        PageHelper.startPage(pageNum, pageSize);
        LambdaQueryWrapper<Problem> problemLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (name != null &&!name.equals("")&& !name.equals("undefined")) {
            problemLambdaQueryWrapper.like(Problem::getName, name);
        }
        if (tags != null && !tags.equals("")&& !tags.equals("undefined")) {
            problemLambdaQueryWrapper.like(Problem::getTags, tags);
        }
        if (difficulty != null && !difficulty.equals("")&& !difficulty.equals("undefined")) {
            problemLambdaQueryWrapper.eq(Problem::getDifficulty, difficulty);
        }
        List<Problem> list = list(problemLambdaQueryWrapper);
        UserDto user = UserHolder.getUser();
        for (Problem problem : list) {
            LambdaQueryWrapper<Favorite> favoriteLambdaQueryWrapper = new LambdaQueryWrapper<>();
            favoriteLambdaQueryWrapper.eq(Favorite::getProblemId, problem.getId());
            favoriteLambdaQueryWrapper.eq(Favorite::getUserId, user.getId());
            Favorite one = favoriteService.getOne(favoriteLambdaQueryWrapper);
            if(one!=null){
                problem.setIsFavorite(Type.favorite);
            }else{
                problem.setIsFavorite(Type.notFavorite);
            }
            LambdaQueryWrapper<Submission> submissionLambdaQueryWrapper = new LambdaQueryWrapper<>();
            submissionLambdaQueryWrapper.eq(Submission::getUserId, user.getId());
            submissionLambdaQueryWrapper.eq(Submission::getProblemId, problem.getId());
            List<Submission> submissionList = submissionService.list(submissionLambdaQueryWrapper);
            if (submissionList.isEmpty()) {
                problem.setStatus(0);
            } else {
                Integer isPass = 0;
                for (Submission submission : submissionList) {
                    if(submission.getPass()== 1)
                    {
                        isPass = 1;
                        break;
                    }
                }
                problem.setStatus(isPass +1);
            }
        }
        return new PageInfo<>(list, navSize);
    }

    @Override
    public String getAnswer(Integer problemId, Integer language) {
        Problem problem = QueryById(problemId);
        String answerPath = "";
        switch (language) {
            case 0:
                answerPath = problemPath + "/" + problem.getName() + "/java" + "/answer.txt";
                break;
            case 1:
                answerPath = problemPath + "/" + problem.getName() + "/c" + "/answer.txt";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + language);
        }
        return fileService.readFile(answerPath);
    }

    @Override
    public String getProblemDescription(Integer problemId) {
        Problem problem = QueryById(problemId);
        String descriptionPath = problemPath + "/" + problem.getName() + "/description.md";
        return fileService.readFile(descriptionPath);
    }

    @Override
    public String getProblemTemplate(Integer problemId, Integer language) {
        Problem problem = QueryById(problemId);
        String templatePath = "";
        switch (language) {
            case 0:
                templatePath = problemPath + "/" + problem.getName() + "/java" + "/template.java";
                break;
            case 1:
                templatePath = problemPath + "/" + problem.getName() + "/c" + "/template.c";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + language);
        }
        return fileService.readFile(templatePath);
    }
}
