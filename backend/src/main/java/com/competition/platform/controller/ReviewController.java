package com.competition.platform.controller;

import com.competition.platform.common.R;
import com.competition.platform.entity.Review;
import com.competition.platform.service.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/review")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public R<Review> submit(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        try {
            Long expertId = (Long) request.getAttribute("userId");
            if (expertId == null) {
                return R.fail("未登录");
            }
            Long projectId = Long.valueOf(body.get("projectId").toString());
            BigDecimal scoreInnovation = new BigDecimal(body.get("scoreInnovation").toString());
            BigDecimal scoreFeasibility = new BigDecimal(body.get("scoreFeasibility").toString());
            BigDecimal scoreTeam = new BigDecimal(body.get("scoreTeam").toString());
            BigDecimal scorePresentation = new BigDecimal(body.get("scorePresentation").toString());
            String comment = (String) body.get("comment");
            return R.ok(reviewService.submitReview(
                    projectId, expertId,
                    scoreInnovation, scoreFeasibility, scoreTeam, scorePresentation,
                    comment
            ));
        } catch (Exception e) {
            log.warn("提交评审失败: {}", e.getMessage());
            return R.fail(e.getMessage());
        }
    }

    @GetMapping("/project/{pid}")
    public R<List<Map<String, Object>>> byProject(@PathVariable Long pid) {
        try {
            return R.ok(reviewService.getReviewsByProject(pid));
        } catch (Exception e) {
            log.warn("查询项目评审失败: {}", e.getMessage());
            return R.fail(e.getMessage());
        }
    }

    @GetMapping("/expert")
    public R<List<Map<String, Object>>> byExpert(HttpServletRequest request) {
        try {
            Long expertId = (Long) request.getAttribute("userId");
            if (expertId == null) {
                return R.fail("未登录");
            }
            return R.ok(reviewService.getReviewsByExpert(expertId));
        } catch (Exception e) {
            log.warn("查询专家评审失败: {}", e.getMessage());
            return R.fail(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public R<String> delete(@PathVariable Long id) {
        try {
            reviewService.deleteReview(id);
            return R.ok("删除成功");
        } catch (Exception e) {
            log.warn("删除评审失败: {}", e.getMessage());
            return R.fail(e.getMessage());
        }
    }
}
