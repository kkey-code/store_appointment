package com.wkr.store_appointment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wkr.store_appointment.pojo.entity.Customer;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CustomerMapper extends BaseMapper<Customer> {
}
