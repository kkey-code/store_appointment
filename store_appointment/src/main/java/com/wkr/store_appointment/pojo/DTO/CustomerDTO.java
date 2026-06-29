package com.wkr.store_appointment.pojo.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CustomerDTO {

    private Long id;
    private String name;
    private String phone;
    private Integer gender;
    private LocalDate birthday;
    private String level;
    private String source;
    private String remark;
}
