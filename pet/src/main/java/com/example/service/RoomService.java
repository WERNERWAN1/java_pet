package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.Room;

import java.util.List;

public interface RoomService extends IService<Room> {
    List<Room> getRoomList(String keyword);
    void addRoom(Room room);
    void updateRoom(Room room);
    void deleteRoom(Integer id);
}