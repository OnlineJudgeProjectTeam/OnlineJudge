package com.example.onlinejudge.dto;

import com.example.onlinejudge.entity.Solution;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SolutionDto extends Solution {
    String content;
    String problemName;

    public SolutionDto(Solution solution) {
        this.setId(solution.getId());
        this.setProblemId(solution.getProblemId());
        this.setUserId(solution.getUserId());
        this.setLanguage(solution.getLanguage());
        this.setLikes(solution.getLikes());
        this.setCreatedTime(solution.getCreatedTime());
        this.setUpdatedTime(solution.getUpdatedTime());
    }
}
