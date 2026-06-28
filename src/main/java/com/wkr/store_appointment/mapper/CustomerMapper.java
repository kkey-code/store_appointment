package com.wkr.store_appointment.mapper;

import com.github.pagehelper.Page;
import com.wkr.store_appointment.pojo.DTO.CustomerPageQueryDTO;
import com.wkr.store_appointment.pojo.entity.Customer;
import com.wkr.store_appointment.pojo.vo.CustomerVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface CustomerMapper {

    @Insert("insert into customer (name, phone, gender, birthday, level, source, remark, create_time, update_time) " +
            "values (#{name}, #{phone}, #{gender}, #{birthday}, #{level}, #{source}, #{remark}, #{createTime}, #{updateTime})")
    void save(Customer customer);

    @Delete("delete from customer where id = #{id}")
    void delete(Long id);

    @Select("select * from customer where id = #{id}")
    CustomerVO getById(Long id);

    Page<CustomerVO> page(CustomerPageQueryDTO customerPageQueryDTO);

    @Update("update customer set name = #{name}, phone = #{phone}, gender = #{gender}, birthday = #{birthday}, level = #{level}, source = #{source}, remark = #{remark}, update_time = #{updateTime} where id = #{id}")
    void update(Customer customer);

    @Select("select count(*) from customer where phone = #{phone} and (#{id} is null or id != #{id})")
    Integer countByPhone(@Param("phone") String phone, @Param("id") Long id);

    @Select("select count(*) from appointment where customer_id = #{id}")
    Integer countAppointmentsByCustomerId(Long id);

    @Select("select count(*) from order_info where customer_id = #{id}")
    Integer countOrdersByCustomerId(Long id);
}
