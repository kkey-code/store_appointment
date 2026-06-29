package com.wkr.store_appointment.service;

import com.wkr.store_appointment.common.PageResult;
import com.wkr.store_appointment.pojo.DTO.ServiceItemDTO;
import com.wkr.store_appointment.pojo.DTO.ServiceItemPageQueryDTO;
import com.wkr.store_appointment.pojo.vo.ServiceItemVO;

public interface ServiceItemService {

    PageResult<ServiceItemVO> serviceItem(ServiceItemPageQueryDTO serviceItemPageQueryDTO);

    void save(ServiceItemDTO serviceItemDTO);

    ServiceItemVO getById(Long id);

    void update(ServiceItemDTO serviceItemDTO);

    void delete(Long id);
}
