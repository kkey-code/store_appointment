package com.wkr.store_appointment.controller;

import com.wkr.store_appointment.common.PageResult;
import com.wkr.store_appointment.common.Result;
import com.wkr.store_appointment.pojo.DTO.ServiceItemDTO;
import com.wkr.store_appointment.pojo.DTO.ServiceItemPageQueryDTO;
import com.wkr.store_appointment.pojo.vo.ServiceItemVO;
import com.wkr.store_appointment.service.ServiceItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/admin/serviceItem")
@Api(tags = "服务项目管理模块")
public class ServiceItemController {

    @Autowired
    private ServiceItemService serviceItemService;

    @GetMapping("/page")
    @ApiOperation("服务项目分页查询")
    public Result<PageResult<ServiceItemVO>> serviceItem(ServiceItemPageQueryDTO serviceItemPageQueryDTO) {

        PageResult<ServiceItemVO> pageResult = serviceItemService.serviceItem(serviceItemPageQueryDTO);
        return Result.success(pageResult);
    }

    @PostMapping
    @ApiOperation("新增服务项目")
    public Result save(@RequestBody ServiceItemDTO serviceItemDTO) {

        serviceItemService.save(serviceItemDTO);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查询服务项目")
    public Result<ServiceItemVO> getById(@PathVariable Long id) {

        ServiceItemVO serviceItemVO = serviceItemService.getById(id);
        return Result.success(serviceItemVO);
    }

    @PutMapping
    @ApiOperation("修改服务项目")
    public Result update(@RequestBody ServiceItemDTO serviceItemDTO) {

        serviceItemService.update(serviceItemDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除服务项目")
    public Result delete(@PathVariable Long id) {

        serviceItemService.delete(id);
        return Result.success();
    }
}
