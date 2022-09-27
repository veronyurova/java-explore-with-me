package ru.practicum.evm.main.service;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Page;
import ru.practicum.evm.main.repository.UserRepository;
import ru.practicum.evm.main.model.User;
import ru.practicum.evm.main.dto.UserDto;
import ru.practicum.evm.main.dto.NewUserRequest;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    private final Pageable pageable = Pageable.ofSize(10);
    private final User user = new User(1L, "email@mail.com", "User");
    private final UserDto userDto = new UserDto(1L, "email@mail.com", "User");

    @BeforeEach
    void beforeEach() {
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void getUsers() {
        Mockito.when(userRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(user)));

        List<UserDto> usersExpected = List.of(userDto);
        List<UserDto> users = userService.getUsers(0, 10);

        assertNotNull(users);
        assertEquals(usersExpected, users);
    }

    @Test
    void getUsersNoUsers() {
        Mockito.when(userRepository.findAll(pageable)).thenReturn(Page.empty());

        List<UserDto> users = userService.getUsers(0, 10);

        assertNotNull(users);
        assertEquals(0, users.size());
    }

    @Test
    void getUsersByIds() {
        Mockito.when(userRepository.findUsersByIdIn(Set.of(1L))).thenReturn(List.of(user));

        List<UserDto> usersExpected = List.of(userDto);
        List<UserDto> users = userService.getUsersByIds(Set.of(1L));

        assertNotNull(users);
        assertEquals(usersExpected, users);
    }

    @Test
    void getUsersByIdsNoUsers() {
        Mockito.when(userRepository.findUsersByIdIn(Set.of(1L))).thenReturn(Collections.emptyList());

        List<UserDto> users = userService.getUsersByIds(Set.of(1L));

        assertNotNull(users);
        assertEquals(0, users.size());
    }

    @Test
    void addUser() {
        User newUser = new User(null, "email@mail.com", "User");
        NewUserRequest newUserRequest = new NewUserRequest("email@mail.com", "User");
        Mockito.when(userRepository.save(newUser)).thenReturn(user);

        UserDto addedUser = userService.addUser(newUserRequest);

        assertNotNull(addedUser);
        assertEquals(userDto, addedUser);
    }
}
