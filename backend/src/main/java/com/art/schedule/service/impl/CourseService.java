package com.art.schedule.service.impl;

import com.art.schedule.common.PageResult;
import com.art.schedule.dto.CourseRequest;
import com.art.schedule.entity.Course;
import com.art.schedule.mapper.CourseMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

@Service
public class CourseService {

    private final CourseMapper courseMapper;

    public CourseService(CourseMapper courseMapper) {
        this.courseMapper = courseMapper;
    }

    public PageResult<Course> list(int page, int size, String search, String category, String type) {
        LambdaQueryWrapper<Course> q = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(search)) q.like(Course::getName, search);
        if (StringUtils.hasText(category)) q.eq(Course::getCategory, category);
        if (StringUtils.hasText(type)) q.eq(Course::getType, type);
        q.orderByDesc(Course::getCreatedAt);
        Page<Course> p = courseMapper.selectPage(new Page<>(page, size), q);
        return PageResult.of(p.getTotal(), p.getRecords());
    }

    public Course getById(Long id) {
        Course c = courseMapper.selectById(id);
        if (c == null) throw new IllegalArgumentException("课程不存在");
        return c;
    }

    public Course create(CourseRequest req) {
        Course c = new Course();
        copyProps(req, c);
        courseMapper.insert(c);
        return c;
    }

    public Course update(Long id, CourseRequest req) {
        Course c = courseMapper.selectById(id);
        if (c == null) throw new IllegalArgumentException("课程不存在");
        copyProps(req, c);
        courseMapper.updateById(c);
        return c;
    }

    public void delete(Long id) {
        courseMapper.deleteById(id);
    }

    public List<Map<String, Object>> options() {
        return courseMapper.selectList(new LambdaQueryWrapper<Course>().select(Course::getId, Course::getName, Course::getCategory))
                .stream()
                .map(c -> Map.<String, Object>of("id", c.getId(), "name", c.getName(), "category", c.getCategory()))
                .toList();
    }

    private void copyProps(CourseRequest req, Course c) {
        c.setName(req.getName());
        c.setCategory(req.getCategory());
        c.setType(req.getType());
        c.setDuration(req.getDuration());
        c.setAgeRange(req.getAgeRange());
        c.setPrice(req.getPrice());
        c.setDescription(req.getDescription());
    }
}
