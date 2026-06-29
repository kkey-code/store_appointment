package com.wkr.store_appointment.pojo.DTO;

import lombok.Data;

@Data
public class ServiceItemPageQueryDTO {

    private Integer page;
    private Integer pageSize;
    private String name;
    private Integer status;

}
