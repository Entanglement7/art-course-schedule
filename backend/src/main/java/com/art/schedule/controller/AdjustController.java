package com.art.schedule.controller;

import com.art.schedule.common.PageResult;
import com.art.schedule.common.Result;
import com.art.schedule.dto.AdjustRequestDto;
import com.art.schedule.entity.AdjustRequest;
import com.art.schedule.entity.User;
import com.art.schedule.service.impl.AdjustService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/adjustments")
public class AdjustController {

    private final AdjustService adjustService;

    public AdjustController(AdjustService adjustService) {
        this.adjustService = adjustService;
    }

    @GetMapping
    public Result<PageResult<AdjustRequest>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @AuthenticationPrincipal User user) {
        // 教师只能看自己的申请，管理员看全部
        Long applicantId = "teacher".equals(user.getRole()) ? user.getId() : null;
        return Result.success(adjustService.list(page, size, status, startDate, endDate, applicantId));
    }

    @PostMapping
    public Result<AdjustRequest> create(@Valid @RequestBody AdjustRequestDto dto,
                                        @AuthenticationPrincipal User user) {
        return Result.success(adjustService.create(dto, user));
    }

    @PutMapping("/{id}/approve")
    public Result<AdjustRequest> approve(@PathVariable Long id,
                                         @AuthenticationPrincipal User user) {
        return Result.success(adjustService.approve(id, user.getId()));
    }

    @PutMapping("/{id}/reject")
    public Result<AdjustRequest> reject(@PathVariable Long id,
                                        @RequestBody Map<String, String> body,
                                        @AuthenticationPrincipal User user) {
        return Result.success(adjustService.reject(id, body.get("rejectReason"), user.getId()));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id,
                               @AuthenticationPrincipal User user) {
        adjustService.delete(id, user);
        return Result.success();
    }
}
