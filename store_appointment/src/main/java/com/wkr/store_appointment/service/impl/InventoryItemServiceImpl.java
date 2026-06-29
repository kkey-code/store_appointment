package com.wkr.store_appointment.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wkr.store_appointment.common.PageResult;
import com.wkr.store_appointment.exception.BaseException;
import com.wkr.store_appointment.mapper.InventoryItemMapper;
import com.wkr.store_appointment.pojo.DTO.InventoryItemDTO;
import com.wkr.store_appointment.pojo.DTO.InventoryItemPageQueryDTO;
import com.wkr.store_appointment.pojo.entity.InventoryItem;
import com.wkr.store_appointment.pojo.vo.InventoryItemVO;
import com.wkr.store_appointment.service.InventoryItemService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class InventoryItemServiceImpl implements InventoryItemService {

    @Autowired
    private InventoryItemMapper inventoryItemMapper;

    /**
     * 分页查询
     */
    @Override
    @Cacheable(cacheNames = "inventoryItem:page", key = "#p0", sync = true)
    public PageResult<InventoryItemVO> page(InventoryItemPageQueryDTO inventoryItemPageQueryDTO) {

        PageHelper.startPage(inventoryItemPageQueryDTO.getPage(), inventoryItemPageQueryDTO.getPageSize());
        Page<InventoryItemVO> page = inventoryItemMapper.page(inventoryItemPageQueryDTO);
        return new PageResult<>(page.getTotal(), new java.util.ArrayList<>(page.getResult()));
    }

    /**
     * 新增
     */
    @Override
    @CacheEvict(cacheNames = "inventoryItem:page", allEntries = true)
    public void save(InventoryItemDTO inventoryItemDTO) {

        validate(inventoryItemDTO);

        InventoryItem inventoryItem = new InventoryItem();
        BeanUtils.copyProperties(inventoryItemDTO, inventoryItem);
        normalizeDefaults(inventoryItem);
        inventoryItem.setCreateTime(LocalDateTime.now());
        inventoryItem.setUpdateTime(LocalDateTime.now());

        inventoryItemMapper.save(inventoryItem);
    }

    /**
     * 根据id查询
     */
    @Override
    @Cacheable(cacheNames = "inventoryItem:detail", key = "#p0", sync = true)
    public InventoryItemVO getById(Long id) {

        return inventoryItemMapper.getById(id);
    }

    /**
     * 修改
     */
    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "inventoryItem:page", allEntries = true),
            @CacheEvict(cacheNames = "inventoryItem:detail", key = "#p0.id")
    })
    public void update(InventoryItemDTO inventoryItemDTO) {

        validate(inventoryItemDTO);

        InventoryItem inventoryItem = new InventoryItem();
        BeanUtils.copyProperties(inventoryItemDTO, inventoryItem);
        normalizeDefaults(inventoryItem);
        inventoryItem.setUpdateTime(LocalDateTime.now());

        inventoryItemMapper.update(inventoryItem);
    }

    /**
     * 删除
     */
    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "inventoryItem:page", allEntries = true),
            @CacheEvict(cacheNames = "inventoryItem:detail", key = "#p0")
    })
    public void delete(Long id) {

        inventoryItemMapper.delete(id);
    }

    private void validate(InventoryItemDTO inventoryItemDTO) {

        if (inventoryItemDTO.getName() == null || inventoryItemDTO.getName().isEmpty()) {
            throw new BaseException("库存名称不能为空");
        }
        if (inventoryItemDTO.getQuantity() != null && inventoryItemDTO.getQuantity().compareTo(BigDecimal.ZERO) < 0) {
            throw new BaseException("当前库存不能小于0");
        }
        if (inventoryItemDTO.getSafetyStock() != null && inventoryItemDTO.getSafetyStock().compareTo(BigDecimal.ZERO) < 0) {
            throw new BaseException("安全库存不能小于0");
        }
        if (inventoryItemDTO.getCostPrice() != null && inventoryItemDTO.getCostPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new BaseException("成本价不能小于0");
        }
    }

    private void normalizeDefaults(InventoryItem inventoryItem) {

        if (inventoryItem.getStatus() == null) {
            inventoryItem.setStatus(1);
        }
        if (inventoryItem.getQuantity() == null) {
            inventoryItem.setQuantity(BigDecimal.ZERO);
        }
        if (inventoryItem.getSafetyStock() == null) {
            inventoryItem.setSafetyStock(BigDecimal.ZERO);
        }
        if (inventoryItem.getCostPrice() == null) {
            inventoryItem.setCostPrice(BigDecimal.ZERO);
        }
    }
}
