package com.competition.platform.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.competition.platform.entity.Project;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CompetitionProjectService {

    Page<Project> page(long page, long size, Long competitionId, String status, String keyword);

    Project getById(Long id);

    @Transactional
    Project save(Project project, Long leaderId);

    Project updateDraft(Project project, Long leaderId);

    Project submit(Long id, Long leaderId);

    @Transactional
    void assignExperts(Long projectId, List<Long> expertIds);

    Project reviewDecision(Long id, String decision, String comment);

    List<Project> getMyProjects(Long userId);

    List<Project> getByCompetition(Long competitionId);

    Project getMyProjectInCompetition(Long userId, Long competitionId);
}
