package com.competition.platform.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.competition.platform.entity.SysUser;

public interface CompetitionUserService {

    Page<SysUser> page(long page, long size, String role, String keyword);

    SysUser getById(Long id);

    void update(Long id, SysUser user);

    void resetPassword(Long id, String newPassword);
}
