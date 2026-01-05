package com.guthub.guthubserver.domain.supplement.service;

import com.guthub.guthubserver.domain.supplement.dto.ReviewRequestDto;
import com.guthub.guthubserver.domain.supplement.dto.ReviewResponseDto;
import com.guthub.guthubserver.domain.supplement.entity.ReviewEntity;
import com.guthub.guthubserver.domain.supplement.entity.SupplementEntity;
import com.guthub.guthubserver.domain.supplement.repository.ReviewRepository;
import com.guthub.guthubserver.domain.supplement.repository.SupplementRepository;
import com.guthub.guthubserver.domain.user.entity.UserEntity;
import com.guthub.guthubserver.domain.user.repository.UserRepository;
import com.guthub.guthubserver.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final SupplementRepository supplementRepository;
    private final UserRepository userRepository;

    @Transactional
    public ReviewResponseDto createReview(ReviewRequestDto dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(ErrorCode.USER_NOT_FOUND.getMessage()));

        SupplementEntity supplement = supplementRepository.findById(dto.getSupplementId())
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.RESOURCE_NOT_FOUND.getMessage()));

        ReviewEntity review = ReviewEntity.builder()
                .user(user)
                .supplement(supplement)
                .rating(dto.getRating())
                .deliveryRating(dto.getDeliveryRating())
                .content(dto.getContent())
                .build();

        reviewRepository.save(review);

        // 건기식 평점 및 리뷰 수 업데이트
        updateSupplementStats(supplement);

        return ReviewResponseDto.fromEntity(review);
    }

    public Page<ReviewResponseDto> getReviews(Long supplementId, Pageable pageable) {
        SupplementEntity supplement = supplementRepository.findById(supplementId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.RESOURCE_NOT_FOUND.getMessage()));

        return reviewRepository.findBySupplementOrderByCreatedAtDesc(supplement, pageable)
                .map(ReviewResponseDto::fromEntity);
    }

    private void updateSupplementStats(SupplementEntity supplement) {
        Long count = reviewRepository.countBySupplement(supplement);
        Double avg = reviewRepository.getAverageRating(supplement);
        
        if (avg == null) avg = 0.0;
        
        supplement.updateRating(avg, count);
    }
}
