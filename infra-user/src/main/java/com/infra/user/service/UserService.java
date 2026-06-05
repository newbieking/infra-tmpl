package com.infra.user.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.infra.common.dto.UserDto;
import com.infra.user.entity.User;
import com.infra.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private static final String CACHE_PREFIX = "user:";

    private final UserRepository userRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final Cache<String, Object> localCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();

    public UserService(UserRepository userRepository, RedisTemplate<String, Object> redisTemplate) {
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
    }

    @Transactional(readOnly = true)
    public UserDto findById(Long id) {
        String cacheKey = CACHE_PREFIX + id;

        // L1: 本地缓存
        Object cached = localCache.getIfPresent(cacheKey);
        if (cached instanceof UserDto dto) {
            log.debug("L1 命中: {}", cacheKey);
            return dto;
        }

        // L2: 分布式缓存
        Object redisVal = redisTemplate.opsForValue().get(cacheKey);
        if (redisVal instanceof UserDto dto) {
            log.debug("L2 命中: {}", cacheKey);
            localCache.put(cacheKey, dto);
            return dto;
        }

        // L3: 数据库
        UserDto dto = userRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));

        // 回填缓存
        redisTemplate.opsForValue().set(cacheKey, dto, 30, TimeUnit.MINUTES);
        localCache.put(cacheKey, dto);
        return dto;
    }

    public UserDto create(UserDto dto) {
        if (userRepository.existsByUsername(dto.username())) {
            throw new IllegalArgumentException("Username already exists: " + dto.username());
        }
        User user = new User(dto.username(), dto.email());
        return toDto(userRepository.save(user));
    }

    public UserDto update(Long id, UserDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        UserDto result = toDto(userRepository.save(user));
        // 清除缓存
        redisTemplate.delete(CACHE_PREFIX + id);
        localCache.invalidate(CACHE_PREFIX + id);
        return result;
    }

    @Transactional(readOnly = true)
    public List<UserDto> findAll() {
        return userRepository.findAll().stream().map(this::toDto).toList();
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
        // 清除缓存
        redisTemplate.delete(CACHE_PREFIX + id);
        localCache.invalidate(CACHE_PREFIX + id);
    }

    private UserDto toDto(User user) {
        return new UserDto(user.getId(), user.getUsername(), user.getEmail(),
                user.getCreatedAt() != null ? user.getCreatedAt().toString() : null,
                user.getUpdatedAt() != null ? user.getUpdatedAt().toString() : null);
    }
}
