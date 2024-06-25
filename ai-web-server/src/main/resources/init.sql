create table user
(
    id        bigint                              not null
        primary key,
    username  varchar(20)                         null,
    password  varchar(20)                         null,
    mobile    varchar(20)                         null,
    nickname  varchar(50)                         null,
    avatar    varchar(255)                        null,
    gender    tinyint                             null comment '性别，0: 未知；1:男性；2:女性',

    temp_role tinyint                             not null comment '临时的角色字段，判断是不是管理员，早晚得去掉',
    create_at timestamp default CURRENT_TIMESTAMP not null,
    update_at timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    constraint user_name_inx
        unique (username)
);

create table app_secret
(
    id          bigint                              not null
        primary key,
    platform_id varchar(30)                         not null comment '平台id',
    app_key     varchar(60)                         not null,
    secret      varchar(60)                         not null,
    algorithm   varchar(20)                         null,
    status      tinyint                             not null comment '1: 有效',
    create_at   timestamp default CURRENT_TIMESTAMP null,
    update_at   timestamp default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP
)
    comment '秘钥' charset = utf8mb4;

create index app_key
    on app_secret (app_key);

create index platform_id
    on app_secret (platform_id);

create table ai_picture_process
(
    id             bigint                                                 not null
        primary key,
    processor_name varchar(50)                                            not null comment 'pcr: 商品图重绘',
    params         json                                                   not null comment '处理所需的参数',
    result         json comment '处理结果',
    state          tinyint comment '-11：重试达到最大次数，-1：失败，0: 待处理，1：成功，11:失败重试中',
    fail_number    tinyint comment '失败的次数' default 0,
    fail_detail    json comment '失败的详情信息，数组格式（可能有多次尝试）',
    fail_strategy  varchar(20)                  default 'none' comment '处理失败时的策略，none: 不重试，retry_3: 重试2次（加上第一次失败3次）',
    create_at      timestamp                    default CURRENT_TIMESTAMP null,
    update_at      timestamp                    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP
);
#
# create table product
# (
#     id        bigint                              not null primary key,
#     price     decimal comment '商品价格，有多个sku的时会同步第一个sku的价格',
#     attr      json                                not null comment '商品属性',
#     create_at timestamp default CURRENT_TIMESTAMP null,
#     update_at timestamp default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP
# );
# create table product_sku
# (
#     id         bigint                              not null primary key,
#     product_id bigint                              not null,
#     sku_name   varchar(20)                         not null,
#     price      decimal comment '该sku的价格',
#     create_at  timestamp default CURRENT_TIMESTAMP null,
#     update_at  timestamp default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP
# );
#
# create table product_attr_dict
# (
#     id              bigint                              not null primary key,
#     attr_name       varchar(20) comment '属性名字',
#     type            varchar(10) comment '属性类型',
#     optional_values json comment '',
#     create_at       timestamp default CURRENT_TIMESTAMP null,
#     update_at       timestamp default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP
# );
#
# create table product_category
# (
#     id bigint not null primary key,
#     parent_id bigint not null comment '根级分类是0',
#
# )