package com.example.controller;

import com.example.common.APIResponse;
import com.example.common.ResponeCode;
import com.example.entity.Submit;
import com.example.permission.Access;
import com.example.permission.AccessLevel;
import com.example.service.SubmitService;
import com.example.service.BatchDeleteService;
import com.example.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/submit")
public class SubmitController {

    @Autowired
    SubmitService submitService;

    @Autowired
    BatchDeleteService batchDeleteService;

    @Autowired
    FileUploadService fileUploadService;

    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(String keyword) {
        List<Submit> list = submitService.getSubmitList(keyword);
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional
    public APIResponse create(Submit submit) throws IOException {
        String imageUrl = fileUploadService.uploadThingImage(submit.getImageFile());
        if (!StringUtils.isEmpty(imageUrl)) {
            submit.setImg(imageUrl);
        }
        submitService.addSubmit(submit);
        return new APIResponse(ResponeCode.SUCCESS, "创建成功");
    }

    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @Transactional
    public APIResponse update(Submit submit) throws IOException {
        String imageUrl = fileUploadService.uploadThingImage(submit.getImageFile());
        if (!StringUtils.isEmpty(imageUrl)) {
            submit.setImg(imageUrl);
        }
        submitService.updateSubmit(submit);
        return new APIResponse(ResponeCode.SUCCESS, "更新成功");
    }

    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids) {
        batchDeleteService.batchDelete(ids, id -> submitService.deleteSubmit(Integer.parseInt(id)));
        return new APIResponse(ResponeCode.SUCCESS, "删除成功");
    }
}
