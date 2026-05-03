package com.art.schedule.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("courses")
public class Course {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String category;
    private String type;
    private Integer duration;
    private String ageRange;
    private BigDecimal price;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @TableLogic
    private Integer deleted;
}
