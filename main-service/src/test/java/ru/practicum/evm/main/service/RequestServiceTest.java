package ru.practicum.evm.main.service;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.evm.main.repository.RequestRepository;
import ru.practicum.evm.main.model.RequestStatus;
import ru.practicum.evm.main.model.ParticipationRequest;
import ru.practicum.evm.main.dto.ParticipationRequestDto;
import ru.practicum.evm.main.dto.UserShortDto;
import ru.practicum.evm.main.dto.EventFullDto;
import ru.practicum.evm.main.exception.ForbiddenOperationException;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class RequestServiceTest {
    private RequestService requestService;
    @Mock
    private RequestRepository requestRepository;
    @Mock
    private EventService eventService;
    private final LocalDateTime date = LocalDateTime.of(2050, 1, 1, 0, 0);
    private final ParticipationRequest request = new ParticipationRequest(1L, 1L, 1L,
            date, RequestStatus.PENDING);
    private final ParticipationRequestDto requestDto = new ParticipationRequestDto(1L, 1L, 1L,
            date, "PENDING");

    @BeforeEach
    void beforeEach() {
        requestService = new RequestServiceImpl(requestRepository, eventService);
    }

    @Test
    void getUserRequests() {
        Mockito.when(requestRepository.findAllByRequester(1L)).thenReturn(List.of(request));

        List<ParticipationRequestDto> requestsExpected = List.of(requestDto);
        List<ParticipationRequestDto> requests = requestService.getUserRequests(1L);

        assertNotNull(requests);
        assertEquals(requestsExpected, requests);
    }

    @Test
    void getUserRequestsNoRequests() {
        Mockito.when(requestRepository.findAllByRequester(1L)).thenReturn(Collections.emptyList());

        List<ParticipationRequestDto> requests = requestService.getUserRequests(1L);

        assertNotNull(requests);
        assertEquals(0, requests.size());
    }

    @Test
    void getEventRequests() {
        Mockito.when(requestRepository.findAllByEvent(1L)).thenReturn(List.of(request));

        List<ParticipationRequestDto> requestsExpected = List.of(requestDto);
        List<ParticipationRequestDto> requests = requestService.getEventRequests(1L, 1L);

        assertNotNull(requests);
        assertEquals(requestsExpected, requests);
    }

    @Test
    void getEventRequestsNoRequests() {
        Mockito.when(requestRepository.findAllByEvent(1L)).thenReturn(Collections.emptyList());

        List<ParticipationRequestDto> requests = requestService.getEventRequests(1L, 1L);

        assertNotNull(requests);
        assertEquals(0, requests.size());
    }

    @Test
    void addRequest() {
        UserShortDto user = new UserShortDto(2L, "User");
        EventFullDto event = new EventFullDto(
                1L,
                "Title",
                "Annotation",
                "Description",
                null,
                null,
                true,
                null,
                user,
                0,
                0,
                false,
                "PUBLISHED",
                null,
                null,
                0L
        );
        ParticipationRequest savedRequest = new ParticipationRequest(1L, 1L, 1L,
                date, RequestStatus.CONFIRMED);
        ParticipationRequestDto requestExpected = new ParticipationRequestDto(1L, 1L, 1L,
                date, "CONFIRMED");
        Mockito.when(eventService.getEventById(1L)).thenReturn(event);
        Mockito.when(requestRepository.findByRequesterAndEvent(1L, 1L)).thenReturn(Optional.empty());
        Mockito.when(requestRepository.save(Mockito.any())).thenReturn(savedRequest);

        ParticipationRequestDto addedRequest = requestService.addRequest(1L, 1L);

        assertNotNull(addedRequest);
        assertEquals(requestExpected, addedRequest);
    }

    @Test
    void addRequestAlreadyExists() {
        UserShortDto user = new UserShortDto(2L, "User");
        EventFullDto event = new EventFullDto(
                1L,
                "Title",
                "Annotation",
                "Description",
                null,
                null,
                true,
                null,
                user,
                0,
                0,
                false,
                "PUBLISHED",
                null,
                null,
                0L
        );
        Mockito.when(eventService.getEventById(1L)).thenReturn(event);
        Mockito.when(requestRepository.findByRequesterAndEvent(1L, 1L)).thenReturn(Optional.of(request));

        assertThrows(ForbiddenOperationException.class, () -> requestService.addRequest(1L, 1L));
    }

    @Test
    void addRequestByEventInitiator() {
        UserShortDto user = new UserShortDto(1L, "User");
        EventFullDto event = new EventFullDto(
                1L,
                "Title",
                "Annotation",
                "Description",
                null,
                null,
                true,
                null,
                user,
                0,
                0,
                false,
                "PUBLISHED",
                null,
                null,
                0L
        );
        Mockito.when(eventService.getEventById(1L)).thenReturn(event);
        Mockito.when(requestRepository.findByRequesterAndEvent(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(ForbiddenOperationException.class, () -> requestService.addRequest(1L, 1L));
    }

    @Test
    void addRequestEventNotPublished() {
        UserShortDto user = new UserShortDto(2L, "User");
        EventFullDto event = new EventFullDto(
                1L,
                "Title",
                "Annotation",
                "Description",
                null,
                null,
                true,
                null,
                user,
                0,
                0,
                false,
                "PENDING",
                null,
                null,
                0L
        );
        Mockito.when(eventService.getEventById(1L)).thenReturn(event);
        Mockito.when(requestRepository.findByRequesterAndEvent(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(ForbiddenOperationException.class, () -> requestService.addRequest(1L, 1L));
    }

    @Test
    void addRequestEventNotAvailable() {
        UserShortDto user = new UserShortDto(2L, "User");
        EventFullDto event = new EventFullDto(
                1L,
                "Title",
                "Annotation",
                "Description",
                null,
                null,
                true,
                null,
                user,
                1,
                2,
                false,
                "PUBLISHED",
                null,
                null,
                0L
        );
        Mockito.when(eventService.getEventById(1L)).thenReturn(event);
        Mockito.when(requestRepository.findByRequesterAndEvent(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(ForbiddenOperationException.class, () -> requestService.addRequest(1L, 1L));
    }

    @Test
    void cancelRequest() {
        ParticipationRequest cancelRequest = new ParticipationRequest(1L, 1L, 1L,
                date, RequestStatus.CANCELED);
        ParticipationRequestDto requestExpected = new ParticipationRequestDto(1L, 1L, 1L,
                date, "CANCELED");
        Mockito.when(requestRepository.findById(1L)).thenReturn(Optional.of(request));
        Mockito.when(requestRepository.save(cancelRequest)).thenReturn(cancelRequest);

        ParticipationRequestDto cancelledRequest = requestService.cancelRequest(1L, 1L);

        assertNotNull(cancelledRequest);
        assertEquals(requestExpected, cancelledRequest);
    }

    @Test
    void cancelRequestByWrongUser() {
        Mockito.when(requestRepository.findById(1L)).thenReturn(Optional.of(request));

        assertThrows(ForbiddenOperationException.class, () -> requestService.cancelRequest(2L, 1L));
    }

    @Test
    void cancelRequestNoSuchRequest() {
        Mockito.when(requestRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> requestService.cancelRequest(1L, 1L));
    }

    @Test
    void confirmRequest() {
        UserShortDto user = new UserShortDto(2L, "User");
        EventFullDto event = new EventFullDto(
                1L,
                "Title",
                "Annotation",
                "Description",
                null,
                null,
                true,
                null,
                user,
                0,
                0,
                true,
                "PUBLISHED",
                null,
                null,
                0L
        );
        ParticipationRequest savedRequest = new ParticipationRequest(1L, 1L, 1L,
                date, RequestStatus.CONFIRMED);
        ParticipationRequestDto requestExpected = new ParticipationRequestDto(1L, 1L, 1L,
                date, "CONFIRMED");
        Mockito.when(eventService.getUserEventById(2L, 1L)).thenReturn(event);
        Mockito.when(requestRepository.findById(1L)).thenReturn(Optional.of(request));
        Mockito.when(requestRepository.save(savedRequest)).thenReturn(savedRequest);

        ParticipationRequestDto confirmedRequest = requestService.confirmRequest(2L, 1L, 1L);

        assertNotNull(confirmedRequest);
        assertEquals(requestExpected, confirmedRequest);
    }

    @Test
    void confirmRequestEventNotAvailable() {
        UserShortDto user = new UserShortDto(2L, "User");
        EventFullDto event = new EventFullDto(
                1L,
                "Title",
                "Annotation",
                "Description",
                null,
                null,
                true,
                null,
                user,
                1,
                2,
                true,
                "PUBLISHED",
                null,
                null,
                0L
        );
        Mockito.when(eventService.getUserEventById(2L, 1L)).thenReturn(event);

        assertThrows(ForbiddenOperationException.class, () -> requestService.confirmRequest(2L, 1L, 1L));
    }

    @Test
    void rejectRequest() {
        ParticipationRequest savedRequest = new ParticipationRequest(1L, 1L, 1L,
                date, RequestStatus.REJECTED);
        ParticipationRequestDto requestExpected = new ParticipationRequestDto(1L, 1L, 1L,
                date, "REJECTED");
        Mockito.when(requestRepository.findById(1L)).thenReturn(Optional.of(request));
        Mockito.when(requestRepository.save(savedRequest)).thenReturn(savedRequest);

        ParticipationRequestDto rejectedRequest = requestService.rejectRequest(2L, 1L, 1L);

        assertNotNull(rejectedRequest);
        assertEquals(requestExpected, rejectedRequest);
    }
}
