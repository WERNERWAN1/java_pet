package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.Animal;

import java.util.List;

public interface AnimalService extends IService<Animal> {
    List<Animal> getAnimalList();
    void addAnimal(Animal animal);
    void updateAnimal(Animal animal);
    void deleteAnimal(Integer pid);
    Animal getAnimalById(Integer pid);
}