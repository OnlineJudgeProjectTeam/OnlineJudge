package com.example.onlinejudge.controller;


import com.example.onlinejudge.common.R;
import com.example.onlinejudge.common.UserHolder;
import com.example.onlinejudge.dto.SolutionDto;
import com.example.onlinejudge.entity.Solution;
import com.example.onlinejudge.service.ISolutionService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author diandianjun
 * @since 2023-06-13
 */
@RestController
@RequestMapping("/solution")
@Api("题解管理")
@CrossOrigin
public class SolutionController {

    @Autowired
    ISolutionService solutionService;

    @PostMapping("/create")
    @ApiOperation("创建题解")
    public R<Solution> createSolution(@ApiParam("题解") @RequestBody SolutionDto solutionDto) throws IOException {
        String content = solutionDto.getContent();
        Integer problemId = solutionDto.getProblemId();
        Integer language = solutionDto.getLanguage();
        Integer userId = UserHolder.getUser().getId();
        Solution solution = solutionService.createSolution(content,problemId,userId,language);
        return R.success(solution);
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation("删除题解")
    public R<String> deleteSolution(@ApiParam("题解id")@PathVariable("id") Integer id) throws IOException {
        solutionService.deleteSolution(id);
        return R.success("删除成功");
    }

    @PostMapping("/update")
    @ApiOperation("修改题解")
    public R<Solution> updateSolution(@ApiParam("题解") @RequestBody SolutionDto solutionDto){
        String content = solutionDto.getContent();
        Integer solutionId = solutionDto.getId();
       Solution solution =  solutionService.updateSolution(solutionId,content);
        return R.success(solution);
    }

    @PutMapping("/like/{id}")
    @ApiOperation("点赞")
    public R<String> likeSolution(@ApiParam("题解id") @PathVariable("id") Integer id) {
        return solutionService.likeSolution(id);

    }


    @PostMapping("/get-solution-list")
    @ApiOperation("用户题解列表")
    R<PageInfo<SolutionDto>> GetSolutionList(@RequestBody HashMap<String,Integer>map){
        Integer pageNum = map.get("pageNum");
        Integer pageSize = map.get("pageSize");
        Integer navSize = map.get("navSize");
        Integer problemId = map.get("problemId");
        Integer userId = map.get("userId");
        R<PageInfo<SolutionDto>> solutionDtoPageInfo = solutionService.getSolutionList(pageNum, pageSize, navSize, problemId,userId);
        return solutionDtoPageInfo;
    }

    @GetMapping("/get-solution/{id}")
    @ApiOperation("获取题解")
    R<SolutionDto> getSolution(@ApiParam("题解id") @PathVariable("id") Integer id){
        SolutionDto solutionDto = solutionService.getSolution(id);
        return R.success(solutionDto);
    }
}
