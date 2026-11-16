package com.lifelist.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lifelist.common.BizException;
import com.lifelist.common.UserContext;
import com.lifelist.entity.Group;
import com.lifelist.entity.GroupMember;
import com.lifelist.entity.User;
import com.lifelist.mapper.GroupMapper;
import com.lifelist.mapper.GroupMemberMapper;
import com.lifelist.mapper.UserMapper;
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
    private final UserMapper userMapper;

    /** 创建分组 */
    public Group create(String name) {
        Group g = new Group();
        g.setUserId(UserContext.get());
        g.setName(name);
        groupMapper.insert(g);
        return g;
    }

    /** 重命名分组 */
    public void rename(Long groupId, String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new BizException("组名不能为空");
        }
        Group g = getOwnedGroup(groupId);
        g.setName(name.trim());
        groupMapper.updateById(g);
    }

    /** 删除分组（连同成员一起删） */
    public void delete(Long groupId) {
        getOwnedGroup(groupId);
        groupMemberMapper.delete(new LambdaQueryWrapper<GroupMember>()
                .eq(GroupMember::getGroupId, groupId));
        groupMapper.deleteById(groupId);
    }

    /** 我的分组列表（带成员数） */
    public List<Group> list() {
        List<Group> list = groupMapper.selectList(new LambdaQueryWrapper<Group>()
                .eq(Group::getUserId, UserContext.get()));
        for (Group g : list) {
            g.setMemberCount(Math.toIntExact(groupMemberMapper.selectCount(
                    new LambdaQueryWrapper<GroupMember>().eq(GroupMember::getGroupId, g.getId()))));
        }
        return list;
    }

    /** 把朋友加入组 */
    public void addMember(Long groupId, Long friendId) {
        getOwnedGroup(groupId);
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

    /** 把朋友移出组 */
    public void removeMember(Long groupId, Long friendId) {
        getOwnedGroup(groupId);
        groupMemberMapper.delete(new LambdaQueryWrapper<GroupMember>()
                .eq(GroupMember::getGroupId, groupId)
                .eq(GroupMember::getFriendId, friendId));
    }

    /** 组内成员列表（带昵称） */
    public List<GroupMember> members(Long groupId) {
        getOwnedGroup(groupId);
        List<GroupMember> list = groupMemberMapper.selectList(new LambdaQueryWrapper<GroupMember>()
                .eq(GroupMember::getGroupId, groupId));
        for (GroupMember m : list) {
            User u = userMapper.selectById(m.getFriendId());
            if (u != null) {
                m.setFriendNickname(u.getNickname() != null && !u.getNickname().isEmpty()
                        ? u.getNickname() : u.getUsername());
            }
        }
        return list;
    }

    private Group getOwnedGroup(Long groupId) {
        Group g = groupMapper.selectById(groupId);
        if (g == null || !g.getUserId().equals(UserContext.get())) {
            throw new BizException("分组不存在或无权操作");
        }
        return g;
    }
}
