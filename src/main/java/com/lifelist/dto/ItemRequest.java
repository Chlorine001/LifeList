package com.lifelist.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

/**
 * 小事创建/更新请求
 * 注意：没有 userId 字段！userId 由后端从 token 里取，
 *       这样用户永远改不了别人的数据（数据隔离的关键）
 */
@Data
public class ItemRequest {

    @NotBlank(message = "阶段不能为空")
    private String stage;

    @NotBlank(message = "小事内容不能为空")
    private String title;

    private String status = "想去做";

    private LocalDate doneDate;

    private String mood;

    private String note;

    private String photo;
}
