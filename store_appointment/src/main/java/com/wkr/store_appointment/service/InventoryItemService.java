package com.wkr.store_appointment.service;

import com.wkr.store_appointment.common.PageResult;
import com.wkr.store_appointment.pojo.DTO.InventoryItemDTO;
import com.wkr.store_appointment.pojo.DTO.InventoryItemPageQueryDTO;
import com.wkr.store_appointment.pojo.vo.InventoryItemVO;

public interface InventoryItemService {

    PageResult<InventoryItemVO> page(InventoryItemPageQueryDTO inventoryItemPageQueryDTO);

    void save(InventoryItemDTO inventoryItemDTO);

    InventoryItemVO getById(Long id);

    void update(InventoryItemDTO inventoryItemDTO);

    void delete(Long id);
}
