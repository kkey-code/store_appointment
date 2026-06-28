package com.wkr.store_appointment.controller;

import com.wkr.store_appointment.common.PageResult;
import com.wkr.store_appointment.common.Result;
import com.wkr.store_appointment.pojo.DTO.InventoryItemDTO;
import com.wkr.store_appointment.pojo.DTO.InventoryItemPageQueryDTO;
import com.wkr.store_appointment.pojo.vo.InventoryItemVO;
import com.wkr.store_appointment.service.InventoryItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/admin/inventory")
@Api(tags = "库存管理模块")
public class InventoryItemController {

    @Autowired
    private InventoryItemService inventoryItemService;

    @GetMapping("/page")
    @ApiOperation("库存分页查询")
    public Result<PageResult<InventoryItemVO>> page(InventoryItemPageQueryDTO inventoryItemPageQueryDTO) {

        log.info("库存分页查询，参数：{}", inventoryItemPageQueryDTO);
        PageResult<InventoryItemVO> pageResult = inventoryItemService.page(inventoryItemPageQueryDTO);
        return Result.success(pageResult);
    }

    @PostMapping
    @ApiOperation("新增库存")
    public Result save(@RequestBody InventoryItemDTO inventoryItemDTO) {

        log.info("新增库存，参数：{}", inventoryItemDTO);
        inventoryItemService.save(inventoryItemDTO);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查询库存")
    public Result<InventoryItemVO> getById(@PathVariable Long id) {

        log.info("根据id查询库存，参数：{}", id);
        InventoryItemVO inventoryItemVO = inventoryItemService.getById(id);
        return Result.success(inventoryItemVO);
    }

    @PutMapping
    @ApiOperation("更新库存")
    public Result update(@RequestBody InventoryItemDTO inventoryItemDTO) {

        log.info("更新库存，参数：{}", inventoryItemDTO);
        inventoryItemService.update(inventoryItemDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除库存")
    public Result delete(@PathVariable Long id) {

        log.info("删除库存，参数：{}", id);
        inventoryItemService.delete(id);
        return Result.success();
    }
}
