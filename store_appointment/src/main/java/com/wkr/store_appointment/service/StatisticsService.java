package com.wkr.store_appointment.service;

import com.wkr.store_appointment.pojo.vo.OrderAmountStatisticsVO;
import com.wkr.store_appointment.pojo.vo.StatisticsOverviewVO;

public interface StatisticsService {

    StatisticsOverviewVO overview();

    OrderAmountStatisticsVO orderAmount();
}
