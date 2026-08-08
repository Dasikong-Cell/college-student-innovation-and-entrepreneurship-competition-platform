package com.competition.platform.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.competition.platform.common.R;
import com.competition.platform.entity.Announcement;
import com.competition.platform.service.CompetitionAnnouncementService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/announcement")
public class CompetitionAnnouncementController {

    private final CompetitionAnnouncementService announcementService;

    public CompetitionAnnouncementController(CompetitionAnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    @GetMapping("/list")
    public R<List<Announcement>> list(@RequestParam(required = false) String category,
                                      @RequestParam(required = false) Integer limit) {
        try {
            return R.ok(announcementService.list(category, limit));
        } catch (Exception e) {
            log.warn("查询公告列表失败: {}", e.getMessage());
            return R.fail(e.getMessage());
        }
    }

    @GetMapping("/page")
    public R<Page<Announcement>> page(@RequestParam(defaultValue = "1") long page,
                                      @RequestParam(defaultValue = "10") long size) {
        try {
            return R.ok(announcementService.page(page, size));
        } catch (Exception e) {
            log.warn("分页查询公告失败: {}", e.getMessage());
            return R.fail(e.getMessage());
        }
    }

    @PostMapping
    public R<Announcement> save(@RequestBody Announcement announcement, HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            return R.ok(announcementService.save(announcement, userId));
        } catch (Exception e) {
            log.warn("发布公告失败: {}", e.getMessage());
            return R.fail(e.getMessage());
        }
    }

    @PutMapping
    public R<Announcement> update(@RequestBody Announcement announcement) {
        try {
            return R.ok(announcementService.update(announcement));
        } catch (Exception e) {
            log.warn("更新公告失败: {}", e.getMessage());
            return R.fail(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public R<String> delete(@PathVariable Long id) {
        try {
            announcementService.delete(id);
            return R.ok("删除成功");
        } catch (Exception e) {
            log.warn("删除公告失败: {}", e.getMessage());
            return R.fail(e.getMessage());
        }
    }
}
