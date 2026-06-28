package com.wkr.store_appointment.pojo.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EmployeeVO {

    private Long id;
    private String name;
    private String phone;
    private Integer gender;
    private String position;
    private Integer status;
    private LocalDateTime createTime;
}
