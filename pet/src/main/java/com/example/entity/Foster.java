package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("foster")
public class Foster extends BaseEntity {
    @TableId(value = "order_id", type = IdType.AUTO)
    private Long orderId;
    @TableField
    private Long clientUid;
    @TableField
    private String petName;
    @TableField
    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;
    @TableField
    private Integer duration;
    @TableField
    private String progress;
    @TableField
    private Integer cageNo;
}
