package com.example.onlinejudge.service.impl;

import com.example.onlinejudge.service.FileService;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class FileServiceImpl implements FileService {

    @Override
    public Boolean fileCopy(String srcPath, String destPath) {
        FileReader fr = null;
        BufferedReader br = null;
        FileWriter fw = null;
        PrintWriter pw = null;
        try {
            fr = new FileReader(srcPath);
            br = new BufferedReader(fr);
            fw = new FileWriter(destPath);
            pw = new PrintWriter(fw);
            String temp = "";
            while ((temp = br.readLine()) != null) {
                pw.println(temp);
                pw.flush();
            }

        } catch (IOException e) {
            throw new RuntimeException("文件复制失败");
        } finally {
            try {
                assert fr != null;
                fr.close();
                assert br != null;
                br.close();
                assert fw != null;
                fw.close();
                assert pw != null;
                pw.close();
            } catch (IOException e) {
                throw new RuntimeException("文件关闭失败");
            }
        }
        return true;
    }

    @Override
    public Boolean writeFile(String path, String content) {
        BufferedWriter writer = null;
        try {
            File file = new File(path);
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false)));
            writer.write(content);
        } catch (IOException e) {
            throw new RuntimeException("文件写入失败");
        } finally {
            try {
                assert writer != null;
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException("文件关闭失败");
            }
        }
        return true;
    }

    @Override
    public Boolean appendFile(String path, String content) {
        BufferedWriter writer = null;
        try {
            File file = new File(path);
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
            writer.write(content);
        } catch (IOException e) {
            throw new RuntimeException("文件写入失败");
        } finally {
            try {
                assert writer != null;
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException("文件关闭失败");
            }
        }
        return true;
    }
}
