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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EmpServiceImpl implements EmpService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 登录
     */
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

    /**
     * 分页查询
     */
    @Override
    @Cacheable(cacheNames = "employee:list", key = "#p0", sync = true)
    public PageResult list(EmployeePageQueryDTO employeePageQuery) {

        PageHelper.startPage(employeePageQuery.getPage(), employeePageQuery.getPageSize());
        Page<EmployeeVO> page = employeeMapper.pageQuery(employeePageQuery);
        return new PageResult(page.getTotal(), new java.util.ArrayList<>(page.getResult()));
    }

    /**
     * 新增员工
     */
    @Override
    @CacheEvict(cacheNames = "employee:list", allEntries = true)
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

    /**
     * 禁用员工
     */
    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "employee:list", allEntries = true),
            @CacheEvict(cacheNames = "employee:detail", key = "#p0")
    })
    public void delete(Long id) {

        employeeMapper.disable(id);
    }

    /**
     * 修改员工
     */
    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "employee:list", allEntries = true),
            @CacheEvict(cacheNames = "employee:detail", key = "#p0.id")
    })
    public void update(EmployeeDTO employeeDTO) {

        checkPhoneUnique(employeeDTO.getPhone(), employeeDTO.getId());

        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);
        employee.setUpdateTime(LocalDateTime.now());

        employeeMapper.update(employee);
    }

    /**
     * 根据id查询员工
     */
    @Override
    @Cacheable(cacheNames = "employee:detail", key = "#p0", sync = true)
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
