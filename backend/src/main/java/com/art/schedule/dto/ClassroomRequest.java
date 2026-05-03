package com.art.schedule.dto;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.util.List;

@Data
public class ClassroomRequest {
    @NotBlank(message = "教室名称不能为空")
    private String name;
    @NotBlank(message = "教室类型不能为空")
    private String type;
    @NotNull(message = "容纳人数不能为空")
    private Integer capacity;
    @NotBlank(message = "楼层不能为空")
    private String floor;
    private String status;
    private String remark;
    private List<String> equipment;
}
