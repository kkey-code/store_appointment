package com.wkr.store_appointment.pojo.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AppointmentVO {

    private Long id;
    private Long customerId;
    private String customerName;
    private String customerPhone;
    private Long employeeId;
    private String employeeName;
    private Long serviceItemId;
    private String serviceItemName;
    private BigDecimal serviceItemPrice;
    private Integer serviceItemCount;
    private String serviceItemsText;
    private LocalDateTime appointmentTime;
    private Integer status;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
