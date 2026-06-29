package com.wkr.store_appointment.service;

import com.wkr.store_appointment.common.PageResult;
import com.wkr.store_appointment.pojo.DTO.OrderDTO;
import com.wkr.store_appointment.pojo.DTO.OrderPageQueryDTO;
import com.wkr.store_appointment.pojo.DTO.OrderPaymentDTO;
import com.wkr.store_appointment.pojo.vo.OrderVO;

public interface OrderService {

    PageResult<OrderVO> page(OrderPageQueryDTO orderPageQueryDTO);

    void save(OrderDTO orderDTO);

    OrderVO getById(Long id);

    void pay(Long id);

    void updatePayment(Long id, OrderPaymentDTO orderPaymentDTO);

    void complete(Long id);

    void cancel(Long id);
}
