package com.wkr.store_appointment.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wkr.store_appointment.common.PageResult;
import com.wkr.store_appointment.exception.BaseException;
import com.wkr.store_appointment.mapper.ServiceItemMapper;
import com.wkr.store_appointment.pojo.DTO.ServiceItemDTO;
import com.wkr.store_appointment.pojo.DTO.ServiceItemPageQueryDTO;
import com.wkr.store_appointment.pojo.entity.ServiceItem;
import com.wkr.store_appointment.pojo.vo.ServiceItemVO;
import com.wkr.store_appointment.service.ServiceItemService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class ServiceItemServiceImpl implements ServiceItemService {

    @Autowired
    private ServiceItemMapper serviceItemMapper;

    @Override
    public PageResult<ServiceItemVO> serviceItem(ServiceItemPageQueryDTO serviceItemPageQueryDTO) {

        PageHelper.startPage(serviceItemPageQueryDTO.getPage(), serviceItemPageQueryDTO.getPageSize());
        Page<ServiceItemVO> page = serviceItemMapper.pageQuery(serviceItemPageQueryDTO);
        return new PageResult<>(page.getTotal(), page.getResult());
    }

    @Override
    public void save(ServiceItemDTO serviceItemDTO) {

        validate(serviceItemDTO);

        ServiceItem serviceItem = new ServiceItem();
        BeanUtils.copyProperties(serviceItemDTO, serviceItem);
        if (serviceItem.getStatus() == null) {
            serviceItem.setStatus(1);
        }
        serviceItem.setCreateTime(LocalDateTime.now());
        serviceItem.setUpdateTime(LocalDateTime.now());

        serviceItemMapper.save(serviceItem);
    }

    @Override
    public ServiceItemVO getById(Long id) {

        return serviceItemMapper.getById(id);
    }

    @Override
    public void update(ServiceItemDTO serviceItemDTO) {

        validate(serviceItemDTO);

        ServiceItem serviceItem = new ServiceItem();
        BeanUtils.copyProperties(serviceItemDTO, serviceItem);
        serviceItem.setUpdateTime(LocalDateTime.now());

        serviceItemMapper.update(serviceItem);
    }

    @Override
    public void delete(Long id) {

        serviceItemMapper.updateStatus(id, 0);
    }

    private void validate(ServiceItemDTO serviceItemDTO) {

        if (serviceItemDTO.getName() == null || serviceItemDTO.getName().isEmpty()) {
            throw new BaseException("服务项目名称不能为空");
        }
        if (serviceItemDTO.getPrice() != null && serviceItemDTO.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new BaseException("价格不能小于0");
        }
        if (serviceItemDTO.getDuration() != null && serviceItemDTO.getDuration() < 0) {
            throw new BaseException("服务时长不能小于0");
        }
    }
}
