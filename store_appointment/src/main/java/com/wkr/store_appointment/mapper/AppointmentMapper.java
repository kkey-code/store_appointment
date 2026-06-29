package com.wkr.store_appointment.mapper;

import com.github.pagehelper.Page;
import com.wkr.store_appointment.pojo.DTO.AppointmentPageQueryDTO;
import com.wkr.store_appointment.pojo.entity.Appointment;
import com.wkr.store_appointment.pojo.vo.AppointmentVO;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;

@Mapper
public interface AppointmentMapper {

    @Insert("insert into appointment (customer_id, employee_id, service_item_id, service_item_count, service_items_text, appointment_time, status, remark, create_time, update_time) " +
            "values (#{customerId}, #{employeeId}, #{serviceItemId}, #{serviceItemCount}, #{serviceItemsText}, #{appointmentTime}, #{status}, #{remark}, #{createTime}, #{updateTime})")
    void save(Appointment appointment);

    AppointmentVO getById(Long id);

    Appointment getEntityById(Long id);

    Page<AppointmentVO> page(AppointmentPageQueryDTO appointmentPageQueryDTO);

    @Update("update appointment " +
            "set customer_id = #{customerId}, " +
            "employee_id = #{employeeId}, " +
            "service_item_id = #{serviceItemId}, " +
            "service_item_count = #{serviceItemCount}, " +
            "service_items_text = #{serviceItemsText}, " +
            "appointment_time = #{appointmentTime}, " +
            "status = #{status}, " +
            "remark = #{remark}, " +
            "update_time = #{updateTime} " +
            "where id = #{id}")
    void update(Appointment appointment);

    @Update("update appointment set status = #{status}, update_time = #{updateTime} where id = #{id}")
    void updateStatus(@Param("id") Long id,
                      @Param("status") Integer status,
                      @Param("updateTime") LocalDateTime updateTime);
}
