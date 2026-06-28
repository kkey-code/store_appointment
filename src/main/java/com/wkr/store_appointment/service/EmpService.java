package com.wkr.store_appointment.service;

import com.wkr.store_appointment.common.PageResult;
import com.wkr.store_appointment.pojo.DTO.EmployeeDTO;
import com.wkr.store_appointment.pojo.DTO.EmployeePageQueryDTO;
import com.wkr.store_appointment.pojo.entity.User;
import com.wkr.store_appointment.pojo.vo.EmployeeVO;

public interface EmpService {

    /**
     * 登录
     */
    User login(String username, String password);

    /**
     * 获取用户列表
     */
    PageResult list(EmployeePageQueryDTO employeePageQuery);

    /**
     * 新增员工
     */
    void save(EmployeeDTO employeeDTO);

    /**
     * 删除员工
     */
    void delete(Long id);

    /**
     * 修改员工
     */
    void update(EmployeeDTO employeeDTO);

    /**
     * 根据id查询员工
     */
    EmployeeVO getById(Long id);
}
