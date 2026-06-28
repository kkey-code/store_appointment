package com.wkr.store_appointment.pojo.DTO;

import lombok.Data;

@Data
public class CustomerPageQueryDTO {

    private Integer page;
    private Integer pageSize;
    private String name;
    private String phone;
    private String level;
}
