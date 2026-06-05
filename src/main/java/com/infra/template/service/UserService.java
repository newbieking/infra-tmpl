package com.infra.template.service;

import com.infra.template.dto.UserDto;
import com.infra.template.entity.User;
import com.infra.template.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDto create(UserDto dto) {
        if (userRepository.existsByUsername(dto.username())) {
            throw new IllegalArgumentException("Username already exists: " + dto.username());
        }
        User user = new User(dto.username(), dto.email());
        user = userRepository.save(user);
        return toDto(user);
    }

    @Transactional(readOnly = true)
    public List<UserDto> findAll() {
        return userRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public UserDto findById(Long id) {
        return userRepository.findById(id).map(this::toDto)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    }

    public UserDto update(Long id, UserDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        user.setUpdatedAt(LocalDateTime.now());
        user = userRepository.save(user);
        return toDto(user);
    }

    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found: " + id);
        }
        userRepository.deleteById(id);
    }

    private UserDto toDto(User user) {
        return new UserDto(user.getId(), user.getUsername(), user.getEmail(),
                user.getCreatedAt(), user.getUpdatedAt());
    }
}
