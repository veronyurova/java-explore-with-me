package ru.practicum.evm.main.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.evm.main.service.UserService;
import ru.practicum.evm.main.dto.UserDto;
import ru.practicum.evm.main.dto.NewUserRequest;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDto> getUsers(@RequestParam(required = false) Set<Long> ids,
                                  @RequestParam(defaultValue = "0") int from,
                                  @RequestParam(defaultValue = "10") int size) {
        if (ids != null && !ids.isEmpty()) return userService.getUsersByIds(ids);
        return userService.getUsers(from, size);
    }

    @PostMapping
    public UserDto addUser(@RequestBody NewUserRequest newUserRequest) {
        return userService.addUser(newUserRequest);
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable Long userId) {
        userService.deleteUserById(userId);
    }
}
