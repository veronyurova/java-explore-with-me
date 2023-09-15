package ru.practicum.evm.main.service;

import ru.practicum.evm.main.dto.CompilationDto;
import ru.practicum.evm.main.dto.NewCompilationDto;

import javax.validation.constraints.Min;
import javax.validation.Valid;
import java.util.List;

public interface CompilationService {
    List<CompilationDto> getCompilations(Boolean pinned, @Min(0) int from, @Min(1) int size);

    CompilationDto getCompilationById(Long compId);

    CompilationDto addCompilation(@Valid NewCompilationDto newCompilationDto);

    void deleteCompilationById(Long compId);

    void addEventToCompilation(Long compId, Long eventId);

    void deleteEventFromCompilation(Long compId, Long eventId);

    void pinCompilation(Long compId);

    void unpinCompilation(Long compId);
}
