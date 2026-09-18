package com.example.controller;

import com.example.common.APIResponse;
import com.example.common.ResponeCode;
import com.example.entity.Animal;
import com.example.service.AnimalService;
import com.example.service.BatchDeleteService;
import com.example.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/animal")
public class AnimalController {

    @Autowired
    AnimalService animalService;

    @Autowired
    BatchDeleteService batchDeleteService;

    @Autowired
    FileUploadService fileUploadService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(String keyword) {
        List<Animal> list = animalService.getAnimalList(keyword);
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public APIResponse detail(Integer pid) {
        Animal animal = animalService.getAnimalById(pid);
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", animal);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional
    public APIResponse create(Animal animal) throws IOException {
        String imageUrl = fileUploadService.uploadThingImage(animal.getImageFile());
        if (!StringUtils.isEmpty(imageUrl)) {
            animal.setPhoto(imageUrl);
        }
        animalService.addAnimal(animal);
        return new APIResponse(ResponeCode.SUCCESS, "创建成功");
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @Transactional
    public APIResponse update(Animal animal) throws IOException {
        String imageUrl = fileUploadService.uploadThingImage(animal.getImageFile());
        if (!StringUtils.isEmpty(imageUrl)) {
            animal.setPhoto(imageUrl);
        }
        animalService.updateAnimal(animal);
        return new APIResponse(ResponeCode.SUCCESS, "更新成功");
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids) {
        batchDeleteService.batchDelete(ids, id -> animalService.deleteAnimal(Integer.parseInt(id)));
        return new APIResponse(ResponeCode.SUCCESS, "删除成功");
    }
}