package com.lifelist.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 好友申请请求
 */
@Data
public class FriendRequest {

    /** 对方用户名（比 friendId 更友好，用户能记住） */
    @NotBlank(message = "对方用户名不能为空")
    private String friendUsername;
}
