package com.competition.platform.service;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.competition.platform.entity.SysUser;
import com.competition.platform.mapper.SysUserMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
public class CompetitionUserServiceImpl implements CompetitionUserService {

    private final SysUserMapper userMapper;

    public CompetitionUserServiceImpl(SysUserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public Page<SysUser> page(long page, long size, String role, String keyword) {
        Page<SysUser> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(role)) {
            wrapper.eq(SysUser::getRole, role);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(SysUser::getUsername, keyword)
                    .or().like(SysUser::getName, keyword)
                    .or().like(SysUser::getPhone, keyword));
        }
        wrapper.orderByDesc(SysUser::getId);
        return userMapper.selectPage(pageParam, wrapper);
    }

    public SysUser getById(Long id) {
        SysUser user = userMapper.selectById(id);
        if (user != null) {
            user.setPassword(null);
        }
        return user;
    }

    public void update(Long id, SysUser user) {
        SysUser dbUser = userMapper.selectById(id);
        if (dbUser == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setId(id);
        user.setPassword(null);
        user.setUsername(null);
        user.setRole(null);
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
    }

    public void resetPassword(Long id, String newPassword) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setPassword(BCrypt.hashpw(newPassword));
        userMapper.updateById(user);
    }
}
