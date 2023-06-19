package com.example.onlinejudge.dto;

import com.example.onlinejudge.entity.Submission;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "SubmissionDto", description = "提交数据传输对象")
public class SubmissionDto extends Submission {
    @ApiModelProperty(value = "题目名称")
    String problemName;
    String code;
    String output;

    public SubmissionDto(Submission submission){
        this.setPass(submission.getPass());
        this.setExecutionTime(submission.getExecutionTime());
        this.setDifficulty(submission.getDifficulty());
        this.setId(submission.getId());
        this.setLanguage(submission.getLanguage());
        this.setProblemId(submission.getProblemId());
        this.setUserId(submission.getUserId());
        this.setMemoryCost(submission.getMemoryCost());
        this.setTimeCost(submission.getTimeCost());
    }
}
