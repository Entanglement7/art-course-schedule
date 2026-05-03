package com.art.schedule.service.impl;

import com.art.schedule.common.PageResult;
import com.art.schedule.dto.ClazzRequest;
import com.art.schedule.entity.Clazz;
import com.art.schedule.entity.Teacher;
import com.art.schedule.mapper.ClazzMapper;
import com.art.schedule.mapper.TeacherMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

@Service
public class ClazzService {

    private final ClazzMapper clazzMapper;
    private final TeacherMapper teacherMapper;

    public ClazzService(ClazzMapper clazzMapper, TeacherMapper teacherMapper) {
        this.clazzMapper = clazzMapper;
        this.teacherMapper = teacherMapper;
    }

    public PageResult<Clazz> list(int page, int size, String search, String category, Long teacherId) {
        LambdaQueryWrapper<Clazz> q = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(search)) q.like(Clazz::getName, search);
        if (StringUtils.hasText(category)) q.eq(Clazz::getCategory, category);
        if (teacherId != null) q.eq(Clazz::getTeacherId, teacherId);
        q.orderByDesc(Clazz::getCreatedAt);
        Page<Clazz> p = clazzMapper.selectPage(new Page<>(page, size), q);
        fillExtra(p.getRecords());
        return PageResult.of(p.getTotal(), p.getRecords());
    }

    public Clazz getById(Long id) {
        Clazz c = clazzMapper.selectById(id);
        if (c == null) throw new IllegalArgumentException("班级不存在");
        fillExtra(java.util.List.of(c));
        return c;
    }

    public Clazz create(ClazzRequest req) {
        Clazz c = new Clazz();
        copyProps(req, c);
        clazzMapper.insert(c);
        fillExtra(java.util.List.of(c));
        return c;
    }

    public Clazz update(Long id, ClazzRequest req) {
        Clazz c = clazzMapper.selectById(id);
        if (c == null) throw new IllegalArgumentException("班级不存在");
        copyProps(req, c);
        clazzMapper.updateById(c);
        fillExtra(java.util.List.of(c));
        return c;
    }

    public void delete(Long id) {
        clazzMapper.deleteById(id);
    }

    private void fillExtra(java.util.List<Clazz> list) {
        list.forEach(c -> {
            Teacher t = teacherMapper.selectById(c.getTeacherId());
            if (t != null) c.setTeacherName(t.getName());
            c.setCurrentCount(clazzMapper.countStudents(c.getId()));
        });
    }

    private void copyProps(ClazzRequest req, Clazz c) {
        c.setName(req.getName());
        c.setCategory(req.getCategory());
        c.setCourseName(req.getCourseName());
        c.setTeacherId(req.getTeacherId());
        c.setStudentCount(req.getStudentCount());
        c.setSchedule(req.getSchedule());
        c.setStartDate(LocalDate.parse(req.getStartDate()));
    }
}
