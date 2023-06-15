package com.example.onlinejudge.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiParam;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author admin
 * @version 1.0.0
 * @ClassName SubmitDto.java
 * @Description TODO
 * @createTime 2023年06月15日 09:56:00
 */
@NoArgsConstructor
@Data
@ApiModel(value = "SubmitDto", description = "提交数据传输对象")
public class SubmitDto {
   private String code ;
   private Integer userId;
   private Integer problemId;
   private Integer language;
}
