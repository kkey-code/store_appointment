package com.wkr.store_appointment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wkr.store_appointment.pojo.DTO.OrderPageQueryDTO;
import com.wkr.store_appointment.pojo.entity.OrderInfo;
import com.wkr.store_appointment.pojo.vo.OrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderMapper extends BaseMapper<OrderInfo> {

    IPage<OrderVO> page(Page<OrderVO> page, @Param("query") OrderPageQueryDTO orderPageQueryDTO);

    OrderVO getById(@Param("id") Long id);

    List<OrderVO> listForExport(@Param("query") OrderPageQueryDTO queryDTO);
}
