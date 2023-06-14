package com.example.onlinejudge.dto;

import com.example.onlinejudge.entity.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "UserCodeDto", description = "含有验证码的用户数据传输对象")
public class UserCodeDto extends User {
    @ApiModelProperty(value = "验证码")
    private String code;
}
