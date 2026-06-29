package com.wkr.store_appointment.pojo.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Appointment {

    private Long id;
    private Long customerId;
    private Long employeeId;
    private Long serviceItemId;
    private Integer serviceItemCount;
    private String serviceItemsText;
    private LocalDateTime appointmentTime;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String remark;
}
