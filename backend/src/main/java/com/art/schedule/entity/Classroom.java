package com.art.schedule.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("classrooms")
public class Classroom {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String type;
    private Integer capacity;
    private String floor;
    private String status;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @TableLogic
    private Integer deleted;

    @TableField(exist = false)
    private List<String> equipment;
}
