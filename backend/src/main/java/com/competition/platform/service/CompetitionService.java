package com.competition.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.competition.platform.entity.Competition;
import com.competition.platform.entity.Project;
import com.competition.platform.mapper.CompetitionMapper;
import com.competition.platform.mapper.ProjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class CompetitionService {

    private final CompetitionMapper competitionMapper;
    private final ProjectMapper projectMapper;

    public CompetitionService(CompetitionMapper competitionMapper, ProjectMapper projectMapper) {
        this.competitionMapper = competitionMapper;
        this.projectMapper = projectMapper;
    }

    public Page<Competition> page(long page, long size, String status, String category, String keyword) {
        Page<Competition> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Competition> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(status)) {
            wrapper.eq(Competition::getStatus, status);
        }
        if (StringUtils.hasText(category)) {
            wrapper.eq(Competition::getCategory, category);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Competition::getTitle, keyword);
        }
        wrapper.orderByDesc(Competition::getId);
        return competitionMapper.selectPage(pageParam, wrapper);
    }

    public Competition getById(Long id) {
        Competition competition = competitionMapper.selectById(id);
        if (competition == null) {
            throw new RuntimeException("大赛不存在");
        }
        return competition;
    }

    public Competition save(Competition competition, Long adminId) {
        competition.setCreatedBy(adminId);
        competition.setStatus("draft");
        competitionMapper.insert(competition);
        return competition;
    }

    public Competition update(Competition competition) {
        Competition db = competitionMapper.selectById(competition.getId());
        if (db == null) {
            throw new RuntimeException("大赛不存在");
        }
        competitionMapper.updateById(competition);
        return competitionMapper.selectById(competition.getId());
    }

    public Competition publish(Long id) {
        Competition competition = competitionMapper.selectById(id);
        if (competition == null) {
            throw new RuntimeException("大赛不存在");
        }
        competition.setStatus("published");
        competitionMapper.updateById(competition);
        return competition;
    }

    public Competition takeDown(Long id) {
        Competition competition = competitionMapper.selectById(id);
        if (competition == null) {
            throw new RuntimeException("大赛不存在");
        }
        competition.setStatus("finished");
        competitionMapper.updateById(competition);
        return competition;
    }

    public void delete(Long id) {
        Competition competition = competitionMapper.selectById(id);
        if (competition == null) {
            throw new RuntimeException("大赛不存在");
        }
        Long count = projectMapper.selectCount(
                new LambdaQueryWrapper<Project>().eq(Project::getCompetitionId, id)
        );
        if (count != null && count > 0) {
            throw new RuntimeException("该大赛下已有项目提交，无法删除");
        }
        competitionMapper.deleteById(id);
    }

    public Map<String, Object> dashboardStats() {
        Map<String, Object> result = new HashMap<>();
        Long total = competitionMapper.selectCount(null);
        result.put("total", total);

        Map<String, Long> byStatus = new HashMap<>();
        for (String status : new String[]{"draft", "published", "judging", "finished"}) {
            Long cnt = competitionMapper.selectCount(
                    new LambdaQueryWrapper<Competition>().eq(Competition::getStatus, status)
            );
            byStatus.put(status, cnt != null ? cnt : 0L);
        }
        result.put("byStatus", byStatus);

        List<Competition> all = competitionMapper.selectList(null);
        Map<String, Long> byCategory = new HashMap<>();
        for (Competition c : all) {
            String cat = c.getCategory() != null ? c.getCategory() : "未分类";
            byCategory.merge(cat, 1L, Long::sum);
        }
        result.put("byCategory", byCategory);

        return result;
    }

    public List<String> listCategories() {
        List<Competition> all = competitionMapper.selectList(null);
        Set<String> set = new LinkedHashSet<>();
        for (Competition c : all) {
            if (c.getCategory() != null && !c.getCategory().isEmpty()) {
                set.add(c.getCategory());
            }
        }
        return new java.util.ArrayList<>(set);
    }
}
