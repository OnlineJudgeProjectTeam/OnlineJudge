package com.example.onlinejudge.service;

import java.nio.charset.Charset;

public interface FileService {
    public Boolean fileCopy(String srcPath, String destPath,Boolean append);
    public Boolean writeFile(String path, String content);
    public Boolean appendFile(String path, String content);
    public String readFile(String path);
    public String readFile(String path, Charset charset);
    public Boolean deleteFile(String path);
}
