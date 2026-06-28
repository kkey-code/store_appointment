package com.wkr.store_appointment.pojo.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderAmountStatisticsVO {

    private List<String> dateList;
    private List<BigDecimal> amountList;
}
