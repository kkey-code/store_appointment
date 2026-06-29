package com.wkr.store_appointment.service.impl;

import com.alibaba.excel.EasyExcel;
import com.wkr.store_appointment.mapper.CustomerMapper;
import com.wkr.store_appointment.mapper.OrderMapper;
import com.wkr.store_appointment.pojo.DTO.CustomerPageQueryDTO;
import com.wkr.store_appointment.pojo.DTO.OrderPageQueryDTO;
import com.wkr.store_appointment.pojo.vo.CustomerVO;
import com.wkr.store_appointment.pojo.vo.OrderVO;
import com.wkr.store_appointment.pojo.vo.export.CustomerExportVO;
import com.wkr.store_appointment.pojo.vo.export.OrderExportVO;
import com.wkr.store_appointment.service.ExportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    /**
     * 导出客户
     */
    @Override
    public void exportCustomers(CustomerPageQueryDTO queryDTO, HttpServletResponse response) {

        List<CustomerExportVO> exportList = customerMapper.listForExport(queryDTO)
                .stream()
                .map(this::toCustomerExportVO)
                .collect(Collectors.toList());

        writeExcel(response, "客户列表.xlsx", "客户列表", CustomerExportVO.class, exportList);
    }

    /**
     * 导出订单
     */
    @Override
    public void exportOrders(OrderPageQueryDTO queryDTO, HttpServletResponse response) {

        List<OrderExportVO> exportList = orderMapper.listForExport(queryDTO)
                .stream()
                .map(this::toOrderExportVO)
                .collect(Collectors.toList());

        writeExcel(response, "订单列表.xlsx", "订单列表", OrderExportVO.class, exportList);
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
        exportVO.setPayStatusDesc(payStatusName(orderVO.getPayStatus()));
        exportVO.setDebtStatusDesc(debtStatusName(orderVO.getDebtStatus()));
        exportVO.setOrderStatusDesc(orderStatusName(orderVO.getOrderStatus()));
        exportVO.setCreateTime(orderVO.getCreateTime() == null ? "" : orderVO.getCreateTime().format(DATE_TIME_FORMATTER));
        return exportVO;
    }

    /**
     * 性别转换
     */
    private String genderName(Integer gender) {

        if (gender == null) {
            return "";
        }
        if (gender == 1) {
            return "男";
        }
        if (gender == 2) {
            return "女";
        }
        return "未知";
    }

    /**
     * 支付状态转换
     */
    private String payStatusName(Integer payStatus) {

        if (payStatus == null) {
            return "";
        }
        if (payStatus == 0) {
            return "未支付";
        }
        if (payStatus == 1) {
            return "已支付";
        }
        if (payStatus == 2) {
            return "已退款";
        }
        return "未知";
    }

    /**
     * 欠款状态转换
     */
    private String debtStatusName(Integer debtStatus) {

        if (debtStatus == null) {
            return "";
        }
        if (debtStatus == 0) {
            return "无欠款";
        }
        if (debtStatus == 1) {
            return "分期中";
        }
        if (debtStatus == 2) {
            return "已结清";
        }
        return "未知";
    }

    /**
     * 订单状态转换
     */
    private String orderStatusName(Integer orderStatus) {

        if (orderStatus == null) {
            return "";
        }
        if (orderStatus == 0) {
            return "待服务";
        }
        if (orderStatus == 1) {
            return "已完成";
        }
        if (orderStatus == 2) {
            return "已取消";
        }
        return "未知";
    }

    /**
     * 写入 Excel 文件流
     */
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
