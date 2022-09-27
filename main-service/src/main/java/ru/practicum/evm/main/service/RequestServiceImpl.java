package ru.practicum.evm.main.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.evm.main.repository.RequestRepository;
import ru.practicum.evm.main.mapper.RequestMapper;
import ru.practicum.evm.main.model.RequestStatus;
import ru.practicum.evm.main.model.ParticipationRequest;
import ru.practicum.evm.main.dto.ParticipationRequestDto;
import ru.practicum.evm.main.dto.EventFullDto;
import ru.practicum.evm.main.exception.ForbiddenOperationException;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final EventService eventService;

    @Autowired
    public RequestServiceImpl(RequestRepository requestRepository,
                              EventService eventService) {
        this.requestRepository = requestRepository;
        this.eventService = eventService;
    }

    @Override
    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        return requestRepository.findAllByRequester(userId)
                .stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto addRequest(Long userId, Long eventId) {
        EventFullDto event = eventService.getEventById(eventId);
        Optional<ParticipationRequest> requestOptional = requestRepository
                .findByRequesterAndEvent(userId, eventId);
        if (requestOptional.isPresent()) {
            String message = "User can only add one participation request per event.";
            log.warn("ForbiddenOperationException at RequestServiceImpl.addRequest: {}", message);
            throw new ForbiddenOperationException(message);
        }
        if (userId.equals(event.getInitiator().getId())) {
            String message = "Initiators can't participate in their own events.";
            log.warn("ForbiddenOperationException at RequestServiceImpl.addRequest: {}", message);
            throw new ForbiddenOperationException(message);
        }
        if (!"PUBLISHED".equals(event.getState())) {
            String message = "Users can only participate in published events.";
            log.warn("ForbiddenOperationException at RequestServiceImpl.addRequest: {}", message);
            throw new ForbiddenOperationException(message);
        }
        if (event.getConfirmedRequests() >= event.getParticipantLimit() &&
                event.getParticipantLimit() != 0) {
            String message = "The number of participants has reached the limit.";
            log.warn("ForbiddenOperationException at RequestServiceImpl.addRequest: {}", message);
            throw new ForbiddenOperationException(message);
        }
        ParticipationRequest request = new ParticipationRequest(null, eventId, userId,
                LocalDateTime.now(), RequestStatus.PENDING);
        if (!event.getRequestModeration()) request.setStatus(RequestStatus.CONFIRMED);
        ParticipationRequest addedRequest = requestRepository.save(request);
        log.info("RequestServiceImpl.addRequest: request {} successfully added",
                 addedRequest.getId());
        return RequestMapper.toRequestDto(addedRequest);
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        ParticipationRequest request = findRequestById(requestId);
        if (!userId.equals(request.getRequester())) {
            String message = "Only requester can cancel request.";
            log.warn("ForbiddenOperationException at RequestServiceImpl.cancelRequest: {}", message);
            throw new ForbiddenOperationException(message);
        }
        request.setStatus(RequestStatus.CANCELED);
        ParticipationRequest cancelledRequest = requestRepository.save(request);
        log.info("RequestServiceImpl.cancelRequest: request {} successfully cancelled", request.getId());
        return RequestMapper.toRequestDto(cancelledRequest);
    }

    @Override
    public List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId) {
        eventService.getUserEventById(userId, eventId);
        return requestRepository.findAllByEvent(eventId)
                .stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto confirmRequest(Long userId, Long eventId, Long reqId) {
        EventFullDto event = eventService.getUserEventById(userId, eventId);
        if (event.getConfirmedRequests() >= event.getParticipantLimit() &&
                event.getParticipantLimit() != 0) {
            String message = "The number of participants has reached the limit.";
            log.warn("ForbiddenOperationException at RequestServiceImpl.confirmRequest: {}", message);
            throw new ForbiddenOperationException(message);
        }
        ParticipationRequest request = findRequestById(reqId);
        request.setStatus(RequestStatus.CONFIRMED);
        ParticipationRequest confirmedRequest = requestRepository.save(request);
        log.info("RequestServiceImpl.confirmRequest: request {} successfully confirmed", request.getId());
        if (event.getConfirmedRequests() == event.getParticipantLimit() - 1) {
            requestRepository.rejectPendingRequests(eventId);
        }
        return RequestMapper.toRequestDto(confirmedRequest);
    }

    @Override
    public ParticipationRequestDto rejectRequest(Long userId, Long eventId, Long reqId) {
        eventService.getUserEventById(userId, eventId);
        ParticipationRequest request = findRequestById(reqId);
        request.setStatus(RequestStatus.REJECTED);
        ParticipationRequest rejectedRequest = requestRepository.save(request);
        log.info("RequestServiceImpl.rejectRequest: request {} successfully rejected", request.getId());
        return RequestMapper.toRequestDto(rejectedRequest);
    }

    private ParticipationRequest findRequestById(Long requestId) {
        Optional<ParticipationRequest> requestOptional = requestRepository.findById(requestId);
        if (requestOptional.isEmpty()) {
            String message = String.format("Request with id=%d was not found.", requestId);
            log.warn("EntityNotFoundException at RequestServiceImpl: {}", message);
            throw new EntityNotFoundException(message);
        }
        return requestOptional.get();
    }
}
