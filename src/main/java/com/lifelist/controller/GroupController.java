package com.lifelist.controller;

import com.lifelist.common.R;
import com.lifelist.entity.Group;
import com.lifelist.entity.GroupMember;
import com.lifelist.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 分组接口（需要登录）
 */
@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    /** 创建分组 */
    @PostMapping
    public R<Group> create(@RequestBody Map<String, String> body) {
        return R.ok(groupService.create(body.get("name")));
    }

    /** 我的分组列表 */
    @GetMapping
    public R<List<Group>> list() {
        return R.ok(groupService.list());
    }

    /** 把朋友加入组 */
    @PostMapping("/{groupId}/members")
    public R<Void> addMember(@PathVariable Long groupId, @RequestBody Map<String, Long> body) {
        groupService.addMember(groupId, body.get("friendId"));
        return R.ok();
    }

    /** 组内成员 */
    @GetMapping("/{groupId}/members")
    public R<List<GroupMember>> members(@PathVariable Long groupId) {
        return R.ok(groupService.members(groupId));
    }
}
