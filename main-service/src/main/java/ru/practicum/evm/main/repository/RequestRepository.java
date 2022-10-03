package ru.practicum.evm.main.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.evm.main.model.ParticipationRequest;
import ru.practicum.evm.main.model.RequestStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {
    List<ParticipationRequest> findAllByRequester(Long userId);

    List<ParticipationRequest> findAllByEvent(Long eventId);

    Optional<ParticipationRequest> findByRequesterAndEvent(Long userId, Long eventId);

    Optional<ParticipationRequest> findByRequesterAndEventAndStatus(Long userId, Long eventId,
                                                                    RequestStatus status);

    @Modifying
    @Transactional
    @Query("UPDATE ParticipationRequest AS pr " +
            "SET pr.status = 'REJECTED' " +
            "WHERE pr.status = 'PENDING' " +
            "AND pr.event = ?1 ")
    void rejectPendingRequests(Long eventId);

    Integer countByEventAndStatus(Long eventId, RequestStatus status);
}
