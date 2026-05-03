package com.art.schedule.controller;

import com.art.schedule.common.PageResult;
import com.art.schedule.common.Result;
import com.art.schedule.dto.TeacherRequest;
import com.art.schedule.entity.Teacher;
import com.art.schedule.service.impl.TeacherService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/teachers")
public class TeacherController {

    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @GetMapping
    public Result<PageResult<Teacher>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String specialty) {
        return Result.success(teacherService.list(page, size, search, specialty));
    }

    @GetMapping("/options")
    public Result<List<Map<String, Object>>> options() {
        return Result.success(teacherService.options());
    }

    @GetMapping("/{id}")
    public Result<Teacher> getById(@PathVariable Long id) {
        return Result.success(teacherService.getById(id));
    }

    @PostMapping
    public Result<Teacher> create(@Valid @RequestBody TeacherRequest req) {
        return Result.success(teacherService.create(req));
    }

    @PutMapping("/{id}")
    public Result<Teacher> update(@PathVariable Long id, @Valid @RequestBody TeacherRequest req) {
        return Result.success(teacherService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        teacherService.delete(id);
        return Result.success();
    }
}
