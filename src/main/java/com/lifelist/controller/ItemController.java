package com.lifelist.controller;

import com.lifelist.common.R;
import com.lifelist.dto.ItemRequest;
import com.lifelist.entity.Item;
import com.lifelist.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 小事清单接口（需要登录，带 token）
 */
@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    /** 列表，可选 ?stage=xx 按阶段过滤 */
    @GetMapping
    public R<List<Item>> list(@RequestParam(required = false) String stage) {
        return R.ok(itemService.list(stage));
    }

    /** 新增 */
    @PostMapping
    public R<Item> create(@Valid @RequestBody ItemRequest req) {
        return R.ok(itemService.create(req));
    }

    /** 更新 */
    @PutMapping("/{id}")
    public R<Item> update(@PathVariable Long id, @Valid @RequestBody ItemRequest req) {
        return R.ok(itemService.update(id, req));
    }

    /** 删除 */
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        itemService.delete(id);
        return R.ok();
    }

    /** 统计：已珍藏数、总数、完成数 */
    @GetMapping("/stats")
    public R<Map<String, Object>> stats() {
        Map<String, Object> map = new HashMap<>();
        map.put("treasured", itemService.countByStatus("已经珍藏"));
        map.put("done", itemService.countByStatus("已经珍藏"));
        map.put("total", itemService.list(null).size());
        return R.ok(map);
    }
}
