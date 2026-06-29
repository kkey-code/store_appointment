package com.wkr.store_appointment.controller;

import com.wkr.store_appointment.common.PageResult;
import com.wkr.store_appointment.common.Result;
import com.wkr.store_appointment.pojo.DTO.AppointmentDTO;
import com.wkr.store_appointment.pojo.DTO.AppointmentPageQueryDTO;
import com.wkr.store_appointment.pojo.vo.AppointmentVO;
import com.wkr.store_appointment.service.AppointmentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/admin/appointment")
@Api(tags = "预约管理模块")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @GetMapping("/page")
    @ApiOperation("预约分页查询")
    public Result<PageResult<AppointmentVO>> page(AppointmentPageQueryDTO appointmentPageQueryDTO) {

        log.info("预约分页查询，参数：{}", appointmentPageQueryDTO);
        PageResult<AppointmentVO> pageResult = appointmentService.page(appointmentPageQueryDTO);
        return Result.success(pageResult);
    }

    @PostMapping
    @ApiOperation("新增预约")
    public Result save(@RequestBody AppointmentDTO appointmentDTO) {

        log.info("新增预约，参数：{}", appointmentDTO);
        appointmentService.save(appointmentDTO);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查询预约")
    public Result<AppointmentVO> getById(@PathVariable Long id) {

        log.info("根据id查询预约，参数：{}", id);
        AppointmentVO appointmentVO = appointmentService.getById(id);
        return Result.success(appointmentVO);
    }

    @PutMapping
    @ApiOperation("修改预约")
    public Result update(@RequestBody AppointmentDTO appointmentDTO) {

        log.info("修改预约，参数：{}", appointmentDTO);
        appointmentService.update(appointmentDTO);
        return Result.success();
    }

    @PutMapping("/{id}/cancel")
    @ApiOperation("取消预约")
    public Result cancel(@PathVariable Long id) {

        log.info("取消预约，参数：{}", id);
        appointmentService.cancel(id);
        return Result.success();
    }

    @PutMapping("/{id}/complete")
    @ApiOperation("完成预约")
    public Result complete(@PathVariable Long id) {

        log.info("完成预约，参数：{}", id);
        appointmentService.complete(id);
        return Result.success();
    }
}
