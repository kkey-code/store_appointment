package com.wkr.store_appointment.service;

import com.wkr.store_appointment.common.PageResult;
import com.wkr.store_appointment.pojo.DTO.AppointmentDTO;
import com.wkr.store_appointment.pojo.DTO.AppointmentPageQueryDTO;
import com.wkr.store_appointment.pojo.vo.AppointmentVO;

public interface AppointmentService {

    PageResult<AppointmentVO> page(AppointmentPageQueryDTO appointmentPageQueryDTO);

    void save(AppointmentDTO appointmentDTO);

    AppointmentVO getById(Long id);

    void update(AppointmentDTO appointmentDTO);

    void cancel(Long id);

    void complete(Long id);
}
