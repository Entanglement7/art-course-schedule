package com.art.schedule.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("teachers")
public class Teacher {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String phone;
    private String specialty;
    private LocalDate joinDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @TableLogic
    private Integer deleted;

    @TableField(exist = false)
    private List<String> courses;

    @TableField(exist = false)
    private String specialties;

    @TableField(exist = false)
    private Integer classCount;

    @TableField(exist = false)
    private Integer studentCount;

    @TableField(exist = false)
    private Integer weekCourseCount;

    @TableField(exist = false)
    private Integer monthCourseCount;

    @TableField(exist = false)
    private Integer teachingYears;

    @TableField(exist = false)
    private List<String> classes;
}
