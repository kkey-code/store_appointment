package com.wkr.store_appointment.mapper;

import com.github.pagehelper.Page;
import com.wkr.store_appointment.pojo.DTO.EmployeePageQueryDTO;
import com.wkr.store_appointment.pojo.entity.Employee;
import com.wkr.store_appointment.pojo.entity.User;
import com.wkr.store_appointment.pojo.vo.EmployeeVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface EmployeeMapper {

    @Select("select * from user where username = #{username}")
    User login(String username);

    Page<EmployeeVO> pageQuery(EmployeePageQueryDTO employeePageQuery);

    @Insert("insert into employee (name, phone, position, remark, update_time, create_time, gender, status) " +
            "values (#{name}, #{phone}, #{position}, #{remark}, #{updateTime}, #{createTime}, #{gender}, #{status})")
    void save(Employee employee);

    @Update("update employee set status = 0, update_time = now() where id = #{id}")
    void disable(Long id);

    @Select("select * from employee where id = #{id}")
    EmployeeVO getById(Long id);

    void update(Employee employee);

    @Select("select count(*) from employee where phone = #{phone} and (#{id} is null or id != #{id})")
    Integer countByPhone(@Param("phone") String phone, @Param("id") Long id);

}
