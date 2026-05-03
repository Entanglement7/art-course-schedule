package com.art.schedule.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@TableName("schedules")
public class Schedule {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long classId;
    private Long courseId;
    private Long teacherId;
    private Long classroomId;
    private LocalDate date;
    private Integer dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @TableField(exist = false)
    private String className;
    @TableField(exist = false)
    private String courseName;
    @TableField(exist = false)
    private String courseCategory;
    @TableField(exist = false)
    private String teacherName;
    @TableField(exist = false)
    private String classroomName;
}
