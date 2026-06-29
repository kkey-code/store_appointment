package com.wkr.store_appointment.pojo.vo.export;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class CustomerExportVO {

    @ExcelProperty("客户ID")
    private Long id;

    @ExcelProperty("姓名")
    private String name;

    @ExcelProperty("手机号")
    private String phone;

    @ExcelProperty("性别")
    private String gender;

    @ExcelProperty("生日")
    private String birthday;

    @ExcelProperty("等级")
    private String level;

    @ExcelProperty("来源")
    private String source;

    @ExcelProperty("备注")
    private String remark;

    @ExcelProperty("创建时间")
    private String createTime;
}
