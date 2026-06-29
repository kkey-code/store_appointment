merge into user (id, username, password, role, employee_id, status, create_time, update_time)
key(id) values
(1, 'admin', '123456', 'admin', 1, 1, current_timestamp, current_timestamp),
(2, 'readonly', '123456', 'readonly', null, 1, current_timestamp, current_timestamp);

merge into employee (id, name, phone, gender, position, status, remark, create_time, update_time)
key(id) values
(1, '管理员', '13800000000', 1, '店长', 1, null, current_timestamp, current_timestamp);

merge into customer (id, name, phone, gender, birthday, level, source, remark, create_time, update_time)
key(id) values
(1, '王女士', '13900000001', 2, null, 'VIP', '到店', '老客户', current_timestamp, current_timestamp),
(2, '赵先生', '13900000002', 1, null, '普通', '朋友介绍', '首次到店', current_timestamp, current_timestamp);

merge into service_item (id, name, price, duration, description, status, create_time, update_time)
key(id) values
(1, '基础护理', 99.00, 60, '基础服务项目', 1, current_timestamp, current_timestamp),
(2, '深度护理', 199.00, 90, '深度服务项目', 1, current_timestamp, current_timestamp),
(3, '肩颈放松', 128.00, 45, '肩颈放松服务', 1, current_timestamp, current_timestamp);
