package com.infra.user.service;

import com.infra.common.dto.UserDto;
import com.infra.user.entity.User;
import com.infra.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void createShouldSaveAndReturnDto() {
        when(userRepository.existsByUsername("alice")).thenReturn(false);
        when(userRepository.existsByEmail("alice@test.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });

        UserDto result = userService.create(new UserDto(null, "alice", "alice@test.com", null, null));

        assertEquals("alice", result.username());
        assertEquals("alice@test.com", result.email());
        assertEquals(1L, result.id());
    }

    @Test
    void createWithDuplicateUsernameShouldThrow() {
        when(userRepository.existsByUsername("alice")).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> userService.create(new UserDto(null, "alice", "a@b.com", null, null)));
    }

    @Test
    void createWithDuplicateEmailShouldThrow() {
        when(userRepository.existsByUsername("alice")).thenReturn(false);
        when(userRepository.existsByEmail("a@b.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> userService.create(new UserDto(null, "alice", "a@b.com", null, null)));
    }

    @Test
    void findAllShouldReturnList() {
        User user = new User("alice", "a@b.com");
        user.setId(1L);
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserDto> result = userService.findAll();

        assertEquals(1, result.size());
        assertEquals("alice", result.get(0).username());
    }

    @Test
    void findByIdShouldReturnDto() {
        User user = new User("alice", "a@b.com");
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDto result = userService.findById(1L);

        assertEquals("alice", result.username());
    }

    @Test
    void findByIdNotFoundShouldThrow() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.findById(99L));
    }

    @Test
    void updateShouldModifyAndReturn() {
        User user = new User("old", "old@b.com");
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserDto result = userService.update(1L, new UserDto(null, "new", "new@b.com", null, null));

        assertEquals("new", result.username());
        assertEquals("new@b.com", result.email());
    }

    @Test
    void deleteShouldCallRepository() {
        userService.delete(1L);
        verify(userRepository).deleteById(1L);
    }
}
