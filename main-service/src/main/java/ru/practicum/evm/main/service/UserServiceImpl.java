package ru.practicum.evm.main.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.evm.main.model.User;
import ru.practicum.evm.main.repository.UserRepository;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Page<User> getUsers(int from, int size) {
        return userRepository.findAll(PageRequest.of(from / size, size));
    }

    @Override
    public List<User> getUsersByIds(Set<Long> ids) {
        return userRepository.findUsersByIdIn(ids);
    }

    @Override
    public User addUser(User user) {
        User addedUser = userRepository.save(user);
        log.info("UserServiceImpl.addUser: user {} successfully added", addedUser.getId());
        return addedUser;
    }

    @Override
    public void deleteUserById(Long userId) {
        userRepository.deleteById(userId);
        log.info("UserServiceImpl.deleteUserById: user {} successfully deleted", userId);
    }
}
