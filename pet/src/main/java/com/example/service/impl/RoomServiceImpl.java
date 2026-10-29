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
        // 按状态排序：空闲(1) > 维护中(3) > 已占用(2)，同状态内按 id 降序
        queryWrapper.orderByAsc("FIELD(status, 1, 3, 2)").orderByDesc("id");
        return roomMapper.selectList(queryWrapper);
    }

    @Override
    public void addRoom(Room room) {
        // 校验房间编号唯一性
        QueryWrapper<Room> qw = new QueryWrapper<>();
        qw.eq("name", room.getName());
        if (roomMapper.selectCount(qw) > 0) {
            throw new RuntimeException("房间编号已存在");
        }
        roomMapper.insert(room);
    }

    @Override
    public void updateRoom(Room room) {
        // 校验房间编号唯一性（排除自身）
        if (room.getName() != null) {
            QueryWrapper<Room> qw = new QueryWrapper<>();
            qw.eq("name", room.getName());
            qw.ne("id", room.getId());
            if (roomMapper.selectCount(qw) > 0) {
                throw new RuntimeException("房间编号已存在");
            }
        }
        roomMapper.updateById(room);
    }

    @Override
    public void deleteRoom(Integer id) {
        roomMapper.deleteById(id);
    }
}