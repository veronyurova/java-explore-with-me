package ru.practicum.evm.main.service;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
    public Page<ReviewDto> getEventReviews(Long eventId, @Min(0) int from, @Min(1) int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<ReviewDto> reviews = reviewRepository.findAllByEventAndStatus(eventId,
                        ReviewStatus.PUBLISHED, pageable).stream()
                .map(ReviewMapper::toReviewDto)
                .collect(Collectors.toList());
        return new PageImpl<>(reviews);
    }

    @Override
    public Page<ReviewDto> getUserReviews(Long userId, @Min(0) int from, @Min(1) int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<ReviewDto> reviews = reviewRepository.findAllByUser(userId, pageable).stream()
                .map(ReviewMapper::toReviewDto)
                .collect(Collectors.toList());
        return new PageImpl<>(reviews);
    }

    @Override
    public ReviewDto addReview(Long userId, @Valid NewReviewDto newReviewDto) {
        Review review = ReviewMapper.toReview(newReviewDto);
        Long eventId = review.getEvent();
        Optional<Review> reviewOptional = reviewRepository.findByUserAndEvent(userId, eventId);
        if (reviewOptional.isPresent()) {
            throw new ForbiddenOperationException("User can only add one review per event.");
        }
        if (!requestService.userParticipateInEvent(userId, eventId)) {
            throw new ForbiddenOperationException("Users can only review events they participate in.");
        }
        review.setUser(userId);
        if (review.getReview() == null) {
            review.setStatus(ReviewStatus.PUBLISHED);
        }
        Review addedReview = reviewRepository.save(review);
        log.info("ReviewServiceImpl.addReview: review {} successfully added", addedReview.getId());
        return ReviewMapper.toReviewDto(addedReview);
    }

    @Override
    public void deleteReviewById(Long userId, Long reviewId) {
        Review review = getById(reviewId);
        if (!userId.equals(review.getUser())) {
            throw new ForbiddenOperationException("Only author can delete review.");
        }
        reviewRepository.deleteById(reviewId);
        log.info("ReviewServiceImpl.deleteReviewById: review {} successfully deleted", reviewId);
    }

    @Override
    public void publishReview(Long reviewId) {
        Review review = getById(reviewId);
        if (!ReviewStatus.PENDING.equals(review.getStatus())) {
            throw new ForbiddenOperationException("Only pending reviews can be published.");
        }
        review.setStatus(ReviewStatus.PUBLISHED);
        reviewRepository.save(review);
        log.info("ReviewServiceImpl.publishEvent: review {} successfully published", review.getId());
    }

    @Override
    public void rejectReview(Long reviewId) {
        Review review = getById(reviewId);
        if (!ReviewStatus.PENDING.equals(review.getStatus())) {
            throw new ForbiddenOperationException("Only pending reviews can be rejected.");
        }
        review.setStatus(ReviewStatus.REJECTED);
        reviewRepository.save(review);
        log.info("ReviewServiceImpl.rejectEvent: review {} successfully rejected", review.getId());
    }

    private Review getById(Long id) {
        Optional<Review> reviewOptional = reviewRepository.findById(id);
        if (reviewOptional.isEmpty()) {
            String message = String.format("Review with id=%d was not found.", id);
            throw new EntityNotFoundException(message);
        }
        return reviewOptional.get();
    }
}
