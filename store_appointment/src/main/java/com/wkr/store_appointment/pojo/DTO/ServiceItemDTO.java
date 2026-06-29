package com.wkr.store_appointment.pojo.DTO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ServiceItemDTO {

    private Long id;
    private String name;
    private BigDecimal price;
    private Integer duration;
    private String description;
    private Integer status;

}
