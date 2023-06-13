package com.example.onlinejudge.service;

public interface MailService {
    void sendVertifyCode(String to, String title, String content);
}
