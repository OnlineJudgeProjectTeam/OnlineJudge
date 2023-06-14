package com.example.onlinejudge.dto;

import com.example.onlinejudge.entity.User;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserCodeDto extends User {
    private String code;
}
