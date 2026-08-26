package com.example.service.impl;

import com.example.service.FileUploadService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传服务实现类
 */
@Service
public class FileUploadServiceImpl implements FileUploadService {
    
    @Value("${BASE_LOCATION}")
    private String uploadPath;
    
    @Override
    public String uploadFile(MultipartFile file, String directory) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        
        try {
            String oldFileName = file.getOriginalFilename();
            String randomStr = UUID.randomUUID().toString();
            String newFileName = randomStr + oldFileName.substring(oldFileName.lastIndexOf("."));
            String filePath = uploadPath + File.separator + directory + File.separator + newFileName;
            File destFile = new File(filePath);
            if (!destFile.getParentFile().exists()) {
                destFile.getParentFile().mkdirs();
            }
            file.transferTo(destFile);
            return newFileName;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public String uploadAvatar(MultipartFile file) {
        return uploadFile(file, "avatar");
    }
    
    @Override
    public String uploadThingImage(MultipartFile file) {
        return uploadFile(file, "image");
    }
    
    @Override
    public String uploadBannerImage(MultipartFile file) {
        return uploadFile(file, "banner");
    }
    
    @Override
    public String uploadAdImage(MultipartFile file) {
        return uploadFile(file, "image");
    }
}