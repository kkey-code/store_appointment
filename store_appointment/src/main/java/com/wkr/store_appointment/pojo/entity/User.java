package com.wkr.store_appointment.pojo.entity;

import lombok.Data;

@Data
public class User {

    private Long id;
    private String username;
    private String password;
    private String role; // 1:员工 2:顾客
    private Integer employee_id; // 员工ID
    private Integer status; // 1:正常 0:禁用
    private String create_time; // 创建时间
    private String update_time; // 更新时间
}
