package com.competition.platform.service;

import com.competition.platform.entity.SysUser;

import java.util.Map;

public interface CompetitionAuthService {

    Map<String, Object> login(String username, String password);

    Map<String, Object> wxLogin(String code);

    SysUser getCurrentUser(Long userId);
}
