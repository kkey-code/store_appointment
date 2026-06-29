package com.wkr.store_appointment.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wkr.store_appointment.common.PageResult;
import com.wkr.store_appointment.exception.BaseException;
import com.wkr.store_appointment.mapper.CustomerMapper;
import com.wkr.store_appointment.pojo.DTO.CustomerDTO;
import com.wkr.store_appointment.pojo.DTO.CustomerPageQueryDTO;
import com.wkr.store_appointment.pojo.entity.Customer;
import com.wkr.store_appointment.pojo.vo.CustomerVO;
import com.wkr.store_appointment.service.CustomerService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerMapper customerMapper;

    /**
     * 分页查询
     */
    @Override
    @Cacheable(cacheNames = "customer:page", key = "#p0", sync = true)
    public PageResult<CustomerVO> page(CustomerPageQueryDTO customerPageQueryDTO) {

        PageHelper.startPage(customerPageQueryDTO.getPage(), customerPageQueryDTO.getPageSize());
        Page<CustomerVO> page = customerMapper.page(customerPageQueryDTO);
        return new PageResult<>(page.getTotal(), new java.util.ArrayList<>(page.getResult()));
    }

    /**
     * 新增
     */
    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "customer:page", allEntries = true),
            @CacheEvict(cacheNames = "statistics:overview", allEntries = true)
    })
    public void save(CustomerDTO customerDTO) {

        checkPhoneUnique(customerDTO.getPhone(), null);

        Customer customer = new Customer();
        BeanUtils.copyProperties(customerDTO, customer);
        if (customer.getLevel() == null || customer.getLevel().isEmpty()) {
            customer.setLevel("普通");
        }
        customer.setCreateTime(LocalDateTime.now());
        customer.setUpdateTime(LocalDateTime.now());

        customerMapper.save(customer);
    }

    /**
     * 根据id查询
     */
    @Override
    @Cacheable(cacheNames = "customer:detail", key = "#p0", sync = true)
    public CustomerVO getById(Long id) {

        return customerMapper.getById(id);
    }

    /**
     * 修改
     */
    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "customer:page", allEntries = true),
            @CacheEvict(cacheNames = "customer:detail", key = "#p0.id")
    })
    public void update(CustomerDTO customerDTO) {

        checkPhoneUnique(customerDTO.getPhone(), customerDTO.getId());

        Customer customer = new Customer();
        BeanUtils.copyProperties(customerDTO, customer);
        customer.setUpdateTime(LocalDateTime.now());

        customerMapper.update(customer);
    }

    /**
     * 删除
     */
    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "customer:page", allEntries = true),
            @CacheEvict(cacheNames = "customer:detail", key = "#p0"),
            @CacheEvict(cacheNames = "statistics:overview", allEntries = true)
    })
    public void delete(Long id) {

        Integer appointmentCount = customerMapper.countAppointmentsByCustomerId(id);
        Integer orderCount = customerMapper.countOrdersByCustomerId(id);
        if ((appointmentCount != null && appointmentCount > 0) || (orderCount != null && orderCount > 0)) {
            throw new BaseException("客户存在关联预约或订单，不能删除");
        }

        customerMapper.delete(id);
    }

    private void checkPhoneUnique(String phone, Long id) {

        if (phone == null || phone.isEmpty()) {
            return;
        }
        Integer count = customerMapper.countByPhone(phone, id);
        if (count != null && count > 0) {
            throw new BaseException("客户手机号已存在");
        }
    }
}
