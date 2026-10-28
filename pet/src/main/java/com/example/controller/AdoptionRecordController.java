package com.example.controller;

import com.example.common.APIResponse;
import com.example.common.ResponeCode;
import com.example.entity.AdoptionRecord;
import com.example.service.AdoptionRecordService;
import com.example.service.BatchDeleteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/adoptionRecord")
public class AdoptionRecordController {

    @Autowired
    AdoptionRecordService adoptionRecordService;

    @Autowired
    BatchDeleteService batchDeleteService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(String breed, String state) {
        List<Map<String, Object>> list = adoptionRecordService.getAdoptionRecordList(breed, state);
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional
    public APIResponse create(AdoptionRecord adoptionRecord) {
        try {
            adoptionRecordService.addAdoptionRecord(adoptionRecord);
            return new APIResponse(ResponeCode.SUCCESS, "创建成功");
        } catch (RuntimeException e) {
            // 重复领养等业务校验失败，返回可读提示而非 500 错误
            return new APIResponse(ResponeCode.FAIL, e.getMessage());
        }
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @Transactional
    public APIResponse update(AdoptionRecord adoptionRecord) {
        adoptionRecordService.updateAdoptionRecord(adoptionRecord);
        return new APIResponse(ResponeCode.SUCCESS, "更新成功");
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids) {
        batchDeleteService.batchDelete(ids, id -> adoptionRecordService.deleteAdoptionRecord(Integer.parseInt(id)));
        return new APIResponse(ResponeCode.SUCCESS, "删除成功");
    }
}
