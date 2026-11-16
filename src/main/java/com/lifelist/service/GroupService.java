package com.lifelist.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lifelist.common.BizException;
import com.lifelist.common.UserContext;
import com.lifelist.entity.Group;
import com.lifelist.entity.GroupMember;
import com.lifelist.mapper.GroupMapper;
import com.lifelist.mapper.GroupMemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 分组服务：把朋友拉到一个组
 * 组是每个人自己的（userId 隔离）
 */
@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupMapper groupMapper;
    private final GroupMemberMapper groupMemberMapper;

    /** 创建分组 */
    public Group create(String name) {
        Group g = new Group();
        g.setUserId(UserContext.get());
        g.setName(name);
        groupMapper.insert(g);
        return g;
    }

    /** 我的分组列表 */
    public List<Group> list() {
        return groupMapper.selectList(new LambdaQueryWrapper<Group>()
                .eq(Group::getUserId, UserContext.get()));
    }

    /** 把朋友加入组 */
    public void addMember(Long groupId, Long friendId) {
        Group g = getOwnedGroup(groupId);
        Long exists = groupMemberMapper.selectCount(new LambdaQueryWrapper<GroupMember>()
                .eq(GroupMember::getGroupId, groupId)
                .eq(GroupMember::getFriendId, friendId));
        if (exists != null && exists > 0) {
            throw new BizException("该朋友已在此组");
        }
        GroupMember m = new GroupMember();
        m.setGroupId(groupId);
        m.setFriendId(friendId);
        groupMemberMapper.insert(m);
    }

    /** 组内成员列表 */
    public List<GroupMember> members(Long groupId) {
        getOwnedGroup(groupId);
        return groupMemberMapper.selectList(new LambdaQueryWrapper<GroupMember>()
                .eq(GroupMember::getGroupId, groupId));
    }

    private Group getOwnedGroup(Long groupId) {
        Group g = groupMapper.selectById(groupId);
        if (g == null || !g.getUserId().equals(UserContext.get())) {
            throw new BizException("分组不存在或无权操作");
        }
        return g;
    }
}
