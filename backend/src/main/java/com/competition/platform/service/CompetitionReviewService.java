package com.competition.platform.service;

import com.competition.platform.entity.Review;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface CompetitionReviewService {

    @Transactional
    Review submitReview(Long projectId, Long expertId,
                        BigDecimal scoreInnovation, BigDecimal scoreFeasibility,
                        BigDecimal scoreTeam, BigDecimal scorePresentation,
                        String comment);

    List<Map<String, Object>> getReviewsByProject(Long projectId);

    List<Map<String, Object>> getReviewsByExpert(Long expertId);

    void deleteReview(Long id);
}
