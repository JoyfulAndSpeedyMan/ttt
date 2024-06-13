create schema erp collate utf8mb4_unicode_ci;
create table user
(
    id        bigint                              not null
        primary key,
    username  varchar(20) charset utf8mb3         null,
    mobile    varchar(20)                         null,
    nickname  varchar(50) charset utf8mb3         null,
    avatar    varchar(255)                        null,
    gender    tinyint                             null comment '性别，0: 未知；1:男性；2:女性',
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
    on app_secret (platform_id)