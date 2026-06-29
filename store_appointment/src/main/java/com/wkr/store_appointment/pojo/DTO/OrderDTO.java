package com.wkr.store_appointment.pojo.DTO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderDTO {

    private Long id;
    private Long appointmentId;
    private Long customerId;
    private Long serviceItemId;
    private String cardType;
    private BigDecimal originalAmount;
    private BigDecimal discountAmount;
    private BigDecimal amount;
    private BigDecimal paidAmount;
    private BigDecimal monthlyPayment;
    private String paymentMethod;
    private String remark;

}
