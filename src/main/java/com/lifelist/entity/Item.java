package com.lifelist.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 小事清单实体，对应表 t_item
 * userId 是数据隔离字段：谁的小事只能谁查
 */
@Data
@TableName("t_item")
public class Item {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 归属用户 */
    private Long userId;

    /** 人生阶段/分类 */
    private String stage;

    /** 小事内容 */
    private String title;

    /** 状态：想去做/等待发生/已经珍藏/留给以后 */
    private String status;

    /** 完成日期 */
    private LocalDate doneDate;

    /** 心情 */
    private String mood;

    /** 一句话记录 */
    private String note;

    /** 照片 URL */
    private String photo;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
