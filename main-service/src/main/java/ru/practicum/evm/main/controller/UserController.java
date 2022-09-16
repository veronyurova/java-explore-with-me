package ru.practicum.evm.main.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.evm.main.service.UserService;
import ru.practicum.evm.main.mapper.UserMapper;
import ru.practicum.evm.main.model.User;
import ru.practicum.evm.main.dto.UserDto;
import ru.practicum.evm.main.dto.NewUserRequest;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin/users")
@Validated
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDto> getUsers(@RequestParam(required = false) Set<Long> ids,
                                  @Min(0) @RequestParam(defaultValue = "0") int from,
                                  @Min(1) @RequestParam(defaultValue = "10") int size) {
        if (ids != null && !ids.isEmpty()) {
            return userService.getUsersByIds(ids)
                    .stream()
                    .map(UserMapper::toUserDto)
                    .collect(Collectors.toList());
        }
        return userService.getUsers(from, size)
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public UserDto addUser(@Valid @RequestBody NewUserRequest newUserRequest) {
        User user = UserMapper.toUser(newUserRequest);
        return UserMapper.toUserDto(userService.addUser(user));
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable Long userId) {
        userService.deleteUserById(userId);
    }
}
