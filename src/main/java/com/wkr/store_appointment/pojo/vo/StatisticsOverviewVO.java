package com.wkr.store_appointment.pojo.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StatisticsOverviewVO {

    private Long customerCount;
    private Long todayAppointmentCount;
    private Long todayOrderCount;
    private BigDecimal todayAmount;
}
