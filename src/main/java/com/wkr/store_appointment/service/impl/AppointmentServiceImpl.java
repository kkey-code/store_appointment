package com.wkr.store_appointment.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wkr.store_appointment.common.PageResult;
import com.wkr.store_appointment.exception.BaseException;
import com.wkr.store_appointment.mapper.AppointmentMapper;
import com.wkr.store_appointment.pojo.DTO.AppointmentDTO;
import com.wkr.store_appointment.pojo.DTO.AppointmentPageQueryDTO;
import com.wkr.store_appointment.pojo.entity.Appointment;
import com.wkr.store_appointment.pojo.vo.AppointmentVO;
import com.wkr.store_appointment.service.AppointmentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    private static final int STATUS_PENDING = 0;
    private static final int STATUS_CONFIRMED = 1;
    private static final int STATUS_COMPLETED = 2;
    private static final int STATUS_CANCELED = 3;

    @Autowired
    private AppointmentMapper appointmentMapper;

    @Override
    public PageResult<AppointmentVO> page(AppointmentPageQueryDTO appointmentPageQueryDTO) {

        PageHelper.startPage(appointmentPageQueryDTO.getPage(), appointmentPageQueryDTO.getPageSize());
        Page<AppointmentVO> page = appointmentMapper.page(appointmentPageQueryDTO);
        return new PageResult<>(page.getTotal(), page.getResult());
    }

    @Override
    public void save(AppointmentDTO appointmentDTO) {

        validate(appointmentDTO);

        Appointment appointment = new Appointment();
        BeanUtils.copyProperties(appointmentDTO, appointment);
        if (appointment.getServiceItemCount() == null) {
            appointment.setServiceItemCount(1);
        }
        if (appointment.getStatus() == null) {
            appointment.setStatus(STATUS_PENDING);
        }
        appointment.setCreateTime(LocalDateTime.now());
        appointment.setUpdateTime(LocalDateTime.now());

        appointmentMapper.save(appointment);
    }

    @Override
    public AppointmentVO getById(Long id) {

        return appointmentMapper.getById(id);
    }

    @Override
    public void update(AppointmentDTO appointmentDTO) {

        validate(appointmentDTO);

        Appointment appointment = new Appointment();
        BeanUtils.copyProperties(appointmentDTO, appointment);
        if (appointment.getServiceItemCount() == null) {
            appointment.setServiceItemCount(1);
        }
        appointment.setUpdateTime(LocalDateTime.now());

        appointmentMapper.update(appointment);
    }

    @Override
    public void cancel(Long id) {

        Appointment appointment = getAppointment(id);
        if (statusEquals(appointment.getStatus(), STATUS_COMPLETED)) {
            throw new BaseException("已完成的预约不能取消");
        }

        appointmentMapper.updateStatus(id, STATUS_CANCELED, LocalDateTime.now());
    }

    @Override
    public void complete(Long id) {

        Appointment appointment = getAppointment(id);
        if (!statusEquals(appointment.getStatus(), STATUS_CONFIRMED)) {
            throw new BaseException("只有已确认预约可以完成");
        }

        appointmentMapper.updateStatus(id, STATUS_COMPLETED, LocalDateTime.now());
    }

    private Appointment getAppointment(Long id) {

        Appointment appointment = appointmentMapper.getEntityById(id);
        if (appointment == null) {
            throw new BaseException("预约不存在");
        }
        return appointment;
    }

    private boolean statusEquals(Integer status, int expected) {

        return status != null && status == expected;
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
        Integer serviceItemCount = appointmentDTO.getServiceItemCount();
        if (serviceItemCount != null && (serviceItemCount < 1 || serviceItemCount > 6)) {
            throw new BaseException("一次预约的项目数量应为1到6个");
        }
    }
}
