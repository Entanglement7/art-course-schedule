package com.art.schedule.dto;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Data
public class CourseRequest {
    @NotBlank(message = "课程名称不能为空")
    private String name;
    @NotBlank(message = "课程类别不能为空")
    private String category;
    @NotBlank(message = "课程类型不能为空")
    private String type;
    @NotNull(message = "课时时长不能为空")
    private Integer duration;
    @NotBlank(message = "适合年龄不能为空")
    private String ageRange;
    @NotNull(message = "价格不能为空")
    private BigDecimal price;
    private String description;
}
