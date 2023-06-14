package com.example.onlinejudge.dto;

import com.example.onlinejudge.entity.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "UserDto", description = "用户数据传输对象")
public class UserDto extends User {
    @ApiModelProperty(value = "token")
    private String token;
}
