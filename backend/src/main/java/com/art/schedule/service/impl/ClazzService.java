package com.art.schedule.service.impl;

import com.art.schedule.common.PageResult;
import com.art.schedule.dto.ClazzRequest;
import com.art.schedule.entity.Clazz;
import com.art.schedule.entity.Teacher;
import com.art.schedule.mapper.ClazzMapper;
import com.art.schedule.mapper.CourseMapper;
import com.art.schedule.mapper.ScheduleMapper;
import com.art.schedule.mapper.TeacherMapper;
import com.art.schedule.entity.Student;
import com.art.schedule.mapper.StudentMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.List;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

@Service
public class ClazzService {

    private final ClazzMapper clazzMapper;
    private final TeacherMapper teacherMapper;
    private final StudentMapper studentMapper;
    private final ScheduleMapper scheduleMapper;
    private final CourseMapper courseMapper;

    public ClazzService(ClazzMapper clazzMapper, TeacherMapper teacherMapper,
                        StudentMapper studentMapper, ScheduleMapper scheduleMapper,
                        CourseMapper courseMapper) {
        this.clazzMapper = clazzMapper;
        this.teacherMapper = teacherMapper;
        this.studentMapper = studentMapper;
        this.scheduleMapper = scheduleMapper;
        this.courseMapper = courseMapper;
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
        scheduleMapper.deleteByClassId(id);
        clazzMapper.deleteById(id);
    }

    public java.util.List<Student> getStudents(Long classId) {
        List<Long> ids = clazzMapper.selectStudentIds(classId);
        if (ids.isEmpty()) return java.util.List.of();
        return studentMapper.selectList(
            new LambdaQueryWrapper<Student>().in(Student::getId, ids)
        );
    }

    public void addStudent(Long classId, Long studentId) {
        Clazz c = clazzMapper.selectById(classId);
        if (c == null) throw new IllegalArgumentException("班级不存在");
        List<Long> existing = clazzMapper.selectStudentIds(classId);
        if (existing.contains(studentId)) throw new IllegalArgumentException("该学生已在班级中");
        clazzMapper.insertStudent(classId, studentId);
    }

    public void removeStudent(Long classId, Long studentId) {
        clazzMapper.deleteStudentFromClass(classId, studentId);
    }

    private void fillExtra(java.util.List<Clazz> list) {
        list.forEach(c -> {
            Teacher t = teacherMapper.selectById(c.getTeacherId());
            if (t != null) c.setTeacherName(t.getName());
            c.setCurrentCount(clazzMapper.countStudents(c.getId()));
            if (c.getCourseId() != null) {
                var course = courseMapper.selectById(c.getCourseId());
                if (course != null) c.setCourseDuration(course.getDuration());
            }
        });
    }

    private void copyProps(ClazzRequest req, Clazz c) {
        c.setName(req.getName());
        c.setCategory(req.getCategory());
        c.setCourseName(req.getCourseName());
        c.setCourseId(req.getCourseId());
        c.setTeacherId(req.getTeacherId());
        c.setStudentCount(req.getStudentCount());
        c.setSchedule(req.getSchedule());
        c.setStartDate(LocalDate.parse(req.getStartDate()));
    }
}
