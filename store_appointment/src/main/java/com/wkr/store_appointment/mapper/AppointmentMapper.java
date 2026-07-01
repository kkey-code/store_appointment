package com.wkr.store_appointment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wkr.store_appointment.pojo.DTO.AppointmentPageQueryDTO;
import com.wkr.store_appointment.pojo.entity.Appointment;
import com.wkr.store_appointment.pojo.vo.AppointmentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AppointmentMapper extends BaseMapper<Appointment> {

    AppointmentVO getById(@Param("id") Long id);

    IPage<AppointmentVO> page(Page<AppointmentVO> page,
                              @Param("query") AppointmentPageQueryDTO appointmentPageQueryDTO);
}
