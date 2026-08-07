package com.competition.platform.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.competition.platform.common.R;
import com.competition.platform.entity.SysUser;
import com.competition.platform.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
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
    public R<String> update(@RequestBody SysUser user) {
        Long userId = (Long) user.getId();
        userService.update(userId, user);
        return R.ok("updated");
    }

    @PutMapping("/{id}/reset-password")
    public R<String> resetPassword(@PathVariable Long id, @RequestBody Map<String, String> body) {
        userService.resetPassword(id, body.getOrDefault("password", "123456"));
        return R.ok("password reset");
    }
}
