package com.example.controller;

import com.example.common.APIResponse;
import com.example.common.ResponeCode;
import com.example.entity.Animal;
import com.example.service.AnimalService;
import com.example.service.BatchDeleteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/animal")
public class AnimalController {

    @Autowired
    AnimalService animalService;

    @Autowired
    BatchDeleteService batchDeleteService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list() {
        List<Animal> list = animalService.getAnimalList();
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public APIResponse detail(Integer pid) {
        Animal animal = animalService.getAnimalById(pid);
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", animal);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional
    public APIResponse create(Animal animal) {
        animalService.addAnimal(animal);
        return new APIResponse(ResponeCode.SUCCESS, "创建成功");
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @Transactional
    public APIResponse update(Animal animal) {
        animalService.updateAnimal(animal);
        return new APIResponse(ResponeCode.SUCCESS, "更新成功");
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids) {
        batchDeleteService.batchDelete(ids, id -> animalService.deleteAnimal(Integer.parseInt(id)));
        return new APIResponse(ResponeCode.SUCCESS, "删除成功");
    }
}