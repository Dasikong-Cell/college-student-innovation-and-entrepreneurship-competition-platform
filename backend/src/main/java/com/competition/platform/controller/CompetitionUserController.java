package com.competition.platform.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.competition.platform.common.R;
import com.competition.platform.entity.SysUser;
import com.competition.platform.service.CompetitionUserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class CompetitionUserController {

    private final CompetitionUserService userService;

    public CompetitionUserController(CompetitionUserService userService) {
        this.userService = userService;
    }

    @GetMapping("/page")
    public R<Page<SysUser>> page(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String keyword) {
        return R.ok(userService.page(page, size, role, keyword));
    }

    @GetMapping("/{id}")
    public R<SysUser> get(@PathVariable Long id) {
        return R.ok(userService.getById(id));
    }

    @PutMapping
    public R<String> update(@RequestBody SysUser user, HttpServletRequest request) {
        Long currentUserId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");
        // 仅本人或管理员可修改；管理员可修改任意用户，普通用户只能修改自己
        if (!"admin".equals(role) && (currentUserId == null || user.getId() == null || !currentUserId.equals(user.getId()))) {
            return R.fail(403, "无权限修改该用户");
        }
        userService.update(user.getId(), user);
        return R.ok("updated");
    }

    @PutMapping("/{id}/reset-password")
    public R<String> resetPassword(@PathVariable Long id, @RequestBody Map<String, String> body, HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        // 重置密码属于高危操作，仅管理员可执行
        if (!"admin".equals(role)) {
            return R.fail(403, "仅管理员可重置密码");
        }
        userService.resetPassword(id, body.getOrDefault("password", "123456"));
        return R.ok("password reset");
    }
}
