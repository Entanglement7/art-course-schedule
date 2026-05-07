package com.art.schedule.dto;

import com.art.schedule.entity.Schedule;
import lombok.Data;
import java.util.List;
import java.util.ArrayList;

@Data
public class SmartScheduleResponse {
    private Boolean success;
    private String message;
    private Integer totalSchedules;
    private List<Schedule> schedules;
    private List<String> warnings;

    public SmartScheduleResponse() {
        this.schedules = new ArrayList<>();
        this.warnings = new ArrayList<>();
    }

    public static SmartScheduleResponse success(List<Schedule> schedules) {
        SmartScheduleResponse response = new SmartScheduleResponse();
        response.setSuccess(true);
        response.setMessage("排课成功");
        response.setTotalSchedules(schedules.size());
        response.setSchedules(schedules);
        return response;
    }

    public static SmartScheduleResponse failure(String message) {
        SmartScheduleResponse response = new SmartScheduleResponse();
        response.setSuccess(false);
        response.setMessage(message);
        response.setTotalSchedules(0);
        return response;
    }
}
