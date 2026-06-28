package com.wkr.store_appointment.controller;

import com.wkr.store_appointment.common.PageResult;
import com.wkr.store_appointment.common.Result;
import com.wkr.store_appointment.pojo.DTO.CustomerDTO;
import com.wkr.store_appointment.pojo.DTO.CustomerPageQueryDTO;
import com.wkr.store_appointment.pojo.vo.CustomerVO;
import com.wkr.store_appointment.service.CustomerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/admin/customer")
@Api(tags = "客户管理模块")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/page")
    @ApiOperation("客户分页查询")
    public Result<PageResult<CustomerVO>> page(CustomerPageQueryDTO customerPageQueryDTO) {

        log.info("客户分页查询，参数：{}", customerPageQueryDTO);
        PageResult<CustomerVO> pageResult = customerService.page(customerPageQueryDTO);
        return Result.success(pageResult);
    }

    @PostMapping
    @ApiOperation("新增客户")
    public Result save(@RequestBody CustomerDTO customerDTO) {

        log.info("新增客户，参数：{}", customerDTO);
        customerService.save(customerDTO);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查询客户")
    public Result<CustomerVO> getById(@PathVariable Long id) {

        log.info("根据id查询客户，参数：{}", id);
        CustomerVO customerVO = customerService.getById(id);
        return Result.success(customerVO);
    }

    @PutMapping
    @ApiOperation("更新客户")
    public Result update(@RequestBody CustomerDTO customerDTO) {

        log.info("更新客户，参数：{}", customerDTO);
        customerService.update(customerDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除客户")
    public Result delete(@PathVariable Long id) {

        log.info("删除客户，参数：{}", id);
        customerService.delete(id);
        return Result.success();
    }
}
