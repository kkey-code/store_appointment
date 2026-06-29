package com.wkr.store_appointment.pojo.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class InventoryItemVO {

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
