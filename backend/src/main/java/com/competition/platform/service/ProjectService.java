package com.competition.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.competition.platform.entity.Project;
import com.competition.platform.entity.Review;
import com.competition.platform.entity.SysUser;
import com.competition.platform.mapper.ProjectMapper;
import com.competition.platform.mapper.ReviewMapper;
import com.competition.platform.mapper.SysUserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProjectService {

    private final ProjectMapper projectMapper;
    private final SysUserMapper userMapper;
    private final ReviewMapper reviewMapper;

    public ProjectService(ProjectMapper projectMapper, SysUserMapper userMapper, ReviewMapper reviewMapper) {
        this.projectMapper = projectMapper;
        this.userMapper = userMapper;
        this.reviewMapper = reviewMapper;
    }

    public Page<Project> page(long page, long size, Long competitionId, String status, String keyword) {
        Page<Project> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();
        if (competitionId != null) {
            wrapper.eq(Project::getCompetitionId, competitionId);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(Project::getStatus, status);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Project::getTitle, keyword)
                    .or().like(Project::getTeamName, keyword)
                    .or().like(Project::getLeaderName, keyword));
        }
        wrapper.orderByDesc(Project::getId);
        return projectMapper.selectPage(pageParam, wrapper);
    }

    public Project getById(Long id) {
        Project project = projectMapper.selectById(id);
        if (project == null) {
            throw new RuntimeException("项目不存在");
        }
        return project;
    }

    @Transactional
    public Project save(Project project, Long leaderId) {
        SysUser user = userMapper.selectById(leaderId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        boolean isDraft = "draft".equalsIgnoreCase(project.getStatus());

        if (!isDraft) {
            Long existCount = projectMapper.selectCount(
                    new LambdaQueryWrapper<Project>()
                            .eq(Project::getCompetitionId, project.getCompetitionId())
                            .eq(Project::getLeaderId, leaderId)
                            .ne(Project::getStatus, "draft")
            );
            if (existCount != null && existCount > 0) {
                throw new RuntimeException("您在该大赛中已提交过项目，不可重复申报");
            }
        }

                if (!StringUtils.hasText(project.getTitle())) {
            project.setTitle(project.getTeamName() != null ? project.getTeamName() : ("未命名项目_" + System.currentTimeMillis()));
        }
        project.setLeaderId(leaderId);
        project.setLeaderName(user.getName());
        if (!StringUtils.hasText(project.getLeaderPhone())) {
            project.setLeaderPhone(user.getPhone());
        }
        if (!StringUtils.hasText(project.getCollege())) {
            project.setCollege(user.getCollege());
        }
        if (!StringUtils.hasText(project.getStatus())) {
            project.setStatus(isDraft ? "draft" : "submitted");
        }
        if (!isDraft) {
            project.setSubmitTime(LocalDateTime.now());
        }
        projectMapper.insert(project);
        return project;
    }

    public Project updateDraft(Project project, Long leaderId) {
        Project db = projectMapper.selectById(project.getId());
        if (db == null) {
            throw new RuntimeException("项目不存在");
        }
        if (!db.getLeaderId().equals(leaderId)) {
            throw new RuntimeException("无权操作该项目");
        }
        if (!"draft".equals(db.getStatus())) {
            throw new RuntimeException("只有草稿状态可以编辑");
        }
        project.setStatus(null);
        project.setLeaderId(null);
        project.setLeaderName(null);
        project.setSubmitTime(null);
        projectMapper.updateById(project);
        return projectMapper.selectById(project.getId());
    }

    public Project submit(Long id, Long leaderId) {
        Project project = projectMapper.selectById(id);
        if (project == null) {
            throw new RuntimeException("项目不存在");
        }
        if (!project.getLeaderId().equals(leaderId)) {
            throw new RuntimeException("无权操作该项目");
        }
        if (!"draft".equals(project.getStatus())) {
            throw new RuntimeException("项目已提交，不可重复提交");
        }
        project.setStatus("submitted");
        project.setSubmitTime(LocalDateTime.now());
        projectMapper.updateById(project);
        return project;
    }

    @Transactional
    public void assignExperts(Long projectId, List<Long> expertIds) {
        Project project = projectMapper.selectById(projectId);
        if (project == null) {
            throw new RuntimeException("项目不存在");
        }
        if (expertIds == null || expertIds.isEmpty()) {
            return;
        }
        project.setStatus("reviewing");
        projectMapper.updateById(project);
    }

    public Project reviewDecision(Long id, String decision, String comment) {
        Project project = projectMapper.selectById(id);
        if (project == null) {
            throw new RuntimeException("项目不存在");
        }
        project.setStatus(decision);
        project.setReviewerComment(comment);
        project.setReviewTime(LocalDateTime.now());
        projectMapper.updateById(project);
        return project;
    }

    public List<Project> getMyProjects(Long userId) {
        return projectMapper.selectList(
                new LambdaQueryWrapper<Project>()
                        .eq(Project::getLeaderId, userId)
                        .orderByDesc(Project::getId)
        );
    }

    public List<Project> getByCompetition(Long competitionId) {
        return projectMapper.selectList(
                new LambdaQueryWrapper<Project>()
                        .eq(Project::getCompetitionId, competitionId)
                        .orderByDesc(Project::getId)
        );
    }

    public Project getMyProjectInCompetition(Long userId, Long competitionId) {
        return projectMapper.selectOne(
                new LambdaQueryWrapper<Project>()
                        .eq(Project::getLeaderId, userId)
                        .eq(Project::getCompetitionId, competitionId)
                        .last("LIMIT 1")
        );
    }
}
