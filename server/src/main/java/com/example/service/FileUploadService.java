package com.example.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传服务接口
 */
public interface FileUploadService {
    
    /**
     * 上传文件
     * @param file 文件
     * @param directory 子目录（如 avatar, image, banner）
     * @return 新文件名
     */
    String uploadFile(MultipartFile file, String directory);
    
    /**
     * 上传用户头像
     * @param file 文件
     * @return 新文件名
     */
    String uploadAvatar(MultipartFile file);
    
    /**
     * 上传商品图片
     * @param file 文件
     * @return 新文件名
     */
    String uploadThingImage(MultipartFile file);
    
    /**
     * 上传轮播图
     * @param file 文件
     * @return 新文件名
     */
    String uploadBannerImage(MultipartFile file);
    
    /**
     * 上传广告图片
     * @param file 文件
     * @return 新文件名
     */
    String uploadAdImage(MultipartFile file);
}