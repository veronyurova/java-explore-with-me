package ru.practicum.evm.main.service;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.evm.main.repository.CompilationRepository;
import ru.practicum.evm.main.repository.CompilationEventRepository;
import ru.practicum.evm.main.dto.EventShortDto;
import ru.practicum.evm.main.dto.CompilationDto;
import ru.practicum.evm.main.dto.NewCompilationDto;
import ru.practicum.evm.main.model.*;

import javax.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class CompilationServiceTest {
    private CompilationService compilationService;
    @Mock
    private CompilationRepository compilationRepository;
    @Mock
    private CompilationEventRepository compEventRepository;
    @Mock
    private EventService eventService;
    private final Pageable pageable = Pageable.ofSize(10);
    private final EventShortDto event = new EventShortDto(
            1L,
            "Title",
            "Annotation",
            null,
            true,
            null,
            null,
            0,
            0L,
            1.0F
    );
    private final Compilation compilation = new Compilation(1L, "Title", true);
    private final CompilationDto compilationDto = new CompilationDto(1L, "Title", true, List.of(event));

    @BeforeEach
    void beforeEach() {
        compilationService = new CompilationServiceImpl(compilationRepository, compEventRepository, eventService);
    }

    @Test
    void getCompilations() {
        Mockito
                .when(compilationRepository.findAllByPinned(true, pageable))
                .thenReturn(List.of(compilation));
        Mockito.when(compEventRepository.findCompilationEventIds(1L)).thenReturn(List.of(1L));
        Mockito.when(eventService.getEventsByIds(List.of(1L))).thenReturn(List.of(event));

        List<CompilationDto> compilationsExpected = List.of(compilationDto);
        List<CompilationDto> compilations = compilationService.getCompilations(true, 0, 10);

        assertNotNull(compilations);
        assertEquals(compilationsExpected, compilations);
    }

    @Test
    void getCompilationsNoCompilations() {
        Mockito
                .when(compilationRepository.findAllByPinned(true, pageable))
                .thenReturn(Collections.emptyList());

        List<CompilationDto> compilations = compilationService.getCompilations(true, 0, 10);

        assertNotNull(compilations);
        assertEquals(0, compilations.size());
    }

    @Test
    void getCompilationById() {
        Mockito.when(compilationRepository.findById(1L)).thenReturn(Optional.of(compilation));
        Mockito.when(compEventRepository.findCompilationEventIds(1L)).thenReturn(List.of(1L));
        Mockito.when(eventService.getEventsByIds(List.of(1L))).thenReturn(List.of(event));

        CompilationDto compilation = compilationService.getCompilationById(1L);

        assertNotNull(compilation);
        assertEquals(compilationDto, compilation);
    }

    @Test
    void getCompilationByIdNoSuchCompilation() {
        Mockito.when(compilationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> compilationService.getCompilationById(1L));
    }

    @Test
    void addCompilation() {
        Compilation newCompilation = new Compilation(null, "Title", true);
        NewCompilationDto newCompilationDto = new NewCompilationDto("Title", true, List.of(1L));
        Mockito.when(compilationRepository.save(newCompilation)).thenReturn(compilation);
        Mockito.when(compEventRepository.findCompilationEventIds(1L)).thenReturn(List.of(1L));
        Mockito.when(eventService.getEventsByIds(List.of(1L))).thenReturn(List.of(event));

        CompilationDto addedCompilation = compilationService.addCompilation(newCompilationDto);

        assertNotNull(addedCompilation);
        assertEquals(compilationDto, addedCompilation);
    }
}
