package ru.practicum.evm.main.service;

import org.springframework.data.domain.Page;
import ru.practicum.evm.main.model.User;

import java.util.List;
import java.util.Set;

public interface UserService {
    Page<User> getUsers(int from, int size);

    List<User> getUsersByIds(Set<Long> ids);

    User addUser(User user);

    void deleteUserById(Long userId);
}
