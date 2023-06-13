package com.example.onlinejudge.service.impl;


import com.example.onlinejudge.service.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class MailServiceImpl implements MailService {
    @Value("${spring.mail.username}")
    private String from;

    @Resource
    private JavaMailSender mailSender;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    public void sendVertifyCode(String to, String title, String content){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from); //发送人
        message.setTo(to);   //收件人
        message.setSubject(title);  //邮件名
        message.setText(content);   //邮件内容（验证码）
        mailSender.send(message);
        logger.info("已经发送");
    }
}
