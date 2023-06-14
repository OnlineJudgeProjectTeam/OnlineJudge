package com.example.onlinejudge.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ACData {
    private BigDecimal acRate;
    private Integer acNum;
    private Integer submitNum;
}
