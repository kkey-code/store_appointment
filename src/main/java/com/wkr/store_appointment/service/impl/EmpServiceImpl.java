package com.wkr.store_appointment.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wkr.store_appointment.common.MessageConstant;
import com.wkr.store_appointment.common.PageResult;
import com.wkr.store_appointment.exception.AccountNotFoundException;
import com.wkr.store_appointment.exception.BaseException;
import com.wkr.store_appointment.mapper.EmployeeMapper;
import com.wkr.store_appointment.pojo.DTO.EmployeeDTO;
import com.wkr.store_appointment.pojo.DTO.EmployeePageQueryDTO;
import com.wkr.store_appointment.pojo.entity.Employee;
import com.wkr.store_appointment.pojo.entity.User;
import com.wkr.store_appointment.pojo.vo.EmployeeVO;
import com.wkr.store_appointment.service.EmpService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EmpServiceImpl implements EmpService {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Override
    public User login(String username, String password) {

        User user = employeeMapper.login(username);

        if (user == null) {
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        if (!password.equals(user.getPassword())) {
            throw new AccountNotFoundException(MessageConstant.PASSWORD_ERROR);
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_LOCKED);
        }

        return user;
    }

    @Override
    public PageResult list(EmployeePageQueryDTO employeePageQuery) {

        PageHelper.startPage(employeePageQuery.getPage(), employeePageQuery.getPageSize());
        Page<EmployeeVO> page = employeeMapper.pageQuery(employeePageQuery);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void save(EmployeeDTO employeeDTO) {

        checkPhoneUnique(employeeDTO.getPhone(), null);

        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);
        if (employee.getStatus() == null) {
            employee.setStatus(1);
        }
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());

        employeeMapper.save(employee);
    }

    @Override
    public void delete(Long id) {

        employeeMapper.disable(id);
    }

    @Override
    public void update(EmployeeDTO employeeDTO) {

        checkPhoneUnique(employeeDTO.getPhone(), employeeDTO.getId());

        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);
        employee.setUpdateTime(LocalDateTime.now());

        employeeMapper.update(employee);
    }

    @Override
    public EmployeeVO getById(Long id) {

        return employeeMapper.getById(id);
    }

    private void checkPhoneUnique(String phone, Long id) {

        if (phone == null || phone.isEmpty()) {
            return;
        }
        Integer count = employeeMapper.countByPhone(phone, id);
        if (count != null && count > 0) {
            throw new BaseException("员工手机号已存在");
        }
    }
}
