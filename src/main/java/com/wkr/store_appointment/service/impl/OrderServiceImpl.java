package com.wkr.store_appointment.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wkr.store_appointment.common.PageResult;
import com.wkr.store_appointment.exception.OrderBusinessException;
import com.wkr.store_appointment.mapper.OrderMapper;
import com.wkr.store_appointment.pojo.DTO.OrderDTO;
import com.wkr.store_appointment.pojo.DTO.OrderPageQueryDTO;
import com.wkr.store_appointment.pojo.DTO.OrderPaymentDTO;
import com.wkr.store_appointment.pojo.entity.OrderInfo;
import com.wkr.store_appointment.pojo.vo.OrderVO;
import com.wkr.store_appointment.service.OrderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class OrderServiceImpl implements OrderService {

    private static final int PAY_STATUS_UNPAID = 0;
    private static final int PAY_STATUS_PAID = 1;
    private static final int DEBT_STATUS_NONE = 0;
    private static final int DEBT_STATUS_INSTALLMENT = 1;
    private static final int DEBT_STATUS_SETTLED = 2;
    private static final int ORDER_STATUS_PENDING = 0;
    private static final int ORDER_STATUS_COMPLETED = 1;
    private static final int ORDER_STATUS_CANCELED = 2;

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public PageResult<OrderVO> page(OrderPageQueryDTO orderPageQueryDTO) {

        PageHelper.startPage(orderPageQueryDTO.getPage(), orderPageQueryDTO.getPageSize());
        Page<OrderVO> page = orderMapper.page(orderPageQueryDTO);
        return new PageResult<>(page.getTotal(), page.getResult());
    }

    @Override
    public void save(OrderDTO orderDTO) {

        validateOrder(orderDTO);

        OrderInfo orderInfo = new OrderInfo();
        BeanUtils.copyProperties(orderDTO, orderInfo);
        orderInfo.setOrderNo(generateOrderNo());
        normalizeAmount(orderInfo);
        orderInfo.setPayStatus(isZero(orderInfo.getDebtAmount()) ? PAY_STATUS_PAID : PAY_STATUS_UNPAID);
        orderInfo.setDebtStatus(isZero(orderInfo.getDebtAmount()) ? DEBT_STATUS_NONE : DEBT_STATUS_INSTALLMENT);
        orderInfo.setOrderStatus(ORDER_STATUS_PENDING);
        if (orderInfo.getPaymentMethod() == null || orderInfo.getPaymentMethod().isEmpty()) {
            orderInfo.setPaymentMethod("现金");
        }
        if (orderInfo.getCardType() == null || orderInfo.getCardType().isEmpty()) {
            orderInfo.setCardType("次卡");
        }
        if (statusEquals(orderInfo.getPayStatus(), PAY_STATUS_PAID)) {
            orderInfo.setPayTime(LocalDateTime.now());
        }
        orderInfo.setCreateTime(LocalDateTime.now());
        orderInfo.setUpdateTime(LocalDateTime.now());

        orderMapper.insert(orderInfo);
    }

    @Override
    public OrderVO getById(Long id) {

        OrderVO orderVO = orderMapper.getById(id);
        if (orderVO == null) {
            throw new OrderBusinessException("订单不存在");
        }
        return orderVO;
    }

    @Override
    public void pay(Long id) {

        OrderInfo orderInfo = getOrderInfo(id);
        if (!statusEquals(orderInfo.getPayStatus(), PAY_STATUS_UNPAID)) {
            throw new OrderBusinessException("只有未支付订单可以支付");
        }

        LocalDateTime now = LocalDateTime.now();
        Integer debtStatus = isPositive(orderInfo.getDebtAmount()) ? DEBT_STATUS_SETTLED : DEBT_STATUS_NONE;
        orderMapper.updatePayStatus(id, PAY_STATUS_PAID, debtStatus, now, now);
    }

    @Override
    public void updatePayment(Long id, OrderPaymentDTO orderPaymentDTO) {

        OrderInfo orderInfo = getOrderInfo(id);
        if (statusEquals(orderInfo.getOrderStatus(), ORDER_STATUS_CANCELED)) {
            throw new OrderBusinessException("已取消订单不能收款");
        }

        BigDecimal amount = valueOrZero(orderInfo.getAmount());
        BigDecimal paidAmount = orderPaymentDTO.getPaidAmount() == null
                ? valueOrZero(orderInfo.getPaidAmount())
                : valueOrZero(orderPaymentDTO.getPaidAmount());
        if (paidAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new OrderBusinessException("已收金额不能小于0");
        }
        if (paidAmount.compareTo(amount) > 0) {
            paidAmount = amount;
        }

        BigDecimal debtAmount = amount.subtract(paidAmount);
        Integer payStatus = isZero(debtAmount) ? PAY_STATUS_PAID : PAY_STATUS_UNPAID;
        Integer debtStatus = resolveDebtStatus(debtAmount, isPositive(orderInfo.getDebtAmount()));
        LocalDateTime payTime = isZero(debtAmount) && isPositive(paidAmount) ? LocalDateTime.now() : orderInfo.getPayTime();
        String paymentMethod = orderPaymentDTO.getPaymentMethod() == null || orderPaymentDTO.getPaymentMethod().isEmpty()
                ? orderInfo.getPaymentMethod()
                : orderPaymentDTO.getPaymentMethod();
        BigDecimal monthlyPayment = orderPaymentDTO.getMonthlyPayment() == null
                ? orderInfo.getMonthlyPayment()
                : valueOrZero(orderPaymentDTO.getMonthlyPayment());

        orderMapper.updatePayment(id, paidAmount, debtAmount, monthlyPayment, paymentMethod, debtStatus, payStatus, payTime, LocalDateTime.now());
    }

    @Override
    public void complete(Long id) {

        OrderInfo orderInfo = getOrderInfo(id);
        if (!statusEquals(orderInfo.getPayStatus(), PAY_STATUS_PAID)) {
            throw new OrderBusinessException("只有已支付订单可以完成");
        }
        if (statusEquals(orderInfo.getOrderStatus(), ORDER_STATUS_CANCELED)) {
            throw new OrderBusinessException("已取消订单不能完成");
        }

        orderMapper.updateOrderStatus(id, ORDER_STATUS_COMPLETED, LocalDateTime.now());
    }

    @Override
    public void cancel(Long id) {

        OrderInfo orderInfo = getOrderInfo(id);
        if (statusEquals(orderInfo.getOrderStatus(), ORDER_STATUS_COMPLETED)) {
            throw new OrderBusinessException("已完成订单不能取消");
        }

        orderMapper.updateOrderStatus(id, ORDER_STATUS_CANCELED, LocalDateTime.now());
    }

    private void validateOrder(OrderDTO orderDTO) {

        if (orderDTO.getCustomerId() == null) {
            throw new OrderBusinessException("客户ID不能为空");
        }
        if (orderDTO.getServiceItemId() == null) {
            throw new OrderBusinessException("服务项目ID不能为空");
        }
        if (orderDTO.getOriginalAmount() != null && orderDTO.getOriginalAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new OrderBusinessException("原价不能小于0");
        }
        if (orderDTO.getDiscountAmount() != null && orderDTO.getDiscountAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new OrderBusinessException("优惠金额不能小于0");
        }
        if (orderDTO.getAmount() != null && orderDTO.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new OrderBusinessException("应收金额不能小于0");
        }
        if (orderDTO.getPaidAmount() != null && orderDTO.getPaidAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new OrderBusinessException("已收金额不能小于0");
        }
        if (orderDTO.getMonthlyPayment() != null && orderDTO.getMonthlyPayment().compareTo(BigDecimal.ZERO) < 0) {
            throw new OrderBusinessException("每月还款金额不能小于0");
        }
    }

    private void normalizeAmount(OrderInfo orderInfo) {

        BigDecimal originalAmount = orderInfo.getOriginalAmount() == null ? valueOrZero(orderInfo.getAmount()) : valueOrZero(orderInfo.getOriginalAmount());
        BigDecimal discountAmount = valueOrZero(orderInfo.getDiscountAmount());
        BigDecimal amount = orderInfo.getAmount() == null ? originalAmount.subtract(discountAmount) : valueOrZero(orderInfo.getAmount());
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            amount = BigDecimal.ZERO;
        }
        BigDecimal paidAmount = valueOrZero(orderInfo.getPaidAmount());
        if (paidAmount.compareTo(amount) > 0) {
            paidAmount = amount;
        }

        orderInfo.setOriginalAmount(originalAmount);
        orderInfo.setDiscountAmount(discountAmount);
        orderInfo.setAmount(amount);
        orderInfo.setPaidAmount(paidAmount);
        orderInfo.setDebtAmount(amount.subtract(paidAmount));
        orderInfo.setMonthlyPayment(valueOrZero(orderInfo.getMonthlyPayment()));
    }

    private OrderInfo getOrderInfo(Long id) {

        OrderInfo orderInfo = orderMapper.getEntityById(id);
        if (orderInfo == null) {
            throw new OrderBusinessException("订单不存在");
        }
        return orderInfo;
    }

    private String generateOrderNo() {

        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        int random = ThreadLocalRandom.current().nextInt(100, 1000);
        return "ORD" + time + random;
    }

    private boolean statusEquals(Integer status, int expected) {

        return status != null && status == expected;
    }

    private Integer resolveDebtStatus(BigDecimal debtAmount, boolean hadDebtBefore) {

        if (isPositive(debtAmount)) {
            return DEBT_STATUS_INSTALLMENT;
        }
        return hadDebtBefore ? DEBT_STATUS_SETTLED : DEBT_STATUS_NONE;
    }

    private BigDecimal valueOrZero(BigDecimal value) {

        return value == null ? BigDecimal.ZERO : value;
    }

    private boolean isZero(BigDecimal value) {

        return valueOrZero(value).compareTo(BigDecimal.ZERO) == 0;
    }

    private boolean isPositive(BigDecimal value) {

        return valueOrZero(value).compareTo(BigDecimal.ZERO) > 0;
    }
}
