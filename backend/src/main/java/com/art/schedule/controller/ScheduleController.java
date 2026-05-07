package com.art.schedule.controller;

import com.art.schedule.common.Result;
import com.art.schedule.dto.ManualScheduleRequest;
import com.art.schedule.dto.SmartScheduleRequest;
import com.art.schedule.dto.SmartScheduleResponse;
import com.art.schedule.entity.Schedule;
import com.art.schedule.service.impl.ScheduleService;
import com.art.schedule.service.impl.SmartScheduleService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final SmartScheduleService smartScheduleService;

    public ScheduleController(ScheduleService scheduleService, SmartScheduleService smartScheduleService) {
        this.scheduleService = scheduleService;
        this.smartScheduleService = smartScheduleService;
    }

    @GetMapping("/week")
    public Result<List<Schedule>> week(
            @RequestParam(required = false) String date) {
        String d = date != null ? date : LocalDate.now().toString();
        return Result.success(scheduleService.getWeekSchedule(d));
    }

    @GetMapping("/teacher/{teacherId}")
    public Result<List<Schedule>> teacher(
            @PathVariable Long teacherId,
            @RequestParam(required = false) String date) {
        String d = date != null ? date : LocalDate.now().toString();
        return Result.success(scheduleService.getTeacherSchedule(teacherId, d));
    }

    @GetMapping("/student/{studentId}")
    public Result<List<Schedule>> student(
            @PathVariable Long studentId,
            @RequestParam(required = false) String date) {
        String d = date != null ? date : LocalDate.now().toString();
        return Result.success(scheduleService.getStudentSchedule(studentId, d));
    }

    @GetMapping("/classroom/{classroomId}")
    public Result<List<Schedule>> classroom(
            @PathVariable Long classroomId,
            @RequestParam(required = false) String date) {
        String d = date != null ? date : LocalDate.now().toString();
        return Result.success(scheduleService.getClassroomSchedule(classroomId, d));
    }

    @PostMapping("/manual")
    public Result<List<Schedule>> manual(@Valid @RequestBody ManualScheduleRequest req) {
        return Result.success(scheduleService.createManual(req));
    }

    @PostMapping("/smart")
    public Result<SmartScheduleResponse> smartSchedule(@Valid @RequestBody SmartScheduleRequest req) {
        return Result.success(smartScheduleService.generateSchedule(req));
    }
}
