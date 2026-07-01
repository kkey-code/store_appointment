package com.wkr.store_appointment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wkr.store_appointment.common.PageResult;
import com.wkr.store_appointment.enums.CommonStatusEnum;
import com.wkr.store_appointment.exception.BaseException;
import com.wkr.store_appointment.mapper.ServiceItemMapper;
import com.wkr.store_appointment.pojo.DTO.ServiceItemDTO;
import com.wkr.store_appointment.pojo.DTO.ServiceItemPageQueryDTO;
import com.wkr.store_appointment.pojo.entity.ServiceItem;
import com.wkr.store_appointment.pojo.vo.ServiceItemVO;
import com.wkr.store_appointment.service.ServiceItemService;
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
public class ServiceItemServiceImpl extends ServiceImpl<ServiceItemMapper, ServiceItem> implements ServiceItemService {

    @Override
    @Cacheable(cacheNames = "serviceItem:page", key = "#p0", sync = true)
    public PageResult<ServiceItemVO> serviceItem(ServiceItemPageQueryDTO serviceItemPageQueryDTO) {

        Page<ServiceItem> page = new Page<>(
                PageQueryUtils.page(serviceItemPageQueryDTO.getPage()),
                PageQueryUtils.pageSize(serviceItemPageQueryDTO.getPageSize()));
        IPage<ServiceItem> result = baseMapper.selectPage(page, serviceItemQuery(serviceItemPageQueryDTO));
        List<ServiceItemVO> records = result.getRecords().stream().map(this::toVO).toList();
        return new PageResult<>(result.getTotal(), records);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "serviceItem:page", allEntries = true),
            @CacheEvict(cacheNames = "appointment:page", allEntries = true),
            @CacheEvict(cacheNames = "order:page", allEntries = true)
    })
    public void save(ServiceItemDTO serviceItemDTO) {

        validate(serviceItemDTO);

        ServiceItem serviceItem = new ServiceItem();
        BeanUtils.copyProperties(serviceItemDTO, serviceItem);
        if (serviceItem.getStatus() == null) {
            serviceItem.setStatus(CommonStatusEnum.ENABLED.getCode());
        }
        serviceItem.setCreateTime(LocalDateTime.now());
        serviceItem.setUpdateTime(LocalDateTime.now());

        baseMapper.insert(serviceItem);
    }

    @Override
    @Cacheable(cacheNames = "serviceItem:detail", key = "#p0", sync = true)
    public ServiceItemVO getById(Long id) {

        ServiceItem serviceItem = baseMapper.selectById(id);
        return serviceItem == null ? null : toVO(serviceItem);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "serviceItem:page", allEntries = true),
            @CacheEvict(cacheNames = "serviceItem:detail", key = "#p0.id"),
            @CacheEvict(cacheNames = "appointment:page", allEntries = true),
            @CacheEvict(cacheNames = "appointment:getById", allEntries = true),
            @CacheEvict(cacheNames = "order:page", allEntries = true),
            @CacheEvict(cacheNames = "order:getById", allEntries = true)
    })
    public void update(ServiceItemDTO serviceItemDTO) {

        validate(serviceItemDTO);

        ServiceItem serviceItem = new ServiceItem();
        BeanUtils.copyProperties(serviceItemDTO, serviceItem);
        serviceItem.setUpdateTime(LocalDateTime.now());

        baseMapper.updateById(serviceItem);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "serviceItem:page", allEntries = true),
            @CacheEvict(cacheNames = "serviceItem:detail", key = "#p0"),
            @CacheEvict(cacheNames = "appointment:page", allEntries = true),
            @CacheEvict(cacheNames = "appointment:getById", allEntries = true),
            @CacheEvict(cacheNames = "order:page", allEntries = true),
            @CacheEvict(cacheNames = "order:getById", allEntries = true)
    })
    public void delete(Long id) {

        ServiceItem serviceItem = new ServiceItem();
        serviceItem.setId(id);
        serviceItem.setStatus(CommonStatusEnum.DISABLED.getCode());
        serviceItem.setUpdateTime(LocalDateTime.now());
        baseMapper.updateById(serviceItem);
    }

    private LambdaQueryWrapper<ServiceItem> serviceItemQuery(ServiceItemPageQueryDTO query) {

        return Wrappers.lambdaQuery(ServiceItem.class)
                .like(StringUtils.hasText(query.getName()), ServiceItem::getName, query.getName())
                .eq(query.getStatus() != null, ServiceItem::getStatus, query.getStatus())
                .orderByDesc(ServiceItem::getCreateTime);
    }

    private void validate(ServiceItemDTO serviceItemDTO) {

        if (!StringUtils.hasText(serviceItemDTO.getName())) {
            throw new BaseException("服务项目名称不能为空");
        }
        if (serviceItemDTO.getPrice() != null && serviceItemDTO.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new BaseException("价格不能小于0");
        }
        if (serviceItemDTO.getDuration() != null && serviceItemDTO.getDuration() < 0) {
            throw new BaseException("服务时长不能小于0");
        }
    }

    private ServiceItemVO toVO(ServiceItem serviceItem) {

        ServiceItemVO serviceItemVO = new ServiceItemVO();
        BeanUtils.copyProperties(serviceItem, serviceItemVO);
        return serviceItemVO;
    }
}
