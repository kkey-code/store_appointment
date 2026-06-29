package com.wkr.store_appointment.mapper;

import com.github.pagehelper.Page;
import com.wkr.store_appointment.pojo.DTO.InventoryItemPageQueryDTO;
import com.wkr.store_appointment.pojo.entity.InventoryItem;
import com.wkr.store_appointment.pojo.vo.InventoryItemVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface InventoryItemMapper {

    Page<InventoryItemVO> page(InventoryItemPageQueryDTO inventoryItemPageQueryDTO);

    @Insert("insert into inventory_item (name, category, unit, quantity, safety_stock, cost_price, supplier, status, remark, create_time, update_time) " +
            "values (#{name}, #{category}, #{unit}, #{quantity}, #{safetyStock}, #{costPrice}, #{supplier}, #{status}, #{remark}, #{createTime}, #{updateTime})")
    void save(InventoryItem inventoryItem);

    @Select("select * from inventory_item where id = #{id}")
    InventoryItemVO getById(Long id);

    @Update("update inventory_item set name = #{name}, category = #{category}, unit = #{unit}, quantity = #{quantity}, safety_stock = #{safetyStock}, cost_price = #{costPrice}, supplier = #{supplier}, status = #{status}, remark = #{remark}, update_time = #{updateTime} where id = #{id}")
    void update(InventoryItem inventoryItem);

    @Delete("delete from inventory_item where id = #{id}")
    void delete(Long id);
}
