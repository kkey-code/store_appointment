package com.wkr.store_appointment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wkr.store_appointment.pojo.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
