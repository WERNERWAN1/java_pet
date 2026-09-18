package com.example.controller;

import com.example.common.APIResponse;
import com.example.common.ResponeCode;
import com.example.entity.Goods;
import com.example.service.GoodsService;
import com.example.service.BatchDeleteService;
import com.example.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    GoodsService goodsService;

    @Autowired
    BatchDeleteService batchDeleteService;

    @Autowired
    FileUploadService fileUploadService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(String keyword) {
        List<Goods> list = goodsService.getGoodsList(keyword);
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public APIResponse detail(Integer id) {
        Goods goods = goodsService.getGoodsById(id);
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", goods);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional
    public APIResponse create(Goods goods) throws IOException {
        String imageUrl = fileUploadService.uploadThingImage(goods.getImageFile());
        if (!StringUtils.isEmpty(imageUrl)) {
            goods.setImg(imageUrl);
        }
        goodsService.addGoods(goods);
        return new APIResponse(ResponeCode.SUCCESS, "创建成功");
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @Transactional
    public APIResponse update(Goods goods) throws IOException {
        String imageUrl = fileUploadService.uploadThingImage(goods.getImageFile());
        if (!StringUtils.isEmpty(imageUrl)) {
            goods.setImg(imageUrl);
        }
        goodsService.updateGoods(goods);
        return new APIResponse(ResponeCode.SUCCESS, "更新成功");
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids) {
        batchDeleteService.batchDelete(ids, id -> goodsService.deleteGoods(Integer.parseInt(id)));
        return new APIResponse(ResponeCode.SUCCESS, "删除成功");
    }
}