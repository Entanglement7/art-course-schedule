package com.art.schedule.service.impl;

import com.art.schedule.common.PageResult;
import com.art.schedule.dto.TeacherRequest;
import com.art.schedule.entity.Teacher;
import com.art.schedule.entity.User;
import com.art.schedule.mapper.TeacherMapper;
import com.art.schedule.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class TeacherService {

    private final TeacherMapper teacherMapper;
    private final UserMapper userMapper;

    public TeacherService(TeacherMapper teacherMapper, UserMapper userMapper) {
        this.teacherMapper = teacherMapper;
        this.userMapper = userMapper;
    }

    public PageResult<Teacher> list(int page, int size, String search, String specialty) {
        LambdaQueryWrapper<Teacher> q = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(search)) q.like(Teacher::getName, search);
        if (StringUtils.hasText(specialty)) q.eq(Teacher::getSpecialty, specialty);
        q.orderByDesc(Teacher::getCreatedAt);
        Page<Teacher> p = teacherMapper.selectPage(new Page<>(page, size), q);
        p.getRecords().forEach(t -> t.setCourses(teacherMapper.selectCourses(t.getId())));
        return PageResult.of(p.getTotal(), p.getRecords());
    }

    public Teacher getById(Long id) {
        Teacher t = teacherMapper.selectById(id);
        if (t == null) throw new IllegalArgumentException("教师不存在");
        t.setCourses(teacherMapper.selectCourses(id));
        return t;
    }

    @Transactional
    public Teacher create(TeacherRequest req) {
        Teacher t = new Teacher();
        copyProps(req, t);
        teacherMapper.insert(t);
        if (req.getCourses() != null && !req.getCourses().isEmpty()) {
            teacherMapper.insertCourses(t.getId(), req.getCourses());
        }
        // 如果指定了用户ID，关联该用户
        if (req.getUserId() != null) {
            User user = userMapper.selectById(req.getUserId());
            if (user != null && "teacher".equals(user.getRole())) {
                user.setTeacherId(t.getId());
                userMapper.updateById(user);
            }
        }
        t.setCourses(req.getCourses());
        return t;
    }

    @Transactional
    public Teacher update(Long id, TeacherRequest req) {
        Teacher t = teacherMapper.selectById(id);
        if (t == null) throw new IllegalArgumentException("教师不存在");
        copyProps(req, t);
        teacherMapper.updateById(t);
        teacherMapper.deleteCourses(id);
        if (req.getCourses() != null && !req.getCourses().isEmpty()) {
            teacherMapper.insertCourses(id, req.getCourses());
        }
        // 如果指定了用户ID，关联该用户
        if (req.getUserId() != null) {
            User user = userMapper.selectById(req.getUserId());
            if (user != null && "teacher".equals(user.getRole())) {
                user.setTeacherId(id);
                userMapper.updateById(user);
            }
        }
        t.setCourses(req.getCourses());
        return t;
    }

    @Transactional
    public void delete(Long id) {
        teacherMapper.deleteById(id);
        teacherMapper.deleteCourses(id);
    }

    public List<Map<String, Object>> options() {
        return teacherMapper.selectList(new LambdaQueryWrapper<Teacher>().select(Teacher::getId, Teacher::getName))
                .stream()
                .map(t -> Map.<String, Object>of("id", t.getId(), "name", t.getName()))
                .toList();
    }

    private void copyProps(TeacherRequest req, Teacher t) {
        t.setName(req.getName());
        t.setPhone(req.getPhone());
        t.setSpecialty(req.getSpecialty());
        t.setJoinDate(LocalDate.parse(req.getJoinDate()));
    }
}
