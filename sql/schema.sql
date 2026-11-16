-- ============================================================
-- 人生清单（LifeList）数据库建表脚本
-- 数据库：MySQL 8
-- 说明：这是「朋友各自登录记账」的完整表结构，
--       核心是每条业务数据都带 user_id（数据隔离的关键）
-- ============================================================

-- 1) 先建数据库
CREATE DATABASE IF NOT EXISTS `life_list`
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE `life_list`;

-- ============================================================
-- 用户表：每个人一个账号，登录凭证
-- ============================================================
CREATE TABLE IF NOT EXISTS `t_user` (
  `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username`      VARCHAR(64)  NOT NULL COMMENT '登录名（唯一）',
  `password`      VARCHAR(128) NOT NULL COMMENT 'BCrypt 加密后的密码',
  `nickname`      VARCHAR(64)  DEFAULT NULL COMMENT '昵称/显示名',
  `avatar`        VARCHAR(255) DEFAULT NULL COMMENT '头像 URL',
  `created_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`       TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除 0未删 1已删',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ============================================================
-- 小事清单表：每件小事，带 user_id 做数据隔离
-- ============================================================
CREATE TABLE IF NOT EXISTS `t_item` (
  `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id`       BIGINT       NOT NULL COMMENT '归属用户（数据隔离字段）',
  `stage`         VARCHAR(64)  NOT NULL COMMENT '人生阶段/分类，如：大学、初入社会',
  `title`         VARCHAR(255) NOT NULL COMMENT '小事内容',
  `status`        VARCHAR(32)  NOT NULL DEFAULT '想去做' COMMENT '状态：想去做/等待发生/已经珍藏/留给以后',
  `done_date`     DATE         DEFAULT NULL COMMENT '完成日期',
  `mood`          VARCHAR(32)  DEFAULT NULL COMMENT '心情',
  `note`          VARCHAR(500) DEFAULT NULL COMMENT '一句话记录',
  `photo`         VARCHAR(255) DEFAULT NULL COMMENT '照片 URL',
  `created_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`       TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_user_stage` (`user_id`, `stage`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='小事清单表';

-- ============================================================
-- 朋友关系表：谁和谁是朋友（好友申请 + 审核）
-- ============================================================
CREATE TABLE IF NOT EXISTS `t_friend` (
  `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id`       BIGINT       NOT NULL COMMENT '发起方（我）',
  `friend_id`     BIGINT       NOT NULL COMMENT '对方用户 id',
  `status`        VARCHAR(16)  NOT NULL DEFAULT 'pending' COMMENT 'pending待审核 / accepted已通过 / rejected已拒绝',
  `created_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  `updated_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`       TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_friend` (`friend_id`),
  UNIQUE KEY `uk_user_friend` (`user_id`, `friend_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='朋友关系表';

-- ============================================================
-- 分组表：把朋友拉到一个组（如「大学同学」）
-- ============================================================
CREATE TABLE IF NOT EXISTS `t_group` (
  `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id`       BIGINT       NOT NULL COMMENT '归属用户（组是每个人自己的）',
  `name`          VARCHAR(64)  NOT NULL COMMENT '组名',
  `created_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`       TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分组表';

-- ============================================================
-- 组成员表：组与朋友的多对多关系
-- ============================================================
CREATE TABLE IF NOT EXISTS `t_group_member` (
  `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `group_id`      BIGINT       NOT NULL COMMENT '组 id',
  `friend_id`     BIGINT       NOT NULL COMMENT '朋友（用户）id',
  `created_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
  PRIMARY KEY (`id`),
  KEY `idx_group` (`group_id`),
  UNIQUE KEY `uk_group_friend` (`group_id`, `friend_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='组成员表';
