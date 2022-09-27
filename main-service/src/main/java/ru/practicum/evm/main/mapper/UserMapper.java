package ru.practicum.evm.main.mapper;

import ru.practicum.evm.main.model.User;
import ru.practicum.evm.main.dto.UserDto;
import ru.practicum.evm.main.dto.NewUserRequest;

public class UserMapper {
    public static User toUser(NewUserRequest newUserRequest) {
        return new User(null, newUserRequest.getEmail(), newUserRequest.getName());
    }

    public static UserDto toUserDto(User user) {
        return new UserDto(user.getId(), user.getEmail(), user.getName());
    }
}
