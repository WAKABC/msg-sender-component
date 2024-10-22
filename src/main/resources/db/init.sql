-- 创建本地消息表
DROP TABLE IF EXISTS t_msg;
CREATE TABLE IF NOT EXISTS t_msg
(
    id               VARCHAR(32) NOT NULL PRIMARY KEY COMMENT '消息id',
    exchange         VARCHAR(100) COMMENT '交换机',
    routing_key      VARCHAR(100) COMMENT '路由key',
    body_json        TEXT        NOT NULL COMMENT '消息体,json格式',
    STATUS           SMALLINT    NOT NULL DEFAULT 0 COMMENT '消息状态，0：待投递到mq，1：投递成功，2：投递失败',
    expect_send_time DATETIME    NOT NULL COMMENT '消息期望投递时间，大于当前时间，则为延迟消息，否则会立即投递',
    actual_send_time DATETIME COMMENT '消息实际投递时间',
    create_time      DATETIME COMMENT '创建时间',
    fail_msg         TEXT COMMENT 'status=2 时，记录消息投递失败的原因',
    fail_count       INT         NOT NULL DEFAULT 0 COMMENT '已投递失败次数',
    send_retry       SMALLINT    NOT NULL DEFAULT 1 COMMENT '投递MQ失败了，是否还需要重试？1：是，0：否',
    next_retry_time  DATETIME COMMENT '投递失败后，下次重试时间',
    update_time      DATETIME COMMENT '最近更新时间',
    KEY idx_status (STATUS)
) COMMENT '本地消息表';


-- 幂等辅助表,用mysql的唯一性约束判断是否重复消费
DROP TABLE IF EXISTS t_idempotent;
CREATE TABLE IF NOT EXISTS t_idempotent
(
    id             VARCHAR(50) PRIMARY KEY COMMENT 'id，主键',
    idempotent_key VARCHAR(500) NOT NULL COMMENT '需要确保幂等的key',
    UNIQUE KEY uq_idempotent_key (idempotent_key)
) COMMENT '幂等辅助表';


-- 创建消息和消费者关联表
-- （producer, producer_bus_id, consumer_class_name）相同时，
-- 此表只会产生一条记录，就是同一条消息被同一个消费者消费，此表只会产生一条记录
DROP TABLE IF EXISTS t_msg_consume;
CREATE TABLE IF NOT EXISTS t_msg_consume
(
    id              VARCHAR(32)  NOT NULL PRIMARY KEY COMMENT '消息id',
    producer        VARCHAR(100) NOT NULL COMMENT '生产者名称',
    producer_bus_id VARCHAR(100) NOT NULL COMMENT '生产者这边消息的唯一标识',
    consumer_class_name        VARCHAR(300) NOT NULL COMMENT '消费者完整类名',
    queue_name      VARCHAR(100) NOT NULL COMMENT '队列名称',
    body_json       TEXT         NOT NULL COMMENT '消息体,json格式',
    STATUS          SMALLINT     NOT NULL DEFAULT 0 COMMENT '消息状态，0：待消费，1：消费成功，2：消费失败',
    create_time     DATETIME COMMENT '创建时间',
    fail_msg        TEXT COMMENT 'status=2 时，记录消息消费失败的原因',
    fail_count      INT          NOT NULL DEFAULT 0 COMMENT '已投递失败次数',
    consume_retry   SMALLINT     NOT NULL DEFAULT 1 COMMENT '消费失败后，是否还需要重试？1：是，0：否',
    next_retry_time DATETIME COMMENT '投递失败后，下次重试时间',
    update_time     DATETIME COMMENT '最近更新时间',
    KEY idx_status (STATUS),
    UNIQUE uq_msg (producer, producer_bus_id, consumer_class_name)
) COMMENT '消息和消费者关联表';



-- 消息消费记录表，它和t_msg_consume表是1对多的关系，不管消费成功失败，消费一次记录一次
-- 主要是为了方便排错，记录下出错原因，'status=2 时，记录消息消费失败的原因'。
DROP TABLE IF EXISTS t_msg_consume_log;
CREATE TABLE IF NOT EXISTS t_msg_consume_log
(
    id              VARCHAR(32)  NOT NULL PRIMARY KEY COMMENT '消息id',
    msg_consume_id        VARCHAR(32) NOT NULL COMMENT '消息和消费者关联记录',
    STATUS          SMALLINT     NOT NULL DEFAULT 0 COMMENT '消费状态，1：消费成功，2：消费失败',
    create_time     DATETIME COMMENT '创建时间',
    fail_msg        TEXT COMMENT 'status=2 时，记录消息消费失败的原因',
    KEY idx_msg_consume_id (msg_consume_id)
) COMMENT '消息消费日志';



-- 顺序消息编号生成器
drop table if exists t_sequential_msg_number_generator;
create table if not exists t_sequential_msg_number_generator
(
    id        varchar(50) primary key comment 'id，主键',
    group_id  varchar(256) not null comment '组id',
    numbering bigint       not null comment '消息编号',
    version   bigint       not null default 0 comment '版本号，每次更新+1',
    unique key uq_group_id (group_id)
) comment '顺序消息排队表';

-- 顺序消息消费信息表，(group_id、queue_name)中的消息消费到哪里了？
drop table if exists t_sequential_msg_consume_position;
create table if not exists t_sequential_msg_consume_position
(
    id         varchar(50) primary key comment 'id，主键',
    group_id   varchar(256) not null comment '组id',
    queue_name varchar(100) not null comment '队列名称',
    consume_numbering  bigint   default 0   not null comment '当前消费位置的编号',
    version   bigint       not null default 0 comment '版本号，每次更新+1',
    unique key uq_group_queue (group_id, queue_name)
) comment '顺序消息消费信息表';

-- 顺序消息排队表
drop table if exists t_sequential_msg_queue;
create table if not exists t_sequential_msg_queue
(
    id          varchar(50) primary key comment 'id，主键',
    group_id    varchar(256) not null comment '组id',
    numbering   bigint       not null comment '消息编号',
    queue_name  varchar(100) not null comment '队列名称',
    msg_json    text         not null comment '消息json格式',
    create_time datetime comment '创建时间',
    unique key uq_group_number_queue (group_id, numbering, queue_name)
) comment '顺序消息排队表';