package com.art.schedule.controller;

import com.art.schedule.common.PageResult;
import com.art.schedule.common.Result;
import com.art.schedule.dto.StudentRequest;
import com.art.schedule.entity.Student;
import com.art.schedule.service.impl.StudentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public Result<PageResult<Student>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String course) {
        return Result.success(studentService.list(page, size, search, course));
    }

    @GetMapping("/{id}")
    public Result<Student> getById(@PathVariable Long id) {
        return Result.success(studentService.getById(id));
    }

    @PostMapping
    public Result<Student> create(@Valid @RequestBody StudentRequest req) {
        return Result.success(studentService.create(req));
    }

    @PutMapping("/{id}")
    public Result<Student> update(@PathVariable Long id, @Valid @RequestBody StudentRequest req) {
        return Result.success(studentService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        studentService.delete(id);
        return Result.success();
    }
}
