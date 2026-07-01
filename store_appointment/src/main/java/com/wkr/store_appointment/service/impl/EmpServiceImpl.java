package com.wkr.store_appointment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wkr.store_appointment.common.MessageConstant;
import com.wkr.store_appointment.common.PageResult;
import com.wkr.store_appointment.enums.CommonStatusEnum;
import com.wkr.store_appointment.exception.AccountNotFoundException;
import com.wkr.store_appointment.exception.BaseException;
import com.wkr.store_appointment.mapper.EmployeeMapper;
import com.wkr.store_appointment.mapper.UserMapper;
import com.wkr.store_appointment.pojo.DTO.EmployeeDTO;
import com.wkr.store_appointment.pojo.DTO.EmployeePageQueryDTO;
import com.wkr.store_appointment.pojo.entity.Employee;
import com.wkr.store_appointment.pojo.entity.User;
import com.wkr.store_appointment.pojo.vo.EmployeeVO;
import com.wkr.store_appointment.service.EmpService;
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
public class EmpServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmpService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User login(String username, String password) {

        User user = userMapper.selectOne(Wrappers.lambdaQuery(User.class)
                .eq(User::getUsername, username)
                .last("limit 1"));

        if (user == null) {
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        if (!password.equals(user.getPassword())) {
            throw new AccountNotFoundException(MessageConstant.PASSWORD_ERROR);
        }
        if (CommonStatusEnum.DISABLED.matches(user.getStatus())) {
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_LOCKED);
        }

        return user;
    }

    @Override
    @Cacheable(cacheNames = "employee:list", key = "#p0", sync = true)
    public PageResult<EmployeeVO> list(EmployeePageQueryDTO employeePageQuery) {

        Page<Employee> page = new Page<>(
                PageQueryUtils.page(employeePageQuery.getPage()),
                PageQueryUtils.pageSize(employeePageQuery.getPageSize()));
        IPage<Employee> result = baseMapper.selectPage(page, employeeQuery(employeePageQuery));
        List<EmployeeVO> records = result.getRecords().stream().map(this::toVO).toList();
        return new PageResult<>(result.getTotal(), records);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "employee:list", allEntries = true),
            @CacheEvict(cacheNames = "appointment:page", allEntries = true)
    })
    public void save(EmployeeDTO employeeDTO) {

        checkPhoneUnique(employeeDTO.getPhone(), null);

        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);
        if (employee.getStatus() == null) {
            employee.setStatus(CommonStatusEnum.ENABLED.getCode());
        }
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());

        baseMapper.insert(employee);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "employee:list", allEntries = true),
            @CacheEvict(cacheNames = "employee:detail", key = "#p0"),
            @CacheEvict(cacheNames = "appointment:page", allEntries = true),
            @CacheEvict(cacheNames = "appointment:getById", allEntries = true)
    })
    public void delete(Long id) {

        Employee employee = new Employee();
        employee.setId(id);
        employee.setStatus(CommonStatusEnum.DISABLED.getCode());
        employee.setUpdateTime(LocalDateTime.now());
        baseMapper.updateById(employee);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "employee:list", allEntries = true),
            @CacheEvict(cacheNames = "employee:detail", key = "#p0.id"),
            @CacheEvict(cacheNames = "appointment:page", allEntries = true),
            @CacheEvict(cacheNames = "appointment:getById", allEntries = true)
    })
    public void update(EmployeeDTO employeeDTO) {

        checkPhoneUnique(employeeDTO.getPhone(), employeeDTO.getId());

        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);
        employee.setUpdateTime(LocalDateTime.now());

        baseMapper.updateById(employee);
    }

    @Override
    @Cacheable(cacheNames = "employee:detail", key = "#p0", sync = true)
    public EmployeeVO getById(Long id) {

        Employee employee = baseMapper.selectById(id);
        return employee == null ? null : toVO(employee);
    }

    private LambdaQueryWrapper<Employee> employeeQuery(EmployeePageQueryDTO query) {

        return Wrappers.lambdaQuery(Employee.class)
                .like(StringUtils.hasText(query.getName()), Employee::getName, query.getName())
                .like(StringUtils.hasText(query.getPhone()), Employee::getPhone, query.getPhone())
                .eq(query.getStatus() != null, Employee::getStatus, query.getStatus())
                .orderByDesc(Employee::getCreateTime);
    }

    private void checkPhoneUnique(String phone, Long id) {

        if (!StringUtils.hasText(phone)) {
            return;
        }
        Long count = baseMapper.selectCount(Wrappers.lambdaQuery(Employee.class)
                .eq(Employee::getPhone, phone)
                .ne(id != null, Employee::getId, id));
        if (count != null && count > 0) {
            throw new BaseException("员工手机号已存在");
        }
    }

    private EmployeeVO toVO(Employee employee) {

        EmployeeVO employeeVO = new EmployeeVO();
        BeanUtils.copyProperties(employee, employeeVO);
        return employeeVO;
    }
}
