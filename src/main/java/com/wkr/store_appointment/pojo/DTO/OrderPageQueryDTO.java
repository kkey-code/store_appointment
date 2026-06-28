package com.wkr.store_appointment.pojo.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderPageQueryDTO {

    private Integer page;
    private Integer pageSize;
    private String orderNo;
    private String customerName;
    private String paymentMethod;
    private Integer payStatus;
    private Integer debtStatus;
    private Integer orderStatus;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
}
