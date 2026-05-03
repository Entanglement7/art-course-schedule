package com.art.schedule.mapper;

import com.art.schedule.entity.Schedule;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import java.time.LocalDate;
import java.util.List;

@Mapper
public interface ScheduleMapper extends BaseMapper<Schedule> {

    @Select("""
        SELECT s.*, cl.name AS class_name, co.name AS course_name, co.category AS course_category,
               t.name AS teacher_name, cr.name AS classroom_name
        FROM schedules s
        LEFT JOIN classes cl ON s.class_id = cl.id
        LEFT JOIN courses co ON s.course_id = co.id
        LEFT JOIN teachers t ON s.teacher_id = t.id
        LEFT JOIN classrooms cr ON s.classroom_id = cr.id
        WHERE s.date BETWEEN #{startDate} AND #{endDate}
        ORDER BY s.date, s.start_time
    """)
    List<Schedule> selectWeekSchedule(@Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate);

    @Select("""
        SELECT s.*, cl.name AS class_name, co.name AS course_name, co.category AS course_category,
               t.name AS teacher_name, cr.name AS classroom_name
        FROM schedules s
        LEFT JOIN classes cl ON s.class_id = cl.id
        LEFT JOIN courses co ON s.course_id = co.id
        LEFT JOIN teachers t ON s.teacher_id = t.id
        LEFT JOIN classrooms cr ON s.classroom_id = cr.id
        WHERE s.teacher_id = #{teacherId} AND s.date BETWEEN #{startDate} AND #{endDate}
        ORDER BY s.date, s.start_time
    """)
    List<Schedule> selectTeacherSchedule(@Param("teacherId") Long teacherId,
                                         @Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate);

    @Select("""
        SELECT s.*, cl.name AS class_name, co.name AS course_name, co.category AS course_category,
               t.name AS teacher_name, cr.name AS classroom_name
        FROM schedules s
        LEFT JOIN classes cl ON s.class_id = cl.id
        LEFT JOIN courses co ON s.course_id = co.id
        LEFT JOIN teachers t ON s.teacher_id = t.id
        LEFT JOIN classrooms cr ON s.classroom_id = cr.id
        JOIN class_students cs ON cs.class_id = s.class_id AND cs.student_id = #{studentId}
        WHERE s.date BETWEEN #{startDate} AND #{endDate}
        ORDER BY s.date, s.start_time
    """)
    List<Schedule> selectStudentSchedule(@Param("studentId") Long studentId,
                                         @Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate);

    @Select("""
        SELECT s.*, cl.name AS class_name, co.name AS course_name, co.category AS course_category,
               t.name AS teacher_name, cr.name AS classroom_name
        FROM schedules s
        LEFT JOIN classes cl ON s.class_id = cl.id
        LEFT JOIN courses co ON s.course_id = co.id
        LEFT JOIN teachers t ON s.teacher_id = t.id
        LEFT JOIN classrooms cr ON s.classroom_id = cr.id
        WHERE s.id = #{id}
    """)
    Schedule selectByIdWithExtra(@Param("id") Long id);

    @Select("""
        SELECT s.*, cl.name AS class_name, co.name AS course_name, co.category AS course_category,
               t.name AS teacher_name, cr.name AS classroom_name
        FROM schedules s
        LEFT JOIN classes cl ON s.class_id = cl.id
        LEFT JOIN courses co ON s.course_id = co.id
        LEFT JOIN teachers t ON s.teacher_id = t.id
        LEFT JOIN classrooms cr ON s.classroom_id = cr.id
        WHERE s.classroom_id = #{classroomId} AND s.date BETWEEN #{startDate} AND #{endDate}
        ORDER BY s.date, s.start_time
    """)
    List<Schedule> selectClassroomSchedule(@Param("classroomId") Long classroomId,
                                           @Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate);
}
