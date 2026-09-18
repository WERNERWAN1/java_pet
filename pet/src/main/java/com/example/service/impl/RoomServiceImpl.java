package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.Room;
import com.example.mapper.RoomMapper;
import com.example.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomServiceImpl extends ServiceImpl<RoomMapper, Room> implements RoomService {

    @Autowired
    RoomMapper roomMapper;

    @Override
    public List<Room> getRoomList(String keyword) {
        QueryWrapper<Room> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(keyword)) {
            queryWrapper.like("name", keyword);
        }
        queryWrapper.orderByDesc("id");
        return roomMapper.selectList(queryWrapper);
    }

    @Override
    public void addRoom(Room room) {
        roomMapper.insert(room);
    }

    @Override
    public void updateRoom(Room room) {
        roomMapper.updateById(room);
    }

    @Override
    public void deleteRoom(Integer id) {
        roomMapper.deleteById(id);
    }
}