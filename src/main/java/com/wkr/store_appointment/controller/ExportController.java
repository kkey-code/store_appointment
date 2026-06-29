package com.wkr.store_appointment.controller;

import com.wkr.store_appointment.pojo.DTO.CustomerPageQueryDTO;
import com.wkr.store_appointment.pojo.DTO.OrderPageQueryDTO;
import com.wkr.store_appointment.service.ExportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/export")
@Api(tags = "导出")
public class ExportController {

    @Autowired
    private ExportService exportService;

    /**
     * 导出客户
     */
    @GetMapping("/customers")
    @ApiOperation("导出客户")
    public void exportCustomers(CustomerPageQueryDTO queryDTO, HttpServletResponse response) {
        exportService.exportCustomers(queryDTO, response);
    }

    /**
     * 导出订单
     */
    @GetMapping("/orders")
    @ApiOperation("导出订单")
    public void exportOrders(OrderPageQueryDTO queryDTO, HttpServletResponse response) {
        exportService.exportOrders(queryDTO, response);
    }
}
