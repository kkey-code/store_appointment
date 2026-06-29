package com.wkr.store_appointment.pojo.DTO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class InventoryItemDTO {

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
}
