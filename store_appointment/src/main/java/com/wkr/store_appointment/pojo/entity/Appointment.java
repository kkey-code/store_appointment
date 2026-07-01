package com.wkr.store_appointment.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("appointment")
public class Appointment {

    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("customer_id")
    private Long customerId;
    @TableField("employee_id")
    private Long employeeId;
    @TableField("service_item_id")
    private Long serviceItemId;
    @TableField("appointment_time")
    private LocalDateTime appointmentTime;
    private Integer status;
    @TableField("create_time")
    private LocalDateTime createTime;
    @TableField("update_time")
    private LocalDateTime updateTime;
    private String remark;
    @TableLogic
    private Integer deleted;
}
