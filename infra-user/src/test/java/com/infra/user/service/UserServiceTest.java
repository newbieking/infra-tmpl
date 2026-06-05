package com.infra.user.service;

import com.infra.common.dto.UserDto;
import com.infra.user.entity.User;
import com.infra.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void createShouldSaveAndReturnDto() {
        when(userRepository.existsByUsername("alice")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });

        UserDto result = userService.create(new UserDto(null, "alice", "alice@test.com", null, null));

        assertEquals("alice", result.username());
        assertEquals(1L, result.id());
    }

    @Test
    void createWithDuplicateUsernameShouldThrow() {
        when(userRepository.existsByUsername("alice")).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> userService.create(new UserDto(null, "alice", "a@b.com", null, null)));
    }

    @Test
    void findByIdShouldReturnDtoFromDb() {
        User user = new User("alice", "a@b.com");
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDto result = userService.findById(1L);

        assertEquals("alice", result.username());
        verify(valueOperations).set(eq("user:1"), any(), anyLong(), any(TimeUnit.class));
    }

    @Test
    void findByIdNotFoundShouldThrow() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.findById(99L));
    }

    @Test
    void findAllShouldReturnList() {
        User user = new User("alice", "a@b.com");
        user.setId(1L);
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserDto> result = userService.findAll();

        assertEquals(1, result.size());
    }

    @Test
    void updateShouldModifyAndReturn() {
        User user = new User("old", "old@b.com");
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserDto result = userService.update(1L, new UserDto(null, "new", "new@b.com", null, null));

        assertEquals("new", result.username());
    }

    @Test
    void deleteShouldCallRepository() {
        userService.delete(1L);
        verify(userRepository).deleteById(1L);
    }
}
