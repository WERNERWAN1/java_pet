package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.Room;

import java.util.List;

public interface RoomService extends IService<Room> {
    List<Room> getRoomList(String keyword);
    void addRoom(Room room);
    void updateRoom(Room room);
    void deleteRoom(Integer id);
    /**
     * 清空房间：宠物离场时置为空闲，并清除寄养宠物名称与照片
     */
    void clearRoom(Long id);
}