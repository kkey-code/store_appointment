package com.wkr.store_appointment.pojo.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentDTO {

    private Long id;
    private Long customerId;
    private Long employeeId;
    private Long serviceItemId;
    private LocalDateTime appointmentTime;
    private Integer status;
    private String remark;

}
