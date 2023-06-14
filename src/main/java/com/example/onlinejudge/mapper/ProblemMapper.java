package com.example.onlinejudge.mapper;

import com.example.onlinejudge.entity.Problem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.HashMap;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author diandianjun
 * @since 2023-06-13
 */
@Mapper
public interface ProblemMapper extends BaseMapper<Problem> {
    @MapKey("id")
    @Select("select id,name from problem")
    public HashMap<Integer,Problem> getProblemMap();
}
