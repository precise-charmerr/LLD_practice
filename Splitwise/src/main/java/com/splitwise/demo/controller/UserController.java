package com.splitwise.demo.controller;

import com.splitwise.demo.dto.GroupCreateRequest;
import com.splitwise.demo.dto.SigninRequest;
import com.splitwise.demo.dto.SignupRequest;
import com.splitwise.demo.service.AuthService;
import com.splitwise.demo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    private final AuthService authService;
    private final UserService userService;

    public UserController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signupUser(@RequestBody SignupRequest signupRequest) throws Exception {
        authService.registerUser(signupRequest);
        return ResponseEntity.ok("User signed up successfully");
    }

    @PostMapping(path = "/signin")
    public ResponseEntity<String> signinUser(@RequestBody SigninRequest signinRequest) {
        authService.authenticateUser(signinRequest);
        return ResponseEntity.ok("User signed in successfully");
    }

    @PostMapping(path = "/create-group")
    public ResponseEntity<String> createGroup(@RequestBody GroupCreateRequest groupCreateRequest) {
        userService.addGroup(groupCreateRequest);
        return ResponseEntity.ok("group created successfully");
    }
}
