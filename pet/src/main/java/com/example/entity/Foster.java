package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("foster")
public class Foster {
    @TableId(value = "order_id", type = IdType.AUTO)
    private Integer orderId;
    private Integer clientUid;
    private String petName;
    private String startDate;
    private Integer duration;
    private String progress;
    private Integer cageNo;
}