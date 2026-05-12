package com.art.schedule.mapper;

import com.art.schedule.entity.Schedule;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Mapper
public interface ScheduleMapper extends BaseMapper<Schedule> {

    @Select("""
        SELECT s.*, cl.name AS class_name, co.name AS course_name, co.category AS course_category,
               t.name AS teacher_name, cr.name AS classroom_name,
               (SELECT COUNT(*) FROM class_students WHERE class_id = s.class_id) AS student_count
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
               t.name AS teacher_name, cr.name AS classroom_name,
               (SELECT COUNT(*) FROM class_students WHERE class_id = s.class_id) AS student_count
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
               t.name AS teacher_name, cr.name AS classroom_name,
               (SELECT COUNT(*) FROM class_students WHERE class_id = s.class_id) AS student_count
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
               t.name AS teacher_name, cr.name AS classroom_name,
               (SELECT COUNT(*) FROM class_students WHERE class_id = s.class_id) AS student_count
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
               t.name AS teacher_name, cr.name AS classroom_name,
               (SELECT COUNT(*) FROM class_students WHERE class_id = s.class_id) AS student_count
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

    @Delete("DELETE FROM schedules WHERE class_id = #{classId}")
    void deleteByClassId(@Param("classId") Long classId);

    // 冲突检测查询方法
    @Select("""
        SELECT COUNT(*) FROM schedules
        WHERE teacher_id = #{teacherId}
          AND day_of_week = #{dayOfWeek}
          AND ((start_time < #{endTime} AND end_time > #{startTime}))
    """)
    int countTeacherConflicts(@Param("teacherId") Long teacherId,
                             @Param("dayOfWeek") int dayOfWeek,
                             @Param("startTime") LocalTime startTime,
                             @Param("endTime") LocalTime endTime);

    @Select("""
        SELECT COUNT(*) FROM schedules
        WHERE classroom_id = #{classroomId}
          AND day_of_week = #{dayOfWeek}
          AND ((start_time < #{endTime} AND end_time > #{startTime}))
    """)
    int countClassroomConflicts(@Param("classroomId") Long classroomId,
                               @Param("dayOfWeek") int dayOfWeek,
                               @Param("startTime") LocalTime startTime,
                               @Param("endTime") LocalTime endTime);

    @Select({
        "<script>",
        "SELECT COUNT(*) FROM schedules s",
        "JOIN class_students cs ON s.class_id = cs.class_id",
        "WHERE cs.student_id IN",
        "<foreach collection='studentIds' item='id' open='(' separator=',' close=')'>",
        "#{id}",
        "</foreach>",
        "AND s.day_of_week = #{dayOfWeek}",
        "AND ((s.start_time &lt; #{endTime} AND s.end_time &gt; #{startTime}))",
        "</script>"
    })
    int countStudentConflicts(@Param("studentIds") List<Long> studentIds,
                             @Param("dayOfWeek") int dayOfWeek,
                             @Param("startTime") LocalTime startTime,
                             @Param("endTime") LocalTime endTime);
}
