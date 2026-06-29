package com.wkr.store_appointment.pojo.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CustomerVO {

    private Long id;
    private String name;
    private String phone;
    private Integer gender;
    private LocalDate birthday;
    private String level;
    private String source;
    private String remark;
    private LocalDateTime createTime;
}
