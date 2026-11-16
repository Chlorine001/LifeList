package com.lifelist.controller;

import com.lifelist.common.R;
import com.lifelist.dto.FriendRequest;
import com.lifelist.entity.Friend;
import com.lifelist.service.FriendService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 朋友关系接口（需要登录）
 */
@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    /** 加好友（按用户名） */
    @PostMapping
    public R<Void> add(@Valid @RequestBody FriendRequest req) {
        friendService.add(req.getFriendUsername());
        return R.ok();
    }

    /** 我的好友列表 */
    @GetMapping
    public R<List<Friend>> list() {
        return R.ok(friendService.list());
    }

    /** 待我审核的申请 */
    @GetMapping("/pending")
    public R<List<Friend>> pending() {
        return R.ok(friendService.pending());
    }

    /** 通过申请 */
    @PostMapping("/{id}/accept")
    public R<Void> accept(@PathVariable Long id) {
        friendService.accept(id);
        return R.ok();
    }

    /** 拒绝申请 */
    @PostMapping("/{id}/reject")
    public R<Void> reject(@PathVariable Long id) {
        friendService.reject(id);
        return R.ok();
    }
}
