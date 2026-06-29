package com.wkr.store_appointment.pojo.DTO;

import lombok.Data;

@Data
public class InventoryItemPageQueryDTO {

    private int page = 1;
    private int pageSize = 10;
    private String name;
    private String category;
    private Integer status;
}
