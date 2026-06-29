package com.wkr.store_appointment.controller;

import com.wkr.store_appointment.common.PageResult;
import com.wkr.store_appointment.common.Result;
import com.wkr.store_appointment.pojo.DTO.OrderDTO;
import com.wkr.store_appointment.pojo.DTO.OrderPageQueryDTO;
import com.wkr.store_appointment.pojo.DTO.OrderPaymentDTO;
import com.wkr.store_appointment.pojo.vo.OrderVO;
import com.wkr.store_appointment.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/admin/order")
@Api(tags = "订单管理模块")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/page")
    @ApiOperation("订单分页查询")
    public Result<PageResult<OrderVO>> page(OrderPageQueryDTO orderPageQueryDTO) {

        log.info("订单分页查询，参数：{}", orderPageQueryDTO);
        PageResult<OrderVO> pageResult = orderService.page(orderPageQueryDTO);
        return Result.success(pageResult);
    }

    @PostMapping
    @ApiOperation("新增订单")
    public Result save(@RequestBody OrderDTO orderDTO) {

        log.info("新增订单，参数：{}", orderDTO);
        orderService.save(orderDTO);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查询订单")
    public Result<OrderVO> getById(@PathVariable Long id) {

        log.info("根据id查询订单，参数：{}", id);
        OrderVO orderVO = orderService.getById(id);
        return Result.success(orderVO);
    }

    @PutMapping("/{id}/pay")
    @ApiOperation("支付订单")
    public Result pay(@PathVariable Long id) {

        log.info("支付订单，参数：{}", id);
        orderService.pay(id);
        return Result.success();
    }

    @PutMapping("/{id}/payment")
    @ApiOperation("更新订单收款")
    public Result updatePayment(@PathVariable Long id, @RequestBody OrderPaymentDTO orderPaymentDTO) {

        log.info("更新订单收款，订单ID：{}，参数：{}", id, orderPaymentDTO);
        orderService.updatePayment(id, orderPaymentDTO);
        return Result.success();
    }

    @PutMapping("/{id}/complete")
    @ApiOperation("完成订单")
    public Result complete(@PathVariable Long id) {

        log.info("完成订单，参数：{}", id);
        orderService.complete(id);
        return Result.success();
    }

    @PutMapping("/{id}/cancel")
    @ApiOperation("取消订单")
    public Result cancel(@PathVariable Long id) {

        log.info("取消订单，参数：{}", id);
        orderService.cancel(id);
        return Result.success();
    }
}
