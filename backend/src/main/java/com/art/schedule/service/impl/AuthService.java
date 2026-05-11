package com.art.schedule.service.impl;

import com.art.schedule.common.JwtUtil;
import com.art.schedule.dto.LoginRequest;
import com.art.schedule.dto.LoginResponse;
import com.art.schedule.dto.RegisterRequest;
import com.art.schedule.entity.User;
import com.art.schedule.entity.Teacher;
import com.art.schedule.entity.Student;
import com.art.schedule.mapper.UserMapper;
import com.art.schedule.mapper.TeacherMapper;
import com.art.schedule.mapper.StudentMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserMapper userMapper;
    private final TeacherMapper teacherMapper;
    private final StudentMapper studentMapper;
    private final JwtUtil jwtUtil;

    public AuthService(UserMapper userMapper, TeacherMapper teacherMapper, StudentMapper studentMapper, JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.teacherMapper = teacherMapper;
        this.studentMapper = studentMapper;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public LoginResponse register(RegisterRequest req) {
        if (userMapper.selectOne(new QueryWrapper<User>().eq("username", req.getUsername())) != null) {
            throw new IllegalArgumentException("用户名已存在");
        }
        String role = "teacher".equals(req.getRole()) ? "teacher" : "student";
        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(req.getPassword());
        user.setName(req.getName());
        user.setRole(role);
        userMapper.insert(user);

        if ("teacher".equals(role)) {
            Teacher teacher = new Teacher();
            teacher.setName(req.getName());
            teacherMapper.insert(teacher);
            user.setTeacherId(teacher.getId());
            userMapper.updateById(user);
        } else if ("student".equals(role)) {
            Student student = new Student();
            student.setName(req.getName());
            studentMapper.insert(student);
            user.setStudentId(student.getId());
            userMapper.updateById(user);
        }

        return buildResponse(user);
    }

    public LoginResponse login(LoginRequest req) {
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", req.getUsername()));
        if (user == null || !req.getPassword().equals(user.getPassword())) {
            throw new IllegalArgumentException("用户名或密码错误");
        }
        return buildResponse(user);
    }

    private LoginResponse buildResponse(User user) {
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        LoginResponse resp = new LoginResponse();
        resp.setToken(token);
        LoginResponse.UserInfoDto info = new LoginResponse.UserInfoDto();
        info.setId(user.getId());
        info.setUsername(user.getUsername());
        info.setName(user.getName());
        info.setRole(user.getRole());
        info.setTeacherId(user.getTeacherId());
        info.setStudentId(user.getStudentId());
        resp.setUserInfo(info);
        return resp;
    }
}
