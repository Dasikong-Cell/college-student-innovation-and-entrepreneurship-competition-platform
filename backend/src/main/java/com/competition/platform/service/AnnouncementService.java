package com.competition.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.competition.platform.entity.Announcement;
import com.competition.platform.mapper.AnnouncementMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AnnouncementService {

    private final AnnouncementMapper announcementMapper;

    public AnnouncementService(AnnouncementMapper announcementMapper) {
        this.announcementMapper = announcementMapper;
    }

    public List<Announcement> list(String category, Integer limit) {
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(category)) {
            wrapper.eq(Announcement::getCategory, category);
        }
        wrapper.orderByDesc(Announcement::getTop)
                .orderByDesc(Announcement::getPublishedAt);
        if (limit != null && limit > 0) {
            wrapper.last("LIMIT " + limit);
        }
        return announcementMapper.selectList(wrapper);
    }

    public Page<Announcement> page(long page, long size) {
        Page<Announcement> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Announcement::getTop)
                .orderByDesc(Announcement::getPublishedAt);
        return announcementMapper.selectPage(pageParam, wrapper);
    }

    public Announcement save(Announcement announcement, Long publisherId) {
        announcement.setPublisherId(publisherId);
        announcement.setPublishedAt(LocalDateTime.now());
        if (announcement.getTop() == null) {
            announcement.setTop(0);
        }
        announcementMapper.insert(announcement);
        return announcement;
    }

    public Announcement update(Announcement announcement) {
        Announcement db = announcementMapper.selectById(announcement.getId());
        if (db == null) {
            throw new RuntimeException("公告不存在");
        }
        announcement.setPublisherId(null);
        announcement.setPublishedAt(null);
        announcementMapper.updateById(announcement);
        return announcementMapper.selectById(announcement.getId());
    }

    public void delete(Long id) {
        Announcement db = announcementMapper.selectById(id);
        if (db == null) {
            throw new RuntimeException("公告不存在");
        }
        announcementMapper.deleteById(id);
    }
}
