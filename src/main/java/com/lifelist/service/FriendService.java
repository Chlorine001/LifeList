package com.lifelist.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lifelist.common.BizException;
import com.lifelist.common.UserContext;
import com.lifelist.entity.Friend;
import com.lifelist.entity.User;
import com.lifelist.mapper.FriendMapper;
import com.lifelist.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 朋友关系服务
 * 加好友是「双向」的：A 申请加 B，B 审核通过后，双方各有一条记录
 */
@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendMapper friendMapper;
    private final UserMapper userMapper;

    /** 我申请加某人为好友（按用户名找对方） */
    public void add(String friendUsername) {
        Long myId = UserContext.get();
        User target = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, friendUsername));
        if (target == null) {
            throw new BizException("找不到该用户");
        }
        if (target.getId().equals(myId)) {
            throw new BizException("不能加自己为好友");
        }
        // 查是否已存在关系
        Long exists = friendMapper.selectCount(new LambdaQueryWrapper<Friend>()
                .eq(Friend::getUserId, myId)
                .eq(Friend::getFriendId, target.getId()));
        if (exists != null && exists > 0) {
            throw new BizException("已是好友或已发送申请");
        }
        // 插入我的「待审核」申请
        Friend f = new Friend();
        f.setUserId(myId);
        f.setFriendId(target.getId());
        f.setStatus("pending");
        friendMapper.insert(f);
    }

    /** 我的好友列表（已通过），带对方昵称 */
    public List<Friend> list() {
        return friendMapper.selectList(new LambdaQueryWrapper<Friend>()
                .eq(Friend::getUserId, UserContext.get())
                .eq(Friend::getStatus, "accepted"));
    }

    /** 待我审核的申请（别人申请加我，我看到的是 friendId 指向我的 pending 记录） */
    public List<Friend> pending() {
        return friendMapper.selectList(new LambdaQueryWrapper<Friend>()
                .eq(Friend::getFriendId, UserContext.get())
                .eq(Friend::getStatus, "pending"));
    }

    /** 审核通过：把「对方申请加我」这条改成 accepted，同时给我补一条反向 accepted */
    public void accept(Long friendRequestId) {
        Friend pending = friendMapper.selectById(friendRequestId);
        if (pending == null || !pending.getFriendId().equals(UserContext.get())) {
            throw new BizException("申请不存在或无权处理");
        }
        pending.setStatus("accepted");
        friendMapper.updateById(pending);

        // 反向也加一条，保证双向都能看到对方
        Long exists = friendMapper.selectCount(new LambdaQueryWrapper<Friend>()
                .eq(Friend::getUserId, pending.getFriendId())
                .eq(Friend::getFriendId, pending.getUserId()));
        if (exists == null || exists == 0) {
            Friend reverse = new Friend();
            reverse.setUserId(pending.getFriendId());
            reverse.setFriendId(pending.getUserId());
            reverse.setStatus("accepted");
            friendMapper.insert(reverse);
        }
    }

    /** 拒绝 */
    public void reject(Long friendRequestId) {
        Friend pending = friendMapper.selectById(friendRequestId);
        if (pending == null || !pending.getFriendId().equals(UserContext.get())) {
            throw new BizException("申请不存在或无权处理");
        }
        pending.setStatus("rejected");
        friendMapper.updateById(pending);
    }
}
