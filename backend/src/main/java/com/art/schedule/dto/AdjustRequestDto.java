package com.art.schedule.dto;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class AdjustRequestDto {
    @NotNull(message = "班级不能为空")
    private Long classId;
    private Long scheduleId;
    @NotBlank(message = "原上课时间不能为空")
    private String originalTime;
    private Integer newDayOfWeek;
    private String newStartTime;
    private String newEndTime;
    @NotBlank(message = "调课原因不能为空")
    private String reason;
}
