package com.example.onlinejudge.dto;

import com.example.onlinejudge.entity.Problem;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ProblemDto extends Problem {
    private String description;
    private String template;

    public ProblemDto(Problem problem) {
        this.setId(problem.getId());
        this.setName(problem.getName());
        this.setDifficulty(problem.getDifficulty());
        this.setTags(problem.getTags());
        this.setMemoryLimit(problem.getMemoryLimit());
        this.setTimeLimit(problem.getTimeLimit());
    }
}
