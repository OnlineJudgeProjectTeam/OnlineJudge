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
import java.security.PublicKey;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author diandianjun
 * @since 2023-06-13
 */
@RestController
@RequestMapping("/solution")
@Api("题解管理")
public class SolutionController {

    @Autowired
    ISolutionService solutionService;

    @PostMapping("/create")
    @ApiOperation("创建题解")
    public R<String> createSolution(@ApiParam("题解") @RequestBody SolutionDto solutionDto) throws IOException {
        String content = solutionDto.getContent();
        Integer problemId = solutionDto.getProblemId();
        Integer language = solutionDto.getLanguage();
        Integer userId = UserHolder.getUser().getId();
        solutionService.createSolution(content,problemId,userId,language);
        return R.success("提交成功");
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation("删除题解")
    public R<String> deleteSolution(@ApiParam("题解id")@PathVariable("id") Integer id) throws IOException {
        solutionService.deleteSolution(id);
        return R.success("删除成功");
    }

    @PostMapping("/update")
    @ApiOperation("修改题解")
    public R<String> updateSolution(@ApiParam("题解") @RequestBody SolutionDto solutionDto){
        String content = solutionDto.getContent();
        Integer solutionId = solutionDto.getId();
        solutionService.updateSolution(solutionId,content);
        return R.success("修改成功");
    }

   @PutMapping("/like/{id}")
   @ApiOperation("点赞")
    public R<String> likeSolution(@ApiParam("题解id")@PathVariable("id") Integer id){
        solutionService.likeSolution(id);
        return R.success("点赞成功");
   }

   @GetMapping("/get-solution-list")
   @ApiOperation("获取题解列表")
   R<PageInfo<SolutionDto>>GetSolutionList( @ApiParam("查询页数") Integer pageNum, @ApiParam("每页大小") Integer pageSize,
                                            @ApiParam("需要展示的页数") Integer navSize, @ApiParam("问题id") Integer problemId, @ApiParam("语言，0代表java，1代表c") Integer language){
        R <PageInfo<SolutionDto>> solutionDtoPageInfo = solutionService.getSolutionList(pageNum,pageSize,navSize,problemId,language);
        return solutionDtoPageInfo;
   }
}
