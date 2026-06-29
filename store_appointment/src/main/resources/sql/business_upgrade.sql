-- Existing database upgrade for payment/debt and multi-item appointment requirements.
-- Run once against the store_appointment database if these columns do not exist yet.

alter table order_info
    add column card_type varchar(20) not null default '次卡' comment '卡类型：当前只记录次卡',
    add column original_amount decimal(10, 2) not null default 0.00 comment '原价/卡费',
    add column discount_amount decimal(10, 2) not null default 0.00 comment '优惠金额',
    add column paid_amount decimal(10, 2) not null default 0.00 comment '已收金额',
    add column debt_amount decimal(10, 2) not null default 0.00 comment '欠款金额',
    add column monthly_payment decimal(10, 2) not null default 0.00 comment '每月计划还款',
    add column payment_method varchar(20) not null default '现金' comment '支付方式：微信、支付宝、现金、次卡',
    add column debt_status tinyint not null default 0 comment '欠款状态：0无欠款，1分期中，2已结清';

update order_info
set original_amount = amount,
    paid_amount = case when pay_status = 1 then amount else 0 end,
    debt_amount = case when pay_status = 1 then 0 else amount end,
    debt_status = case when pay_status = 1 then 0 else 1 end
where original_amount = 0;

create index idx_order_info_payment_method on order_info (payment_method);
create index idx_order_info_debt_status on order_info (debt_status);

alter table appointment
    add column service_item_count int not null default 1 comment '项目数量：1到6个',
    add column service_items_text varchar(255) null comment '本次预约项目明细';
