package com.art.schedule.controller;

import com.art.schedule.common.Result;
import com.art.schedule.dto.LoginRequest;
import com.art.schedule.dto.LoginResponse;
import com.art.schedule.dto.RegisterRequest;
import com.art.schedule.entity.User;
import com.art.schedule.mapper.UserMapper;
import com.art.schedule.service.impl.AuthService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserMapper userMapper;

    public AuthController(AuthService authService, UserMapper userMapper) {
        this.authService = authService;
        this.userMapper = userMapper;
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

    @PostMapping("/change-password")
    public Result<Void> changePassword(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ChangePasswordRequest req) {
        User u = userMapper.selectById(user.getId());
        if (!req.getOldPassword().equals(u.getPassword())) {
            throw new IllegalArgumentException("原密码错误");
        }
        u.setPassword(req.getNewPassword());
        userMapper.updateById(u);
        return Result.success();
    }

    @GetMapping("/unlinked-users")
    public Result<List<Map<String, Object>>> getUnlinkedUsers(@RequestParam String role) {
        LambdaQueryWrapper<User> query = new LambdaQueryWrapper<>();
        query.eq(User::getRole, role);
        if ("teacher".equals(role)) {
            query.isNull(User::getTeacherId);
        } else if ("student".equals(role)) {
            query.isNull(User::getStudentId);
        }
        query.select(User::getId, User::getUsername, User::getName);

        List<Map<String, Object>> users = userMapper.selectList(query).stream()
                .map(u -> Map.<String, Object>of(
                    "id", u.getId(),
                    "username", u.getUsername(),
                    "name", u.getName()
                ))
                .toList();
        return Result.success(users);
    }

    @Data
    public static class ChangePasswordRequest {
        @NotBlank(message = "原密码不能为空")
        private String oldPassword;
        @NotBlank(message = "新密码不能为空")
        private String newPassword;
    }
}
