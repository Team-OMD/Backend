package com.onmydesk.backend.user.controller;

import com.onmydesk.backend.user.domain.User;
import com.onmydesk.backend.user.dto.UserRequest;
import com.onmydesk.backend.user.dto.UserResponse;
import com.onmydesk.backend.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Validated @RequestBody UserRequest userRequest
    ) {
        return ResponseEntity.ok(userService.signup(userRequest));
    }

    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<UserResponse> getMyUserInfo(HttpServletRequest request) {
        return ResponseEntity.ok(userService.getMyUserWithAuthorities());
    }

//    @GetMapping("/user/{username}")
//    @PreAuthorize("hasAnyRole('ADMIN')")
//    public ResponseEntity<UserRequest> getUserInfo(@PathVariable String username) {
//        return ResponseEntity.ok(userService.getUserWithAuthorities(username));
//    }
}