package com.splitwise.demo.service;

import com.splitwise.demo.dto.SigninRequest;
import com.splitwise.demo.dto.SignupRequest;
import com.splitwise.demo.exception.ResourceNotFoundException;
import com.splitwise.demo.model.User;
import com.splitwise.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // register user
    public void registerUser(SignupRequest signupRequest) {
        if(userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new RuntimeException("User already exists");
        }
        User user = new User(signupRequest.getUsername(), signupRequest.getEmail(), signupRequest.getPassword());
        userRepository.save(user);
    }

    // authenticate user
    public void authenticateUser(SigninRequest signinRequest) {
        User user = userRepository.findByEmail(signinRequest.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + signinRequest.getEmail()));

        if(!user.getPassword().equals(signinRequest.getPassword())) {
            throw new RuntimeException("Passwords do not match!!");
        }
    }

}