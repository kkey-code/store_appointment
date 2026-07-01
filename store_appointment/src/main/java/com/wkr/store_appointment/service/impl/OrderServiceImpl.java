package com.wkr.store_appointment.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wkr.store_appointment.common.PageResult;
import com.wkr.store_appointment.enums.CardTypeEnum;
import com.wkr.store_appointment.enums.DebtStatusEnum;
import com.wkr.store_appointment.enums.OrderStatusEnum;
import com.wkr.store_appointment.enums.PayStatusEnum;
import com.wkr.store_appointment.enums.PaymentMethodEnum;
import com.wkr.store_appointment.exception.OrderBusinessException;
import com.wkr.store_appointment.mapper.OrderMapper;
import com.wkr.store_appointment.pojo.DTO.OrderDTO;
import com.wkr.store_appointment.pojo.DTO.OrderPageQueryDTO;
import com.wkr.store_appointment.pojo.DTO.OrderPaymentDTO;
import com.wkr.store_appointment.pojo.entity.OrderInfo;
import com.wkr.store_appointment.pojo.vo.OrderVO;
import com.wkr.store_appointment.service.OrderService;
import com.wkr.store_appointment.utils.PageQueryUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, OrderInfo> implements OrderService {

    @Override
    @Cacheable(cacheNames = "order:page", key = "#p0", sync = true)
    public PageResult<OrderVO> page(OrderPageQueryDTO orderPageQueryDTO) {

        Page<OrderVO> page = new Page<>(
                PageQueryUtils.page(orderPageQueryDTO.getPage()),
                PageQueryUtils.pageSize(orderPageQueryDTO.getPageSize()));
        IPage<OrderVO> result = baseMapper.page(page, orderPageQueryDTO);
        return new PageResult<>(result.getTotal(), result.getRecords());
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "order:page", allEntries = true),
            @CacheEvict(cacheNames = "statistics:overview", allEntries = true),
            @CacheEvict(cacheNames = "statistics:orderAmount", allEntries = true)
    })
    public void save(OrderDTO orderDTO) {

        validateOrder(orderDTO);

        OrderInfo orderInfo = new OrderInfo();
        BeanUtils.copyProperties(orderDTO, orderInfo);
        orderInfo.setOrderNo(generateOrderNo());
        normalizeAmount(orderInfo);
        orderInfo.setPayStatus(isZero(orderInfo.getDebtAmount()) ? PayStatusEnum.PAID.getCode() : PayStatusEnum.UNPAID.getCode());
        orderInfo.setDebtStatus(isZero(orderInfo.getDebtAmount()) ? DebtStatusEnum.NONE.getCode() : DebtStatusEnum.INSTALLMENT.getCode());
        orderInfo.setOrderStatus(OrderStatusEnum.PENDING.getCode());
        if (!StringUtils.hasText(orderInfo.getPaymentMethod())) {
            orderInfo.setPaymentMethod(PaymentMethodEnum.CASH.getLabel());
        }
        if (!StringUtils.hasText(orderInfo.getCardType())) {
            orderInfo.setCardType(CardTypeEnum.TIME_CARD.getLabel());
        }
        if (PayStatusEnum.PAID.matches(orderInfo.getPayStatus())) {
            orderInfo.setPayTime(LocalDateTime.now());
        }
        orderInfo.setCreateTime(LocalDateTime.now());
        orderInfo.setUpdateTime(LocalDateTime.now());

        baseMapper.insert(orderInfo);
    }

    @Override
    @Cacheable(cacheNames = "order:getById", key = "#p0", sync = true)
    public OrderVO getById(Long id) {

        OrderVO orderVO = baseMapper.getById(id);
        if (orderVO == null) {
            throw new OrderBusinessException("订单不存在");
        }
        return orderVO;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "order:page", allEntries = true),
            @CacheEvict(cacheNames = "order:getById", key = "#p0"),
            @CacheEvict(cacheNames = "statistics:overview", allEntries = true),
            @CacheEvict(cacheNames = "statistics:orderAmount", allEntries = true)
    })
    public void pay(Long id) {

        OrderInfo orderInfo = getOrderInfo(id);
        if (!PayStatusEnum.UNPAID.matches(orderInfo.getPayStatus())) {
            throw new OrderBusinessException("只有未支付订单可以支付");
        }

        LocalDateTime now = LocalDateTime.now();
        OrderInfo update = new OrderInfo();
        update.setId(id);
        update.setPayStatus(PayStatusEnum.PAID.getCode());
        update.setPaidAmount(valueOrZero(orderInfo.getAmount()));
        update.setDebtAmount(BigDecimal.ZERO);
        update.setDebtStatus(isPositive(orderInfo.getDebtAmount()) ? DebtStatusEnum.SETTLED.getCode() : DebtStatusEnum.NONE.getCode());
        update.setPayTime(now);
        update.setUpdateTime(now);
        baseMapper.updateById(update);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "order:page", allEntries = true),
            @CacheEvict(cacheNames = "order:getById", key = "#p0"),
            @CacheEvict(cacheNames = "statistics:overview", allEntries = true),
            @CacheEvict(cacheNames = "statistics:orderAmount", allEntries = true)
    })
    public void updatePayment(Long id, OrderPaymentDTO orderPaymentDTO) {

        OrderInfo orderInfo = getOrderInfo(id);
        if (OrderStatusEnum.CANCELED.matches(orderInfo.getOrderStatus())) {
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
        Integer payStatus = isZero(debtAmount) ? PayStatusEnum.PAID.getCode() : PayStatusEnum.UNPAID.getCode();
        Integer debtStatus = resolveDebtStatus(debtAmount, isPositive(orderInfo.getDebtAmount()));
        LocalDateTime payTime = isZero(debtAmount) && isPositive(paidAmount) ? LocalDateTime.now() : orderInfo.getPayTime();
        String paymentMethod = !StringUtils.hasText(orderPaymentDTO.getPaymentMethod())
                ? orderInfo.getPaymentMethod()
                : orderPaymentDTO.getPaymentMethod();
        BigDecimal monthlyPayment = orderPaymentDTO.getMonthlyPayment() == null
                ? orderInfo.getMonthlyPayment()
                : valueOrZero(orderPaymentDTO.getMonthlyPayment());

        OrderInfo update = new OrderInfo();
        update.setId(id);
        update.setPaidAmount(paidAmount);
        update.setDebtAmount(debtAmount);
        update.setMonthlyPayment(monthlyPayment);
        update.setPaymentMethod(paymentMethod);
        update.setDebtStatus(debtStatus);
        update.setPayStatus(payStatus);
        update.setPayTime(payTime);
        update.setUpdateTime(LocalDateTime.now());
        baseMapper.updateById(update);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "order:page", allEntries = true),
            @CacheEvict(cacheNames = "order:getById", key = "#p0"),
            @CacheEvict(cacheNames = "statistics:overview", allEntries = true),
            @CacheEvict(cacheNames = "statistics:orderAmount", allEntries = true)
    })
    public void complete(Long id) {

        OrderInfo orderInfo = getOrderInfo(id);
        if (!PayStatusEnum.PAID.matches(orderInfo.getPayStatus())) {
            throw new OrderBusinessException("只有已支付订单可以完成");
        }
        if (OrderStatusEnum.CANCELED.matches(orderInfo.getOrderStatus())) {
            throw new OrderBusinessException("已取消订单不能完成");
        }

        updateOrderStatus(id, OrderStatusEnum.COMPLETED);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "order:page", allEntries = true),
            @CacheEvict(cacheNames = "order:getById", key = "#p0"),
            @CacheEvict(cacheNames = "statistics:overview", allEntries = true),
            @CacheEvict(cacheNames = "statistics:orderAmount", allEntries = true)
    })
    public void cancel(Long id) {

        OrderInfo orderInfo = getOrderInfo(id);
        if (OrderStatusEnum.COMPLETED.matches(orderInfo.getOrderStatus())) {
            throw new OrderBusinessException("已完成订单不能取消");
        }

        updateOrderStatus(id, OrderStatusEnum.CANCELED);
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

        OrderInfo orderInfo = baseMapper.selectById(id);
        if (orderInfo == null) {
            throw new OrderBusinessException("订单不存在");
        }
        return orderInfo;
    }

    private void updateOrderStatus(Long id, OrderStatusEnum orderStatus) {

        OrderInfo update = new OrderInfo();
        update.setId(id);
        update.setOrderStatus(orderStatus.getCode());
        update.setUpdateTime(LocalDateTime.now());
        baseMapper.updateById(update);
    }

    private String generateOrderNo() {

        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        int random = ThreadLocalRandom.current().nextInt(100, 1000);
        return "ORD" + time + random;
    }

    private Integer resolveDebtStatus(BigDecimal debtAmount, boolean hadDebtBefore) {

        if (isPositive(debtAmount)) {
            return DebtStatusEnum.INSTALLMENT.getCode();
        }
        return hadDebtBefore ? DebtStatusEnum.SETTLED.getCode() : DebtStatusEnum.NONE.getCode();
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
