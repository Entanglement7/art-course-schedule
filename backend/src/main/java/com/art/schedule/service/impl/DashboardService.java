package com.art.schedule.service.impl;

import com.art.schedule.mapper.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.art.schedule.entity.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    private final TeacherMapper teacherMapper;
    private final StudentMapper studentMapper;
    private final ClassroomMapper classroomMapper;
    private final ScheduleMapper scheduleMapper;
    private final ClazzMapper clazzMapper;
    private final CourseMapper courseMapper;

    public DashboardService(TeacherMapper teacherMapper, StudentMapper studentMapper,
                            ClassroomMapper classroomMapper, ScheduleMapper scheduleMapper,
                            ClazzMapper clazzMapper, CourseMapper courseMapper) {
        this.teacherMapper = teacherMapper;
        this.studentMapper = studentMapper;
        this.classroomMapper = classroomMapper;
        this.scheduleMapper = scheduleMapper;
        this.clazzMapper = clazzMapper;
        this.courseMapper = courseMapper;
    }

    public Map<String, Object> adminStats() {
        Map<String, Object> data = new HashMap<>();
        data.put("totalStudents", studentMapper.selectCount(null));
        data.put("totalTeachers", teacherMapper.selectCount(null));
        data.put("totalClassrooms", classroomMapper.selectCount(null));

        LocalDate today = LocalDate.now();
        List<Schedule> todaySchedules = scheduleMapper.selectWeekSchedule(today, today);
        data.put("todayCourses", todaySchedules.size());

        long music = courseMapper.selectCount(new LambdaQueryWrapper<Course>().eq(Course::getCategory, "音乐"));
        long dance = courseMapper.selectCount(new LambdaQueryWrapper<Course>().eq(Course::getCategory, "舞蹈"));
        long art = courseMapper.selectCount(new LambdaQueryWrapper<Course>().eq(Course::getCategory, "美术"));
        data.put("courseStats", Map.of("music", music, "dance", dance, "art", art, "total", music + dance + art));
        data.put("todayCourseList", todaySchedules);
        return data;
    }

    public Map<String, Object> teacherStats(Long teacherId) {
        Map<String, Object> data = new HashMap<>();
        long myClasses = clazzMapper.selectCount(new LambdaQueryWrapper<Clazz>().eq(Clazz::getTeacherId, teacherId));
        data.put("myClasses", myClasses);

        LocalDate today = LocalDate.now();
        List<Schedule> todaySchedules = scheduleMapper.selectTeacherSchedule(teacherId, today, today);
        data.put("todayClasses", todaySchedules.size());
        data.put("todayCourseList", todaySchedules);

        long totalStudents = clazzMapper.selectList(new LambdaQueryWrapper<Clazz>().eq(Clazz::getTeacherId, teacherId))
                .stream().mapToLong(c -> clazzMapper.countStudents(c.getId())).sum();
        data.put("totalStudents", totalStudents);

        LocalDate monday = today.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        LocalDate sunday = monday.plusDays(6);
        data.put("weekHours", scheduleMapper.selectTeacherSchedule(teacherId, monday, sunday).size());
        return data;
    }

    public Map<String, Object> studentStats(Long studentId) {
        Map<String, Object> data = new HashMap<>();
        long enrolledCourses = studentMapper.selectCourses(studentId).size();
        data.put("enrolledCourses", enrolledCourses);

        LocalDate today = LocalDate.now();
        List<Schedule> todaySchedules = scheduleMapper.selectStudentSchedule(studentId, today, today);
        data.put("todayClasses", todaySchedules.size());
        data.put("todayCourseList", todaySchedules);

        long totalHours = scheduleMapper.selectStudentSchedule(studentId,
                LocalDate.now().minusMonths(6), LocalDate.now()).size();
        data.put("totalHours", totalHours);
        data.put("attendanceRate", 95);

        List<Long> classIds = clazzMapper.selectList(
                new LambdaQueryWrapper<Clazz>().inSql(Clazz::getId,
                        "SELECT class_id FROM class_students WHERE student_id = " + studentId))
                .stream().map(Clazz::getId).toList();
        data.put("studentClasses", classIds);
        return data;
    }
}
