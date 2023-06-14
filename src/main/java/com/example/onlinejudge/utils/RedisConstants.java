package com.example.onlinejudge.utils;

public class RedisConstants {
    public static final String LOGIN_USER_KEY = "login:token:";
    public static final Long LOGIN_USER_TTL = 30L;
    public static final String LOGIN_CODE_KEY = "login:code:";
    public static final Long LOGIN_CODE_TTL = 15L;
    public static final String CACHE_USER_KEY = "cache:user:";
    public static final Long CACHE_USER_TTL = 30L;
    public static final String CACHE_PROBLEM_KEY = "cache:problem:";
    public static final Long CACHE_PROBLEM_TTL = 30L;
    public static final String SOLUTION_LIKED_KEY = "solution:liked:";

}
