package com.competition.platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("review")
public class Review {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("project_id")
    private Long projectId;

    @TableField("expert_id")
    private Long expertId;

    @TableField("score_innovation")
    private BigDecimal scoreInnovation;

    @TableField("score_feasibility")
    private BigDecimal scoreFeasibility;

    @TableField("score_team")
    private BigDecimal scoreTeam;

    @TableField("score_presentation")
    private BigDecimal scorePresentation;

    @TableField("score_total")
    private BigDecimal scoreTotal;

    private String comment;

    @TableField("score_time")
    private LocalDateTime scoreTime;
}
