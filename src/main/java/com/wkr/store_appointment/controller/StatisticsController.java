package com.wkr.store_appointment.controller;

import com.wkr.store_appointment.common.Result;
import com.wkr.store_appointment.pojo.vo.OrderAmountStatisticsVO;
import com.wkr.store_appointment.pojo.vo.StatisticsOverviewVO;
import com.wkr.store_appointment.service.StatisticsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/admin/statistics")
@Api(tags = "数据统计模块")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/overview")
    @ApiOperation("首页统计数据")
    public Result<StatisticsOverviewVO> overview() {

        log.info("首页统计数据");
        return Result.success(statisticsService.overview());
    }

    @GetMapping("/orderAmount")
    @ApiOperation("最近7天订单金额统计")
    public Result<OrderAmountStatisticsVO> orderAmount() {

        log.info("最近7天订单金额统计");
        return Result.success(statisticsService.orderAmount());
    }
}
