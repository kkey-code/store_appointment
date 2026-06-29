package com.wkr.store_appointment.service;

import com.wkr.store_appointment.pojo.DTO.CustomerPageQueryDTO;
import com.wkr.store_appointment.pojo.DTO.OrderPageQueryDTO;
import jakarta.servlet.http.HttpServletResponse;

public interface ExportService {

    void exportCustomers(CustomerPageQueryDTO queryDTO, HttpServletResponse response);

    void exportOrders(OrderPageQueryDTO queryDTO, HttpServletResponse response);
}
