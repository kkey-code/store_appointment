package com.wkr.store_appointment.pojo.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Employee {

    private Long id;
    private String name;
    private String phone;
    private Integer gender;
    private String position; // 职位
    private Integer status;
    private String remark; // 备注

    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}
