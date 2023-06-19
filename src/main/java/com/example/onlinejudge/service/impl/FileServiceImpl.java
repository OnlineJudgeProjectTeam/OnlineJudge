package com.example.onlinejudge.service.impl;

import com.example.onlinejudge.service.FileService;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class FileServiceImpl implements FileService {

    @Override
    public Boolean fileCopy(String srcPath, String destPath,Boolean append) {
        FileReader fr = null;
        BufferedReader br = null;
        FileWriter fw = null;
        PrintWriter pw = null;
        try {
            fr = new FileReader(srcPath);
            br = new BufferedReader(fr);
            fw = new FileWriter(destPath,append);
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

    @Override
    public String readFile(String path) {
        FileReader fr = null;
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        try {
            fr = new FileReader(path);
            br = new BufferedReader(fr);
            String temp = "";
            while ((temp = br.readLine()) != null) {
                sb.append(temp);
                sb.append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException("文件读取失败");
        } finally {
            try {
                assert fr != null;
                fr.close();
                assert br != null;
                br.close();
            } catch (IOException e) {
                throw new RuntimeException("文件关闭失败");
            }
        }
        return sb.toString();
    }

    @Override
    public String readFile(String path, Charset charset) {
        InputStreamReader isr = null;
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        try {
            isr = new InputStreamReader(Files.newInputStream(Paths.get(path)),charset);
            br = new BufferedReader(isr);
            String temp = "";
            while ((temp = br.readLine()) != null) {
                sb.append(temp);
                sb.append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException("文件读取失败");
        } finally {
            try {
                assert isr != null;
                isr.close();
                assert br != null;
                br.close();
            } catch (IOException e) {
                throw new RuntimeException("文件关闭失败");
            }
        }
        return sb.toString();
    }

    @Override
    public Boolean deleteFile(String path) {
        File file = new File(path);
        if(!file.exists()){
            return false;
        }
        if(file.isFile()){
            return file.delete();
        }
        File[] files = file.listFiles();
        assert files != null;
        for (File f : files) {
            if(f.isFile()){
                if(!f.delete()){
                    System.out.println(f.getAbsolutePath()+" delete error!");
                    return false;
                }
            }else{
                if(!this.deleteFile(f.getAbsolutePath())){
                    return false;
                }
            }
        }
        return file.delete();
    }
}
