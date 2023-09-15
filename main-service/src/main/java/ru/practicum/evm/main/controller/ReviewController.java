package ru.practicum.evm.main.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import ru.practicum.evm.main.service.ReviewService;
import ru.practicum.evm.main.dto.NewReviewDto;
import ru.practicum.evm.main.dto.ReviewDto;

@RestController
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping("/events/{eventId}/reviews")
    public Page<ReviewDto> getEventReviews(@PathVariable Long eventId,
                                           @RequestParam(defaultValue = "0") int from,
                                           @RequestParam(defaultValue = "10") int size) {
        return reviewService.getEventReviews(eventId, from, size);
    }

    @GetMapping("/users/{userId}/reviews")
    public Page<ReviewDto> getUserReviews(@PathVariable Long userId,
                                          @RequestParam(defaultValue = "0") int from,
                                          @RequestParam(defaultValue = "10") int size) {
        return reviewService.getUserReviews(userId, from, size);
    }

    @PostMapping("/users/{userId}/reviews")
    public ReviewDto addReview(@PathVariable Long userId,
                               @RequestBody NewReviewDto newReviewDto) {
        return reviewService.addReview(userId, newReviewDto);
    }

    @DeleteMapping("/users/{userId}/reviews/{reviewId}")
    public void deleteReviewById(@PathVariable Long userId,
                                 @PathVariable Long reviewId) {
        reviewService.deleteReviewById(userId, reviewId);
    }

    @PatchMapping("admin/reviews/{reviewId}/publish")
    public void publishReview(@PathVariable Long reviewId) {
        reviewService.publishReview(reviewId);
    }

    @PatchMapping("admin/reviews/{reviewId}/reject")
    public void rejectReview(@PathVariable Long reviewId) {
        reviewService.rejectReview(reviewId);
    }
}
