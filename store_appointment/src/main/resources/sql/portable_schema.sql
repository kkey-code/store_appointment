create table if not exists user (
    id bigint auto_increment primary key,
    username varchar(50) not null unique,
    password varchar(100) not null,
    role varchar(20) not null default 'staff',
    employee_id bigint,
    status tinyint not null default 1,
    create_time datetime not null default current_timestamp,
    update_time datetime not null default current_timestamp
);

create table if not exists employee (
    id bigint auto_increment primary key,
    name varchar(50) not null,
    phone varchar(20),
    gender tinyint,
    position varchar(50),
    status tinyint not null default 1,
    remark varchar(255),
    create_time datetime,
    update_time datetime
);

create table if not exists customer (
    id bigint auto_increment primary key,
    name varchar(50) not null,
    phone varchar(20),
    gender tinyint,
    birthday date,
    level varchar(20),
    source varchar(50),
    remark varchar(255),
    create_time datetime,
    update_time datetime
);

create table if not exists service_item (
    id bigint auto_increment primary key,
    name varchar(100) not null,
    price decimal(10, 2),
    duration int,
    description varchar(255),
    status tinyint not null default 1,
    create_time datetime,
    update_time datetime
);

create table if not exists appointment (
    id bigint auto_increment primary key,
    customer_id bigint,
    employee_id bigint,
    service_item_id bigint,
    service_item_count int default 1,
    service_items_text varchar(500),
    appointment_time datetime,
    status tinyint not null default 0,
    remark varchar(255),
    create_time datetime,
    update_time datetime
);

create table if not exists order_info (
    id bigint auto_increment primary key,
    order_no varchar(50),
    appointment_id bigint,
    customer_id bigint,
    service_item_id bigint,
    card_type varchar(50),
    original_amount decimal(10, 2) default 0,
    discount_amount decimal(10, 2) default 0,
    amount decimal(10, 2) default 0,
    paid_amount decimal(10, 2) default 0,
    debt_amount decimal(10, 2) default 0,
    monthly_payment decimal(10, 2) default 0,
    payment_method varchar(50),
    debt_status tinyint default 0,
    pay_status tinyint default 0,
    order_status tinyint default 0,
    pay_time datetime,
    remark varchar(255),
    create_time datetime,
    update_time datetime
);

create table if not exists inventory_item (
    id bigint auto_increment primary key,
    name varchar(64) not null,
    category varchar(64),
    unit varchar(20),
    quantity decimal(10, 2) not null default 0,
    safety_stock decimal(10, 2) not null default 0,
    cost_price decimal(10, 2) not null default 0,
    supplier varchar(100),
    status tinyint not null default 1,
    remark varchar(255),
    create_time datetime,
    update_time datetime
);

create index if not exists idx_customer_name on customer(name);
create index if not exists idx_customer_phone on customer(phone);
create index if not exists idx_inventory_name on inventory_item(name);
create index if not exists idx_inventory_category on inventory_item(category);
