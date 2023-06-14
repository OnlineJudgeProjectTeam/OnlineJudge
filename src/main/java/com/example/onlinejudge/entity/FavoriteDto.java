package com.example.onlinejudge.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "FavoriteDto", description = "收藏数据传输对象")
@Data
public class FavoriteDto extends Favorite{
    @ApiModelProperty(value = "题目")
    private Problem problem;

    public FavoriteDto(Favorite favorite){
        this.setId(favorite.getId());
        this.setUserId(favorite.getUserId());
        this.setProblemId(favorite.getProblemId());
    }
}
