package com.wkr.store_appointment.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("inventory_item")
public class InventoryItem {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String category;
    private String unit;
    private BigDecimal quantity;
    private BigDecimal safetyStock;
    private BigDecimal costPrice;
    private String supplier;
    private Integer status;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
