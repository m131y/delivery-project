package org.example.delivery.user.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.delivery.user.dto.request.LoginRequest;
import org.example.delivery.user.dto.request.SignupRequest;
import org.example.delivery.user.dto.response.UserResponse;
import org.example.delivery.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(@RequestBody @Valid SignupRequest signupRequest) {
        return ResponseEntity.ok(userService.signup(signupRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@RequestBody @Valid LoginRequest loginRequest, HttpServletResponse res) {
        return ResponseEntity.ok(userService.login(loginRequest, res));
    }

}
