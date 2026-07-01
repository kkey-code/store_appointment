merge into user (id, username, password, role, employee_id, status, create_time, update_time)
key(id) values
(1, 'admin', '123456', 'admin', 1, 1, current_timestamp, current_timestamp),
(2, 'readonly', '123456', 'readonly', null, 1, current_timestamp, current_timestamp);

merge into employee (id, name, phone, gender, position, status, remark, create_time, update_time)
key(id) values
(1, '管理员', '13800000000', 1, '店长', 1, null, current_timestamp, current_timestamp),
(2, '李美容师', '13800000011', 2, '美容师', 1, '擅长面部护理', current_timestamp, current_timestamp),
(3, '陈理疗师', '13800000012', 1, '理疗师', 1, '负责肩颈和放松项目', current_timestamp, current_timestamp),
(4, '周顾问', '13800000013', 2, '客户顾问', 1, '负责客户跟进', current_timestamp, current_timestamp);

merge into customer (id, name, phone, gender, birthday, level, source, remark, create_time, update_time)
key(id) values
(1, '王女士', '13900000001', 2, null, 'VIP', '到店', '老客户', current_timestamp, current_timestamp),
(2, '赵先生', '13900000002', 1, null, '普通', '朋友介绍', '首次到店', current_timestamp, current_timestamp),
(3, '刘女士', '13900000003', 2, dateadd('YEAR', -29, current_date), 'VIP', '小红书', '关注皮肤补水', current_timestamp, current_timestamp),
(4, '孙先生', '13900000004', 1, dateadd('YEAR', -36, current_date), '普通', '门店活动', '肩颈紧张', current_timestamp, current_timestamp),
(5, '陈女士', '13900000005', 2, dateadd('YEAR', -32, current_date), '金卡', '老客转介绍', '每月固定护理', current_timestamp, current_timestamp),
(6, '何女士', '13900000006', 2, dateadd('YEAR', -27, current_date), '普通', '线上预约', '首次体验', current_timestamp, current_timestamp),
(7, '周先生', '13900000007', 1, dateadd('YEAR', -41, current_date), '银卡', '到店', '关注理疗放松', current_timestamp, current_timestamp),
(8, '吴女士', '13900000008', 2, dateadd('YEAR', -34, current_date), 'VIP', '朋友介绍', '准备办卡', current_timestamp, current_timestamp);

merge into service_item (id, name, price, duration, description, status, create_time, update_time)
key(id) values
(1, '基础护理', 99.00, 60, '基础服务项目', 1, current_timestamp, current_timestamp),
(2, '深度护理', 199.00, 90, '深度服务项目', 1, current_timestamp, current_timestamp),
(3, '肩颈放松', 128.00, 45, '肩颈放松服务', 1, current_timestamp, current_timestamp),
(4, '补水修护', 168.00, 75, '补水修护护理', 1, current_timestamp, current_timestamp),
(5, '全身放松', 298.00, 120, '全身放松理疗', 1, current_timestamp, current_timestamp),
(6, '会员复查', 0.00, 30, '会员护理后复查', 1, current_timestamp, current_timestamp);

merge into appointment (id, customer_id, employee_id, service_item_id, appointment_time, status, remark, create_time, update_time)
key(id) values
(1, 1, 2, 2, dateadd('HOUR', 10, current_date), 1, '老客户今日到店护理', dateadd('DAY', -1, current_timestamp), current_timestamp),
(2, 3, 2, 4, dateadd('HOUR', 14, current_date), 0, '皮肤干燥，安排补水项目', current_timestamp, current_timestamp),
(3, 4, 3, 3, dateadd('HOUR', 16, current_date), 1, '下班后来店', current_timestamp, current_timestamp),
(4, 5, 2, 1, dateadd('DAY', -1, dateadd('HOUR', 11, current_date)), 2, '已完成护理', dateadd('DAY', -2, current_timestamp), dateadd('DAY', -1, current_timestamp)),
(5, 6, 4, 6, dateadd('DAY', 1, dateadd('HOUR', 15, current_date)), 0, '新客复查提醒', current_timestamp, current_timestamp),
(6, 7, 3, 5, dateadd('DAY', 2, dateadd('HOUR', 19, current_date)), 0, '晚间预约', current_timestamp, current_timestamp);

merge into order_info (id, order_no, appointment_id, customer_id, service_item_id, card_type, original_amount, discount_amount, amount, paid_amount, debt_amount, monthly_payment, payment_method, debt_status, pay_status, order_status, pay_time, remark, create_time, update_time)
key(id) values
(1, 'ORD-DEMO-20260630001', 1, 1, 2, '护理卡', 399.00, 40.00, 359.00, 359.00, 0.00, 0.00, '微信', 0, 1, 1, current_timestamp, '老客户护理收款', current_timestamp, current_timestamp),
(2, 'ORD-DEMO-20260630002', 3, 4, 3, '次卡', 128.00, 0.00, 128.00, 80.00, 48.00, 48.00, '支付宝', 1, 0, 0, null, '客户尾款下次补齐', current_timestamp, current_timestamp),
(3, 'ORD-DEMO-20260629001', 4, 5, 1, '会员卡', 336.00, 36.00, 300.00, 300.00, 0.00, 0.00, '现金', 0, 1, 1, dateadd('DAY', -1, current_timestamp), '双项目套餐', dateadd('DAY', -1, current_timestamp), dateadd('DAY', -1, current_timestamp)),
(4, 'ORD-DEMO-20260628001', null, 8, 5, '疗程卡', 1288.00, 188.00, 1100.00, 500.00, 600.00, 200.00, '微信', 1, 0, 0, null, '疗程卡分期付款', dateadd('DAY', -2, current_timestamp), dateadd('DAY', -2, current_timestamp)),
(5, 'ORD-DEMO-20260627001', null, 2, 3, '次卡', 128.00, 8.00, 120.00, 120.00, 0.00, 0.00, '支付宝', 0, 1, 1, dateadd('DAY', -3, current_timestamp), '肩颈体验', dateadd('DAY', -3, current_timestamp), dateadd('DAY', -3, current_timestamp)),
(6, 'ORD-DEMO-20260626001', null, 3, 4, '护理卡', 498.00, 60.00, 438.00, 438.00, 0.00, 0.00, '微信', 0, 1, 1, dateadd('DAY', -4, current_timestamp), '补水疗程', dateadd('DAY', -4, current_timestamp), dateadd('DAY', -4, current_timestamp));

merge into inventory_item (id, name, category, unit, quantity, safety_stock, cost_price, supplier, status, remark, create_time, update_time)
key(id) values
(1, '补水面膜', '护理耗材', '盒', 36.00, 10.00, 28.00, '美业供应商A', 1, '常用补水项目耗材', current_timestamp, current_timestamp),
(2, '一次性床单', '消耗品', '包', 18.00, 8.00, 35.00, '门店耗材批发', 1, '每日消耗', current_timestamp, current_timestamp),
(3, '精油套装', '护理耗材', '瓶', 12.00, 5.00, 68.00, '芳疗供应商', 1, '肩颈项目使用', current_timestamp, current_timestamp),
(4, '洁面乳', '产品', '瓶', 24.00, 6.00, 42.00, '护肤供应商B', 1, '可售卖产品', current_timestamp, current_timestamp),
(5, '按摩膏', '护理耗材', '罐', 6.00, 8.00, 55.00, '理疗供应商', 1, '低于安全库存，需补货', current_timestamp, current_timestamp),
(6, '会员护理卡', '卡券', '张', 50.00, 10.00, 3.00, '印刷供应商', 1, '办卡登记使用', current_timestamp, current_timestamp),
(7, '肩颈热敷包', '护理耗材', '个', 9.00, 5.00, 18.00, '理疗供应商', 1, '热敷辅助用品', current_timestamp, current_timestamp);
