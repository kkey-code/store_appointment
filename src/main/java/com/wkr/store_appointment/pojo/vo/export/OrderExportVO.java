package com.wkr.store_appointment.pojo.vo.export;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.NumberFormat;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单导出 VO，Excel 专用
 */
@Data
public class OrderExportVO {

    @ExcelProperty(value = "订单编号", index = 0)
    private String orderNo;

    @ExcelProperty(value = "客户ID", index = 1)
    private Long customerId;

    @ExcelProperty(value = "客户姓名", index = 2)
    private String customerName;

    @ExcelProperty(value = "服务项目", index = 3)
    private String serviceItemName;

    @ExcelProperty(value = "卡类型", index = 4)
    private String cardType;

    @ExcelProperty(value = "原价", index = 5)
    @NumberFormat("0.00")
    private BigDecimal originalAmount;

    @ExcelProperty(value = "优惠", index = 6)
    @NumberFormat("0.00")
    private BigDecimal discountAmount;

    @ExcelProperty(value = "应收金额", index = 7)
    @NumberFormat("0.00")
    private BigDecimal amount;

    @ExcelProperty(value = "已付金额", index = 8)
    @NumberFormat("0.00")
    private BigDecimal paidAmount;

    @ExcelProperty(value = "欠款金额", index = 9)
    @NumberFormat("0.00")
    private BigDecimal debtAmount;

    @ExcelProperty(value = "月付计划", index = 10)
    @NumberFormat("0.00")
    private BigDecimal monthlyPayment;

    @ExcelProperty(value = "支付方式", index = 11)
    private String paymentMethod;

    @ExcelProperty(value = "支付状态", index = 12)
    private String payStatusDesc;

    @ExcelProperty(value = "欠款状态", index = 13)
    private String debtStatusDesc;

    @ExcelProperty(value = "订单状态", index = 14)
    private String orderStatusDesc;

    @ExcelProperty(value = "创建时间", index = 15)
    private String createTime;
}
