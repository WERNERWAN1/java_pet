package com.example.controller;

import com.example.common.APIResponse;
import com.example.common.ResponeCode;
import com.example.entity.PetProductWish;
import com.example.permission.Access;
import com.example.permission.AccessLevel;
import com.example.service.PetProductWishService;
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
@RequestMapping("/petProductWish")
public class PetProductWishController {

    private final static Logger logger = LoggerFactory.getLogger(PetProductWishController.class);

    @Autowired
    PetProductWishService petProductWishService;

    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/wish", method = RequestMethod.POST)
    @Transactional
    public APIResponse wish(PetProductWish petProductWish) throws IOException {
        // Service内部处理添加愿望和更新计数
        boolean success = petProductWishService.addWish(petProductWish);
        if (success) {
            return new APIResponse(ResponeCode.SUCCESS, "添加成功");
        } else {
            return new APIResponse(ResponeCode.SUCCESS, "您已添加过了");
        }
    }

    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/unWish", method = RequestMethod.POST)
    @Transactional
    public APIResponse unWish(String id) throws IOException {
        petProductWishService.deletePetProductWish(id);
        return new APIResponse(ResponeCode.SUCCESS, "取消收藏成功");
    }

    @RequestMapping(value = "/getUserWishList", method = RequestMethod.GET)
    @Transactional
    public APIResponse getUserWishList(String userId) throws IOException {
        List<Map> lists = petProductWishService.getPetProductWishList(userId);
        return new APIResponse(ResponeCode.SUCCESS, "获取成功", lists);
    }
}
