package com.bookstore.service;

import com.bookstore.dto.UserDTO;
import com.bookstore.model.User;
import com.bookstore.repository.UserRepository;
import com.bookstore.security.JwtTokenProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public UserService(UserRepository userRepository, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public User registerUser(UserDTO userDTO) {
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User already exists");
        }
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        User user = new User(userDTO.getEmail(), passwordEncoder.encode(userDTO.getPassword()));
        return userRepository.save(user);
    }

    public String login(UserDTO userDTO) {
        String email = userDTO.getEmail();
        String password = userDTO.getPassword();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        if (new BCryptPasswordEncoder().matches(password, user.getPasswordHash())) {
            return jwtTokenProvider.createToken(email, user.getRole());
        } else {
            throw new IllegalArgumentException("Invalid username or password");
        }
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
}
