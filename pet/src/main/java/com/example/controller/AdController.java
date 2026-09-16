package com.example.controller;

import com.example.common.APIResponse;
import com.example.common.ResponeCode;
import com.example.entity.Ad;
import com.example.permission.Access;
import com.example.permission.AccessLevel;
import com.example.service.AdService;
import com.example.service.BatchDeleteService;
import com.example.service.FileUploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * @author wanchun
 * @email email
 */
@RestController
@RequestMapping("/ad")
public class AdController {

    private final static Logger logger = LoggerFactory.getLogger(AdController.class);

    @Autowired
    AdService service;
    
    @Autowired
    FileUploadService fileUploadService;
    
    @Autowired
    BatchDeleteService batchDeleteService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(){
        List<Ad> list =  service.getAdList();
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional
    public APIResponse create(Ad ad) throws IOException {
        // 处理图片上传
        String imageUrl = fileUploadService.uploadAdImage(ad.getImageFile());
        if(!StringUtils.isEmpty(imageUrl)) {
            ad.setImage(imageUrl);
        }

        service.createAd(ad);
        return new APIResponse(ResponeCode.SUCCESS, "创建成功");
    }

    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids){
        batchDeleteService.batchDelete(ids, service::deleteAd);
        return new APIResponse(ResponeCode.SUCCESS, "删除成功");
    }

    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @Transactional
    public APIResponse update(Ad ad) throws IOException {
        // 处理图片上传
        String imageUrl = fileUploadService.uploadAdImage(ad.getImageFile());
        if(!StringUtils.isEmpty(imageUrl)) {
            ad.setImage(imageUrl);
        }

        service.updateAd(ad);
        return new APIResponse(ResponeCode.SUCCESS, "更新成功");
    }

}
