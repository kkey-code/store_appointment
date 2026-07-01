package com.wkr.store_appointment.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("customer")
public class Customer {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String phone;
    private Integer gender;
    private LocalDate birthday;
    private String level;
    private String source;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;

}
