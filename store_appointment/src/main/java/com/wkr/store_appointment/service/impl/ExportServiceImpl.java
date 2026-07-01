package com.wkr.store_appointment.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.wkr.store_appointment.enums.DebtStatusEnum;
import com.wkr.store_appointment.enums.GenderEnum;
import com.wkr.store_appointment.enums.OrderStatusEnum;
import com.wkr.store_appointment.enums.PayStatusEnum;
import com.wkr.store_appointment.mapper.CustomerMapper;
import com.wkr.store_appointment.mapper.OrderMapper;
import com.wkr.store_appointment.pojo.DTO.CustomerPageQueryDTO;
import com.wkr.store_appointment.pojo.DTO.OrderPageQueryDTO;
import com.wkr.store_appointment.pojo.entity.Customer;
import com.wkr.store_appointment.pojo.vo.CustomerVO;
import com.wkr.store_appointment.pojo.vo.OrderVO;
import com.wkr.store_appointment.pojo.vo.export.CustomerExportVO;
import com.wkr.store_appointment.pojo.vo.export.OrderExportVO;
import com.wkr.store_appointment.service.ExportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExportServiceImpl implements ExportService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public void exportCustomers(CustomerPageQueryDTO queryDTO, HttpServletResponse response) {

        List<CustomerExportVO> exportList = customerMapper.selectList(customerQuery(queryDTO))
                .stream()
                .map(this::toCustomerVO)
                .map(this::toCustomerExportVO)
                .collect(Collectors.toList());

        writeExcel(response, "客户列表.xlsx", "客户列表", CustomerExportVO.class, exportList);
    }

    @Override
    public void exportOrders(OrderPageQueryDTO queryDTO, HttpServletResponse response) {

        List<OrderExportVO> exportList = orderMapper.listForExport(queryDTO)
                .stream()
                .map(this::toOrderExportVO)
                .collect(Collectors.toList());

        writeExcel(response, "订单列表.xlsx", "订单列表", OrderExportVO.class, exportList);
    }

    private LambdaQueryWrapper<Customer> customerQuery(CustomerPageQueryDTO query) {

        return Wrappers.lambdaQuery(Customer.class)
                .like(StringUtils.hasText(query.getName()), Customer::getName, query.getName())
                .like(StringUtils.hasText(query.getPhone()), Customer::getPhone, query.getPhone())
                .eq(StringUtils.hasText(query.getLevel()), Customer::getLevel, query.getLevel())
                .orderByDesc(Customer::getCreateTime);
    }

    private CustomerVO toCustomerVO(Customer customer) {

        CustomerVO customerVO = new CustomerVO();
        BeanUtils.copyProperties(customer, customerVO);
        return customerVO;
    }

    private CustomerExportVO toCustomerExportVO(CustomerVO customerVO) {

        CustomerExportVO exportVO = new CustomerExportVO();
        BeanUtils.copyProperties(customerVO, exportVO);
        exportVO.setGender(genderName(customerVO.getGender()));
        exportVO.setBirthday(customerVO.getBirthday() == null ? "" : customerVO.getBirthday().toString());
        exportVO.setCreateTime(customerVO.getCreateTime() == null ? "" : customerVO.getCreateTime().format(DATE_TIME_FORMATTER));
        return exportVO;
    }

    private OrderExportVO toOrderExportVO(OrderVO orderVO) {

        OrderExportVO exportVO = new OrderExportVO();
        BeanUtils.copyProperties(orderVO, exportVO);
        exportVO.setPayStatusDesc(statusName(orderVO.getPayStatus(), PayStatusEnum::labelOf));
        exportVO.setDebtStatusDesc(statusName(orderVO.getDebtStatus(), DebtStatusEnum::labelOf));
        exportVO.setOrderStatusDesc(statusName(orderVO.getOrderStatus(), OrderStatusEnum::labelOf));
        exportVO.setCreateTime(orderVO.getCreateTime() == null ? "" : orderVO.getCreateTime().format(DATE_TIME_FORMATTER));
        return exportVO;
    }

    private String genderName(Integer gender) {

        return gender == null ? "" : GenderEnum.labelOf(gender);
    }

    private String statusName(Integer status, java.util.function.Function<Integer, String> resolver) {

        return status == null ? "" : resolver.apply(status);
    }

    private <T> void writeExcel(HttpServletResponse response, String fileName, String sheetName, Class<T> head, List<T> data) {

        try {
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFileName);
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");

            EasyExcel.write(response.getOutputStream(), head)
                    .autoCloseStream(false)
                    .sheet(sheetName)
                    .doWrite(data);
        } catch (IOException ex) {
            throw new RuntimeException("导出文件失败", ex);
        }
    }
}
