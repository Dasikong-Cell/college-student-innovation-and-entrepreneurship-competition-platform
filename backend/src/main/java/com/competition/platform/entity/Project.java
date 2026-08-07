package com.competition.platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("project")
public class Project {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("competition_id")
    private Long competitionId;

    private String title;

    @TableField("team_name")
    private String teamName;

    @TableField("leader_id")
    private Long leaderId;

    @TableField("leader_name")
    private String leaderName;

    @TableField("leader_phone")
    private String leaderPhone;

    @TableField("team_members")
    private String teamMembers;

    private String college;

    private String category;

    @TableField("abstract")
    private String abstractContent;

    @TableField("plan_content")
    private String planContent;

    @TableField("innovation_points")
    private String innovationPoints;

    @TableField("expected_results")
    private String expectedResults;

    private String attachments;

    private String status;

    @TableField("submit_time")
    private LocalDateTime submitTime;

    @TableField("review_time")
    private LocalDateTime reviewTime;

    @TableField("reviewer_comment")
    private String reviewerComment;

    @TableField("final_score")
    private BigDecimal finalScore;

    private String prize;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
