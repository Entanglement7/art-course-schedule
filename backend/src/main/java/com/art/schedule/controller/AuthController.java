package com.art.schedule.controller;

import com.art.schedule.common.Result;
import com.art.schedule.dto.LoginRequest;
import com.art.schedule.dto.LoginResponse;
import com.art.schedule.dto.RegisterRequest;
import com.art.schedule.entity.User;
import com.art.schedule.service.impl.AuthService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public Result<LoginResponse> register(@Valid @RequestBody RegisterRequest req) {
        return Result.success(authService.register(req));
    }

    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        return Result.success(authService.login(req));
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.success();
    }

    @GetMapping("/me")
    public Result<User> me(@AuthenticationPrincipal User user) {
        user.setPassword(null);
        return Result.success(user);
    }
}
