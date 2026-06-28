package com.wkr.store_appointment.pojo.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderVO {

    private Long id;
    private String orderNo;
    private Long appointmentId;
    private Long customerId;
    private String customerName;
    private Long serviceItemId;
    private String serviceItemName;
    private String cardType;
    private BigDecimal originalAmount;
    private BigDecimal discountAmount;
    private BigDecimal amount;
    private BigDecimal paidAmount;
    private BigDecimal debtAmount;
    private BigDecimal monthlyPayment;
    private String paymentMethod;
    private Integer debtStatus;
    private Integer payStatus;
    private Integer orderStatus;
    private LocalDateTime payTime;
    private LocalDateTime createTime;
}
