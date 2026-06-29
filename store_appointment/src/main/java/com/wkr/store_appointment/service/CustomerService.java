package com.wkr.store_appointment.service;

import com.wkr.store_appointment.common.PageResult;
import com.wkr.store_appointment.pojo.DTO.CustomerDTO;
import com.wkr.store_appointment.pojo.DTO.CustomerPageQueryDTO;
import com.wkr.store_appointment.pojo.vo.CustomerVO;

public interface CustomerService {

    PageResult<CustomerVO> page(CustomerPageQueryDTO customerPageQueryDTO);

    void save(CustomerDTO customerDTO);

    CustomerVO getById(Long id);

    void update(CustomerDTO customerDTO);

    void delete(Long id);
}
