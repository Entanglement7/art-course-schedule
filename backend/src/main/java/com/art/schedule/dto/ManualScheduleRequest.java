package com.art.schedule.dto;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class ManualScheduleRequest {
    @NotNull(message = "班级不能为空")
    private Long classId;
    @NotNull(message = "课程不能为空")
    private Long courseId;
    @NotNull(message = "教室不能为空")
    private Long classroomId;
    @NotNull(message = "星期不能为空")
    private Integer dayOfWeek;
    @NotBlank(message = "开始时间不能为空")
    private String startTime;
    @NotBlank(message = "结束时间不能为空")
    private String endTime;
    @NotBlank(message = "开始日期不能为空")
    private String startDate;
    private String repeatType;
    private Integer weekCount;
}
