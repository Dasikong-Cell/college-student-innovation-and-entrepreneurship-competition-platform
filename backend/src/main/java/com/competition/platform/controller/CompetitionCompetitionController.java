package com.competition.platform.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.competition.platform.common.R;
import com.competition.platform.entity.Competition;
import com.competition.platform.service.CompetitionCompetitionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/competition")
public class CompetitionCompetitionController {

    private final CompetitionCompetitionService competitionService;

    public CompetitionCompetitionController(CompetitionCompetitionService competitionService) {
        this.competitionService = competitionService;
    }

    @GetMapping("/page")
    public R<Page<Competition>> page(@RequestParam(defaultValue = "1") long page,
                                     @RequestParam(defaultValue = "10") long size,
                                     @RequestParam(required = false) String status,
                                     @RequestParam(required = false) String category,
                                     @RequestParam(required = false) String keyword) {
        try {
            return R.ok(competitionService.page(page, size, status, category, keyword));
        } catch (Exception e) {
            log.warn("分页查询大赛失败: {}", e.getMessage());
            return R.fail(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public R<Competition> getById(@PathVariable Long id) {
        try {
            return R.ok(competitionService.getById(id));
        } catch (Exception e) {
            log.warn("查询大赛详情失败: {}", e.getMessage());
            return R.fail(e.getMessage());
        }
    }

    @PostMapping
    public R<Competition> save(@RequestBody Competition competition, HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            return R.ok(competitionService.save(competition, userId));
        } catch (Exception e) {
            log.warn("创建大赛失败: {}", e.getMessage());
            return R.fail(e.getMessage());
        }
    }

    @PutMapping
    public R<Competition> update(@RequestBody Competition competition) {
        try {
            return R.ok(competitionService.update(competition));
        } catch (Exception e) {
            log.warn("更新大赛失败: {}", e.getMessage());
            return R.fail(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public R<String> delete(@PathVariable Long id) {
        try {
            competitionService.delete(id);
            return R.ok("删除成功");
        } catch (Exception e) {
            log.warn("删除大赛失败: {}", e.getMessage());
            return R.fail(e.getMessage());
        }
    }

    @PutMapping("/{id}/publish")
    public R<Competition> publish(@PathVariable Long id) {
        try {
            return R.ok(competitionService.publish(id));
        } catch (Exception e) {
            log.warn("发布大赛失败: {}", e.getMessage());
            return R.fail(e.getMessage());
        }
    }

    @PutMapping("/{id}/take-down")
    public R<Competition> takeDown(@PathVariable Long id) {
        try {
            return R.ok(competitionService.takeDown(id));
        } catch (Exception e) {
            log.warn("下架大赛失败: {}", e.getMessage());
            return R.fail(e.getMessage());
        }
    }

    @GetMapping("/stats")
    public R<Map<String, Object>> stats() {
        try {
            return R.ok(competitionService.dashboardStats());
        } catch (Exception e) {
            log.warn("获取大赛统计失败: {}", e.getMessage());
            return R.fail(e.getMessage());
        }
    }

    @GetMapping("/categories")
    public R<List<String>> categories() {
        try {
            return R.ok(competitionService.listCategories());
        } catch (Exception e) {
            log.warn("获取分类列表失败: {}", e.getMessage());
            return R.fail(e.getMessage());
        }
    }
}
