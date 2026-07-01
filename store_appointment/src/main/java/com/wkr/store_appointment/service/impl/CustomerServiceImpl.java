package com.wkr.store_appointment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wkr.store_appointment.common.PageResult;
import com.wkr.store_appointment.enums.CustomerLevelEnum;
import com.wkr.store_appointment.exception.BaseException;
import com.wkr.store_appointment.mapper.AppointmentMapper;
import com.wkr.store_appointment.mapper.CustomerMapper;
import com.wkr.store_appointment.mapper.OrderMapper;
import com.wkr.store_appointment.pojo.DTO.CustomerDTO;
import com.wkr.store_appointment.pojo.DTO.CustomerPageQueryDTO;
import com.wkr.store_appointment.pojo.entity.Appointment;
import com.wkr.store_appointment.pojo.entity.Customer;
import com.wkr.store_appointment.pojo.entity.OrderInfo;
import com.wkr.store_appointment.pojo.vo.CustomerVO;
import com.wkr.store_appointment.service.CustomerService;
import com.wkr.store_appointment.utils.PageQueryUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements CustomerService {

    @Autowired
    private AppointmentMapper appointmentMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Override
    @Cacheable(cacheNames = "customer:page", key = "#p0", sync = true)
    public PageResult<CustomerVO> page(CustomerPageQueryDTO customerPageQueryDTO) {

        Page<Customer> page = new Page<>(
                PageQueryUtils.page(customerPageQueryDTO.getPage()),
                PageQueryUtils.pageSize(customerPageQueryDTO.getPageSize()));
        IPage<Customer> result = baseMapper.selectPage(page, customerQuery(customerPageQueryDTO));
        List<CustomerVO> records = result.getRecords().stream().map(this::toVO).toList();
        return new PageResult<>(result.getTotal(), records);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "customer:page", allEntries = true),
            @CacheEvict(cacheNames = "appointment:page", allEntries = true),
            @CacheEvict(cacheNames = "order:page", allEntries = true),
            @CacheEvict(cacheNames = "statistics:overview", allEntries = true)
    })
    public void save(CustomerDTO customerDTO) {

        checkPhoneUnique(customerDTO.getPhone(), null);

        Customer customer = new Customer();
        BeanUtils.copyProperties(customerDTO, customer);
        if (!StringUtils.hasText(customer.getLevel())) {
            customer.setLevel(CustomerLevelEnum.NORMAL.getLabel());
        }
        customer.setCreateTime(LocalDateTime.now());
        customer.setUpdateTime(LocalDateTime.now());

        baseMapper.insert(customer);
    }

    @Override
    @Cacheable(cacheNames = "customer:detail", key = "#p0", sync = true)
    public CustomerVO getById(Long id) {

        Customer customer = baseMapper.selectById(id);
        return customer == null ? null : toVO(customer);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "customer:page", allEntries = true),
            @CacheEvict(cacheNames = "customer:detail", key = "#p0.id"),
            @CacheEvict(cacheNames = "appointment:page", allEntries = true),
            @CacheEvict(cacheNames = "appointment:getById", allEntries = true),
            @CacheEvict(cacheNames = "order:page", allEntries = true),
            @CacheEvict(cacheNames = "order:getById", allEntries = true)
    })
    public void update(CustomerDTO customerDTO) {

        checkPhoneUnique(customerDTO.getPhone(), customerDTO.getId());

        Customer customer = new Customer();
        BeanUtils.copyProperties(customerDTO, customer);
        customer.setUpdateTime(LocalDateTime.now());

        baseMapper.updateById(customer);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "customer:page", allEntries = true),
            @CacheEvict(cacheNames = "customer:detail", key = "#p0"),
            @CacheEvict(cacheNames = "appointment:page", allEntries = true),
            @CacheEvict(cacheNames = "appointment:getById", allEntries = true),
            @CacheEvict(cacheNames = "order:page", allEntries = true),
            @CacheEvict(cacheNames = "order:getById", allEntries = true),
            @CacheEvict(cacheNames = "statistics:overview", allEntries = true)
    })
    public void delete(Long id) {

        Long appointmentCount = appointmentMapper.selectCount(Wrappers.lambdaQuery(Appointment.class)
                .eq(Appointment::getCustomerId, id));
        Long orderCount = orderMapper.selectCount(Wrappers.lambdaQuery(OrderInfo.class)
                .eq(OrderInfo::getCustomerId, id));
        if ((appointmentCount != null && appointmentCount > 0) || (orderCount != null && orderCount > 0)) {
            throw new BaseException("客户存在关联预约或订单，不能删除");
        }

        baseMapper.deleteById(id);
    }

    private LambdaQueryWrapper<Customer> customerQuery(CustomerPageQueryDTO query) {

        return Wrappers.lambdaQuery(Customer.class)
                .like(StringUtils.hasText(query.getName()), Customer::getName, query.getName())
                .like(StringUtils.hasText(query.getPhone()), Customer::getPhone, query.getPhone())
                .eq(StringUtils.hasText(query.getLevel()), Customer::getLevel, query.getLevel())
                .orderByDesc(Customer::getCreateTime);
    }

    private void checkPhoneUnique(String phone, Long id) {

        if (!StringUtils.hasText(phone)) {
            return;
        }
        Long count = baseMapper.selectCount(Wrappers.lambdaQuery(Customer.class)
                .eq(Customer::getPhone, phone)
                .ne(id != null, Customer::getId, id));
        if (count != null && count > 0) {
            throw new BaseException("客户手机号已存在");
        }
    }

    private CustomerVO toVO(Customer customer) {

        CustomerVO customerVO = new CustomerVO();
        BeanUtils.copyProperties(customer, customerVO);
        return customerVO;
    }
}
