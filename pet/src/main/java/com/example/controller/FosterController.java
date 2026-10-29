package com.example.controller;

import com.example.common.APIResponse;
import com.example.common.ResponeCode;
import com.example.entity.Foster;
import com.example.entity.Room;
import com.example.service.FosterService;
import com.example.service.RoomService;
import com.example.service.BatchDeleteService;
import com.example.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/foster")
public class FosterController {

    private static final Set<Integer> VALID_PROGRESS = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));

    @Autowired
    FosterService fosterService;

    @Autowired
    RoomService roomService;

    @Autowired
    BatchDeleteService batchDeleteService;

    @Autowired
    FileUploadService fileUploadService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(String progress) {
        List<Map<String, Object>> list = fosterService.getFosterList(progress);
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional
    public APIResponse create(Foster foster) throws IOException {
        try {
            // 寄养宠物照片：复用商品图片上传目录，前端通过 /staticfiles/image/ 访问
            String imageUrl = fileUploadService.uploadThingImage(foster.getImageFile());
            if (!StringUtils.isEmpty(imageUrl)) {
                foster.setPetPhoto(imageUrl);
            }
            if (foster.getCageNo() != null) {
                Room room = roomService.getById(foster.getCageNo());
                if (room == null) {
                    return new APIResponse(ResponeCode.FAIL, "房间不存在");
                }
                if (room.getStatus() != null && room.getStatus() == 2) {
                    return new APIResponse(ResponeCode.FAIL, "该房间已被占用");
                }
            }
            fosterService.addFoster(foster);
            if (foster.getCageNo() != null) {
                Room room = roomService.getById(foster.getCageNo());
                if (room != null) {
                    room.setStatus(2);
                    room.setAnimal(foster.getPetName());
                    roomService.updateRoom(room);
                }
            }
            return new APIResponse(ResponeCode.SUCCESS, "创建成功");
        } catch (RuntimeException e) {
            return new APIResponse(ResponeCode.FAIL, e.getMessage());
        }
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @Transactional
    public APIResponse update(Foster foster) {
        Foster old = fosterService.getById(foster.getOrderId());
        if (old == null) {
            return new APIResponse(ResponeCode.FAIL, "记录不存在");
        }
        Integer newProgress = foster.getProgress();
        if (newProgress != null) {
            if (!VALID_PROGRESS.contains(newProgress)) {
                return new APIResponse(ResponeCode.FAIL, "无效的进度状态");
            }
            if (old.getProgress() != null) {
                if (!isValidTransition(old.getProgress(), newProgress)) {
                    return new APIResponse(ResponeCode.FAIL,
                            "不允许从进度 " + old.getProgress() + " 转换到 " + newProgress);
                }
            }
        }
        if (foster.getCageNo() != null && !foster.getCageNo().equals(old.getCageNo())) {
            Room newRoom = roomService.getById(foster.getCageNo());
            if (newRoom == null) {
                return new APIResponse(ResponeCode.FAIL, "房间不存在");
            }
            if (newRoom.getStatus() != null && newRoom.getStatus() == 2) {
                return new APIResponse(ResponeCode.FAIL, "该房间已被占用");
            }
        }
        Integer progress = newProgress != null ? newProgress : old.getProgress();
        if (progress != null && (progress == 3 || progress == 4)) {
            Integer cageNo = (old.getCageNo() != null) ? old.getCageNo() : foster.getCageNo();
            if (cageNo != null) {
                Room used = roomService.getById(cageNo);
                if (used != null) {
                    used.setStatus(1);
                    used.setAnimal(null);
                    used.setAnimalPhoto(null);
                    roomService.updateRoom(used);
                }
            }
        }
        if (foster.getProgress() != null) {
            old.setProgress(foster.getProgress());
        }
        if (foster.getCageNo() != null) {
            old.setCageNo(foster.getCageNo());
            Room room = roomService.getById(foster.getCageNo());
            if (room != null) {
                room.setStatus(2);
                room.setAnimal(old.getPetName());
                roomService.updateRoom(room);
            }
        }
        fosterService.updateFoster(old);
        return new APIResponse(ResponeCode.SUCCESS, "更新成功");
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @Transactional
    public APIResponse delete(String ids) {
        batchDeleteService.batchDelete(ids, id -> {
            int orderId = Integer.parseInt(id);
            Foster foster = fosterService.getById(orderId);
            if (foster != null && foster.getCageNo() != null) {
                Integer progress = foster.getProgress();
                if (progress != null && progress == 2) {
                    Room room = roomService.getById(foster.getCageNo());
                    if (room != null) {
                        room.setStatus(1);
                        room.setAnimal(null);
                        room.setAnimalPhoto(null);
                        roomService.updateRoom(room);
                    }
                }
            }
            fosterService.deleteFoster(orderId);
        });
        return new APIResponse(ResponeCode.SUCCESS, "删除成功");
    }

    private boolean isValidTransition(int from, int to) {
        switch (from) {
            case 5: return to == 1 || to == 2;
            case 1: return to == 2 || to == 5;
            case 2: return to == 3 || to == 4;
            default: return false;
        }
    }
}
