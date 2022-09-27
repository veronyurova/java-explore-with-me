package ru.practicum.evm.main.mapper;

import ru.practicum.evm.main.model.ParticipationRequest;
import ru.practicum.evm.main.dto.ParticipationRequestDto;

public class RequestMapper {
    public static ParticipationRequestDto toRequestDto(ParticipationRequest request) {
        return new ParticipationRequestDto(
                request.getId(),
                request.getEvent(),
                request.getRequester(),
                request.getCreated(),
                request.getStatus().toString()
        );
    }
}
