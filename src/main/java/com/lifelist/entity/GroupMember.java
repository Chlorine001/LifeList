package com.lifelist.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 组成员实体，对应表 t_group_member
 * 组与朋友的多对多关系
 */
@Data
@TableName("t_group_member")
public class GroupMember {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long groupId;

    private Long friendId;

    private LocalDateTime createdAt;

    /** 好友昵称（联表查出，非数据库字段） */
    @TableField(exist = false)
    private String friendNickname;
}
