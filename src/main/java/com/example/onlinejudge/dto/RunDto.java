package com.example.onlinejudge.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "RunDto", description = "运行结果数据传输对象")
public class RunDto {
    @ApiModelProperty(value = "运行结果")
    String message;
    @ApiModelProperty(value = "运行时间")
    Long timeCost;
    @ApiModelProperty(value = "运行内存")
    Long memoryCost;
    BigDecimal timeBeat;
    BigDecimal memoryBeat;


    public RunDto(String message, Long timeCost, Long memoryCost, BigDecimal timeBeat, BigDecimal memoryBeat) {
        this.message = message;
        this.timeCost = timeCost;
        this.memoryCost = memoryCost;
        this.timeBeat = timeBeat;
        this.memoryBeat = memoryBeat;
    }
}
