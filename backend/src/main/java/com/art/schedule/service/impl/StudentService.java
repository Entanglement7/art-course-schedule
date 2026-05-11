package com.art.schedule.service.impl;

import com.art.schedule.common.PageResult;
import com.art.schedule.dto.StudentRequest;
import com.art.schedule.entity.Student;
import com.art.schedule.entity.User;
import com.art.schedule.mapper.StudentMapper;
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
public class StudentService {

    private final StudentMapper studentMapper;
    private final UserMapper userMapper;

    public StudentService(StudentMapper studentMapper, UserMapper userMapper) {
        this.studentMapper = studentMapper;
        this.userMapper = userMapper;
    }

    public PageResult<Student> list(int page, int size, String search, String course) {
        LambdaQueryWrapper<Student> q = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(search)) q.like(Student::getName, search);
        q.orderByDesc(Student::getCreatedAt);

        if (StringUtils.hasText(course)) {
            // 通过子查询过滤有该课程的学生
            q.inSql(Student::getId,
                "SELECT student_id FROM student_courses WHERE course_name = '" + course + "'");
        }

        Page<Student> p = studentMapper.selectPage(new Page<>(page, size), q);
        p.getRecords().forEach(s -> s.setCourses(studentMapper.selectCourses(s.getId())));
        return PageResult.of(p.getTotal(), p.getRecords());
    }

    public Student getById(Long id) {
        Student s = studentMapper.selectById(id);
        if (s == null) throw new IllegalArgumentException("学生不存在");
        s.setCourses(studentMapper.selectCourses(id));
        return s;
    }

    @Transactional
    public Student create(StudentRequest req) {
        Student s = new Student();
        copyProps(req, s);
        studentMapper.insert(s);
        if (req.getCourses() != null && !req.getCourses().isEmpty()) {
            studentMapper.insertCourses(s.getId(), req.getCourses());
        }
        // 如果指定了用户ID，关联该用户
        if (req.getUserId() != null) {
            User user = userMapper.selectById(req.getUserId());
            if (user != null && "student".equals(user.getRole())) {
                user.setStudentId(s.getId());
                userMapper.updateById(user);
            }
        }
        s.setCourses(req.getCourses());
        return s;
    }

    @Transactional
    public Student update(Long id, StudentRequest req) {
        Student s = studentMapper.selectById(id);
        if (s == null) throw new IllegalArgumentException("学生不存在");
        copyProps(req, s);
        studentMapper.updateById(s);
        studentMapper.deleteCourses(id);
        if (req.getCourses() != null && !req.getCourses().isEmpty()) {
            studentMapper.insertCourses(id, req.getCourses());
        }
        // 如果指定了用户ID，关联该用户
        if (req.getUserId() != null) {
            User user = userMapper.selectById(req.getUserId());
            if (user != null && "student".equals(user.getRole())) {
                user.setStudentId(id);
                userMapper.updateById(user);
            }
        }
        s.setCourses(req.getCourses());
        return s;
    }

    @Transactional
    public void delete(Long id) {
        studentMapper.deleteById(id);
        studentMapper.deleteCourses(id);
    }

    public List<Map<String, Object>> options() {
        return studentMapper.selectList(new LambdaQueryWrapper<Student>()
                .select(Student::getId, Student::getName))
                .stream()
                .map(s -> Map.<String, Object>of("id", s.getId(), "name", s.getName()))
                .toList();
    }

    private void copyProps(StudentRequest req, Student s) {
        s.setName(req.getName());
        s.setAge(req.getAge());
        s.setGender(req.getGender());
        s.setParentName(req.getParentName());
        s.setPhone(req.getPhone());
        s.setEnrollDate(LocalDate.parse(req.getEnrollDate()));
    }
}
