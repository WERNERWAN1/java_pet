package com.example.controller;

import com.example.common.APIResponse;
import com.example.common.ResponeCode;
import com.example.entity.Foster;
import com.example.entity.Room;
import com.example.service.FosterService;
import com.example.service.RoomService;
import com.example.service.BatchDeleteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/foster")
public class FosterController {

    @Autowired
    FosterService fosterService;

    @Autowired
    RoomService roomService;

    @Autowired
    BatchDeleteService batchDeleteService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(String progress) {
        List<Map<String, Object>> list = fosterService.getFosterList(progress);
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional
    public APIResponse create(Foster foster) {
        try {
            fosterService.addFoster(foster);
            return new APIResponse(ResponeCode.SUCCESS, "创建成功");
        } catch (RuntimeException e) {
            // 寄养天数非法等业务校验失败，返回可读提示而非 500 错误
            return new APIResponse(ResponeCode.FAIL, e.getMessage());
        }
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @Transactional
    public APIResponse update(Foster foster) {
        // 先获取原记录，保留未传的字段
        Foster old = fosterService.getById(foster.getOrderId());
        if (old == null) {
            return new APIResponse(ResponeCode.FAIL, "记录不存在");
        }
        // 宠物领回或寄养完成时，同步释放占用的房间，避免房间长期显示"已占用"
        if ("已领回".equals(foster.getProgress()) || "已完成".equals(foster.getProgress())) {
            Integer cageNo = (old.getCageNo() != null) ? old.getCageNo() : foster.getCageNo();
            if (cageNo != null) {
                Room used = roomService.getById(cageNo);
                if (used != null) {
                    used.setStatus("空闲");
                    used.setAnimal(null);
                    roomService.updateRoom(used);
                }
            }
        }
        if (foster.getProgress() != null) {
            old.setProgress(foster.getProgress());
        }
        if (foster.getCageNo() != null) {
            old.setCageNo(foster.getCageNo());
            // 更新房间状态为已占用
            Room room = roomService.getById(foster.getCageNo());
            if (room != null) {
                room.setStatus("已占用");
                room.setAnimal(old.getPetName());
                roomService.updateRoom(room);
            }
        }
        fosterService.updateFoster(old);
        return new APIResponse(ResponeCode.SUCCESS, "更新成功");
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids) {
        batchDeleteService.batchDelete(ids, id -> fosterService.deleteFoster(Integer.parseInt(id)));
        return new APIResponse(ResponeCode.SUCCESS, "删除成功");
    }
}