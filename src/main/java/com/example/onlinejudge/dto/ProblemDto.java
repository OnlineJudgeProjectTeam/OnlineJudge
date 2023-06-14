package com.example.onlinejudge.dto;

import com.example.onlinejudge.entity.Problem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "ProblemDto", description = "题目数据传输对象")
public class ProblemDto extends Problem {
    @ApiModelProperty(value = "题目描述")
    private String description;
    @ApiModelProperty(value = "模板代码")
    private String template;
    @ApiModelProperty(value = "用户是否收藏该题目")
    private Integer isFavorite;

    public ProblemDto(Problem problem) {
        this.setId(problem.getId());
        this.setName(problem.getName());
        this.setDifficulty(problem.getDifficulty());
        this.setTags(problem.getTags());
        this.setMemoryLimit(problem.getMemoryLimit());
        this.setTimeLimit(problem.getTimeLimit());
        this.setFavorites(problem.getFavorites());
    }
}
