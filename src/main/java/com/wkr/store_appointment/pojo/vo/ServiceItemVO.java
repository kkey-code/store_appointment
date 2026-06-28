package com.wkr.store_appointment.pojo.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ServiceItemVO {

    private Long id;
    private String name;
    private BigDecimal price;
    private Integer duration;
    private String description;
    private Integer status; // 0-下架 1-上架
    private LocalDateTime createTime;
}
