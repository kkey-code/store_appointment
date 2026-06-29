package com.wkr.store_appointment.pojo.DTO;

import lombok.Data;

@Data
public class EmployeeDTO {
    private Long id;
    private String name;
    private String phone;
    private Integer gender;
    private String position;
    private Integer status;
    private String remark;
}

