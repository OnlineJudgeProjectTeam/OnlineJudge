package com.example.onlinejudge.dto;

import com.example.onlinejudge.entity.Solution;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "SolutionDto", description = "提交数据传输对象")
@NoArgsConstructor
@ToString(callSuper = true)
public class SolutionDto extends Solution {
    @ApiModelProperty(value = "题目内容")
    String content;
    @ApiModelProperty(value = "题目名称")
    String problemName;
    String name;
    String avatar;

    public SolutionDto(Solution solution) {
        this.setIsLike(solution.getIsLike());
        this.setId(solution.getId());
        this.setProblemId(solution.getProblemId());
        this.setUserId(solution.getUserId());
        this.setLanguage(solution.getLanguage());
        this.setLikes(solution.getLikes());
        this.setCreatedTime(solution.getCreatedTime());
        this.setUpdatedTime(solution.getUpdatedTime());
    }
}
