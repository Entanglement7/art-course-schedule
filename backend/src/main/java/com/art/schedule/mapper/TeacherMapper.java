package com.art.schedule.mapper;

import com.art.schedule.entity.Teacher;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface TeacherMapper extends BaseMapper<Teacher> {

    @Select("SELECT course_name FROM teacher_courses WHERE teacher_id = #{teacherId}")
    List<String> selectCourses(Long teacherId);

    @Delete("DELETE FROM teacher_courses WHERE teacher_id = #{teacherId}")
    void deleteCourses(Long teacherId);

    @Insert("<script>INSERT INTO teacher_courses (teacher_id, course_name) VALUES " +
            "<foreach collection='courses' item='c' separator=','>(#{teacherId}, #{c})</foreach></script>")
    void insertCourses(@Param("teacherId") Long teacherId, @Param("courses") List<String> courses);

    @Select("SELECT COUNT(*) FROM classes WHERE teacher_id = #{teacherId}")
    Integer selectClassCount(Long teacherId);

    @Select("SELECT COALESCE(SUM(student_count), 0) FROM classes WHERE teacher_id = #{teacherId}")
    Integer selectStudentCount(Long teacherId);

    @Select("SELECT name FROM classes WHERE teacher_id = #{teacherId}")
    List<String> selectClassNames(Long teacherId);

    @Select("SELECT COUNT(*) FROM schedules WHERE teacher_id = #{teacherId} AND WEEK(date) = WEEK(CURDATE())")
    Integer selectWeekCourseCount(Long teacherId);

    @Select("SELECT COUNT(*) FROM schedules WHERE teacher_id = #{teacherId} AND MONTH(date) = MONTH(CURDATE())")
    Integer selectMonthCourseCount(Long teacherId);
}
