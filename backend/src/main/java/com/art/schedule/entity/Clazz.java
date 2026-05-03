package com.art.schedule.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("classes")
public class Clazz {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String category;
    private String courseName;
    private Long teacherId;
    private Integer studentCount;
    private String schedule;
    private LocalDate startDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @TableLogic
    private Integer deleted;

    @TableField(exist = false)
    private String teacherName;

    @TableField(exist = false)
    private Integer currentCount;
}
