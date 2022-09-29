package ru.practicum.evm.main.service;

import ru.practicum.evm.main.dto.ReviewDto;
import ru.practicum.evm.main.dto.NewReviewDto;

import javax.validation.constraints.Min;
import javax.validation.Valid;
import java.util.List;

public interface ReviewService {
    List<ReviewDto> getEventReviews(Long eventId, @Min(0) int from, @Min(1) int size);

    List<ReviewDto> getUserReviews(Long userId);

    ReviewDto addReview(Long userId, @Valid NewReviewDto newReviewDto);

    void deleteReviewById(Long userId, Long reviewId);

    void publishReview(Long reviewId);

    void rejectReview(Long reviewId);
}
