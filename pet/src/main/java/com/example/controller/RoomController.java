package com.example.controller;

import com.example.common.APIResponse;
import com.example.common.ResponeCode;
import com.example.entity.Room;
import com.example.service.RoomService;
import com.example.service.BatchDeleteService;
import com.example.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/room")
public class RoomController {

    @Autowired
    RoomService roomService;

    @Autowired
    BatchDeleteService batchDeleteService;

    @Autowired
    FileUploadService fileUploadService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(String keyword) {
        List<Room> list = roomService.getRoomList(keyword);
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional
    public APIResponse create(Room room) throws IOException {
        String imageUrl = fileUploadService.uploadThingImage(room.getImageFile());
        if (!StringUtils.isEmpty(imageUrl)) {
            room.setAnimalPhoto(imageUrl);
        }
        roomService.addRoom(room);
        return new APIResponse(ResponeCode.SUCCESS, "创建成功");
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @Transactional
    public APIResponse update(Room room) throws IOException {
        String imageUrl = fileUploadService.uploadThingImage(room.getImageFile());
        if (!StringUtils.isEmpty(imageUrl)) {
            room.setAnimalPhoto(imageUrl);
        }
        roomService.updateRoom(room);
        return new APIResponse(ResponeCode.SUCCESS, "更新成功");
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids) {
        batchDeleteService.batchDelete(ids, id -> roomService.deleteRoom(Integer.parseInt(id)));
        return new APIResponse(ResponeCode.SUCCESS, "删除成功");
    }
}