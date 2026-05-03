package com.art.schedule.mapper;

import com.art.schedule.entity.Clazz;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ClazzMapper extends BaseMapper<Clazz> {

    @Select("SELECT COUNT(*) FROM class_students WHERE class_id = #{classId}")
    Integer countStudents(Long classId);

    @Select("SELECT student_id FROM class_students WHERE class_id = #{classId}")
    List<Long> selectStudentIds(Long classId);

    @Delete("DELETE FROM class_students WHERE class_id = #{classId}")
    void deleteStudents(Long classId);

    @Insert("<script>INSERT INTO class_students (class_id, student_id) VALUES " +
            "<foreach collection='studentIds' item='s' separator=','>(#{classId}, #{s})</foreach></script>")
    void insertStudents(@Param("classId") Long classId, @Param("studentIds") List<Long> studentIds);
}
