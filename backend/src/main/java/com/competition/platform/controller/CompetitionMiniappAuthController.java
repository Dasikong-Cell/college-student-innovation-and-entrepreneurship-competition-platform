package com.competition.platform.controller;

import cn.hutool.core.util.StrUtil;
import com.competition.platform.common.R;
import com.competition.platform.service.CompetitionAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/miniapp/auth")
public class CompetitionMiniappAuthController {

    private final CompetitionAuthService authService;

    public CompetitionMiniappAuthController(CompetitionAuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/wx-login")
    public R<Map<String, Object>> wxLogin(@RequestBody Map<String, String> body) {
        try {
            String code = body.get("code");
            if (StrUtil.isBlank(code)) {
                return R.fail("code不能为空");
            }
            return R.ok(authService.wxLogin(code));
        } catch (Exception e) {
            log.warn("微信登录失败: {}", e.getMessage());
            return R.fail(e.getMessage());
        }
    }

    @PostMapping("/bind-phone")
    public R<Map<String, Object>> bindPhone(@RequestBody Map<String, String> body) {
        try {
            String token = body.get("token");
            String phone = body.get("phone");
            String smsCode = body.get("smsCode");
            if (StrUtil.isBlank(phone)) {
                return R.fail("手机号不能为空");
            }
            Map<String, Object> result = new HashMap<>();
            result.put("phone", phone);
            return R.ok(result);
        } catch (Exception e) {
            log.warn("绑定手机号失败: {}", e.getMessage());
            return R.fail(e.getMessage());
        }
    }
}
