package com.competition.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.competition.platform.entity.Project;
import com.competition.platform.entity.Review;
import com.competition.platform.entity.SysUser;
import com.competition.platform.mapper.ProjectMapper;
import com.competition.platform.mapper.ReviewMapper;
import com.competition.platform.mapper.SysUserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CompetitionReviewServiceImpl implements CompetitionReviewService {

    private final ReviewMapper reviewMapper;
    private final SysUserMapper userMapper;
    private final ProjectMapper projectMapper;

    public CompetitionReviewServiceImpl(ReviewMapper reviewMapper, SysUserMapper userMapper, ProjectMapper projectMapper) {
        this.reviewMapper = reviewMapper;
        this.userMapper = userMapper;
        this.projectMapper = projectMapper;
    }

    @Transactional
    public Review submitReview(Long projectId, Long expertId,
                               BigDecimal scoreInnovation, BigDecimal scoreFeasibility,
                               BigDecimal scoreTeam, BigDecimal scorePresentation,
                               String comment) {
        Review exist = reviewMapper.selectOne(
                new LambdaQueryWrapper<Review>()
                        .eq(Review::getProjectId, projectId)
                        .eq(Review::getExpertId, expertId)
        );
        if (exist != null) {
            throw new RuntimeException("您已对该项目提交过评审，无法重复打分");
        }

        BigDecimal weighted = scoreInnovation.multiply(new BigDecimal("2.5"))
                .add(scoreFeasibility.multiply(new BigDecimal("2.5")))
                .add(scoreTeam.multiply(new BigDecimal("2.5")))
                .add(scorePresentation.multiply(new BigDecimal("2.5")))
                .setScale(2, RoundingMode.HALF_UP);

        Review review = new Review();
        review.setProjectId(projectId);
        review.setExpertId(expertId);
        review.setScoreInnovation(scoreInnovation);
        review.setScoreFeasibility(scoreFeasibility);
        review.setScoreTeam(scoreTeam);
        review.setScorePresentation(scorePresentation);
        review.setScoreTotal(weighted);
        review.setComment(comment);
        review.setScoreTime(LocalDateTime.now());
        reviewMapper.insert(review);

        autoCalculateFinalScore(projectId);
        return review;
    }

    public List<Map<String, Object>> getReviewsByProject(Long projectId) {
        List<Review> reviews = reviewMapper.selectList(
                new LambdaQueryWrapper<Review>()
                        .eq(Review::getProjectId, projectId)
                        .orderByDesc(Review::getScoreTime)
        );
        if (reviews.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> expertIds = reviews.stream().map(Review::getExpertId).distinct().collect(Collectors.toList());
        List<SysUser> experts = userMapper.selectBatchIds(expertIds);
        Map<Long, SysUser> expertMap = experts.stream().collect(Collectors.toMap(SysUser::getId, u -> u));

        List<Map<String, Object>> result = new ArrayList<>();
        for (Review r : reviews) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", r.getId());
            item.put("projectId", r.getProjectId());
            item.put("expertId", r.getExpertId());
            SysUser expert = expertMap.get(r.getExpertId());
            item.put("expertName", expert != null ? expert.getName() : "未知专家");
            item.put("scoreInnovation", r.getScoreInnovation());
            item.put("scoreFeasibility", r.getScoreFeasibility());
            item.put("scoreTeam", r.getScoreTeam());
            item.put("scorePresentation", r.getScorePresentation());
            item.put("scoreTotal", r.getScoreTotal());
            item.put("comment", r.getComment());
            item.put("scoreTime", r.getScoreTime());
            result.add(item);
        }
        return result;
    }

    public List<Map<String, Object>> getReviewsByExpert(Long expertId) {
        List<Review> reviews = reviewMapper.selectList(
                new LambdaQueryWrapper<Review>()
                        .eq(Review::getExpertId, expertId)
                        .orderByDesc(Review::getScoreTime)
        );
        if (reviews.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> projectIds = reviews.stream().map(Review::getProjectId).distinct().collect(Collectors.toList());
        List<Project> projects = projectMapper.selectBatchIds(projectIds);
        Map<Long, Project> projectMap = projects.stream().collect(Collectors.toMap(Project::getId, p -> p));

        List<Map<String, Object>> result = new ArrayList<>();
        for (Review r : reviews) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", r.getId());
            item.put("projectId", r.getProjectId());
            item.put("scoreInnovation", r.getScoreInnovation());
            item.put("scoreFeasibility", r.getScoreFeasibility());
            item.put("scoreTeam", r.getScoreTeam());
            item.put("scorePresentation", r.getScorePresentation());
            item.put("scoreTotal", r.getScoreTotal());
            item.put("comment", r.getComment());
            item.put("scoreTime", r.getScoreTime());
            Project p = projectMap.get(r.getProjectId());
            if (p != null) {
                item.put("projectTitle", p.getTitle());
                item.put("teamName", p.getTeamName());
                item.put("leaderName", p.getLeaderName());
                item.put("competitionId", p.getCompetitionId());
            }
            result.add(item);
        }
        return result;
    }

    public void deleteReview(Long id) {
        Review review = reviewMapper.selectById(id);
        if (review == null) {
            throw new RuntimeException("评审记录不存在");
        }
        reviewMapper.deleteById(id);
        autoCalculateFinalScore(review.getProjectId());
    }

    public void autoCalculateFinalScore(Long projectId) {
        List<Review> reviews = reviewMapper.selectList(
                new LambdaQueryWrapper<Review>()
                        .eq(Review::getProjectId, projectId)
        );
        Project project = projectMapper.selectById(projectId);
        if (project == null) {
            return;
        }
        if (reviews.isEmpty()) {
            project.setFinalScore(null);
        } else {
            BigDecimal sum = BigDecimal.ZERO;
            for (Review r : reviews) {
                if (r.getScoreTotal() != null) {
                    sum = sum.add(r.getScoreTotal());
                }
            }
            BigDecimal avg = sum.divide(new BigDecimal(reviews.size()), 2, RoundingMode.HALF_UP);
            project.setFinalScore(avg);
        }
        projectMapper.updateById(project);
    }
}
