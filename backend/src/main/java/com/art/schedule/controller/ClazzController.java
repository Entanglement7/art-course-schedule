package com.art.schedule.controller;

import com.art.schedule.common.PageResult;
import com.art.schedule.common.Result;
import com.art.schedule.dto.ClazzRequest;
import com.art.schedule.entity.Clazz;
import com.art.schedule.entity.Student;
import com.art.schedule.service.impl.ClazzService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/classes")
public class ClazzController {

    private final ClazzService clazzService;

    public ClazzController(ClazzService clazzService) {
        this.clazzService = clazzService;
    }

    @GetMapping
    public Result<PageResult<Clazz>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Long teacherId) {
        return Result.success(clazzService.list(page, size, search, category, teacherId));
    }

    @GetMapping("/{id}")
    public Result<Clazz> getById(@PathVariable Long id) {
        return Result.success(clazzService.getById(id));
    }

    @PostMapping
    public Result<Clazz> create(@Valid @RequestBody ClazzRequest req) {
        return Result.success(clazzService.create(req));
    }

    @PutMapping("/{id}")
    public Result<Clazz> update(@PathVariable Long id, @Valid @RequestBody ClazzRequest req) {
        return Result.success(clazzService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        clazzService.delete(id);
        return Result.success();
    }

    @GetMapping("/{id}/students")
    public Result<List<Student>> getStudents(@PathVariable Long id) {
        return Result.success(clazzService.getStudents(id));
    }

    @PostMapping("/{id}/students")
    public Result<Void> addStudent(@PathVariable Long id, @RequestBody Map<String, Long> body) {
        clazzService.addStudent(id, body.get("studentId"));
        return Result.success();
    }

    @DeleteMapping("/{id}/students/{studentId}")
    public Result<Void> removeStudent(@PathVariable Long id, @PathVariable Long studentId) {
        clazzService.removeStudent(id, studentId);
        return Result.success();
    }
}
