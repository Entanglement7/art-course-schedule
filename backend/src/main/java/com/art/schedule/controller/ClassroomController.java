package com.art.schedule.controller;

import com.art.schedule.common.PageResult;
import com.art.schedule.common.Result;
import com.art.schedule.dto.ClassroomRequest;
import com.art.schedule.entity.Classroom;
import com.art.schedule.service.impl.ClassroomService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/classrooms")
public class ClassroomController {

    private final ClassroomService classroomService;

    public ClassroomController(ClassroomService classroomService) {
        this.classroomService = classroomService;
    }

    @GetMapping
    public Result<PageResult<Classroom>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String type) {
        return Result.success(classroomService.list(page, size, search, type));
    }

    @GetMapping("/{id}")
    public Result<Classroom> getById(@PathVariable Long id) {
        return Result.success(classroomService.getById(id));
    }

    @PostMapping
    public Result<Classroom> create(@Valid @RequestBody ClassroomRequest req) {
        return Result.success(classroomService.create(req));
    }

    @PutMapping("/{id}")
    public Result<Classroom> update(@PathVariable Long id, @Valid @RequestBody ClassroomRequest req) {
        return Result.success(classroomService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        classroomService.delete(id);
        return Result.success();
    }
}
