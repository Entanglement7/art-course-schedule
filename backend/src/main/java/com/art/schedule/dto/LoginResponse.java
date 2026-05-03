package com.art.schedule.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private UserInfoDto userInfo;

    @Data
    public static class UserInfoDto {
        private Long id;
        private String username;
        private String name;
        private String role;
        private Long teacherId;
        private Long studentId;
    }
}
