package com.example.onlinejudge.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author diandianjun
 * @since 2023-06-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("submission")
@ApiModel(value="Submission对象", description="")
public class Submission implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer problemId;

    private Integer userId;

    @ApiModelProperty(value = "0代表java，1代表c，2代表JavaScript")
    private Integer language;

    @ApiModelProperty(value = "1代表通过，0代表未通过")
    private Integer pass;

    @ApiModelProperty(value = "提交时间")
    private Integer executionTime;


}
