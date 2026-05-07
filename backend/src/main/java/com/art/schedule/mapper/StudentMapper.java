package com.art.schedule.mapper;

import com.art.schedule.entity.Student;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface StudentMapper extends BaseMapper<Student> {

    @Select("SELECT course_name FROM student_courses WHERE student_id = #{studentId}")
    List<String> selectCourses(Long studentId);

    @Select("""
        SELECT DISTINCT c.id, c.name, c.category,
               (SELECT t.name FROM classes cl
                JOIN teachers t ON cl.teacher_id = t.id
                WHERE cl.course_name = c.name LIMIT 1) as teacher_name,
               (SELECT cl.schedule FROM classes cl
                WHERE cl.course_name = c.name LIMIT 1) as schedule,
               (SELECT cr.name FROM classrooms cr
                WHERE cr.type = CONCAT(c.category, '教室') LIMIT 1) as classroom_name
        FROM student_courses sc
        JOIN courses c ON sc.course_name = c.name
        WHERE sc.student_id = #{studentId}
    """)
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "name", column = "name"),
        @Result(property = "category", column = "category"),
        @Result(property = "teacher", column = "teacher_name"),
        @Result(property = "schedule", column = "schedule"),
        @Result(property = "classroom", column = "classroom_name")
    })
    List<java.util.Map<String, Object>> selectCourseDetails(Long studentId);

    @Delete("DELETE FROM student_courses WHERE student_id = #{studentId}")
    void deleteCourses(Long studentId);

    @Insert("<script>INSERT INTO student_courses (student_id, course_name) VALUES " +
            "<foreach collection='courses' item='c' separator=','>(#{studentId}, #{c})</foreach></script>")
    void insertCourses(@Param("studentId") Long studentId, @Param("courses") List<String> courses);
}
