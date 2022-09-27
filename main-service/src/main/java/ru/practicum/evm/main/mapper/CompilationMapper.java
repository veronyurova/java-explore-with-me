package ru.practicum.evm.main.mapper;

import ru.practicum.evm.main.model.Compilation;
import ru.practicum.evm.main.dto.CompilationDto;
import ru.practicum.evm.main.dto.NewCompilationDto;
import ru.practicum.evm.main.dto.EventShortDto;

import java.util.List;

public class CompilationMapper {
    public static Compilation toCompilation(NewCompilationDto newCompilationDto) {
        return new Compilation(
                null,
                newCompilationDto.getTitle(),
                newCompilationDto.getPinned()
        );
    }

    public static CompilationDto toCompilationDto(Compilation compilation,
                                                  List<EventShortDto> compilationEvents) {
        return new CompilationDto(
                compilation.getId(),
                compilation.getTitle(),
                compilation.getPinned(),
                compilationEvents
        );
    }
}
