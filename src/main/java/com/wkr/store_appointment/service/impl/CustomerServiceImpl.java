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
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerMapper customerMapper;

    @Override
    public PageResult<CustomerVO> page(CustomerPageQueryDTO customerPageQueryDTO) {

        PageHelper.startPage(customerPageQueryDTO.getPage(), customerPageQueryDTO.getPageSize());
        Page<CustomerVO> page = customerMapper.page(customerPageQueryDTO);
        return new PageResult<>(page.getTotal(), page.getResult());
    }

    @Override
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

    @Override
    public CustomerVO getById(Long id) {

        return customerMapper.getById(id);
    }

    @Override
    public void update(CustomerDTO customerDTO) {

        checkPhoneUnique(customerDTO.getPhone(), customerDTO.getId());

        Customer customer = new Customer();
        BeanUtils.copyProperties(customerDTO, customer);
        customer.setUpdateTime(LocalDateTime.now());

        customerMapper.update(customer);
    }

    @Override
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
