package com.art.schedule.dto;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class ClazzRequest {
    @NotBlank(message = "班级名称不能为空")
    private String name;
    @NotBlank(message = "课程类别不能为空")
    private String category;
    @NotBlank(message = "课程名称不能为空")
    private String courseName;
    @NotNull(message = "授课教师不能为空")
    private Long teacherId;
    @NotNull(message = "班级容量不能为空")
    private Integer studentCount;
    @NotBlank(message = "上课时间不能为空")
    private String schedule;
    @NotBlank(message = "开班日期不能为空")
    private String startDate;
}
