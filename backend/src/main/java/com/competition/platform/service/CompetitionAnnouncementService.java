package com.competition.platform.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.competition.platform.entity.Announcement;

import java.util.List;

public interface CompetitionAnnouncementService {

    List<Announcement> list(String category, Integer limit);

    Page<Announcement> page(long page, long size);

    Announcement save(Announcement announcement, Long publisherId);

    Announcement update(Announcement announcement);

    void delete(Long id);
}
