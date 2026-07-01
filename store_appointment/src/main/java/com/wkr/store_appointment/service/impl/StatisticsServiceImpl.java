package com.wkr.store_appointment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.wkr.store_appointment.enums.PayStatusEnum;
import com.wkr.store_appointment.mapper.AppointmentMapper;
import com.wkr.store_appointment.mapper.CustomerMapper;
import com.wkr.store_appointment.mapper.StatisticsMapper;
import com.wkr.store_appointment.pojo.entity.Appointment;
import com.wkr.store_appointment.pojo.entity.Customer;
import com.wkr.store_appointment.pojo.entity.OrderInfo;
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
    private CustomerMapper customerMapper;

    @Autowired
    private AppointmentMapper appointmentMapper;

    @Autowired
    private StatisticsMapper statisticsMapper;

    @Override
    @Cacheable(cacheNames = "statistics:overview", key = "'data'", sync = true)
    public StatisticsOverviewVO overview() {

        LocalDate today = LocalDate.now();
        LocalDateTime beginTime = today.atStartOfDay();
        LocalDateTime endTime = today.plusDays(1).atStartOfDay();

        StatisticsOverviewVO statisticsOverviewVO = new StatisticsOverviewVO();
        statisticsOverviewVO.setCustomerCount(customerMapper.selectCount(Wrappers.lambdaQuery(Customer.class)));
        statisticsOverviewVO.setTodayAppointmentCount(appointmentMapper.selectCount(
                Wrappers.lambdaQuery(Appointment.class)
                        .ge(Appointment::getAppointmentTime, beginTime)
                        .lt(Appointment::getAppointmentTime, endTime)));
        statisticsOverviewVO.setTodayOrderCount(statisticsMapper.selectCount(
                Wrappers.lambdaQuery(OrderInfo.class)
                        .ge(OrderInfo::getCreateTime, beginTime)
                        .lt(OrderInfo::getCreateTime, endTime)));
        statisticsOverviewVO.setTodayAmount(sumPaidAmount(beginTime, endTime));
        return statisticsOverviewVO;
    }

    @Override
    @Cacheable(cacheNames = "statistics:orderAmount", key = "'data'", sync = true)
    public OrderAmountStatisticsVO orderAmount() {

        List<String> dateList = new ArrayList<>();
        List<BigDecimal> amountList = new ArrayList<>();

        LocalDate beginDate = LocalDate.now().minusDays(6);
        for (int i = 0; i < 7; i++) {
            LocalDate date = beginDate.plusDays(i);
            dateList.add(date.toString());
            amountList.add(sumPaidAmount(date.atStartOfDay(), date.plusDays(1).atStartOfDay()));
        }

        OrderAmountStatisticsVO orderAmountStatisticsVO = new OrderAmountStatisticsVO();
        orderAmountStatisticsVO.setDateList(dateList);
        orderAmountStatisticsVO.setAmountList(amountList);
        return orderAmountStatisticsVO;
    }

    private BigDecimal sumPaidAmount(LocalDateTime beginTime, LocalDateTime endTime) {

        QueryWrapper<OrderInfo> queryWrapper = Wrappers.<OrderInfo>query()
                .select("coalesce(sum(amount), 0)")
                .eq("pay_status", PayStatusEnum.PAID.getCode())
                .ge("create_time", beginTime)
                .lt("create_time", endTime);
        List<Object> values = statisticsMapper.selectObjs(queryWrapper);
        if (values == null || values.isEmpty() || values.get(0) == null) {
            return BigDecimal.ZERO;
        }

        Object value = values.get(0);
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        return new BigDecimal(value.toString());
    }
}
