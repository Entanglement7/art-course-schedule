package com.art.schedule.controller;

import com.art.schedule.common.Result;
import com.art.schedule.entity.User;
import com.art.schedule.service.impl.DashboardService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public Result<Map<String, Object>> dashboard(@AuthenticationPrincipal User user) {
        Map<String, Object> data = switch (user.getRole()) {
            case "admin" -> dashboardService.adminStats();
            case "teacher" -> {
                if (user.getTeacherId() == null) {
                    yield dashboardService.unlinkedTeacherStats();
                }
                yield dashboardService.teacherStats(user.getTeacherId());
            }
            case "student" -> {
                if (user.getStudentId() == null) {
                    yield dashboardService.unlinkedStudentStats();
                }
                yield dashboardService.studentStats(user.getStudentId());
            }
            default -> Map.of();
        };
        return Result.success(data);
    }
}
