package com.example.library.service;

import com.example.library.entity.AppUser;
import com.example.library.repository.AppUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(AppUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AppUser register(String username, String password, String email) {
        if(userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }
        AppUser user = new AppUser(username, email, passwordEncoder.encode(password));
        userRepository.save(user);
        return user;
    }

    public Optional<AppUser> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
