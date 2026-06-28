package com.wkr.store_appointment.pojo.DTO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderPaymentDTO {

    private BigDecimal paidAmount;
    private BigDecimal monthlyPayment;
    private String paymentMethod;
}
