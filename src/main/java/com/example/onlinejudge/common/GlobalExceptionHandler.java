package com.example.onlinejudge.common;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.io.IOException;

@ControllerAdvice(annotations = {RestController.class, Controller.class})   //全局异常处理
@ResponseBody                                                               //返回json数据
public class GlobalExceptionHandler {
    @ExceptionHandler(FileNotFoundException.class)                          //捕获指定异常
    public R<String> fileNotFoundException(FileNotFoundException exception){
        String message=exception.getMessage();
        return R.error(message);
    }

    @ExceptionHandler(IOException.class)
    public R<String> IOException(IOException exception){
        String message=exception.getMessage();
        return R.error(message);
    }

    @ExceptionHandler(RuntimeException.class)
    public R<String> RuntimeException(RuntimeException exception){
        String message=exception.getMessage();
        return R.error(message);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public R<String> Exception(IllegalArgumentException exception){
        String message=exception.getMessage();
        return R.error(message);
    }
}