package com.example.onlinejudge.service;

public interface FileService {
    public Boolean fileCopy(String srcPath, String destPath);
    public Boolean writeFile(String path, String content);
    public Boolean appendFile(String path, String content);
}
