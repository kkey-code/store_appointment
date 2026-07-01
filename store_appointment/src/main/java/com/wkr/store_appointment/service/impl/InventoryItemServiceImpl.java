package com.wkr.store_appointment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wkr.store_appointment.common.PageResult;
import com.wkr.store_appointment.enums.CommonStatusEnum;
import com.wkr.store_appointment.exception.BaseException;
import com.wkr.store_appointment.mapper.InventoryItemMapper;
import com.wkr.store_appointment.pojo.DTO.InventoryItemDTO;
import com.wkr.store_appointment.pojo.DTO.InventoryItemPageQueryDTO;
import com.wkr.store_appointment.pojo.entity.InventoryItem;
import com.wkr.store_appointment.pojo.vo.InventoryItemVO;
import com.wkr.store_appointment.service.InventoryItemService;
import com.wkr.store_appointment.utils.PageQueryUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class InventoryItemServiceImpl extends ServiceImpl<InventoryItemMapper, InventoryItem> implements InventoryItemService {

    @Override
    @Cacheable(cacheNames = "inventoryItem:page", key = "#p0", sync = true)
    public PageResult<InventoryItemVO> page(InventoryItemPageQueryDTO inventoryItemPageQueryDTO) {

        Page<InventoryItem> page = new Page<>(
                PageQueryUtils.page(inventoryItemPageQueryDTO.getPage()),
                PageQueryUtils.pageSize(inventoryItemPageQueryDTO.getPageSize()));
        IPage<InventoryItem> result = baseMapper.selectPage(page, inventoryQuery(inventoryItemPageQueryDTO));
        List<InventoryItemVO> records = result.getRecords().stream().map(this::toVO).toList();
        return new PageResult<>(result.getTotal(), records);
    }

    @Override
    @CacheEvict(cacheNames = "inventoryItem:page", allEntries = true)
    public void save(InventoryItemDTO inventoryItemDTO) {

        validate(inventoryItemDTO);

        InventoryItem inventoryItem = new InventoryItem();
        BeanUtils.copyProperties(inventoryItemDTO, inventoryItem);
        normalizeDefaults(inventoryItem);
        inventoryItem.setCreateTime(LocalDateTime.now());
        inventoryItem.setUpdateTime(LocalDateTime.now());

        baseMapper.insert(inventoryItem);
    }

    @Override
    @Cacheable(cacheNames = "inventoryItem:detail", key = "#p0", sync = true)
    public InventoryItemVO getById(Long id) {

        InventoryItem inventoryItem = baseMapper.selectById(id);
        return inventoryItem == null ? null : toVO(inventoryItem);
    }

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

        baseMapper.updateById(inventoryItem);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "inventoryItem:page", allEntries = true),
            @CacheEvict(cacheNames = "inventoryItem:detail", key = "#p0")
    })
    public void delete(Long id) {

        baseMapper.deleteById(id);
    }

    private LambdaQueryWrapper<InventoryItem> inventoryQuery(InventoryItemPageQueryDTO query) {

        return Wrappers.lambdaQuery(InventoryItem.class)
                .like(StringUtils.hasText(query.getName()), InventoryItem::getName, query.getName())
                .eq(StringUtils.hasText(query.getCategory()), InventoryItem::getCategory, query.getCategory())
                .eq(query.getStatus() != null, InventoryItem::getStatus, query.getStatus())
                .orderByDesc(InventoryItem::getUpdateTime)
                .orderByDesc(InventoryItem::getId);
    }

    private void validate(InventoryItemDTO inventoryItemDTO) {

        if (!StringUtils.hasText(inventoryItemDTO.getName())) {
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
            inventoryItem.setStatus(CommonStatusEnum.ENABLED.getCode());
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

    private InventoryItemVO toVO(InventoryItem inventoryItem) {

        InventoryItemVO inventoryItemVO = new InventoryItemVO();
        BeanUtils.copyProperties(inventoryItem, inventoryItemVO);
        return inventoryItemVO;
    }
}
