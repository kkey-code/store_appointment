package com.wkr.store_appointment.mapper;

import com.github.pagehelper.Page;
import com.wkr.store_appointment.pojo.DTO.ServiceItemPageQueryDTO;
import com.wkr.store_appointment.pojo.entity.ServiceItem;
import com.wkr.store_appointment.pojo.vo.ServiceItemVO;
import org.apache.ibatis.annotations.*;

@Mapper
public interface ServiceItemMapper {

    Page<ServiceItemVO> pageQuery(ServiceItemPageQueryDTO serviceItemPageQueryDTO);

    @Insert("insert into service_item (name, price, duration, description, status, create_time, update_time) " +
            "values (#{name}, #{price}, #{duration}, #{description}, #{status}, #{createTime}, #{updateTime})")
    void save(ServiceItem serviceItem);

    @Select("select * from service_item where id = #{id}")
    ServiceItemVO getById(Long id);

    @Update("update service_item set name = #{name}, price = #{price}, duration = #{duration}, description = #{description}, status = #{status}, update_time = #{updateTime} where id = #{id}")
    void update(ServiceItem serviceItem);

    @Update("update service_item set status = #{status} where id = #{id}")
    void updateStatus(@Param("id") Long id, @Param("status") Integer status);
}
