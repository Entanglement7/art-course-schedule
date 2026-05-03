package com.art.schedule.dto;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.util.List;

@Data
public class TeacherRequest {
    @NotBlank(message = "姓名不能为空")
    private String name;
    @NotBlank(message = "电话不能为空")
    private String phone;
    @NotBlank(message = "专业不能为空")
    private String specialty;
    @NotNull(message = "入职日期不能为空")
    private String joinDate;
    private List<String> courses;
}
