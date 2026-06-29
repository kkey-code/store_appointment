package com.wkr.store_appointment.service.impl;

import com.wkr.store_appointment.mapper.StatisticsMapper;
import com.wkr.store_appointment.pojo.vo.OrderAmountStatisticsVO;
import com.wkr.store_appointment.pojo.vo.StatisticsOverviewVO;
import com.wkr.store_appointment.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private StatisticsMapper statisticsMapper;

    /**
     * 统计数据
     */
    @Override
    @Cacheable(cacheNames = "statistics:overview", key = "'data'", sync = true)
    public StatisticsOverviewVO overview() {

        LocalDate today = LocalDate.now();
        LocalDateTime beginTime = today.atStartOfDay(); // 今天开始时间
        LocalDateTime endTime = today.plusDays(1).atStartOfDay(); // 明天开始时间

        StatisticsOverviewVO statisticsOverviewVO = new StatisticsOverviewVO();
        statisticsOverviewVO.setCustomerCount(nullToZero(statisticsMapper.countCustomers())); // 客户数量
        statisticsOverviewVO.setTodayAppointmentCount(nullToZero(statisticsMapper.countTodayAppointments(beginTime, endTime))); // 今天预约数量
        statisticsOverviewVO.setTodayOrderCount(nullToZero(statisticsMapper.countTodayOrders(beginTime, endTime))); // 今天订单数量
        statisticsOverviewVO.setTodayAmount(nullToZero(statisticsMapper.sumTodayAmount(beginTime, endTime))); // 今天金额
        return statisticsOverviewVO;
    }

    /**
     * 营业额统计
     */
    @Override
    @Cacheable(cacheNames = "statistics:orderAmount", key = "'data'", sync = true)
    public OrderAmountStatisticsVO orderAmount() {

        List<String> dateList = new ArrayList<>();
        List<BigDecimal> amountList = new ArrayList<>();

        LocalDate beginDate = LocalDate.now().minusDays(6);
        for (int i = 0; i < 7; i++) {
            LocalDate date = beginDate.plusDays(i);
            String dateText = date.toString();
            dateList.add(dateText);
            amountList.add(nullToZero(statisticsMapper.sumPaidAmountByDate(dateText)));
        }

        OrderAmountStatisticsVO orderAmountStatisticsVO = new OrderAmountStatisticsVO();
        orderAmountStatisticsVO.setDateList(dateList);
        orderAmountStatisticsVO.setAmountList(amountList);
        return orderAmountStatisticsVO;
    }

    private Long nullToZero(Long value) {

        return value == null ? 0L : value;
    }

    private BigDecimal nullToZero(BigDecimal value) {

        return value == null ? BigDecimal.ZERO : value;
    }
}
