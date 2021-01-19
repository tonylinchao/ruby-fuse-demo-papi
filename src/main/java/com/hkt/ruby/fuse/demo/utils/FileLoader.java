package com.hkt.ruby.fuse.demo.utils;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
@Data
public class FileLoader {

    private static FileLoader mFileLoader = null;

    private String fileContent;

    public static FileLoader getInstance(String filePath){
        if (mFileLoader == null){
            synchronized (FileLoader.class){
                if(mFileLoader == null){
                    mFileLoader = new FileLoader(filePath);
                }
            }
        }
        return mFileLoader;
    }

    private FileLoader(String filePath) {

        log.info("File path: " + filePath);
        Resource resource = new ClassPathResource(filePath);
        fileContent = "";

        try {
            InputStream inputStream = resource.getInputStream();
            byte[] bdata = FileCopyUtils.copyToByteArray(inputStream);
            fileContent = new String(bdata, StandardCharsets.UTF_8);
            log.info("Retrieve data success!");
        } catch (IOException e) {
            log.error("IOException", e);
        }
    }

}
