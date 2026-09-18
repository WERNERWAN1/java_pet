package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.Animal;
import com.example.mapper.AnimalMapper;
import com.example.service.AnimalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnimalServiceImpl extends ServiceImpl<AnimalMapper, Animal> implements AnimalService {

    @Autowired
    AnimalMapper animalMapper;

    @Override
    public List<Animal> getAnimalList(String keyword) {
        QueryWrapper<Animal> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(keyword)) {
            queryWrapper.like("nickname", keyword);
        }
        queryWrapper.orderByDesc("pid");
        return animalMapper.selectList(queryWrapper);
    }

    @Override
    public void addAnimal(Animal animal) {
        animalMapper.insert(animal);
    }

    @Override
    public void updateAnimal(Animal animal) {
        animalMapper.updateById(animal);
    }

    @Override
    public void deleteAnimal(Integer pid) {
        animalMapper.deleteById(pid);
    }

    @Override
    public Animal getAnimalById(Integer pid) {
        return animalMapper.selectById(pid);
    }
}