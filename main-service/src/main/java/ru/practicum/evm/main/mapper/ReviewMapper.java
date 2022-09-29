package ru.practicum.evm.main.mapper;

import ru.practicum.evm.main.model.ReviewStatus;
import ru.practicum.evm.main.model.Review;
import ru.practicum.evm.main.dto.ReviewDto;
import ru.practicum.evm.main.dto.NewReviewDto;

public class ReviewMapper {
    public static Review toReview(NewReviewDto newReviewDto) {
        return new Review(
                null,
                null,
                newReviewDto.getEvent(),
                newReviewDto.getReview(),
                newReviewDto.getPositive(),
                ReviewStatus.PENDING
        );
    }

    public static ReviewDto toReviewDto(Review review) {
        return new ReviewDto(
                review.getId(),
                review.getUser(),
                review.getEvent(),
                review.getReview(),
                review.getPositive(),
                review.getStatus().toString()
        );
    }
}
