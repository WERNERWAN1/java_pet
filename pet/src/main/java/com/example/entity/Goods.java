package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("goods")
public class Goods {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String name;
    private String img;
    private Integer price;
    private Integer num;
}