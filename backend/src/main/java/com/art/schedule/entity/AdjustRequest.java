package com.art.schedule.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@TableName("adjust_requests")
public class AdjustRequest {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long applicantId;
    private Long classId;
    private Long scheduleId;
    private String originalTime;
    private Integer newDayOfWeek;
    private LocalTime newStartTime;
    private LocalTime newEndTime;
    private String reason;
    private String status;
    private String rejectReason;
    private LocalDateTime applyTime;
    private LocalDateTime reviewedAt;
    private Long reviewerId;

    @TableField(exist = false)
    private String applicantName;
    @TableField(exist = false)
    private String className;
    @TableField(exist = false)
    private String courseName;
}
