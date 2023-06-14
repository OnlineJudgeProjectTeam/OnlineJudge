package com.example.onlinejudge.dto;

import lombok.Data;

@Data
public class RunDto {
    String message;
    Long timeCost;
    Long memoryCost;

    public RunDto(String message, Long timeCost, Long memoryCost) {
        this.message = message;
        this.timeCost = timeCost;
        this.memoryCost = memoryCost;
    }
}
