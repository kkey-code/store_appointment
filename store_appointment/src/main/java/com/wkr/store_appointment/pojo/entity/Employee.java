package com.wkr.store_appointment.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("employee")
public class Employee {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String phone;
    private Integer gender;
    private String position; // 职位
    private Integer status;
    private String remark; // 备注

    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}
