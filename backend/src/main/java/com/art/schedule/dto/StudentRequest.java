package com.art.schedule.dto;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.util.List;

@Data
public class StudentRequest {
    @NotBlank(message = "姓名不能为空")
    private String name;
    @NotNull(message = "年龄不能为空")
    @Min(value = 3, message = "年龄最小3岁")
    @Max(value = 18, message = "年龄最大18岁")
    private Integer age;
    @NotBlank(message = "性别不能为空")
    private String gender;
    @NotBlank(message = "家长姓名不能为空")
    private String parentName;
    @NotBlank(message = "联系电话不能为空")
    private String phone;
    @NotNull(message = "入学日期不能为空")
    private String enrollDate;
    private List<String> courses;
}
