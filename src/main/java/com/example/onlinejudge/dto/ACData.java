package com.example.onlinejudge.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "ACData", description = "AC率数据")
public class ACData {
    @ApiModelProperty(value = "通过率")
    private BigDecimal acRate;
    @ApiModelProperty(value = "通过数")
    private Integer acNum;
    @ApiModelProperty(value = "提交数")
    private Integer submitNum;
}
