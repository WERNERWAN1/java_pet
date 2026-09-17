package com.example.controller;

import com.example.common.APIResponse;
import com.example.common.ResponeCode;
import com.example.entity.Foster;
import com.example.service.FosterService;
import com.example.service.BatchDeleteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/foster")
public class FosterController {

    @Autowired
    FosterService fosterService;

    @Autowired
    BatchDeleteService batchDeleteService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list() {
        List<Map<String, Object>> list = fosterService.getFosterList();
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional
    public APIResponse create(Foster foster) {
        fosterService.addFoster(foster);
        return new APIResponse(ResponeCode.SUCCESS, "创建成功");
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @Transactional
    public APIResponse update(Foster foster) {
        fosterService.updateFoster(foster);
        return new APIResponse(ResponeCode.SUCCESS, "更新成功");
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids) {
        batchDeleteService.batchDelete(ids, id -> fosterService.deleteFoster(Integer.parseInt(id)));
        return new APIResponse(ResponeCode.SUCCESS, "删除成功");
    }
}