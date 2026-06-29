package com.wkr.store_appointment.pojo.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderAmountStatisticsVO {

    private List<String> dateList; //日期列表
    private List<BigDecimal> amountList; //金额列表
}
