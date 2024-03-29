package ru.practicum.evm.main.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.evm.main.model.Review;
import ru.practicum.evm.main.model.ReviewStatus;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findAllByEventAndStatus(Long eventId, ReviewStatus status, Pageable pageable);

    Page<Review> findAllByUser(Long userId, Pageable pageable);

    Optional<Review> findByUserAndEvent(Long userId, Long eventId);

    @Query("SELECT AVG(CASE r.positive WHEN true THEN 1 ELSE 0 END) " +
            "FROM Review AS r " +
            "WHERE r.event = :eventId")
    Float countEventRating(Long eventId);
}
