package com.art.schedule.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度3-20位")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 30, message = "密码长度6-30位")
    private String password;

    @NotBlank(message = "姓名不能为空")
    private String name;

    // 注册时只允许选 teacher 或 student，admin 不开放自助注册
    private String role = "student";
}
