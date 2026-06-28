create table if not exists inventory_item (
    id bigint not null auto_increment comment '主键',
    name varchar(64) not null comment '库存名称',
    category varchar(64) null comment '分类',
    unit varchar(20) null comment '单位',
    quantity decimal(10, 2) not null default 0 comment '当前库存',
    safety_stock decimal(10, 2) not null default 0 comment '安全库存',
    cost_price decimal(10, 2) not null default 0 comment '成本价',
    supplier varchar(100) null comment '供应商',
    status tinyint not null default 1 comment '状态 0停用 1启用',
    remark varchar(255) null comment '备注',
    create_time datetime null comment '创建时间',
    update_time datetime null comment '更新时间',
    primary key (id),
    key idx_inventory_item_name (name),
    key idx_inventory_item_category (category),
    key idx_inventory_item_status (status)
) comment '库存物品表';
