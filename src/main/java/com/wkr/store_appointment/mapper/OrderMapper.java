package com.wkr.store_appointment.mapper;

import com.github.pagehelper.Page;
import com.wkr.store_appointment.pojo.DTO.OrderPageQueryDTO;
import com.wkr.store_appointment.pojo.entity.OrderInfo;
import com.wkr.store_appointment.pojo.vo.OrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

@Mapper
public interface OrderMapper {

    Page<OrderVO> page(OrderPageQueryDTO orderPageQueryDTO);

    void insert(OrderInfo orderInfo);

    OrderVO getById(Long id);

    OrderInfo getEntityById(Long id);

    void updatePayStatus(@Param("id") Long id,
                         @Param("payStatus") Integer payStatus,
                         @Param("debtStatus") Integer debtStatus,
                         @Param("payTime") LocalDateTime payTime,
                         @Param("updateTime") LocalDateTime updateTime);

    void updatePayment(@Param("id") Long id,
                       @Param("paidAmount") java.math.BigDecimal paidAmount,
                       @Param("debtAmount") java.math.BigDecimal debtAmount,
                       @Param("monthlyPayment") java.math.BigDecimal monthlyPayment,
                       @Param("paymentMethod") String paymentMethod,
                       @Param("debtStatus") Integer debtStatus,
                       @Param("payStatus") Integer payStatus,
                       @Param("payTime") LocalDateTime payTime,
                       @Param("updateTime") LocalDateTime updateTime);

    void updateOrderStatus(@Param("id") Long id,
                           @Param("orderStatus") Integer orderStatus,
                           @Param("updateTime") LocalDateTime updateTime);
}
