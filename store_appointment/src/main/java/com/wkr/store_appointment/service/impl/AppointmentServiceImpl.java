package com.wkr.store_appointment.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wkr.store_appointment.common.PageResult;
import com.wkr.store_appointment.enums.AppointmentStatusEnum;
import com.wkr.store_appointment.exception.BaseException;
import com.wkr.store_appointment.mapper.AppointmentMapper;
import com.wkr.store_appointment.pojo.DTO.AppointmentDTO;
import com.wkr.store_appointment.pojo.DTO.AppointmentPageQueryDTO;
import com.wkr.store_appointment.pojo.entity.Appointment;
import com.wkr.store_appointment.pojo.vo.AppointmentVO;
import com.wkr.store_appointment.service.AppointmentService;
import com.wkr.store_appointment.utils.PageQueryUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AppointmentServiceImpl extends ServiceImpl<AppointmentMapper, Appointment> implements AppointmentService {

    @Override
    @Cacheable(cacheNames = "appointment:page", key = "#p0", sync = true)
    public PageResult<AppointmentVO> page(AppointmentPageQueryDTO appointmentPageQueryDTO) {

        Page<AppointmentVO> page = new Page<>(
                PageQueryUtils.page(appointmentPageQueryDTO.getPage()),
                PageQueryUtils.pageSize(appointmentPageQueryDTO.getPageSize()));
        IPage<AppointmentVO> result = baseMapper.page(page, appointmentPageQueryDTO);
        return new PageResult<>(result.getTotal(), result.getRecords());
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "appointment:page", allEntries = true),
            @CacheEvict(cacheNames = "statistics:overview", allEntries = true)
    })
    public void save(AppointmentDTO appointmentDTO) {

        validate(appointmentDTO);

        Appointment appointment = new Appointment();
        BeanUtils.copyProperties(appointmentDTO, appointment);
        if (appointment.getStatus() == null) {
            appointment.setStatus(AppointmentStatusEnum.PENDING.getCode());
        }
        appointment.setCreateTime(LocalDateTime.now());
        appointment.setUpdateTime(LocalDateTime.now());

        baseMapper.insert(appointment);
    }

    @Override
    @Cacheable(cacheNames = "appointment:getById", key = "#p0", sync = true)
    public AppointmentVO getById(Long id) {

        return baseMapper.getById(id);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "appointment:page", allEntries = true),
            @CacheEvict(cacheNames = "appointment:getById", key = "#p0.id"),
            @CacheEvict(cacheNames = "statistics:overview", allEntries = true)
    })
    public void update(AppointmentDTO appointmentDTO) {

        validate(appointmentDTO);

        Appointment appointment = new Appointment();
        BeanUtils.copyProperties(appointmentDTO, appointment);
        appointment.setUpdateTime(LocalDateTime.now());

        baseMapper.updateById(appointment);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "appointment:page", allEntries = true),
            @CacheEvict(cacheNames = "appointment:getById", key = "#p0"),
            @CacheEvict(cacheNames = "statistics:overview", allEntries = true)
    })
    public void cancel(Long id) {

        Appointment appointment = getAppointment(id);
        if (AppointmentStatusEnum.COMPLETED.matches(appointment.getStatus())) {
            throw new BaseException("已完成的预约不能取消");
        }

        updateStatus(id, AppointmentStatusEnum.CANCELED);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "appointment:page", allEntries = true),
            @CacheEvict(cacheNames = "appointment:getById", key = "#p0"),
            @CacheEvict(cacheNames = "statistics:overview", allEntries = true)
    })
    public void complete(Long id) {

        Appointment appointment = getAppointment(id);
        if (!AppointmentStatusEnum.CONFIRMED.matches(appointment.getStatus())) {
            throw new BaseException("只有已确认预约可以完成");
        }

        updateStatus(id, AppointmentStatusEnum.COMPLETED);
    }

    private Appointment getAppointment(Long id) {

        Appointment appointment = baseMapper.selectById(id);
        if (appointment == null) {
            throw new BaseException("预约不存在");
        }
        return appointment;
    }

    private void updateStatus(Long id, AppointmentStatusEnum status) {

        Appointment appointment = new Appointment();
        appointment.setId(id);
        appointment.setStatus(status.getCode());
        appointment.setUpdateTime(LocalDateTime.now());
        baseMapper.updateById(appointment);
    }

    private void validate(AppointmentDTO appointmentDTO) {

        if (appointmentDTO.getCustomerId() == null) {
            throw new BaseException("客户ID不能为空");
        }
        if (appointmentDTO.getEmployeeId() == null) {
            throw new BaseException("员工ID不能为空");
        }
        if (appointmentDTO.getServiceItemId() == null) {
            throw new BaseException("主服务项目ID不能为空");
        }
        if (appointmentDTO.getAppointmentTime() == null) {
            throw new BaseException("预约时间不能为空");
        }
    }
}
