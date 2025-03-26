create table users
(
    id              int unsigned primary key auto_increment comment '主键id',
    name            varchar(64)        not null,
    password        varchar(120)       null comment '密码',
    position        varchar(10)        null comment '职位',
    phone           varchar(11) unique null comment '手机号',
    administrator   tinyint(1) default 0 comment '是否超管',
    sso_token       text               null comment '单点登录token',
    sso_expire_time datetime           null comment 'token过期时间',
    created_time    datetime           null comment '创建时间',
    modified_time   datetime           null comment '修改时间',
    deleted_time    datetime           null comment '删除时间'
);

create table log_operations
(
    id           int unsigned not null primary key auto_increment,
    user_id      int unsigned not null comment '用户id',
    user_agent   text         null comment '用户代理',
    ip           varchar(100) null comment 'ip',
    content      text         null comment '操作内容',
    created_time datetime     null comment '创建内容',
    constraint log_operations_user_id_foreign
        foreign key (user_id) references users (id)
)
    comment '操作日志';


CREATE TABLE supplier_info
(
    id                      INT AUTO_INCREMENT COMMENT '主键ID',
    supplier_name           VARCHAR(32) unique NOT NULL COMMENT '供货商名称',
    supplier_contact_person VARCHAR(255) COMMENT '供货商联系人姓名',
    supplier_contact        VARCHAR(11)        NOT NULL COMMENT '供货商联系方式',
    supplier_address        VARCHAR(32) COMMENT '供货商地址',
    created_time            DATETIME COMMENT '创建时间',
    modified_time           DATETIME COMMENT '修改时间',
    deleted_time            DATETIME COMMENT '删除时间',
    bank_name               VARCHAR(32) COMMENT '开户银行',
    bank_account            VARCHAR(32) COMMENT '银行账号',
    PRIMARY KEY (id)
) COMMENT ='供货商信息表';

CREATE TABLE material_inbound_record
(
    id                   INT AUTO_INCREMENT COMMENT '主键ID',
    material_name        VARCHAR(20)    NOT NULL COMMENT '原料名',
    quantity             int            NOT NULL COMMENT '数量',
    unit                 VARCHAR(50)    not null                  default 'KG' COMMENT '单位',
    tax                  int            null comment '税率',
    unit_price           DECIMAL(10, 2) NOT NULL COMMENT '含税单价',
    total_price          DECIMAL(10, 2) NOT NULL COMMENT '含税总金额',
    supplier_id          INT            NOT NULL COMMENT '供货商ID',
    order_initiated_time DATETIME       NOT NULL COMMENT '订单发起时间',
    inbound_time         DATETIME       NULL COMMENT '入库时间',
    remark               TEXT           null COMMENT '备注',
    order_contract       varchar(255)   null comment '订单合同',
    material_left        int            NULL COMMENT '原料剩余',
    status               enum ('未入库','已入库','使用中','用尽') default '未入库' comment '状态',
    created_time         DATETIME COMMENT '创建时间',
    modified_time        DATETIME COMMENT '修改时间',
    deleted_time         DATETIME COMMENT '删除时间',
    PRIMARY KEY (id)
) COMMENT ='原料入库记录表';

create table info_categories
(
    id            int unsigned auto_increment primary key,
    search        varchar(40)              not null,
    name          varchar(40) unique       not null,
    type          smallint unsigned        not null comment '字典名',
    display_order int unsigned default '0' not null comment '排序',
    description   text                     null,
    data          json                     null,
    module        varchar(255)             null comment '字典类别'
) comment '类别-无嵌套';

CREATE TABLE produce_material
(
    id                         INT unsigned AUTO_INCREMENT primary key COMMENT '主键ID',
    material_inbound_record_id int            not null comment '原料id',
    material_name              varchar(20)    not null comment '原料名',
    supplier_id                INT            NOT NULL COMMENT '供货商ID',
    produce_record_id          INT            NOT NULL COMMENT '生产记录ID',
    quantity_used              int            NOT NULL COMMENT '使用数量',
    unit                       VARCHAR(50)    not null default 'KG' COMMENT '单位',
    cost_per_unit              DECIMAL(10, 2) NOT NULL COMMENT '单位成本',
    total_cost                 DECIMAL(10, 2) not null COMMENT '该项成本总计',
    created_time               datetime       null comment '创建时间',
    modified_time              datetime       null comment '修改时间',
    deleted_time               datetime       null comment '删除时间'
) COMMENT ='生产原料使用记录';

create table produce_record
(
    id               INT unsigned AUTO_INCREMENT primary key COMMENT '主键ID',
    product_name     varchar(32)             not null comment '产品名',
    unit             VARCHAR(50)             not null default 'KG' COMMENT '单位',
    produce_quantity int                     not null comment '该次生产数量',
    total_cost       DECIMAL(10, 2) unsigned not null comment '该次生产的产品总成本',
    left_quantity    int                     not null default 0 comment '剩余数量 为出库准备',
    remark           TEXT                    null COMMENT '备注',
    created_time     datetime                null comment '创建时间',
    modified_time    datetime                null comment '修改时间',
    deleted_time     datetime                null comment '删除时间'
) comment '生产记录';

create table employees
(
    id                  int unsigned primary key auto_increment comment '主键id',
    id_card             varchar(18) unique not null comment '身份证',
    name                varchar(10)        not null,
    gender              enum ('男', '女')  not null comment '性别',
    nation              varchar(10)        null comment '民族',
    position            varchar(10)        null comment '职位',
    native_place        varchar(30)        null comment '籍贯',
    phone               varchar(11) unique null comment '手机号',
    base_salary         DECIMAL(10, 2) COMMENT '员工底薪',
    contract_start_date datetime           null comment '合同起始日期',
    contract_end_date   datetime           null comment '合同结束日期',
    created_time        datetime           null comment '创建时间',
    modified_time       datetime           null comment '修改时间',
    deleted_time        datetime           null comment '删除时间'
) comment '员工表';

create table employees_salary
(
    id               int unsigned primary key auto_increment comment '主键id',
    employees_id     int unsigned   not null comment '员工id',
    salary_year      int(4)         not null comment '结算年份',
    salary_mouth     int(2)         not null null comment '结算月份',
    allowance        DECIMAL(10, 2) DEFAULT 0 COMMENT '补贴（餐补、交通补等）',
    performance      DECIMAL(10, 2) DEFAULT 0 COMMENT '业绩提成',
    overtime_pay     DECIMAL(10, 2) DEFAULT 0 COMMENT '奖金',
    absence          DECIMAL(10, 2) default 0 comment '缺勤',
    gross_salary     DECIMAL(10, 2) default 0 comment '应发工资',
    social_insurance DECIMAL(10, 2) DEFAULT 0 COMMENT '社保扣除',
    loan_deduction   DECIMAL(10, 2) DEFAULT 0 comment '借扣款',
    tax              DECIMAL(10, 2) DEFAULT 0 COMMENT '个税扣除',
    net_salary       DECIMAL(10, 2) NOT NULL COMMENT '实际到手薪资（计算字段）',
    created_time     datetime       null comment '创建时间',
    modified_time    datetime       null comment '修改时间',
    deleted_time     datetime       null comment '删除时间',
    unique (employees_id, salary_year, salary_mouth)
) comment '员工薪资结算表';

CREATE TABLE customer_info
(
    id               INT AUTO_INCREMENT primary key COMMENT '主键',
    company_name     VARCHAR(32) unique NOT NULL COMMENT '客户公司名称',
    shipping_address TEXT               NOT NULL COMMENT '收货地址',
    contact_person   VARCHAR(32)        NOT NULL COMMENT '联系人姓名',
    contact_phone    VARCHAR(15) unique NOT NULL COMMENT '联系电话',
    email            VARCHAR(255) unique COMMENT '联系人电子邮箱地址',
    fax              VARCHAR(20) unique COMMENT '传真号码',
    office_phone     VARCHAR(15) unique COMMENT '办公电话号码',
    created_time     datetime           null comment '创建时间',
    modified_time    datetime           null comment '修改时间',
    deleted_time     datetime           null comment '删除时间'
) COMMENT ='客户信息表';

create table `orders`
(
    id                  INT AUTO_INCREMENT primary key COMMENT '主键',
    customer_info_id    int            not null comment '客户id',
    order_num           varchar(12)  unique  not null comment '合同编号',
    tax                 enum ('含13%增值税','不含税') comment '是否含税',
    payment_method      enum ('现金','月结30天') comment '付款方式',
    paid_amount         DECIMAL(10, 2) not null default 0 comment '已结',
    order_creation_time datetime       not null comment '订单创建时间',
    #     contract_image      varchar(255)   null comment '合同图片',
    created_time        datetime       not null comment '创建时间',
    modified_time       datetime       null comment '修改时间',
    deleted_time        datetime       null comment '删除时间'
) engine = InnoDB
  default CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci comment '订单';


create table order_product
(
    id                INT AUTO_INCREMENT primary key COMMENT '主键',
    order_id          int            not null comment '订单id',
    product_name      varchar(32)    not null comment '产品名',
    quantity          int            not null comment '数量',
    unit              VARCHAR(50)    not null default 'KG' COMMENT '单位',
    unit_price        DECIMAL(10, 2) NOT NULL COMMENT '单价',
    outbound_quantity int            not null default 0 comment '已出库数量',
    created_time      datetime       not null comment '创建时间',
    modified_time     datetime       null comment '修改时间',
    deleted_time      datetime       null comment '删除时间',
    INDEX idx_order_id (order_id)
) engine = InnoDB
  default charset = utf8mb4
  collate = utf8mb4_unicode_ci comment '订单-产品';

CREATE TABLE product_outbound_record
(
    id                INT AUTO_INCREMENT COMMENT '主键ID',
    produce_record_id INT         NOT NULL COMMENT '产品入库记录id',
    order_product_id  int         not null comment '订单产品表id',
    quantity          int         not null comment '数量',
    unit              VARCHAR(50) not null default 'KG' COMMENT '单位',
    outbound_time     datetime    not null comment '出库时间',
    created_time      DATETIME COMMENT '创建时间',
    modified_time     DATETIME COMMENT '修改时间',
    deleted_time      DATETIME COMMENT '删除时间',
    PRIMARY KEY (id),
    INDEX idx_order_id (order_product_id),
    index idx_material_inbound_record_id (produce_record_id)
) engine = InnoDB
  default charset = utf8mb4
  collate = utf8mb4_unicode_ci COMMENT ='产品出库记录表';

create table order_receipt
(
    id              int auto_increment primary key comment '主键id',
    order_id        int      not null comment '订单id',
    amount_received decimal(10, 2) comment '收款金额',
    receipt_time    datetime not null comment '收款时间',
    created_time    DATETIME COMMENT '创建时间',
    modified_time   DATETIME COMMENT '修改时间',
    deleted_time    DATETIME COMMENT '删除时间',
    index idx_order_id (order_id)
) engine = InnoDB
  default charset = utf8mb4
  collate = utf8mb4_unicode_ci COMMENT ='订单收款';

insert into info_categories(search, name, type, display_order, description, data, module)
values ('KG', '千克', 1, 1, '千克', null, '数量单位'),
       ('t', '吨', 1, 2, '吨', null, '数量单位');
insert into info_categories(search, name, type, display_order, description, data, module)
values ('yl1', '原料1', 2, 1, '原料1', null, '原料'),
       ('yl2', '原料2', 2, 2, '原料2', null, '原料'),
       ('yl3', '原料3', 2, 3, '原料3', null, '原料'),
       ('yl4', '原料4', 2, 4, '原料4', null, '原料'),
       ('cp1', '产品1', 3, 1, '产品1', null, '产品'),
       ('cp2', '产品2', 3, 2, '产品2', null, '产品'),
       ('cp3', '产品3', 3, 3, '产品3', null, '产品');
insert into info_categories(search, name, type, display_order, description, data, module)
values ('hzzs', '含13%增值税', 4, 1, '是否含税', null, '订单'),
       ('bhs', '不含税', 4, 2, '是否含税', null, '订单'),
       ('xj', '现金', 5, 1, '付款方式', null, '订单'),
       ('yj', '月结30天', 5, 2, '付款方式', null, '订单');
insert into info_categories(search, name, type, display_order, description, data, module)
values ('2150AS', '2150AS', 3, 4, '硅胶', null, '产品'),
       ('2170AS', '2170AS', 3, 5, '硅胶', null, '产品');