package com.infra.auth.service;

import com.infra.auth.entity.SysUser;
import com.infra.auth.repository.SysUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {

    private final SysUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(SysUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public SysUser authenticate(String username, String rawPassword) {
        SysUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在: " + username));

        if (!user.isEnabled()) {
            throw new IllegalArgumentException("账号已被禁用: " + username);
        }

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new IllegalArgumentException("密码错误");
        }

        return user;
    }

    public SysUser register(String username, String rawPassword, String role) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("用户名已存在: " + username);
        }

        SysUser user = new SysUser(username, passwordEncoder.encode(rawPassword), role);
        return userRepository.save(user);
    }

    public void changePassword(Long userId, String oldPassword, String newPassword) {
        SysUser user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在: " + userId));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("原密码错误");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
