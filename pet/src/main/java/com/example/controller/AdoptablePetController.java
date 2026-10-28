package com.example.controller;

import com.example.common.APIResponse;
import com.example.common.ResponeCode;
import com.example.entity.AdoptablePet;
import com.example.service.AdoptablePetService;
import com.example.service.BatchDeleteService;
import com.example.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/adoptablePet")
public class AdoptablePetController {

    @Autowired
    AdoptablePetService adoptablePetService;

    @Autowired
    BatchDeleteService batchDeleteService;

    @Autowired
    FileUploadService fileUploadService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(String keyword) {
        List<AdoptablePet> list = adoptablePetService.getAdoptablePetList(keyword);
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public APIResponse detail(Integer pid) {
        AdoptablePet adoptablePet = adoptablePetService.getAdoptablePetById(pid);
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", adoptablePet);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional
    public APIResponse create(AdoptablePet adoptablePet) throws IOException {
        String imageUrl = fileUploadService.uploadThingImage(adoptablePet.getImageFile());
        if (!StringUtils.isEmpty(imageUrl)) {
            adoptablePet.setPhoto(imageUrl);
        }
        adoptablePetService.addAdoptablePet(adoptablePet);
        return new APIResponse(ResponeCode.SUCCESS, "创建成功");
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @Transactional
    public APIResponse update(AdoptablePet adoptablePet) throws IOException {
        String imageUrl = fileUploadService.uploadThingImage(adoptablePet.getImageFile());
        if (!StringUtils.isEmpty(imageUrl)) {
            adoptablePet.setPhoto(imageUrl);
        }
        adoptablePetService.updateAdoptablePet(adoptablePet);
        return new APIResponse(ResponeCode.SUCCESS, "更新成功");
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids) {
        batchDeleteService.batchDelete(ids, id -> adoptablePetService.deleteAdoptablePet(Integer.parseInt(id)));
        return new APIResponse(ResponeCode.SUCCESS, "删除成功");
    }
}
