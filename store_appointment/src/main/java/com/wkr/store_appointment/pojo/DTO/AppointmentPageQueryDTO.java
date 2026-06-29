package com.wkr.store_appointment.pojo.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentPageQueryDTO {

    private Integer page;
    private Integer pageSize;
    private String customerName;
    private String employeeName;
    private Integer status;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
}
