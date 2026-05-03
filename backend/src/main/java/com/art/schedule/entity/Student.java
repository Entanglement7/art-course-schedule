package com.art.schedule.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("students")
public class Student {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Integer age;
    private String gender;
    private String parentName;
    private String phone;
    private LocalDate enrollDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @TableLogic
    private Integer deleted;

    @TableField(exist = false)
    private List<String> courses;
}
