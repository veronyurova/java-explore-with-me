package ru.practicum.evm.main.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import ru.practicum.evm.main.repository.CompilationRepository;
import ru.practicum.evm.main.repository.CompilationEventRepository;
import ru.practicum.evm.main.mapper.CompilationMapper;
import ru.practicum.evm.main.model.Compilation;
import ru.practicum.evm.main.model.CompilationEvent;
import ru.practicum.evm.main.dto.CompilationDto;
import ru.practicum.evm.main.dto.NewCompilationDto;
import ru.practicum.evm.main.dto.EventShortDto;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.Min;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Validated
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final CompilationEventRepository compilationEventRepository;
    private final EventService eventService;

    @Autowired
    public CompilationServiceImpl(CompilationRepository compilationRepository,
                                  CompilationEventRepository compilationEventRepository,
                                  EventService eventService) {
        this.compilationRepository = compilationRepository;
        this.compilationEventRepository = compilationEventRepository;
        this.eventService = eventService;
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, @Min(0) int from, @Min(1) int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return compilationRepository.findAllByPinned(pinned, pageable)
                .stream()
                .map(c -> CompilationMapper.toCompilationDto(c, getCompilationEvents(c.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        Compilation compilation = findCompilationById(compId);
        return CompilationMapper.toCompilationDto(compilation, getCompilationEvents(compId));
    }

    @Override
    public CompilationDto addCompilation(@Valid NewCompilationDto newCompilationDto) {
        Compilation compilation = CompilationMapper.toCompilation(newCompilationDto);
        Compilation addedCompilation = compilationRepository.save(compilation);
        Long compId = addedCompilation.getId();
        for (Long eventId : newCompilationDto.getEvents()) {
            CompilationEvent compilationEvent = new CompilationEvent(null, compId, eventId);
            compilationEventRepository.save(compilationEvent);
        }
        log.info("CompilationServiceImpl.addCompilation: compilation {} successfully added", compId);
        return CompilationMapper.toCompilationDto(addedCompilation, getCompilationEvents(compId));
    }

    @Override
    public void deleteCompilationById(Long compId) {
        compilationRepository.deleteById(compId);
        log.info("CompilationServiceImpl.deleteCompilationById: compilation {} successfully deleted", compId);
    }

    @Override
    public void addEventToCompilation(Long compId, Long eventId) {
        CompilationEvent compilationEvent = new CompilationEvent(null, compId, eventId);
        compilationEventRepository.save(compilationEvent);
        log.info("CompilationServiceImpl.addEventToCompilation: event {} " +
                 "successfully added to compilation {}", eventId, compId);
    }

    @Override
    public void deleteEventFromCompilation(Long compId, Long eventId) {
        compilationEventRepository.deleteByCompilationAndEvent(compId, eventId);
        log.info("CompilationServiceImpl.deleteEventFromCompilation: event {} " +
                 "successfully deleted from compilation {}", eventId, compId);
    }

    @Override
    public void pinCompilation(Long compId) {
        Compilation compilation = findCompilationById(compId);
        compilation.setPinned(true);
        compilationRepository.save(compilation);
        log.info("CompilationServiceImpl.pinCompilation: compilation {} successfully pinned", compId);
    }

    @Override
    public void unpinCompilation(Long compId) {
        Compilation compilation = findCompilationById(compId);
        compilation.setPinned(false);
        compilationRepository.save(compilation);
        log.info("CompilationServiceImpl.pinCompilation: compilation {} successfully unpinned", compId);
    }

    private Compilation findCompilationById(Long compId) {
        Optional<Compilation> compilationOptional = compilationRepository.findById(compId);
        if (compilationOptional.isEmpty()) {
            String message = String.format("Compilation with id=%d was not found.", compId);
            log.warn("EntityNotFoundException at CompilationServiceImpl: {}", message);
            throw new EntityNotFoundException(message);
        }
        return compilationOptional.get();
    }

    private List<EventShortDto> getCompilationEvents(Long compId) {
        List<Long> ids = compilationEventRepository.findCompilationEventIds(compId);
        return eventService.getEventsByIds(ids);
    }
}
