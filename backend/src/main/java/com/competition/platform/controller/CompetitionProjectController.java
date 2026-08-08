package com.competition.platform.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.competition.platform.common.R;
import com.competition.platform.entity.Project;
import com.competition.platform.service.CompetitionProjectService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/project")
public class CompetitionProjectController {

    private final CompetitionProjectService projectService;

    public CompetitionProjectController(CompetitionProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/page")
    public R<Page<Project>> page(@RequestParam(defaultValue = "1") long page,
                                 @RequestParam(defaultValue = "10") long size,
                                 @RequestParam(required = false) Long competitionId,
                                 @RequestParam(required = false) String status,
                                 @RequestParam(required = false) String keyword) {
        try {
            return R.ok(projectService.page(page, size, competitionId, status, keyword));
        } catch (Exception e) {
            log.warn("分页查询项目失败: {}", e.getMessage());
            return R.fail(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public R<Project> getById(@PathVariable Long id) {
        try {
            return R.ok(projectService.getById(id));
        } catch (Exception e) {
            log.warn("查询项目详情失败: {}", e.getMessage());
            return R.fail(e.getMessage());
        }
    }

    @PostMapping
    public R<Project> save(@RequestBody Project project, HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            return R.ok(projectService.save(project, userId));
        } catch (Exception e) {
            log.warn("创建项目失败: {}", e.getMessage());
            return R.fail(e.getMessage());
        }
    }

    @PutMapping("/{id}/draft")
    public R<Project> updateDraft(@PathVariable Long id, @RequestBody Project project, HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            project.setId(id);
            return R.ok(projectService.updateDraft(project, userId));
        } catch (Exception e) {
            log.warn("更新项目草稿失败: {}", e.getMessage());
            return R.fail(e.getMessage());
        }
    }

    @PostMapping("/{id}/submit")
    public R<Project> submit(@PathVariable Long id, HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            return R.ok(projectService.submit(id, userId));
        } catch (Exception e) {
            log.warn("提交项目失败: {}", e.getMessage());
            return R.fail(e.getMessage());
        }
    }

    @PutMapping("/{id}/review")
    public R<Project> review(@PathVariable Long id, @RequestBody Map<String, String> body, HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return R.fail("未登录");
            }
            String decision = body.get("decision");
            String comment = body.get("comment");
            if (StrUtil.isBlank(decision)) {
                return R.fail("审核结论不能为空");
            }
            return R.ok(projectService.reviewDecision(id, decision, comment));
        } catch (Exception e) {
            log.warn("项目审核失败: {}", e.getMessage());
            return R.fail(e.getMessage());
        }
    }

    @GetMapping("/my")
    public R<List<Project>> myProjects(HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return R.fail("未登录");
            }
            return R.ok(projectService.getMyProjects(userId));
        } catch (Exception e) {
            log.warn("查询我的项目失败: {}", e.getMessage());
            return R.fail(e.getMessage());
        }
    }

    @GetMapping("/by-competition/{cid}")
    public R<List<Project>> byCompetition(@PathVariable Long cid) {
        try {
            return R.ok(projectService.getByCompetition(cid));
        } catch (Exception e) {
            log.warn("按大赛查询项目失败: {}", e.getMessage());
            return R.fail(e.getMessage());
        }
    }

    @GetMapping("/my/competition/{cid}")
    public R<Project> myCompetitionProject(@PathVariable Long cid, HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return R.fail("未登录");
            }
            return R.ok(projectService.getMyProjectInCompetition(userId, cid));
        } catch (Exception e) {
            log.warn("查询我的参赛项目失败: {}", e.getMessage());
            return R.fail(e.getMessage());
        }
    }
}
