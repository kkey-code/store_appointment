package com.wkr.store_appointment.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("order_info")
public class OrderInfo {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderNo;
    private Long appointmentId;
    private Long customerId;
    private Long serviceItemId;
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
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
