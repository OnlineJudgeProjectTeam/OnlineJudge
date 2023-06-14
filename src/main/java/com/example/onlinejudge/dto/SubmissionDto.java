package com.example.onlinejudge.dto;

import com.example.onlinejudge.entity.Submission;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SubmissionDto extends Submission {
    String problemName;

    public SubmissionDto(Submission submission){
        this.setPass(submission.getPass());
        this.setExecutionTime(submission.getExecutionTime());
        this.setDifficulty(submission.getDifficulty());
        this.setId(submission.getId());
        this.setLanguage(submission.getLanguage());
        this.setProblemId(submission.getProblemId());
        this.setUserId(submission.getUserId());
    }
}
