package com.example.controller;

import com.example.common.APIResponse;
import com.example.common.ResponeCode;
import com.example.entity.PetProductCollect;
import com.example.permission.Access;
import com.example.permission.AccessLevel;
import com.example.service.PetProductCollectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/petProductCollect")
public class PetProductCollectController {

    private final static Logger logger = LoggerFactory.getLogger(PetProductCollectController.class);

    @Autowired
    PetProductCollectService petProductCollectService;

    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/collect", method = RequestMethod.POST)
    @Transactional
    public APIResponse collect(PetProductCollect petProductCollect) throws IOException {
        // Service内部处理收藏和更新计数
        boolean success = petProductCollectService.addCollect(petProductCollect);
        if (success) {
            return new APIResponse(ResponeCode.SUCCESS, "收藏成功");
        } else {
            return new APIResponse(ResponeCode.SUCCESS, "您已收藏过了");
        }
    }

    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/unCollect", method = RequestMethod.POST)
    @Transactional
    public APIResponse unCollect(String id) throws IOException {
        petProductCollectService.deletePetProductCollect(id);
        return new APIResponse(ResponeCode.SUCCESS, "取消收藏成功");
    }

    @RequestMapping(value = "/getUserCollectList", method = RequestMethod.GET)
    @Transactional
    public APIResponse getUserCollectList(String userId) throws IOException {
        List<Map> lists = petProductCollectService.getPetProductCollectList(userId);
        return new APIResponse(ResponeCode.SUCCESS, "获取成功", lists);
    }
}
