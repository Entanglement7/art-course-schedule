package com.art.schedule.mapper;

import com.art.schedule.entity.Student;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface StudentMapper extends BaseMapper<Student> {

    @Select("SELECT course_name FROM student_courses WHERE student_id = #{studentId}")
    List<String> selectCourses(Long studentId);

    @Delete("DELETE FROM student_courses WHERE student_id = #{studentId}")
    void deleteCourses(Long studentId);

    @Insert("<script>INSERT INTO student_courses (student_id, course_name) VALUES " +
            "<foreach collection='courses' item='c' separator=','>(#{studentId}, #{c})</foreach></script>")
    void insertCourses(@Param("studentId") Long studentId, @Param("courses") List<String> courses);
}
