package ru.practicum.evm.main.service;

import ru.practicum.evm.main.dto.UserDto;
import ru.practicum.evm.main.dto.NewUserRequest;

import javax.validation.constraints.Min;
import javax.validation.Valid;
import java.util.List;
import java.util.Set;

public interface UserService {
    List<UserDto> getUsers(@Min(0) int from, @Min(1) int size);

    List<UserDto> getUsersByIds(Set<Long> ids);

    UserDto addUser(@Valid NewUserRequest newUserRequest);

    void deleteUserById(Long userId);
}
