package com.competition.platform.controller;

import cn.hutool.core.util.StrUtil;
import com.competition.platform.common.R;
import com.competition.platform.entity.SysUser;
import com.competition.platform.service.CompetitionAuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class CompetitionAuthController {

    private final CompetitionAuthService authService;

    public CompetitionAuthController(CompetitionAuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public R<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        try {
            String username = body.get("username");
            String password = body.get("password");
            if (StrUtil.isBlank(username) || StrUtil.isBlank(password)) {
                return R.fail("用户名或密码不能为空");
            }
            return R.ok(authService.login(username, password));
        } catch (Exception e) {
            log.warn("登录失败: {}", e.getMessage());
            return R.fail(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public R<String> logout() {
        return R.ok("ok");
    }

    @GetMapping("/me")
    public R<SysUser> me(HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return R.fail("未登录");
            }
            return R.ok(authService.getCurrentUser(userId));
        } catch (Exception e) {
            log.warn("获取当前用户失败: {}", e.getMessage());
            return R.fail(e.getMessage());
        }
    }
}
