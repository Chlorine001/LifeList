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

    /** 重命名分组 */
    @PutMapping("/{groupId}")
    public R<Void> rename(@PathVariable Long groupId, @RequestBody Map<String, String> body) {
        groupService.rename(groupId, body.get("name"));
        return R.ok();
    }

    /** 删除分组 */
    @DeleteMapping("/{groupId}")
    public R<Void> delete(@PathVariable Long groupId) {
        groupService.delete(groupId);
        return R.ok();
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

    /** 把朋友移出组 */
    @DeleteMapping("/{groupId}/members/{friendId}")
    public R<Void> removeMember(@PathVariable Long groupId, @PathVariable Long friendId) {
        groupService.removeMember(groupId, friendId);
        return R.ok();
    }

    /** 组内成员 */
    @GetMapping("/{groupId}/members")
    public R<List<GroupMember>> members(@PathVariable Long groupId) {
        return R.ok(groupService.members(groupId));
    }
}
