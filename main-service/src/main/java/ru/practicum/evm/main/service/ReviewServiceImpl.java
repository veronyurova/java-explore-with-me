package ru.practicum.evm.main.service;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import ru.practicum.evm.main.repository.ReviewRepository;
import ru.practicum.evm.main.mapper.ReviewMapper;
import ru.practicum.evm.main.model.ReviewStatus;
import ru.practicum.evm.main.model.Review;
import ru.practicum.evm.main.dto.ReviewDto;
import ru.practicum.evm.main.dto.NewReviewDto;
import ru.practicum.evm.main.exception.ForbiddenOperationException;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.Min;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final RequestService requestService;

    @Override
    public List<ReviewDto> getEventReviews(Long eventId, @Min(0) int from, @Min(1) int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return reviewRepository.findAllByEventAndStatus(eventId, ReviewStatus.PUBLISHED, pageable)
                .stream()
                .map(ReviewMapper::toReviewDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewDto> getUserReviews(Long userId) {
        return reviewRepository.findAllByUser(userId)
                .stream()
                .map(ReviewMapper::toReviewDto)
                .collect(Collectors.toList());
    }

    @Override
    public ReviewDto addReview(Long userId, @Valid NewReviewDto newReviewDto) {
        Review review = ReviewMapper.toReview(newReviewDto);
        Long eventId = review.getEvent();
        Optional<Review> reviewOptional = reviewRepository.findByUserAndEvent(userId, eventId);
        if (reviewOptional.isPresent()) {
            String message = "User can only add one review per event.";
            log.warn("ForbiddenOperationException at ReviewServiceImpl.addReview: {}", message);
            throw new ForbiddenOperationException(message);
        }
        if (!requestService.userParticipateInEvent(userId, eventId)) {
            String message = "Users can only review events they participate in.";
            log.warn("ForbiddenOperationException at ReviewServiceImpl.addReview: {}", message);
            throw new ForbiddenOperationException(message);
        }
        review.setUser(userId);
        if (review.getReview() == null) review.setStatus(ReviewStatus.PUBLISHED);
        Review addedReview = reviewRepository.save(review);
        log.info("ReviewServiceImpl.addReview: review {} successfully added", addedReview.getId());
        return ReviewMapper.toReviewDto(addedReview);
    }

    @Override
    public void deleteReviewById(Long userId, Long reviewId) {
        Review review = findReviewById(reviewId);
        if (!userId.equals(review.getUser())) {
            String message = "Only author can delete review.";
            log.warn("ForbiddenOperationException at ReviewServiceImpl.deleteReviewById: {}", message);
            throw new ForbiddenOperationException(message);
        }
        reviewRepository.deleteById(reviewId);
        log.info("ReviewServiceImpl.deleteReviewById: review {} successfully deleted", reviewId);
    }

    @Override
    public void publishReview(Long reviewId) {
        Review review = findReviewById(reviewId);
        if (!ReviewStatus.PENDING.equals(review.getStatus())) {
            String message = "Only pending reviews can be published.";
            log.warn("ForbiddenOperationException at ReviewServiceImpl.publishReview: {}", message);
            throw new ForbiddenOperationException(message);
        }
        review.setStatus(ReviewStatus.PUBLISHED);
        reviewRepository.save(review);
        log.info("ReviewServiceImpl.publishEvent: review {} successfully published", review.getId());
    }

    @Override
    public void rejectReview(Long reviewId) {
        Review review = findReviewById(reviewId);
        if (!ReviewStatus.PENDING.equals(review.getStatus())) {
            String message = "Only pending reviews can be rejected.";
            log.warn("ForbiddenOperationException at ReviewServiceImpl.rejectReview: {}", message);
            throw new ForbiddenOperationException(message);
        }
        review.setStatus(ReviewStatus.REJECTED);
        reviewRepository.save(review);
        log.info("ReviewServiceImpl.rejectEvent: review {} successfully rejected", review.getId());
    }

    private Review findReviewById(Long reviewId) {
        Optional<Review> reviewOptional = reviewRepository.findById(reviewId);
        if (reviewOptional.isEmpty()) {
            String message = String.format("Review with id=%d was not found.", reviewId);
            log.warn("EntityNotFoundException at ReviewServiceImpl: {}", message);
            throw new EntityNotFoundException(message);
        }
        return reviewOptional.get();
    }
}
