package ru.practicum.evm.main.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import ru.practicum.evm.main.repository.UserRepository;
import ru.practicum.evm.main.mapper.UserMapper;
import ru.practicum.evm.main.model.User;
import ru.practicum.evm.main.dto.UserDto;
import ru.practicum.evm.main.dto.NewUserRequest;

import javax.validation.constraints.Min;
import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@Validated
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserDto> getUsers(@Min(0) int from, @Min(1) int size) {
        return userRepository.findAll(PageRequest.of(from / size, size))
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDto> getUsersByIds(Set<Long> ids) {
        return userRepository.findUsersByIdIn(ids)
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto addUser(@Valid NewUserRequest newUserRequest) {
        User user = UserMapper.toUser(newUserRequest);
        User addedUser = userRepository.save(user);
        log.info("UserServiceImpl.addUser: user {} successfully added", addedUser.getId());
        return UserMapper.toUserDto(addedUser);
    }

    @Override
    public void deleteUserById(Long userId) {
        userRepository.deleteById(userId);
        log.info("UserServiceImpl.deleteUserById: user {} successfully deleted", userId);
    }
}
