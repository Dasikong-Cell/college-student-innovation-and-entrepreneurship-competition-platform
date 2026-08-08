package com.competition.platform.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.competition.platform.entity.Competition;

import java.util.List;
import java.util.Map;

public interface CompetitionCompetitionService {

    Page<Competition> page(long page, long size, String status, String category, String keyword);

    Competition getById(Long id);

    Competition save(Competition competition, Long adminId);

    Competition update(Competition competition);

    Competition publish(Long id);

    Competition takeDown(Long id);

    void delete(Long id);

    Map<String, Object> dashboardStats();

    List<String> listCategories();
}
