package com.hkt.ruby.fuse.demo.utils;

import com.hkt.ruby.fuse.demo.properties.OnPremProperties;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
@Data
@EnableConfigurationProperties({OnPremProperties.class})
public class FileLoader {

    @Autowired
    private OnPremProperties onPremProperties;

    private static FileLoader mFileLoader;

    private String fileContent;

    public static FileLoader getInstance(){

        if (mFileLoader == null){
            synchronized (FileLoader.class){
                if(mFileLoader == null){
                    mFileLoader = new FileLoader();
                }
            }
        }
        return mFileLoader;
    }

    private FileLoader() {

        log.info("File path: " + onPremProperties.getFile().getLargeFile());
        Resource resource = new ClassPathResource(onPremProperties.getFile().getLargeFile());
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
