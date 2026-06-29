package com.wkr.store_appointment.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Mapper
public interface StatisticsMapper {

    Long countCustomers();

    Long countTodayAppointments(@Param("beginTime") LocalDateTime beginTime,
                                @Param("endTime") LocalDateTime endTime);

    Long countTodayOrders(@Param("beginTime") LocalDateTime beginTime,
                          @Param("endTime") LocalDateTime endTime);

    BigDecimal sumTodayAmount(@Param("beginTime") LocalDateTime beginTime,
                              @Param("endTime") LocalDateTime endTime);

    BigDecimal sumPaidAmountByDate(@Param("date") String date);
}
