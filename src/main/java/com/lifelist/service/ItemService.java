package com.lifelist.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lifelist.common.BizException;
import com.lifelist.common.UserContext;
import com.lifelist.dto.ItemRequest;
import com.lifelist.entity.Item;
import com.lifelist.mapper.ItemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 小事清单服务
 *
 * 数据隔离的关键：所有查询/更新/删除都强制带上 userId（从 UserContext 取），
 * 这样用户 A 永远操作不到用户 B 的数据。
 */
@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemMapper itemMapper;

    /** 查当前用户的所有小事，可选按阶段过滤 */
    public List<Item> list(String stage) {
        LambdaQueryWrapper<Item> wrapper = new LambdaQueryWrapper<Item>()
                .eq(Item::getUserId, UserContext.get())  // 隔离
                .eq(stage != null && !stage.isEmpty(), Item::getStage, stage)
                .orderByAsc(Item::getId);
        return itemMapper.selectList(wrapper);
    }

    /** 新增一件小事 */
    public Item create(ItemRequest req) {
        Item item = new Item();
        item.setUserId(UserContext.get());  // 隔离：归属当前用户
        applyRequest(item, req);
        itemMapper.insert(item);
        return item;
    }

    /** 更新一件小事（只能改自己的） */
    public Item update(Long id, ItemRequest req) {
        Item item = getOwnedItem(id);
        applyRequest(item, req);
        itemMapper.updateById(item);
        return item;
    }

    /** 删除一件小事（只能删自己的） */
    public void delete(Long id) {
        getOwnedItem(id);  // 先确认是自己的，再删
        itemMapper.deleteById(id);
    }

    /** 统计：按状态计数，供进度条/统计页用 */
    public long countByStatus(String status) {
        return itemMapper.selectCount(new LambdaQueryWrapper<Item>()
                .eq(Item::getUserId, UserContext.get())
                .eq(Item::getStatus, status));
    }

    /** 查一条「属于当前用户」的小事，找不到或不是自己的都抛异常 */
    private Item getOwnedItem(Long id) {
        Item item = itemMapper.selectById(id);
        if (item == null || !item.getUserId().equals(UserContext.get())) {
            throw new BizException("小事不存在或无权操作");
        }
        return item;
    }

    private void applyRequest(Item item, ItemRequest req) {
        item.setStage(req.getStage());
        item.setTitle(req.getTitle());
        item.setStatus(req.getStatus());
        item.setDoneDate(req.getDoneDate());
        item.setMood(req.getMood());
        item.setNote(req.getNote());
        item.setPhoto(req.getPhoto());
    }
}
