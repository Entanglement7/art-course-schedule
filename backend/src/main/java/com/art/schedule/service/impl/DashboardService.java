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

        // 获取学生报名的课程详细信息
        List<Map<String, Object>> courseDetails = studentMapper.selectCourseDetails(studentId);
        data.put("enrolledCourses", courseDetails.size());
        data.put("myCourses", courseDetails);

        LocalDate today = LocalDate.now();
        List<Schedule> todaySchedules = scheduleMapper.selectStudentSchedule(studentId, today, today);
        data.put("todayClasses", todaySchedules.size());
        data.put("todayCourseList", todaySchedules);

        long totalHours = scheduleMapper.selectStudentSchedule(studentId,
                LocalDate.now().minusMonths(6), LocalDate.now()).size();
        data.put("totalHours", totalHours);

        // 计算本周课时
        LocalDate monday = today.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        LocalDate sunday = monday.plusDays(6);
        long weekHours = scheduleMapper.selectStudentSchedule(studentId, monday, sunday).size();
        data.put("weekHours", weekHours);

        // 获取学生所在的班级信息
        List<Clazz> myClasses = clazzMapper.selectList(
                new LambdaQueryWrapper<Clazz>().inSql(Clazz::getId,
                        "SELECT class_id FROM class_students WHERE student_id = " + studentId));

        // 为每个班级添加教师名字
        List<Map<String, Object>> classesWithTeacher = myClasses.stream().map(clazz -> {
            Map<String, Object> classInfo = new HashMap<>();
            classInfo.put("id", clazz.getId());
            classInfo.put("name", clazz.getName());
            classInfo.put("studentCount", clazzMapper.countStudents(clazz.getId()));

            // 获取教师名字
            Teacher teacher = teacherMapper.selectById(clazz.getTeacherId());
            classInfo.put("teacher", teacher != null ? teacher.getName() : "");

            return classInfo;
        }).toList();

        data.put("myClasses", classesWithTeacher);
        return data;
    }

    public Map<String, Object> unlinkedTeacherStats() {
        Map<String, Object> data = new HashMap<>();
        data.put("unlinked", true);
        data.put("message", "您的账号尚未关联教师信息，请联系管理员完成关联");
        data.put("myClasses", 0);
        data.put("todayClasses", 0);
        data.put("totalStudents", 0);
        data.put("weekHours", 0);
        data.put("todayCourseList", List.of());
        return data;
    }

    public Map<String, Object> unlinkedStudentStats() {
        Map<String, Object> data = new HashMap<>();
        data.put("unlinked", true);
        data.put("message", "您的账号尚未关联学生信息，请联系管理员完成关联");
        data.put("enrolledCourses", 0);
        data.put("todayClasses", 0);
        data.put("totalHours", 0);
        data.put("weekHours", 0);
        data.put("todayCourseList", List.of());
        data.put("studentClasses", List.of());
        return data;
    }
}
