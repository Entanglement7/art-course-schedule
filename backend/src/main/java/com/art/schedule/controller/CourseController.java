package com.art.schedule.controller;

import com.art.schedule.common.PageResult;
import com.art.schedule.common.Result;
import com.art.schedule.dto.CourseRequest;
import com.art.schedule.entity.Course;
import com.art.schedule.service.impl.CourseService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public Result<PageResult<Course>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String type) {
        return Result.success(courseService.list(page, size, search, category, type));
    }

    @GetMapping("/options")
    public Result<List<Map<String, Object>>> options() {
        return Result.success(courseService.options());
    }

    @GetMapping("/{id}")
    public Result<Course> getById(@PathVariable Long id) {
        return Result.success(courseService.getById(id));
    }

    @PostMapping
    public Result<Course> create(@Valid @RequestBody CourseRequest req) {
        return Result.success(courseService.create(req));
    }

    @PutMapping("/{id}")
    public Result<Course> update(@PathVariable Long id, @Valid @RequestBody CourseRequest req) {
        return Result.success(courseService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        courseService.delete(id);
        return Result.success();
    }
}
