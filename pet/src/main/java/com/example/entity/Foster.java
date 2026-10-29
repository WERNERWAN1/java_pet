package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
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
    /** 宠物体重(kg)，定点数避免浮点精度误差 */
    @TableField
    private BigDecimal petWeight;
    /** 宠物照片地址（审核辨识用） */
    @TableField
    private String petPhoto;
    /** 寄养备注（病史/饮食/特殊照护需求） */
    @TableField
    private String remark;
    /** 待上传的宠物照片文件，仅接收表单参数，不落库 */
    @TableField(exist = false)
    private MultipartFile imageFile;
    @TableField
    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;
    @TableField
    private Integer duration;
    /** 寄养进度：1-待处理，2-寄养中，3-已完成，4-已领回，5-已预约 */
    @TableField
    private Integer progress;
    @TableField
    private Integer cageNo;
}
