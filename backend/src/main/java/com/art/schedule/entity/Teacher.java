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
}
