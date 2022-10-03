package ru.practicum.evm.main.service;

import org.springframework.data.domain.Page;
import ru.practicum.evm.main.dto.ReviewDto;
import ru.practicum.evm.main.dto.NewReviewDto;

import javax.validation.constraints.Min;
import javax.validation.Valid;

public interface ReviewService {
    Page<ReviewDto> getEventReviews(Long eventId, @Min(0) int from, @Min(1) int size);

    Page<ReviewDto> getUserReviews(Long userId, @Min(0) int from, @Min(1) int size);

    ReviewDto addReview(Long userId, @Valid NewReviewDto newReviewDto);

    void deleteReviewById(Long userId, Long reviewId);

    void publishReview(Long reviewId);

    void rejectReview(Long reviewId);
}
