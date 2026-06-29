/**
 * 通用分页响应模型：封装总记录数和当前页数据。
 */
package com.wkr.store_appointment.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResult<T> implements Serializable {

    private long total;
    private List<T> records;
}
