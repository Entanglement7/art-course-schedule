package com.art.schedule.dto;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.util.List;

@Data
public class SmartScheduleRequest {
    private Long classId;               // 班级ID（如果选择已有班级）
    private String className;           // 班级名称（如果新建班级）

    @NotNull(message = "教师不能为空")
    private Long teacherId;

    @NotNull(message = "教室不能为空")
    private Long classroomId;

    @NotNull(message = "学生列表不能为空")
    @Size(min = 1, message = "至少选择一个学生")
    private List<Long> studentIds;

    @NotNull(message = "每周上课次数不能为空")
    @Min(value = 1, message = "每周至少上1次课")
    @Max(value = 5, message = "每周最多上5次课")
    private Integer sessionsPerWeek;

    @NotBlank(message = "排课周期不能为空")
    private String schedulePeriod;      // "week" | "two-weeks" | "month"

    @NotBlank(message = "开始日期不能为空")
    private String startDate;
}
